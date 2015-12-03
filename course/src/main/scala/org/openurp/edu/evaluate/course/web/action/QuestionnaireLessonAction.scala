package org.openurp.edu.evaluate.course.web.action

import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.evaluation.lesson.model.QuestionnaireLesson
import org.openurp.edu.base.code.model.ExamMode
import org.openurp.base.model.Department
import org.openurp.edu.base.code.model.ExamMode
import org.openurp.edu.base.code.model.CourseType
import org.openurp.edu.evaluation.model.Questionnaire
import org.openurp.base.model.Semester
import org.beangle.data.dao.OqlBuilder
import org.openurp.base.model.Semester
import java.sql.Date
import org.openurp.base.model.Semester
import org.openurp.base.model.Semester
import org.openurp.base.model.Semester
import org.openurp.edu.evaluate.course.service.StdEvaluateSwitchService
import org.openurp.base.model.Semester
import org.openurp.edu.base.model.Project
import org.openurp.edu.lesson.model.Lesson
import org.openurp.edu.lesson.model.Lesson
import org.beangle.webmvc.entity.helper.QueryHelper
import org.beangle.commons.collection.Order
import org.beangle.data.dao.QueryBuilder
import org.openurp.edu.lesson.model.Lesson
import org.beangle.webmvc.api.view.View

class QuestionnaireLessonAction extends RestfulAction[QuestionnaireLesson] {

  var evaluateSwitchService: StdEvaluateSwitchService = _

  override def indexSetting(): Unit = {
    val departments = entityDao.getAll(classOf[Department])
    put("departments", departments)
    val examModes = entityDao.getAll(classOf[ExamMode])
    put("examModes", examModes);
    val courseTypes = entityDao.getAll(classOf[CourseType])
    put("courseTypes", courseTypes);
    put("questionnaires", entityDao.getAll(classOf[Questionnaire]));
  }

  override def search(): String = {
    val questionnaireId = getLong("questionnaire.id").getOrElse(0)
    val semesterId = 20141
    val projectId = 1
    //    if (semesterId != null) 
    val semester = entityDao.get(classOf[Semester], semesterId)
    val project = entityDao.get(classOf[Project], projectId)
    // 检查时间
    val evaluateSwitch = evaluateSwitchService.getEvaluateSwitch(semester, project);
    if (null != evaluateSwitch) {
      //        && evaluateSwitch.checkOpen(new java.util.Date())) {
      put("isEvaluateSwitch", true);
    }
    val query = OqlBuilder.from(classOf[Questionnaire], "questionnaire");
    query.where("questionnaire.state=true").where(
      "questionnaire.beginOn <= :now and (questionnaire.endOn is null or questionnaire.endOn >= :now)",
      new java.util.Date());
    put("questionnaires", entityDao.search(query));
    // 判断(问卷是否存在)
    //    if (questionnaireId != null && questionnaireId == 0) {
    questionnaireId match {
      case 0 =>//无问卷教学任务list
        put("lessons", entityDao.search(getQueryBuilderByLesson()))
        forward("lessonList");
      case -1 =>//有问卷
        put("questionnaireLessons", entityDao.search(getQueryBuilder()))
        forward("list");
    }
  }

  protected override def getQueryBuilder(): OqlBuilder[QuestionnaireLesson] = {
    val query = OqlBuilder.from(classOf[QuestionnaireLesson], "questionnaireLesson");
    query.where(QueryHelper.extractConditions(classOf[Lesson], "lesson", null))
//    val semesterId = getInt("lesson.semester.id");
    val semesterId = 20141
//    if (semesterId == null) {
//      query.where("lesson.semester.id = :semester", semesterId);
      query.where("lesson.semester.id = :semester", 20141);
//    }
    // 隐性条件(问卷类别,起始周期,上课人数)
    val questionnaireId = longId("questionnaire")
    val startWeekFrom = getInt("startWeekFrom");
    val startWeekEnd = getInt("startWeekEnd");
    val stdCountFrom = getInt("stdCountFrom");
    val stdCountEnd = getInt("stdCountEnd");
//    val hasTeacher = getBoolean("hasTeacher").get;

    if ( questionnaireId != -1) {
      query.where("questionnaireLesson.questionnaire.id =:questionnaireId", questionnaireId);
      
    }
    //    if (startWeekFrom != null) {
    //      query.where("lesson.schedule.startWeek >=:startWeekFrom", startWeekFrom);
    //    }
    //    if (startWeekEnd != null) {
    //      query.where("lesson.schedule.startWeek <=:startWeekEnd", startWeekEnd);
    //    }
//    stdCountFrom foreach { s =>
//      query.where("lesson.teachclass.stdCount >=:stdCountFrom", s);
//    }
//    stdCountEnd foreach { e =>
//      query.where("lesson.teachclass.stdCount <=:stdCountEnd", e);
//    }
//    if (hasTeacher != null) {
//      if (hasTeacher) {
//        query.where("size(lesson.teachers) != 0");
//      } else {
//        query.where("size(lesson.teachers) = 0");
//      }
//    }
//    query.where("lesson.teachDepart.id in (:teachDeparts)", getInt("teachDeparts"))
    query.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit);

