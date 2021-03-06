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
package org.openurp.edu.evaluation.clazz.web.action

import java.time.LocalDate

import org.beangle.commons.collection.{Collections, Order}
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.model.Semester
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.evaluation.clazz.result.model.{EvaluateResult, QuestionResult}
import org.openurp.edu.evaluation.clazz.stat.model.CourseEvalStat
import org.openurp.edu.evaluation.model.Option

class CourseEvalSearchAction extends ProjectRestfulAction[CourseEvalStat] {
  override def index(): View = {
    put("currentSemester", getCurrentSemester)
    forward()
  }

  override def search(): View = {
    // 页面条件
    val semesterId = getInt("semester.id").get
    val semester = entityDao.get(classOf[Semester], semesterId)
    val courseEvalStat = OqlBuilder.from(classOf[CourseEvalStat], "courseEvalStat")
    populateConditions(courseEvalStat)
    courseEvalStat.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
    courseEvalStat.where("courseEvalStat.clazz.semester=:semester", semester)
    put("courseEvalStats", entityDao.search(courseEvalStat))
    forward()
  }

  @mapping(value = "{id}")
  override def info(id: String): View = {
    val questionnaireStat = entityDao.get(classOf[CourseEvalStat], getLong("courseEvalStat.id").get)
    put("questionnaireStat", questionnaireStat)
    // zongrenci fix
    val query = OqlBuilder.from[Array[Any]](classOf[EvaluateResult].getName, "result")
    query.where("result.teacher =:tea", questionnaireStat.teacher)
    query.where("result.clazz.course=:course", questionnaireStat.course)
    query.select("case when result.statType =1 then count(result.id) end,count(result.id)")
    query.groupBy("result.statType")
    entityDao.search(query) foreach { a =>
      put("number1", a(0))
      put("number2", a(1))
    }
    val list = Collections.newBuffer[Option]
    val questions = questionnaireStat.questionnaire.questions
    questions foreach { question =>
      val options = question.optionGroup.options
      options foreach { option =>
        var tt = 0
        list foreach { oldOption =>
          if (oldOption.id == option.id) {
            tt += 1
          }
        }
        if (tt == 0) {
          list += option
        }
      }
    }
    put("options", list)
    val querys = OqlBuilder.from[Long](classOf[Clazz].getName, "clazz")
    querys.join("clazz.teachers", "teacher")
    querys.where("teacher=:teach", questionnaireStat.teacher)
    querys.where("clazz.course=:c", questionnaireStat.course)
    querys.join("clazz.enrollment.courseTakers", "courseTaker")
    querys.select("count(courseTaker.id)")
    val numbers = entityDao.search(querys)(0)
    put("numbers", entityDao.search(querys)(0))
    val que = OqlBuilder.from(classOf[QuestionResult], "questionR")
    que.where("questionR.result.teacher=:teaId", questionnaireStat.teacher)
    que.where("questionR.result.clazz.course=:less", questionnaireStat.course)
    que.select("questionR.question.id,questionR.option.id,count(*)")
    que.groupBy("questionR.question.id,questionR.option.id")
    put("questionRs", entityDao.search(que))
    val quer = OqlBuilder.from(classOf[QuestionResult], "questionR")
    quer.where("questionR.result.teacher=:teaId", questionnaireStat.teacher)
    quer.where("questionR.result.clazz.course=:less", questionnaireStat.course)
    quer.select("questionR.question.id,questionR.question.content,sum(questionR.score)/count(questionR.id)*100")
    quer.groupBy("questionR.question.id,questionR.question.contents")
    put("questionResults", entityDao.search(quer))
    forward()
  }
}
