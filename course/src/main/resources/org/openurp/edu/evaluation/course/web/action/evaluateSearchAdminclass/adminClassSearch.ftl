[#ftl]
[@b.head/]
[@b.toolbar title='班级评教详细信息' id='adClassStateInfoBar']
    bar.addClose();
[/@]
[@b.form name="evaluateSearchAdminClassSearchForm" action="!search" target="contentDiv"]
    [@b.grid items=evaluateSearchAdminClassList var="evaluateSearchAdminClass" sortable="false"]
        [@b.row]
            [@b.col property="student.code" title="学号"/]
            [@b.col property="student.person.name.formatedName" title="姓名"]
            [@b.a href="!info?stuIds=${evaluateSearchAdminClass.student.id!}&semester.id=${semester.id!}"]
            ${(evaluateSearchAdminClass.student.person.name.formatedName)?if_exists}
            [/@][/@]
            [@b.col property="haveFinish" title="已评课次"/]
            [@b.col property="countAll" title="总课次"/]
            [@b.col property="" title="完成率"]${(evaluateSearchAdminClass.finishRate)!0}%[/@]
     [/@]
[/@]
[/@]
[@b.foot/]