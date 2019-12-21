
var List = function () {

    // 构建bgm列表对象
    var usersList = function () {
        var contextPath = $("#hdnContextPath").val();
        var apiServer = $("#apiServer").val();
        var jqGrid = $("#usersList");
        jqGrid.jqGrid({
            caption: "所有用户列表",
            url : contextPath + '/users/list.action',
            datatype : "json",
            mtype: "post",
            styleUI: 'Bootstrap', // 设置jqgrid的全局样式为bootstrap样式
            colNames : [ 'ID', '头像', '用户名', '昵称', '粉丝数', '关注数', '获赞数'],
            colModel : [
                {name : 'id',index : 'id',width : 30},
                {name : 'faceImage',index : 'faceImage',width : 50,
                    formatter: function (cellvalue, option, rowObject) {
                        var src = apiServer + cellvalue;
                        var html = '<img src="'+ src +'" width="120"></img>';
                        return html;
                    }
                },
                {name : 'username',index : 'username',width : 20},
                {name : 'nickname',index : 'nickname',width : 20},
                {name : 'fansCounts',index : 'fansCounts',width : 20},
                {name : 'followCounts',index : 'followCounts',width : 20},
                {name : 'receiveLikeCounts',index : 'receiveLikeCounts',width : 20},
            ],
            viewrecords: true,              // 定义是否要显示总记录数
            rowNum : 10,                    // 在grid上显示记录条数，这个参数是要传给后台
            pager : '#usersListPager',        // 分页控件id
            rownumWidth: 36,                // 如果rownumbers为true时，则可以设置行号的高度
            height: 400,                    // 表格高度，可以是数字，像素值或百分比
            rownumbers: true,               // 如果为true则会在表格左边新增一列，显示行顺序号，从1开始递增
            autowidth: true,                // 如果为true时，则当表格在首次被创建时会根据父元素比例重新调整表格
            subGrid: false                  // 是否启用子表格
        }).navGrid('#usersListPager', {
            edit: false,
            add: false,
            del: false,
            search: false
        });

        // 随着窗口的变化，设置jqgrid的宽度
        $(window).bind('resize', function () {
            var width = $('.usersList_wrapper').width()*0.99;
            jqGrid.setGridWidth(width);
        });

        // 不显示水平滚动条
        jqGrid.closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });

        $("#searchUserListButton").click(function () {
            var searchUserListForm = $("#searchUserListForm");
            jqGrid.jqGrid().setGridParam({
                page: 1,
                url: contextPath + "/users/list.action?" + searchUserListForm.serialize(),
            }).trigger("reloadGrid");
        });
    }

    return {
        init: function () {
            usersList();
        }
    }
} ();



jQuery(document).ready(function() {
    List.init();
});