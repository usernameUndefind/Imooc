var deleteBgm = function (id) {

    var flag = window.confirm("是否确认删除？？？");
    if(!flag) {
        return;
    }
    $.ajax({
        url: $('#hdnContextPath').val() + '/video/delBgm.action?id=' + id,
        type: "POST",
        success: function (data) {
            if (data.status == 200 && data.msg == 'OK') {
                alert('删除成功~~~');
                // 刷新页面
                var grid = $("#bgmList");
                grid.jqGrid().trigger("reloadGrid");
            }
        }
    });
}

var BgmList = function () {

    // 构建bgm列表对象
    var handleBgmList = function () {
        var contextPath = $("#hdnContextPath").val();
        var bgmServer = $("#bgmServer").val();
        jQuery("#bgmList").jqGrid({
            caption: "所有bgm列表",
            url : contextPath + '/video/bgmList.action',
            datatype : "json",
            styleUI: 'Bootstrap', // 设置jqgrid的全局样式为bootstrap样式
            colNames : [ 'ID', '歌曲名称', '作者', '播放', '操作'],
            colModel : [
                {name : 'id',index : 'id',width : 30},
                {name : 'name',index : 'name',width : 30},
                {name : 'author',index : 'author',width : 20},
                {name : 'path',index : 'path',width : 50,
                    formatter: function(cellvalue) {
                        var src = bgmServer + cellvalue;
                        var html = "<a href='"+ src +"' target='_blank'>点我播放</a>";
                        return html;
                    }
                },
                {name : '',index : '',width : 50,
                    formatter: function(cellvaluebgmList,option, rowObject) {
                        var html = '<button class="btn btn-outline blue-chambray" id="" onclick=deleteBgm("' + rowObject.id + '")  style="padding: 1px 3px 1px 3px;">删除</button>';
                        return html;
                    }
                },
            ],
            viewrecords: true,              // 定义是否要显示总记录数
            rowNum : 10,                    // 在grid上显示记录条数，这个参数是要传给后台
            pager : '#bgmListPager',        // 分页控件id
            rownumWidth: 36,                // 如果rownumbers为true时，则可以设置行号的高度
            height: 400,                    // 表格高度，可以是数字，像素值或百分比
            rownumbers: true,               // 如果为true则会在表格左边新增一列，显示行顺序号，从1开始递增
            autowidth: true,                // 如果为true时，则当表格在首次被创建时会根据父元素比例重新调整表格
            subGrid: false                  // 是否启用子表格
        }).navGrid('#bgmListPager', {
            edit: false,
            add: false,
            del: false,
            search: false
        });
    }

    return {
        init: function () {
            handleBgmList();
        }
    }
} ();

jQuery(document).ready(function() {
   BgmList.init();
});