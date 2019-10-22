function errmsg(msg) {
    $('.layui-tab-bar').hide();
    $('.layui-table').hide();
    $('#errmsg').html(msg).addClass("error").show();
}

function getSystemJobs() {
    $.ajax({
        type: 'GET',
        url: "/api/anta-scheduler-center-service/manage/getSystemJobs",
        success: function (data, textStatus) {
            addJobListTable(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            errmsg('查询系统任务失败');
            console.log(errorThrown);
        },
        async: true
    });
}

function runningJobs() {
    $.ajax({
        type: 'GET',
        url: "/api/anta-scheduler-center-service/manage/runningJobs",
        success: function (data, textStatus) {
            addRunningJobsTable(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(errorThrown);
            errmsg('查询执行任务失败');
        },
        async: true
    });
}

function stopJob(strJob) {
    var job = JSON.parse(strJob);
    $.ajax({
        type: 'POST',
        url: "/api/anta-scheduler-center-service/manage/stopJob",
        data: job,
        success: function (data, textStatus) {
            getSystemJobs();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(errorThrown);
            errmsg('查询系统任务失败');
        },
        async: true
    });
}


function startJob(strJob) {
    var job = JSON.parse(strJob);
    $.ajax({
        type: 'POST',
        url: "/api/anta-scheduler-center-service/manage/startJob",
        data: job,
        success: function (data, textStatus) {
            getSystemJobs();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(errorThrown);
            errmsg('查询系统任务失败');
        },
        async: true
    });
}

function executeJob(strJob) {
    var job = JSON.parse(strJob);
    $.ajax({
        type: 'POST',
        url: "/api/anta-scheduler-center-service/manage/triggerJob",
        data: job,
        success: function (data, textStatus) {
            getSystemJobs();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(errorThrown);
            errmsg('查询系统任务失败');
        },
        async: true
    });
}

function addJobListTable(datas) {
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
            if (job.properties.x_exists == 'true') {
                styleStart = 'layui-btn-disabled" disabled="true"';
                styleStop = 'layui-btn-danger" disable';
            }
            var jobData = JSON.stringify(job).replace(/"/g, '&quot;');

            html += '<tr id="jobs_id_"' + i + ">";
            html += '<td><input type="button" class=" layui-btn layui-anim layui-btn-ms ' + styleStop + ' data-anim="layui-anim-scaleSpring" value="停止" onclick="stopJob(\'' + jobData + '\')"/>'
                + '<input type="button" class="layui-btn layui-anim layui-btn-ms ' + styleStart + ' data-anim="layui-anim-scaleSpring" value="启动" onclick="startJob(\'' + jobData + '\')"/>'
                + '<input type="button" class="layui-btn layui-anim layui-btn-ms layui-btn-green" data-anim="layui-anim-scaleSpring" value="立即执行" onclick="executeJob(\'' + jobData + '\')"/>'
                + '</td>';
            html += '<td>' + job.groupName + '</td>';
            html += '<td>' + job.jobName + '</td>';
            html += '<td>' + job.properties.x_cron + '</td>';
            html += '<td>' + job.properties.x_description + '</td>';
            html += '</tr>';
        });
        $("#jobs").html(html);
        click_anim()
    }

}
//绑定鼠标点击按钮动画
function click_anim(){
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
}

function addRunningJobsTable(datas) {
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
}

$('.layui-tab-bar').hide();
layui.use('element', function () {
    var $ = layui.jquery
        , element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块

    element.on('tab(demo)', function (elem) {
        if ($(this).attr('lay-id') == '22') {
            runningJobs();
            $('#refresh').attr('lay-id', '22');
            $('#down_log').show();
        } else {
            getSystemJobs();
            $('#refresh').attr('lay-id', '11');
            $('#down_log').hide();

        }
        location.hash = 'demo=' + $(this).attr('lay-id');
        $('#tab-tittle').html($(this).text())
    });

});
$('#refresh').on('click', function () {
    if ($(this).attr('lay-id') == '22') {
        runningJobs();
    } else {
        getSystemJobs();
    }
})
$('#down_log').on('click', function () {
    window.open(window.location.protocol + "//" + window.location.host + "/api/anta-scheduler-center-service/manage/logs");
})

