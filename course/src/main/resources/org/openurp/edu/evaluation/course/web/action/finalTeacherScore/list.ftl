[#ftl]
[@b.head/]
[@b.form name="courseEvaluteStatSearchForm" action="!search" target="contentDiv"]    <table id="bar" width="100%"></table>
    <input type="hidden" name="semester.id" value="${semesterId!}">
    [@b.grid items=teacherFinalScores var="teacherFinalScore" sortable="true"]    
        [@b.gridbar title="教师个人评教记录"]
        bar.addItem("${b.text('action.info')}", action.info());
        [/@]
        [@b.row]
            [@b.boxcol/]
            [@b.col property="staff.state.department.name" title="教师所属部门"/]
            [@b.col property="staff.code" title="教师工号"/]
            [@b.col property="staff.person.name.formatedName" title="教师姓名"/]
            [@b.col property="score" title="学生评教"]${teacherFinalScore.stdScore}[/@]
            [@b.col property="score" title="督导评教"]${teacherFinalScore.superScore}[/@]
            [@b.col property="score" title="院系评教"]${teacherFinalScore.depScore}[/@]
            [@b.col property="score" title="最后得分"]${teacherFinalScore.final}[/@]
            [@b.col property="rank" title="全校排名"/]
            [@b.col property="departRank" title="院系排名"/]
        [/@]
    [/@]
[/@]
[@b.foot/]