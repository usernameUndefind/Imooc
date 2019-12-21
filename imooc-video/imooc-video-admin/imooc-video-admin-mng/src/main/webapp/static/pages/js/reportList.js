var forbidVideo = function (id) {

    var flag = window.confirm("是否确认禁用该视频？？？");
    if(!flag) {
        return;
    }
    $.ajax({
        url: $('#hdnContextPath').val() + '/report/updateVideoStatus.action?videoId=' + id,
        type: "POST",
        success: function (data) {
            if (data.status == 200 && data.msg == 'OK') {
                alert('禁用成功~~~');
                // 刷新页面
                var grid = $("#usersReportsList");
                grid.jqGrid().trigger("reloadGrid");
            }
        }
    });
}

var ReportList = function () {

    // 构建bgm列表对象
    var ReportListFun = function () {
        var contextPath = $("#hdnContextPath").val();
        var apiServer = $("#apiServer").val();
        var jqGrid = $("#usersReportsList");
        jqGrid.jqGrid({
            caption: "举报列表",
            url : contextPath + '/report/list.action',
            datatype : "json",
            styleUI: 'Bootstrap', // 设置jqgrid的全局样式为bootstrap样式
            colNames : [ 'ID', '举报类型', '举报内容', '被举报人', '被举报视频ID', '被举报视频','视频状态','提交用户','举报日期','操作'],
            colModel : [
                {name : 'id',index : 'id',width : 30},
                {name : 'reportType',index : 'reportType',width : 30},
                {name : 'reportContent',index : 'reportContent',width : 30},
                {name : 'dealUserName',index : 'dealUserName',width : 30},
                {name : 'dealVideoId',index : 'dealVideoId',width : 30},
                {name : 'dealVideoPath',index : 'dealVideoPath',width : 30,
                        formatter: function (cellvalue, options, rowObject) {
                            var src = apiServer + cellvalue;
                            var html = '<a href="'+ src +'" target="_blank">点击播放</a>';
                            return html;
                        }
                },
                {name : 'videoStatus',index : 'videoStatus',width : 30,
                        formatter: function (cellvalue, options, rowObject) {
                            return cellvalue == 1 ? "正常" : "禁播";
                        }
                },
                {name : 'submitUserName',index : 'submitUserName',width : 30},
                {name : 'createTime',index : 'createTime',width : 30,
                        formatter: function (cellvalue, options, rowObject) {
                            var createTime = Common.formatTime(cellvalue, 'yyyy-MM-dd HH:mm:ss');
                            return createTime;
                        }
                },
                {name : '',index : '',width : 30,
                        formatter: function (cellvalue, options, rowObject) {
                            var button = '<button class="btn btn-outline blue-chambray" id="" onclick=forbidVideo("' + rowObject.dealVideoId + '") style="padding: 1px 3px 1px 3px;">禁止播放</button>';
                            // var button = '<button class="btn btn-outline blue-chambray" id=""  style="padding: 1px 3px 1px 3px;">禁止播放</button>';
                            return button;
                        }
                },
            ],
            viewrecords: true,              // 定义是否要显示总记录数
            rowNum : 10,                    // 在grid上显示记录条数，这个参数是要传给后台
            pager : '#usersReportsListPager',        // 分页控件id
            rownumWidth: 36,                // 如果rownumbers为true时，则可以设置行号的高度
            height: 400,                    // 表格高度，可以是数字，像素值或百分比
            rownumbers: true,               // 如果为true则会在表格左边新增一列，显示行顺序号，从1开始递增
            autowidth: true,                // 如果为true时，则当表格在首次被创建时会根据父元素比例重新调整表格
            subGrid: false                  // 是否启用子表格
        }).navGrid('#usersReportsListPager', {
            edit: false,
            add: false,
            del: false,
            search: false
        });
    }

    return {
        init: function () {
            ReportListFun();
        }
    }
} ();

jQuery(document).ready(function() {
    ReportList.init();
});