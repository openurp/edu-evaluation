[#ftl]
[@b.head/]
[@b.toolbar title='院系-问卷评教结果统计' id='departmentEvaluateBar' ]
    //var detailMenu = bar.addMenu("院系课程评教比较","departmentChoiceConfig()");
    //    detailMenu.addItem("学院历史评教", "depHistoryStat()", "info.png");
    bar.addItem("学校问卷评教总分统计","schoolStat()");
    bar.addItem("删除全校问卷统计结果","removeAll()");
    //bar.addItem("学校教师评教分项汇总","collegeGroupItemInfo()");
    bar.addItem("初始化/重新统计","statisticResult()");
[/@]
<div class="search-container">
  <div class="search-panel">
        [@b.form action="!search?orderBy=departEvalStat.totalScore desc" name="departEvaluateStatIndexForm" title="ui.searchForm" target="contentDiv" theme="search"]
            [@urp_base.semester  name="semester.id" label="学年学期" value=currentSemester/]
            [@b.select name="departEvalStat.department.id" label="开课院系" items=departments empty="..."/]
            [@b.select name="departEvalStat.questionnaire.id" label="所用问卷" items=[] ]
                [#list questionnaires as q]
                    <option value="${q.id}">${q.description}</option>
                [/#list]
            [/@]
        [/@]
  </div>
  <div class="search-list">
            [@b.div id="contentDiv" href="!search?orderBy=departEvalStat.totalScore desc&semester.id="+currentSemester.id/]
  </div>
</div>
<script type="text/javaScript">
    var form = document.departEvaluateStatIndexForm;

    function departmentChoiceConfig(){
        bg.form.addInput(form,"semester.id",document.departEvaluateStatIndexForm['semester.id'].value);
      [#--  bg.form.submit(form, "${b.url('!departmentChoiceConfig')}");--]
    }
    function depHistoryStat(){
       [#-- bg.form.submit(form, "${b.url('!depHistoryStat')}");--]
    }
    function historyCollegeStat(){
       [#-- bg.form.submit(form, "${b.url('!historyCollegeStat')}");--]
    }

    function changeSemester(){
        bg.form.addInput(form, "semester.id", $("input[name='semester.id']").val());
        bg.form.submit(form);
    }
    function collegeGroupItemInfo(){
        bg.form.addInput(form,"semester.id",document.departEvaluateStatIndexForm['semester.id'].value);
        [#-- bg.form.submit(form, "${b.url('!collegeGroupItemInfo')}");--]
    }
   function statisticResult(){
        form.target="_blank";
        bg.form.submit(form, "${b.url('!statHome')}","main");
        form.target="contentDiv";
    }
   function schoolStat(){
        bg.form.addInput(form, "semester.id",document.departEvaluateStatIndexForm['semester.id'].value);
        bg.form.submit(form,"${b.url('!schoolStat')}");
    }
   function removeAll(){
        bg.form.addInput(form, "semester.id",document.departEvaluateStatIndexForm['semester.id'].value);
        bg.form.submit(form,"${b.url('!removeAll')}");
    }

</script>
[@b.foot/]
