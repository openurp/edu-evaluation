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

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.model.Department
import org.openurp.code.edu.model.EducationLevel
import org.openurp.edu.evaluation.clazz.stat.model.ClazzEvalStat
import org.openurp.edu.evaluation.model.Questionnaire

class EvaluateResultStatAction extends RestfulAction[ClazzEvalStat] {

  override protected def indexSetting(): Unit = {
    put("educations", entityDao.getAll(classOf[EducationLevel]))
    put("departments", entityDao.search(OqlBuilder.from(classOf[Department], "dep").where("dep.teaching =:tea", true)))
    put("questionnaires", entityDao.search(OqlBuilder.from(classOf[Questionnaire], "qn")))
  }

}