    query
  }

  /**
   * 查询语句(教学任务,缺失问卷) 无问卷教学任务list
   *
   * @return
   */
  protected def getQueryBuilderByLesson(): QueryBuilder[Lesson] = {
    val query = OqlBuilder.from(classOf[Lesson], "lesson").where("lesson.project.id=:projectId",
      getInt("project").getOrElse(1))
    populateConditions(query);
//    val semesterId = getInt("lesson.semester.id");
//    val semesterId =20141
//    if (semesterId == null) {
//      query.where("lesson.semester = :semester", get("semester"));
      query.where("lesson.semester.id = :semester", 20141);
//    }
    // 隐性条件(问卷类别,起始周期,上课人数)
    val startWeekFrom = getInt("startWeekFrom");
    val startWeekEnd = getInt("startWeekEnd");
    val stdCountFrom = getInt("stdCountFrom");
    val stdCountEnd = getInt("stdCountEnd");
    val hasTeacher = 1
//      getBoolean("hasTeacher");
    //
    //    if (startWeekFrom != null) {
    //      query.where("lesson.schedule.startWeek >=:startWeekFrom", startWeekFrom);
    //    }
    //    if (startWeekEnd != null) {
    //      query.where("lesson.schedule.startWeek <=:startWeekEnd", startWeekEnd);
    //    }
//    stdCountFrom foreach { s =>
//      query.where("lesson.teachclass.stdCount >=:stdCountFrom", s);
//    }
//    stdCountEnd foreach { e =>
//      query.where("lesson.teachclass.stdCount <=:stdCountEnd", e);
//    }
//    if (hasTeacher!=null) {
//      if (hasTeacher==1) {
//        query.where("size(lesson.teachers) != 0");
//      } else {
//        query.where("size(lesson.teachers) = 0");
//      }
//    }
    // 排除(已有问卷)
    query
      .where("not exists(from " + classOf[QuestionnaireLesson].getName + " questionnaireLesson"
        + " where questionnaireLesson.lesson = lesson)");
    query.orderBy(get(Order.OrderStr).orNull);
    query
  }
  /**
   * 设置(评教类型,定制问卷,条件-问卷缺失)
   *
   * @return
   */
  def saveQuestionnaireLesson() :View={
    val isAll = get("isAll").get;
    var lessons: Seq[Lesson] = Seq();
    // 获取(更新对象)
    if ("all".equals(isAll)) {
      val qurey = getQueryBuilderByLesson() 
      lessons = entityDao.search(getQueryBuilderByLesson())
    } else {
      val ids = longIds("lesson");
      lessons = entityDao.find(classOf[Lesson], ids);
    }
      val questionnaireId = longId("questionnaire");
      if (questionnaireId != 0) {
        val isEvaluate = getBoolean("isEvaluate").get;
        val questionnaire = entityDao.get(classOf[Questionnaire], questionnaireId);
        //        for (Lesson lesson : lessons) {
        lessons foreach { lesson =>
          val questionnaireLesson = new QuestionnaireLesson();
          questionnaireLesson.questionnaire = questionnaire
          questionnaireLesson.lesson = lesson
          questionnaireLesson.evaluateByTeacher = isEvaluate
          entityDao.saveOrUpdate(questionnaireLesson);
        }
      }
      redirect("search", "info.action.success");

  }

  /**
   * 设置(评教类型,定制问卷,条件-问卷存在)
   *
   * @return
   */
  def updateQuestionnaireLesson():View= {
    val isAll = get("isAll").get;
    var questionnaireLessons: Seq[QuestionnaireLesson] = Seq();
    // 获取(更新对象)
    if ("all".equals(isAll)) {
      questionnaireLessons = entityDao.getAll(classOf[QuestionnaireLesson]);
    } else {
      val ids = longIds(shortName);
      questionnaireLessons = entityDao.find(classOf[QuestionnaireLesson], ids);
    }

//    try {
      val questionnaireId = this.getLong("questionnaire.id").getOrElse(0L)
      // 判断(是否删除)
      if (questionnaireId == 0L) {
        entityDao.remove(questionnaireLessons);
      }
      else{
        val isEvaluate = getBoolean("isEvaluate").get
        val questionnaire = entityDao.get(classOf[Questionnaire], questionnaireId);
        //        for (QuestionnaireLesson questionnaireLesson : questionnaireLessons) {
        questionnaireLessons foreach { questionnaireLesson =>
          questionnaireLesson.questionnaire = questionnaire
          questionnaireLesson.evaluateByTeacher = isEvaluate

        }
        entityDao.saveOrUpdate(questionnaireLessons);
      } 
      redirect("search", "--------info.action.success");
//    } catch {
//      case e: Exception =>
//        redirect("search", "----..---info.action.failure");
//    }
  }

  def setEvaluateSwitchService(evaluateSwitchService: StdEvaluateSwitchService) {
    this.evaluateSwitchService = evaluateSwitchService;
  }
}
