if(typeof(manageAPI) == "undefined")
{
    manageAPI = {};
}

var manageAPIBaseURL= "/api/scheduler-service/manage";

(function ()
{
    manageAPI.getOrDelete = function(url, method, onSuccess, onError) {
        $.ajax({
            type: method,
            url: url,
            success: function (data, textStatus) {
                console.log(textStatus);
                onSuccess(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                onError();
            },
            async: true
        });
    };

    manageAPI.postOrPut = function(url, method, data, onSuccess, onError) {
        $.ajax({
            type: method,
            url: url,
            contentType: "application/json",
            data: data,
            success: function (data, textStatus) {
                console.log(textStatus);
                onSuccess(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                onError();
            },
            async: true
        });
    };

    manageAPI.createJob = function (jobMeta, onSuccess, onError) {
        var url = manageAPIBaseURL + "/createJob";
        var data = JSON.stringify(jobMeta);
        manageAPI.postOrPut(url, "POST", data, onSuccess, onError);
    };

    manageAPI.scheduleJob = function (jobName, jobGroup, onSuccess, onError) {
        var url = manageAPIBaseURL + "/scheduleJob";
        url += "?jobName=" + encodeURIComponent(jobName);
        url += "&jobGroup=" + encodeURIComponent(jobGroup);
        manageAPI.getOrDelete(url, "GET", onSuccess, onError);
    };

    manageAPI.unscheduleJob = function (jobName, jobGroup, onSuccess, onError) {
        var url = manageAPIBaseURL + "/unscheduleJob";
        url += "?jobName=" + encodeURIComponent(jobName);
        url += "&jobGroup=" + encodeURIComponent(jobGroup);
        manageAPI.getOrDelete(url, "DELETE", onSuccess, onError);
    };

    manageAPI.executeJob = function (jobName, jobGroup, onSuccess, onError) {
        var url = manageAPIBaseURL + "/executeJob";
        url += "?jobName=" + encodeURIComponent(jobName);
        url += "&jobGroup=" + encodeURIComponent(jobGroup);
        manageAPI.getOrDelete(url, "GET", onSuccess, onError);
    };

    manageAPI.runningJobs = function (onSuccess, onError) {
        var url = manageAPIBaseURL + "/runningJobs";
        manageAPI.getOrDelete(url, "GET", onSuccess, onError);
    };

    manageAPI.getAllJobs = function(onSuccess, onError) {
        var url = manageAPIBaseURL + "/getAllJobs";
        manageAPI.getOrDelete(url, "GET", onSuccess, onError);
    };

    manageAPI.logs = function(onSuccess, onError) {
        var url = manageAPIBaseURL + "/logs";
        manageAPI.getOrDelete(url, "GET", onSuccess, onError);
    };
}());