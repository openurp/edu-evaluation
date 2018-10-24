[#ftl]
[@b.head/]
[@b.grid items=evaluateSearchDepartmentList var="evaluateSearchDepartment" sortable="true"]
   [@b.row]
        [@b.col width="10%" property="lesson.no" title="课程序号"/]
        [@b.col width="10%" property="lesson.course.code" title="课程代码" /]
        [@b.col width="10%" property="lesson.course.name" title="课程名称" width="10%"/]
        [@b.col title="教师姓名"]
            [#list evaluateSearchDepartment.lesson.teachers as teacher]
            ${teacher.user.name}<br>
            [/#list]
        [/@]
        [@b.col property="lesson.teachclass.name" title="教学班" width="30%"/]
        [@b.col width="10%" property="haveFinish" title="已评人次"/]
        [@b.col width="10%" property="countAll" title="总人次"/]
        [@b.col width="10%" property="finishRate" title="完成率"/]
[/@]
[/@]
[@b.foot/]
