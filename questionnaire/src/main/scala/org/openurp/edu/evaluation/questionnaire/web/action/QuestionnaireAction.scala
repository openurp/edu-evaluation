/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.evaluation.questionnaire.web.action

import java.time.{ Instant, LocalDate }

import scala.collection.mutable.Buffer

import org.beangle.commons.collection.{ Collections, Order }
import org.beangle.commons.lang.{ Numbers, Strings }
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.annotation.param
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.model.Department
import org.openurp.boot.edu.helper.ProjectSupport
import org.openurp.edu.evaluation.clazz.model.QuestionnaireClazz
import org.openurp.edu.evaluation.model.{ Question, QuestionType, Questionnaire }

class QuestionnaireAction extends RestfulAction[Questionnaire] with ProjectSupport{

  override def search(): View = {
    val builder = OqlBuilder.from(classOf[Questionnaire], "questionnaire")
    populateConditions(builder)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
    val questionnaires = entityDao.search(builder)

    put("questionnaires", questionnaires)
    forward()
  }

  override def editSetting(entity: Questionnaire): Unit = {
    val questionnaire = entity.asInstanceOf[Questionnaire]
    val departmentList = entityDao.getAll(classOf[Department])
    put("departments", departmentList)
    val questionTree = Collections.newMap[QuestionType, Buffer[Question]]
    questionnaire.questions foreach { question =>
      val key = question.questionType
      var questions: Buffer[Question] = questionTree.get(key).orNull
      if (null == questions) {
        questions = Collections.newBuffer
      }
      questions += question
      questions.sortWith((x, y) => x.priority < y.priority)
      questionTree.put(key, questions)
    }
    put("questions", questionnaire.questions)
    put("questionTree", questionTree)

  }

  override def info(@param("id") id: String): View = {
    if (id == 0L) {
      logger.info("查看失败")
      redirect("search", "请选择一条记录")
    }
    val questionnaire = entityDao.get(classOf[Questionnaire], Numbers.toLong(id))
    val questionTree = Collections.newMap[QuestionType, Buffer[Question]]
    questionnaire.questions foreach { question =>
      val key = question.questionType
      var questions: Buffer[Question] = questionTree.get(key).orNull
      if (null == questions) {
        questions = Collections.newBuffer
      }
      questions += question
      questions.sortWith((x, y) => x.priority > y.priority)
      questionTree.put(key, questions)
    }
    put("questionTree", questionTree)
    put("questionnaire", questionnaire)
    forward()
  }

  override def saveAndRedirect(entity: Questionnaire): View = {
    val questionnaire = entity.asInstanceOf[Questionnaire]
    questionnaire.beginOn = LocalDate.parse(get("questionnaire.beginOn").get)
    questionnaire.updatedAt = Instant.now
    questionnaire.createBy = Securities.user
    if (null == questionnaire.project) {
      questionnaire.project = getProject
    }
    questionnaire.endOn = get("questionnaire.endOn").filter(Strings.isNotBlank(_)).map(LocalDate.parse(_))
    questionnaire.questions.clear()
    questionnaire.questions ++= entityDao.find(classOf[Question], longIds("questionnaire.question"))

    entityDao.saveOrUpdate(questionnaire)
    redirect("search", "info.save.success")
  }

  override def remove(): View = {
    val questionnaireIds = longIds("questionnaire")
    val query1 = OqlBuilder.from(classOf[Questionnaire], "questionnaire")
    query1.where("questionnaire.id in (:questionnaireIds)", questionnaireIds)
    val questionnaires = entityDao.search(query1)
    val query = OqlBuilder.from(classOf[QuestionnaireClazz], "ql")
    query.where("ql.questionnaire in (:questionnaires)", questionnaires)
    val qls = entityDao.search(query)
    if (!qls.isEmpty) { return redirect("search", "删除失败,选择的数据中已有被课程问卷引用"); }

    entityDao.remove(questionnaires)
    return redirect("search", "删除成功")
  }

  def searchQuestion(): View = {
    val questionSeq = get("questionSeq")

    val entityQuery = OqlBuilder.from(classOf[Question], "question")
    entityQuery.where(
      "question.questionType.beginOn <= :now and (question.questionType.endOn is null or question.questionType.endOn >= :now)",
      LocalDate.now)
    if (!get("questionTypeId").isEmpty) {
      val typeId = getLong("questionTypeId").get
      if (typeId != 0L) {
        entityQuery.where("question.questionType.id=:id", typeId)
      }
    }
    if (questionSeq.isEmpty) {
      entityQuery.where("question.id not in (:questionIds)", questionSeq)
    }
    put("questionSeqIds", questionSeq)
    val questions = entityDao.search(entityQuery)
    put("questions", questions)
    forward()
  }

}
