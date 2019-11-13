if(typeof(jobsHTMLAPI) == "undefined")
{
    jobsHTMLAPI = {};
}

(function ()
{
    jobsHTMLAPI.errmsg = function(msg) {
        $('.layui-tab-bar').hide();
        $('.layui-table').hide();
        $('#errmsg').html(msg).addClass("error").show();
    };

    jobsHTMLAPI.getAllJobs = function() {
        function onSuccess(data) {
            jobsHTMLAPI.addJobListTable(data.result);
        }
        function onError() {
            jobsHTMLAPI.errmsg('查询系统任务失败');
        }
        manageAPI.getAllJobs(onSuccess, onError);
    };

    jobsHTMLAPI.createJob = function(formData) {
        var jobMeta = {};
        jobMeta.groupName = formData.jobGroup;
        jobMeta.jobName = formData.jobName;
        jobMeta.properties = {};
        jobMeta.properties.x_cron = formData.jobSchedule;
        jobMeta.properties.x_description = formData.jobDescription;
        function onSuccess(data) {
            if(data) {
                jobsHTMLAPI.errmsg('创建成功');
            } else {
                jobsHTMLAPI.errmsg('创建失败');
            }
        }
        function onError() {
            jobsHTMLAPI.errmsg('创建失败');
        }
        manageAPI.createJob(jobMeta, onSuccess, onError);
    };

    jobsHTMLAPI.runningJobs = function() {
        function onSuccess(data) {
            jobsHTMLAPI.addRunningJobsTable(data);
        }
        function onError() {
            jobsHTMLAPI.errmsg('查询执行任务失败');
        }
        manageAPI.runningJobs(onSuccess, onError);
    };

    jobsHTMLAPI.scheduleJob = function(jobName, jobGroup) {
        function onSuccess(data) {
            if(data.success) {
              jobsHTMLAPI.getAllJobs();
            } else {
              jobsHTMLAPI.errmsg('启动任务失败: ' + data.errorMessage);
            }
        }
        function onError() {
            jobsHTMLAPI.errmsg('查询系统任务失败');
        }
        manageAPI.scheduleJob(jobName, jobGroup, onSuccess, onError);
    };

    jobsHTMLAPI.unscheduleJob = function(jobName, jobGroup) {
        function onSuccess(data) {
            jobsHTMLAPI.getAllJobs();
        }
        function onError() {
            jobsHTMLAPI.errmsg('查询系统任务失败');
        }
        manageAPI.unscheduleJob(jobName, jobGroup, onSuccess, onError);
    };

    jobsHTMLAPI.executeJob = function(jobName, jobGroup) {
        function onSuccess(data) {
            jobsHTMLAPI.getAllJobs();
        }
        function onError() {
            jobsHTMLAPI.errmsg('查询系统任务失败');
        }
        manageAPI.executeJob(jobName, jobGroup, onSuccess, onError);
    };

    jobsHTMLAPI.addJobListTable = function(datas) {
        $('.layui-tab-bar').hide();
        $('.layui-table').show();
        $('#errmsg').hide();

        var html = '';
        if(datas.length<=0){
            $("#jobs").html("<tr><td colspan=\'5\'>未查询到相关数据</td></tr>");
        }else{
            $.each(datas, function (i, job) {
                var styleStop = 'layui-btn-disabled" disabled="true"';
                var styleStart = 'layui-btn-normal"';
                if (job.properties.x_scheduled == 'true') {
                    styleStart = 'layui-btn-disabled" disabled="true"';
                    styleStop = 'layui-btn-danger" disable';
                }
                html += '<tr id="jobs_id_"' + i + ">";
                html += '<td><input type="button" class=" layui-btn layui-anim layui-btn-ms ' + styleStop + ' data-anim="layui-anim-scaleSpring" value="停止" onclick="jobsHTMLAPI.unscheduleJob(\'' + job.jobName + '\',\'' + job.groupName + '\')"/>'
                    + '<input type="button" class="layui-btn layui-anim layui-btn-ms ' + styleStart + ' data-anim="layui-anim-scaleSpring" value="启动" onclick="jobsHTMLAPI.scheduleJob(\'' + job.jobName + '\',\'' + job.groupName + '\')"/>'
                    + '<input type="button" class="layui-btn layui-anim layui-btn-ms layui-btn-green" data-anim="layui-anim-scaleSpring" value="立即执行" onclick="jobsHTMLAPI.executeJob(\'' + job.jobName + '\',\'' + job.groupName + '\')"/>'
                    + '</td>';
                html += '<td>' + job.groupName + '</td>';
                html += '<td>' + job.jobName + '</td>';
                html += '<td>' + job.properties.x_cron + '</td>';
                html += '<td>' + job.properties.x_description + '</td>';
                html += '</tr>';
            });
            $("#jobs").html(html);
            jobsHTMLAPI.click_anim()
        }

    };

    //绑定鼠标点击按钮动画
    jobsHTMLAPI.click_anim = function(){
        layui.use('jquery', function () {
            var $ = layui.$;
            $('.layui-anim').on('click', function () {
                var othis = $(this), anim = othis.data('anim');
                //停止循环
                if (othis.hasClass('layui-anim-loop')) {
                    return othis.removeClass(anim);
                }
                othis.removeClass(anim);
                setTimeout(function () {
                    othis.addClass(anim);
                });
                //恢复渐隐
                if (anim === 'layui-anim-fadeout') {
                    setTimeout(function () {
                        othis.removeClass(anim);
                    }, 500);
                }
            });
        });
    };

    jobsHTMLAPI.addRunningJobsTable = function(datas) {
        $('.layui-tab-bar').hide();
        $('.layui-table').show();
        $('#errmsg').hide();
        var runningJobhtml = '';
        if(datas.length<=0){
            runningJobhtml="<tr><td colspan='5'>未查询到相关数据</td></tr>"
        }else{
            $.each(datas, function (i, job) {
                runningJobhtml += '<tr id="running_jobs_id_"' + i + ">";
                runningJobhtml += '<td>' + job.executionID + '</td>';
                runningJobhtml += '<td>' + job.jobMeta.groupName + '</td>';
                runningJobhtml += '<td>' + job.jobMeta.jobName + '</td>';
                runningJobhtml += '<td>' + job.jobStatus + '</td>';
                runningJobhtml += '<td></td>';
                runningJobhtml += '</tr>';
            });
        }
        $("#runingJobs-tbody").html(runningJobhtml);
    };

}());