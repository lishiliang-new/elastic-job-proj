layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload',
    'element', 'form', 'formSelects'], function () {
    var laypage = layui.laypage; // 分页
    var layer = layui.layer; // 弹层
    var table = layui.table; // 表格
    var form = layui.form;
    var upload = layui.upload;
    var $ = layui.$;
    var formSelects = layui.formSelects;
    var laydate = layui.laydate;

    //机构列表
    var listOrgs = [];
    //当前表格查询条件
    var tableSearchParam = {};
    //修改当前行
    var updateRow = {};
    //add接口文件列表
    let addUploadFiles;
    //update接口文件列表
    let updateUploadFiles;

    //关闭提示框
    parent.layer.closeAll();

    /**
     * 表格
     */
    table.render({
        elem: '#job_config_table',
        data: [],
        page: true,
        cols: [[
            {type:'checkbox'},
            {field: 'jobCode', width: 200, title: '任务编码', align:'center'},
            {field: 'jobName', width: 200, title: '任务名称', sort: true, align:'center'}
            , {field: 'businessSource', width: 150, title: '业务来源', sort: true, align:'center'}
            , {field: 'cron', width: 140, title: 'cron表达式', sort: true, align:'center'}
            // , {field: 'jobParameter', width: 300, title: 'job任务相关参数', sort: true, align:'center'}
            , {field: 'description', width: 220, title: '任务描述', sort: true, align:'center'}
            , {field: 'enable', width: 100, title: '启用状态', sort: true, align:'center',
                templet: function(data){
                    if(data.deleteFlag == true){
                        return "<span style='color:  #b2b2b2'>已下线</span>";
                    } else if(data.enable ==true) {
                        return "<span style='color: green'>启用</span>";
                    } else {
                        return "<span style='color: #b2b2b2'>停用</span>";
                    }
                }
            }
            , {field: 'jobCycle', width: 120, title: '运行周期', sort: true, align:'center',
                templet: function(data){
                    //1-秒，2-日，3-周，4-月，5-季度，6-年'
                    if(data.jobCycle == 1) {
                        return "秒";
                    }
                    if(data.jobCycle == 2) {
                        return "日";
                    }
                    if(data.jobCycle == 3) {
                        return "周";
                    }
                    if(data.jobCycle == 4) {
                        return "月";
                    }
                    if(data.jobCycle == 5) {
                        return "季度";
                    }
                    if(data.jobCycle == 6) {
                        return "年";
                    }
                }
            }
            , {field: 'jobPeriod', width: 120, title: '任务运行间隔', align:'center'}
            , {field: 'jobFirstDate', width: 120, title: '首次执行日期', align:'center'}
            , {field: 'jobFirstTime', width: 120, title: '首次执行时间', align:'center'}
            , {field: 'jobCanSubscribe', width: 120, title: '是否可订阅', align:'center',
                templet: function(data){
                    if (data.jobCanSubscribe == 1) {
                        return "可订阅";
                    } else {
                        return "不可订阅";
                    }
                }
            }
            , {field: 'jobOrgNo', width: 120, title: '任务归属权限机构', align:'center'}
            , {field: 'jobDependences', width: 120, title: '任务依赖', align:'center'}
            , {fixed: 'right',  title: '操作', align: 'center', toolbar: '#barBtn', width: 390}
        ]],
    });


    //监听查询按钮
    $(document).on("click", "#queryBtn", function () {
        loadJobConfigData();
    });


    /**
     * 加载表格数据
     */
    function loadJobConfigData() {

        tableSearchParam.jobCode = $("#jobCode").val();//任务编码
        tableSearchParam.jobParameter = $("#jobParameter").val();//job参数
        tableSearchParam.businessSource = $("#businessSource").val(); //业务
        tableSearchParam.jobName = $("#jobName").val(); //描述

        var url = "/job/config/pageInfo"
        if ($("#enable").val() != "") {
            url = url + "?enable=" + $("#enable").val();
        }

        //loading图标
        var load = layer.load(1);
        table.reload("job_config_table", {
            url: url,
            method : 'post',
            page: {
                curr: 1, //重新从第 1 页开始
                //limits: [10], //每页条数的选择项
                limit: 50 //每页显示50条
            },//查询条件
            where: tableSearchParam
            ,done:function () {
                //加载完数据关闭loading图标
                layer.close(load)
            }
        });
    }

    //初始化表格数据
    loadJobConfigData();
    //初始化机构数据
    loadListOrgs();


    // 监听表格操作事件
    table.on('tool(job_config_table_filter)', function (obj) {
        var data = obj.data; // 获得当前行数据
        var layEvent = obj.event; // 获得 lay-event 对应的值
        //删除事件
        if (layEvent === 'delete') {
            deleteOne(data);
        }
        //禁用启用事件
        if (layEvent === 'enable') {
            enableOne(data);
        }
        //触发任务事件
        if (layEvent === 'trigger') {
            triggerOne(data);
        }
        //恢复事件
        if (layEvent === 'restore') {
            restoreOne(data);
        }
        //下载事件
        if (layEvent === 'download') {
            if (data.jobParameter ==null || data.jobParameter == "") {
                parent.layer.msg("下载路径不能为空");
                return;
            }
            var filePath = data.jobParameter.split(";")[0].split("=")[1]
            if (filePath ==null || filePath == "") {
                parent.layer.msg("下载路径不能为空");
                return;
            }
            layer.confirm("确认是否下载", {icon: 3, title: '提示'}, function (index) {
                location.href = "/job/config/download?filePath=" + filePath;
                layer.closeAll();
            });
        }
        //切换事件
        if (layEvent === 'changeEnv') {
            changeEnvOne(data);
        }


        //更新事件
        if (layEvent === 'update') {
            //清空表单
            $("#update-form")[0].reset();
            form.render();
            updateRow = data;
            updateUploadFiles = null;
            layer.open({
                type: 1,
                title: "修改任务配置",
                area: ['560px', '750px'],
                shadeClose: true,
                btnAlign: 'c',
                content: $("#update-main"),
                success: function (layero, index) {
                    //删除状态才可以修改jobCode
                    if (data.deleteFlag == 1) {
                        $("#updateJobCode").removeProp("readOnly");
                    } else {
                        $("#updateJobCode").prop("readonly","readonly");
                    }
                    //赋值
                    $("#updateJobCode").val(data.jobCode);
                    $("#updateJobName").val(data.jobName);
                    $("#updateCron").val(data.cron);
                    //$("#updateJobParameter").val(data.jobParameter);
                    $("#updateDescription").val(data.description);
                    $("#updateBusinessSource").find("option[value=" + data.businessSource+ "]").prop("selected", true);
                    $("#updateJobCycle").find("option[value=" + data.jobCycle+ "]").prop("selected", true);
                    $("#updateJobPeriod").val(data.jobPeriod);
                    $("#updateJobCanSubscribe").find("option[value=" + data.jobCanSubscribe+ "]").prop("selected", true);
                    $("#updateJobDependences").val(data.jobDependences);
                    //时间回显
                    var dateTime = data.jobFirstDate;
                    if (data.jobFirstDate.length == 4) {
                        dateTime += "0101";
                    }
                    if (data.jobFirstDate.length == 6) {
                        dateTime += "01";
                    }
                    $("#updateDateTime").val(dateTime + " " + data.jobFirstTime);

                    //机构回显
                    updateListOrg(data.jobOrgNo == null ? [] : data.jobOrgNo.split(","))

                    showOrHide(data.businessSource, "update")
                    if (data.businessSource == "kettle_job") {
                        var fileName = data.jobParameter.split(";")[0].split("=")[1];
                        $("#updateKtrFile").val(fileName.substring(fileName.lastIndexOf("/") + 1));
                        $("#updateKettleLogLevel").find("option[value=" + data.jobParameter.split(";")[1].split("=")[1]+ "]").prop("selected", true);
                    }
                    layero.find('#scanColsed').on('click', function () {
                        layer.close(index);
                    });
                    form.render();
                }
            });
        }

        //详情
        if (layEvent === 'detail') {
            layer.open({
                type: 1,
                title: "任务配置详情",
                area: ['720px', '750px'],
                shadeClose: true,
                btnAlign: 'c',
                content: $("#detail-main"),
                success: function (layero, index) {
                    layEventDetail(data);
                    form.render();
                    layero.find('#detailColsed').on('click', function () {
                        layer.close(index);
                    });
                }
            });
        }
    });

    /**
     * 获取所有机构
     * @param orgs
     */
    function loadListOrgs() {
        $.ajaxSetup({ //ajax异步转同步
            async: false
        });
        $.post("/job/config/orgList", function (res) {
            if (res.code === 0) {
                listOrgs = res.data;
            }
        });
    }
    /**
     * update机构信息回显
     * @param data
     */
    function updateListOrg(orgs) {

        var selectObjs = [];
        $.each(listOrgs, function (index, val) {
            var selectObj = {
                "id" : val.id,
                "name" : val.name,
                "selected" : (orgs.indexOf(val.id) != -1) ? "selected" : "",
            };
            selectObjs.push(selectObj);
        });
        formSelects.config('updateJobOrgNo', {
            keyName: 'name',
            keyVal: 'id'
        }).data('updateJobOrgNo', 'local', {arr: selectObjs});

    }


    /**
     * detail 填充
     */
    function layEventDetail(data) {
        $("#detailJobCode").html(data.jobCode);
        $("#detailCron").html(data.cron);
        $("#detailJobParameter").html(data.jobParameter);
        $("#detailDescription").html(data.description);
        $("#detailBusinessSource").html(data.businessSource);
        $("#detailShardTotalCount").html(data.shardTotalCount);
        $("#detailShardingItemParameters").html(data.shardingItemParameters);
        $("#detailEnable").html(data.enable ? "启用" : "停用");
        $("#detailCreateTime").html(data.createTime);
        $("#detailUpdateTime").html(data.updateTime);
        $("#detailCreateOper").html(data.createOper);
        $("#detailUpdateOper").html(data.updateOper);

        $.ajaxSetup({ //ajax异步转同步
            async: false
        });
        $.post("/job/config/getJobDetail", data, function (res) {
            if (res.code === 0) {
                $("#detailJobStatus").append('');
                $("#detailInstanceCount").append('');
                $("#detailJobStatus").html(res.data.jobStatus);
                $("#detailInstanceCount").html(res.data.instanceCount);
                $("#detailShardingInfos").html("");

                if (res.data.shardingInfos == null || res.data.shardingInfos.length == 0) {
                    $("#detailShardingInfos").append('');
                    return;
                }
                var shardingInfosHtml = "<table class=\"layui-table\" >" +
                    "<tr >"+
                    "<th lay-data=\"width:100\" style=\"text-align:center\"  >分片项</th>"+
                    "<th lay-data=\"width:100\" style=\"text-align:center\" >进程id</th>"+
                    "<th lay-data=\"width:100,text-align:center\" style=\"text-align:center\" >节点转移</th>"+
                    "<th lay-data=\"width:100, sort:true, text-align:center\" style=\"text-align:center\">服务器ip</th>"+
                    "<th lay-data=\"width:100,text-align:center\" style=\"text-align:center\" >状态</th>"+
                    "</tr>";

                $.each(res.data.shardingInfos, function (index, val) {
                    var failover = val.failover ? "已转移" : "未转移"
                    shardingInfosHtml += ("<tr> <th style=\"text-align:center\">"
                        + val.item + "</th> <th  style=\"text-align:center\"> "
                        + val.instanceId + "</th><th  style=\"text-align:center\"> "
                        + failover + "</th> <th  style=\"text-align:center\"> "
                        + val.serverIp + "</th><th  style=\"text-align:center\">"
                        + val.status.name + "</th> </tr>");
                });
                shardingInfosHtml += "</table>\n";
                $("#detailShardingInfos").append(shardingInfosHtml);

            }
        });

    }

    //添加数据
    $(document).on('click', '#addJobBtn', function () {
        addJobForm();
        //机构下拉列表
        formSelects.config('addJobOrgNo', {
            keyName: 'organizationName',
            keyVal: 'organizationId'
        })
        // .data('addJobOrgNo', 'server', {
        //     url: "/system/selectListOrg"
        // });
        .data('addJobOrgNo', 'local', {arr:listOrgs});
    });

    //菜单添加页面
    function addJobForm() {
        layer.open({
            type: 1,
            title: "任务配置",
            area: ['550px', '640px'],
            shadeClose: true,
            btnAlign: 'c',
            content: $("#add-main")
        });

    }


    function showOrHide(data, source) {
        if(data == "kettle_job"){
            $("#"+source +"KtrFileDiv").show();
            // $("#"+source +"KtrPushFileNameDiv").show();
            $("#"+source +"KettleLogLevelDiv").show();
            $("#"+source +"UploadDiv").show();
            //添加验证
            $("#"+source +"KtrFile")[0].setAttribute("lay-verify","required");
            // $("#"+source +"KtrPushFileName")[0].setAttribute("lay-verify","required");
            $("#"+source +"KettleLogLevel")[0].setAttribute("lay-verify","required");
            form.render();
        } else {
            clearFile(source);
            $("#"+source +"KtrFileDiv").hide();
            // $("#"+source +"KtrPushFileNameDiv").hide();
            $("#"+source +"KettleLogLevelDiv").hide();
            $("#"+source +"UploadDiv").hide();
            $("#"+source +"KtrFile").val("");
            // $("#"+source +"KtrPushFileName").val("");
            // $("#"+source +"KettleLogLevel").val("");
            $("#"+source + "KtrFile").removeAttr("lay-verify");
            // $("#"+source + "KtrPushFileName").removeAttr("lay-verify");
            $("#"+source +"KettleLogLevel").removeAttr("lay-verify");
        }
    }


    //监听addBusinessSourceFilter下拉框选择事件
    form.on('select(addBusinessSourceFilter)', function(data){
        showOrHide(data.value, "add")
    });
    //监听updateBusinessSourceFilter下拉框选择事件
    form.on('select(updateBusinessSourceFilter)', function(data){
        showOrHide(data.value, "update")
    });

    //监听addJobCycleFilter下拉框选择事件
    form.on('select(addJobCycleFilter)', function(data){
        parent.layer.msg("运行周期已变更, 请查看任务运行间隔 首次运行时间 是否匹配")
    });
    //监听updateJobCycleFilter下拉框选择事件
    form.on('select(updateJobCycleFilter)', function(data){
        parent.layer.msg("运行周期已变更, 请查看任务运行间隔 首次运行时间 是否匹配")
    });


    //重置添加文件按钮
    $(document).on('click', '#addResetFile', function () {
        clearFile("add");
        $("#addKtrFile").val("");
    });
    upload.render({
        async: false,
        elem: '#addImportFile', //绑定元素
        url: '/job/config/add',
        size: 5120,//限制5M
        exts: 'ktr',   //文件后缀
        auto: false ,       /*true为选中直接提交，false为不提交根据bindAction属性上的id提交*/
        bindAction: '#formSave',
        accept: 'file',

        choose:function(obj) {
            clearFile("add");    //将所有文件先删除
            addUploadFiles = obj.pushFile();
            obj.preview(function (index, file, result) {
                $('#addImportFile').attr('src', result);
                obj.pushFile(); //再把当前文件重新加入队列
                if (file.name != "") {
                    $("#addKtrFile").val(file.name);
                }
            });
        },
        before: function(obj) {
            this.data.jobCode = $.trim($("#addJobCode").val()); //任务编码
            this.data.jobName = $.trim($("#addJobName").val()); //任务编码
            this.data.businessSource = $("#addBusinessSource").val();//业务来源
            this.data.cron = $.trim($("#addCron").val());//cron表达式
            this.data.enable = $("#addEnable").val();//是否启用
            this.data.description = $.trim($("#addDescription").val());
            this.data.jobParameter = "kettleLogLevel=" + $("#addKettleLogLevel").val();

            this.data.jobCycle = $("#addJobCycle").val();
            this.data.jobPeriod = $("#addJobPeriod").val();
            this.data.jobCanSubscribe = $("#addJobCanSubscribe").val();
            this.data.jobDependences = $("#addJobDependences").val();
            this.data.jobOrgNo = formSelects.value('addJobOrgNo', 'valStr')

            var addDateTime = $.trim($("#addDateTime").val());
            if (addDateTime != null && addDateTime != "") {
                this.data.jobFirstDate = addDateTime.split(" ")[0]
                this.data.jobFirstTime = addDateTime.split(" ")[1]
            }

            //this.data.extraParameter = $("#addKtrPushFileName").val());
            //阻止事件的冒泡 先校验在上传
            //window.event.stopPropagation();
            //layui.event("formSave").stopPropagation();

            //文件上传 校验参数在上传
            return formVerify("add-main");
        },
        done: function(res, index, upload) {//上传完毕回调
            $("#addKtrFile").val();
            success(res, "add");
        }
        , error: function() {//请求异常回调
            // layer.closeAll('loading');
            // layer.msg('异常，请稍后重试！');
        },
    });


    //重置修改文件按钮
    $(document).on('click', '#updateResetFile', function () {
        clearFile("update");
        if (updateRow.businessSource == "kettle_job") {
            var fileName = updateRow.jobParameter.split(";")[0].split("=")[1];
            $("#updateKtrFile").val(fileName.substring(fileName.lastIndexOf("/") + 1));
            clearFile("update")
        } else {
            $("#updateKtrFile").val("");
        }
    });

    upload.render({
        async: false,
        elem: '#updateImportFile', //绑定元素
        url: '/job/config/update',
        size: 5120,//限制5M
        exts: 'ktr',   //文件后缀
        auto: false ,       /*true为选中直接提交，false为不提交根据bindAction属性上的id提交*/
        bindAction: '#formUpdate',
        accept: 'file',
        choose:function(obj) {
            clearFile("update");    //将所有文件先删除
            updateUploadFiles = obj.pushFile();
            obj.preview(function (index, file, result) {
                $('#updateImportFile').attr('src', result);
                obj.pushFile(); //再把当前文件重新加入队列
                if (file.name != "") {
                    $("#updateKtrFile").val(file.name);
                }
            });
        },

        before: function(obj) {

            updateRow.jobCode = $.trim($("#updateJobCode").val());
            updateRow.jobName = $.trim($("#updateJobName").val());
            updateRow.cron = $.trim($("#updateCron").val());
            updateRow.description = $.trim($("#updateDescription").val());
            updateRow.jobCycle = $.trim($("#updateJobCycle").val());
            updateRow.jobPeriod = $.trim($("#updateJobPeriod").val());
            updateRow.jobCanSubscribe= $.trim($("#updateJobCanSubscribe").val());
            updateRow.jobDependences= $.trim($("#updateJobDependences").val());
            updateRow.jobOrgNo = formSelects.value('updateJobOrgNo', 'valStr')

            var updateDateTime = $.trim($("#updateDateTime").val());
            if (updateDateTime != null && updateDateTime != "") {
                updateRow.jobFirstDate= updateDateTime.split(" ")[0];
                updateRow.jobFirstTime= updateDateTime.split(" ")[1];
            }

            updateRow.jobParameter = "kettleLogLevel=" + $("#updateKettleLogLevel").val();
            //updateRow.extraParameter = $("#updateKtrPushFileName").val());
            this.data = updateRow;

            //阻止事件的冒泡 先校验在上传
            window.event.stopPropagation();

            //阻止事件的冒泡 先校验在上传
            //window.event.stopPropagation();
            //layui.event("formUpdate").stopPropagation();

            //文件上传 校验参数在上传
            return formVerify("update-main");

        },
        done: function(res, index, upload) {//上传完毕回调
            $("#updateKtrFile").val();
            success(res, "update");
        }
        , error: function() {//请求异常回调
            // layer.closeAll('loading');
            // layer.msg('异常，请稍后重试！');
        },

    });



    /**
     * 表单验证
     * @param {*} formId 表单所在容器id
     * @returns 是否通过验证
     */
    function formVerify(formId) {
        var stop = null //验证不通过状态
            , verify = layui.form.config.verify //验证规则
            , DANGER = 'layui-form-danger' //警示样式
            , formElem = $('#' + formId) //当前所在表单域
            , verifyElem = formElem.find('*[lay-verify]') //获取需要校验的元素
            , device = layui.device()

        //开始校验
        layui.each(verifyElem, function (_, item) {
            var othis = $(this)
                , vers = othis.attr('lay-verify').split('|')
                , verType = othis.attr('lay-verType') //提示方式
                , value = othis.val()
            othis.removeClass(DANGER) //移除警示样式
            //遍历元素绑定的验证规则
            layui.each(vers, function (_, thisVer) {
                var isTrue //是否命中校验
                    , errorText = '' //错误提示文本
                    , isFn = typeof verify[thisVer] === 'function'

                //匹配验证规则
                if (verify[thisVer]) {
                    var isTrue = isFn ? errorText = verify[thisVer](value, item) : !verify[thisVer][0].test(value)
                    errorText = errorText || verify[thisVer][1]

                    if (thisVer === 'required') {
                        errorText = othis.attr('lay-reqText') || errorText
                    }

                    //如果是必填项或者非空命中校验，则阻止提交，弹出提示
                    if (isTrue) {
                        //提示层风格
                        if (verType === 'tips') {
                            layer.tips(errorText, function () {
                                if (typeof othis.attr('lay-ignore') !== 'string') {
                                    if (item.tagName.toLowerCase() === 'select' || /^checkbox|radio$/.test(item.type)) {
                                        return othis.next()
                                    }
                                }
                                return othis
                            }(), { tips: 1 })
                        } else if (verType === 'alert') {
                            layer.alert(errorText, { title: '提示', shadeClose: true })
                        } else {
                            layer.msg(errorText, { icon: 5, shift: 6 })
                        }

                        //非移动设备自动定位焦点
                        if (!device.android && !device.ios) {
                            setTimeout(function () {
                                item.focus()
                            }, 7)
                        }

                        othis.addClass(DANGER)
                        return stop = true
                    }
                }
            })
            if (stop) return stop
        })
        if (stop) return false
        return true
    }




    //清空文件队列
    function clearFile(source){
        if (source == "add") {
            for (let file in addUploadFiles) {
                delete addUploadFiles[file];
            }
        }
        if (source == "update") {
            for (let file in updateUploadFiles) {
                delete updateUploadFiles[file];
            }
        }
    }

    //删除数据
    function deleteOne(data) {
        var msg = data.deleteFlag ?  "确认是否删除" : "确认是否终止";
        layer.confirm(msg, {icon: 3, title: '提示'}, function (index) {
            //已经是删除的数据 在进行删除直接删除数据否则修改deleteFlag标识
            var url = data.deleteFlag ?  "/job/config/delete" : "/job/config/update";
            data.deleteFlag = 1
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post(url, data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg('操作成功！', {icon: 1, time: 2000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }


    //启用 禁用
    function enableOne(data) {

        var msg = data.enable == 0 ? "启用" : "禁用";
        layer.confirm("确认是否" + msg, {icon: 3, title: '提示'}, function (index) {
            data.enable == 1 ? data.enable = 0 : data.enable = 1
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post("/job/config/update", data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg(msg + '成功！', {icon: 1, time: 2000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }

    //手动触发
    function triggerOne(data) {
        layer.confirm("确认是否触发", {icon: 3, title: '提示'}, function (index) {
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post("/job/config/triggerJob", data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg('触发任务成功！', {icon: 1, time: 2000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }

    //删除状态恢复
    function restoreOne(data) {
        layer.confirm("确认是否恢复", {icon: 3, title: '提示'}, function (index) {
            data.deleteFlag = 0;
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post("/job/config/update", data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg( '恢复成功！', {icon: 1, time: 2000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }

    //下载ktr脚本
    function download(data) {
        layer.confirm("确认是否下载文件", {icon: 3, title: '提示'}, function (index) {
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post("/job/config/download", data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg( '下载文件成功！', {icon: 1, time: 2000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }
    //切换环境
    function changeEnvOne(data) {
        layer.confirm("确认是否切换环境", {icon: 3, title: '提示'}, function (index) {
            $.ajaxSetup({ //ajax异步转同步
                async: false
            });
            $.post("/job/config/change/environment", data, function (res) {
                if (res.code === 0) {
                    layer.closeAll();
                    parent.layer.msg( '切换环境成功！切换环境后确认目标环境对应目录是否存在该任务的脚本文件没有则上传脚本文件!!!', {icon: 1, time: 3000, shade: 0.2});
                    loadJobConfigData();
                } else {
                    parent.layer.msg(res.msg, {time: 3000 });
                }
            });
        });
    }

    //添加按钮
    form.on('submit(formSave)', function (obj) {
        obj.stopPropagation();
        $.ajaxSetup({ //ajax异步转同步
            async: false
        });

        var addDateTime = $("#addDateTime").val();
        var jobFirstDate = "";
        var jobFirstTime = "";
        if (addDateTime != null && addDateTime != "") {
            jobFirstDate = addDateTime.split(" ")[0]
            jobFirstTime = addDateTime.split(" ")[1]
        }

        var data = {
            "jobCode": $.trim($("#addJobCode").val()),
            "jobName": $.trim($("#addJobName").val()),
            "businessSource": $("#addBusinessSource").val(),
            "cron": $.trim($("#addCron").val()),
            "enable": $("#addEnable").val(),
            "jobParameter": $.trim($("#addJobParameter").val()),
            "description": $.trim($("#addDescription").val()),

            "jobCycle" : $("#addJobCycle").val(),
            "jobPeriod" : $("#addJobPeriod").val(),
            "jobCanSubscribe" : $("#addJobCanSubscribe").val(),
            "jobDependences" : $("#addJobDependences").val(),
            "jobOrgNo" : formSelects.value('addJobOrgNo', 'valStr'),
            "jobFirstDate" : jobFirstDate,
            "jobFirstTime" : jobFirstTime,

        };
        //不是kettle_job业务 不进行文件上传
        if ($("#addBusinessSource").val() != "kettle_job") {
            var ajaxParam = {
                method: 'post',
                url: '/job/config/add',
                data: data,
                async: false, /*设置成同步*/
                success: function (res) {
                    success(res, "add");
                }
            }
            $.ajax(ajaxParam);
        }
    });


    //修改按钮
    form.on('submit(formUpdate)', function (obj) {

        $.ajaxSetup({ //ajax异步转同步
            async: false
        });

        updateRow.jobCode = $.trim($("#updateJobCode").val());
        updateRow.cron = $.trim($("#updateCron").val());
        updateRow.jobName = $.trim($("#updateJobName").val());
        updateRow.description = $.trim($("#updateDescription").val());
        updateRow.jobCycle = $.trim($("#updateJobCycle").val());
        updateRow.jobPeriod = $.trim($("#updateJobPeriod").val());
        updateRow.jobCanSubscribe= $.trim($("#updateJobCanSubscribe").val());
        updateRow.jobDependences= $.trim($("#updateJobDependences").val());

        var updateDateTime = $.trim($("#updateDateTime").val());
        if (updateDateTime != null && updateDateTime != "") {
            updateRow.jobFirstDate= updateDateTime.split(" ")[0];
            updateRow.jobFirstTime= updateDateTime.split(" ")[1];
        }

        updateRow.jobOrgNo = formSelects.value('updateJobOrgNo', 'valStr')
        //job描述
        var ajaxParam = {
            method: 'post',
            url: '/job/config/update',
            data: updateRow,
            async: false, /*设置成同步*/
            success: function (res) {
                success(res, "update");
            }
        }

        //不是kettle_job业务 或者没有选择文件 调用不上传接口
        if ($("#updateBusinessSource").val() != "kettle_job" ) {
            $.ajax(ajaxParam);
        } else if (updateUploadFiles == "" || updateUploadFiles == null) {
            //处理扩展参数
            // ajaxParam.data.extraParameter = $("#updateKtrPushFileName").val());
            var ktrFileInfo = updateRow.jobParameter.split(";")[0]
            ajaxParam.data.jobParameter = ktrFileInfo.substring(0, ktrFileInfo.lastIndexOf("/") + 1) +  $("#updateKtrFile").val() + ";" + "kettleLogLevel=" + $("#updateKettleLogLevel").val()
            $.ajax(ajaxParam);
        }

    });

    //批量启用
    $(document).on('click', '#batchEnable', function () {
        var list = layui.table.checkStatus('job_config_table').data;
        var ids = list.map(e=>e.id)
        batchEnableOrDisable(ids, 1)
    });
    //批量禁用
    $(document).on('click', '#batchDisable', function () {
        var list = layui.table.checkStatus('job_config_table').data;
        var ids = list.map(e=>e.id)
        batchEnableOrDisable(ids, 0)
    });


    //批量启用禁用
    function batchEnableOrDisable(ids, enable) {
        if (ids.length == 0) {
            parent.layer.msg("请先选择在操作");
            return;
        }

        var msg = "批量" + (enable == 1 ? "启用" : "禁用");
        layer.confirm("确认是否" + msg, {icon: 3, title: '提示'}, function (index) {
            $.ajax({
                traditional:true,
                method: 'post',
                url: '/job/config/batchEnbleOrDisable',
                data: {"ids": ids, "enable" : enable},
                async: false, /*设置成同步*/
                success: function (res) {
                    if (res.code == "0") {
                        layer.closeAll();
                        parent.layer.msg(msg + '成功！', {icon: 1, time: 2000, shade: 0.2});
                        loadJobConfigData();
                    } else {
                        parent.layer.msg(res.msg, {time: 3000 });
                    }
                }
            });

        });
    }



    //操作成功框
    function success(res, source) {
        if (res.code === 0) {
            // clearFile("add");
            // clearFile("update");
            //成功确认
            layer.open({
                type: 1
                , id: 'addDemo' //防止重复弹出
                , content: '<div style="padding: 20px 100px;">' + '操作成功' + '</div>'
                , btn: '确认'
                , btnAlign: 'c' //按钮居中
                , yes: function () {
                    layer.closeAll();
                    //window.location.reload();
                    loadJobConfigData();
                },
                cancel: function () {
                    layer.closeAll();
                    //window.location.reload();
                    loadJobConfigData();
                }
            });
        } else {
            parent.layer.msg(res.msg, {time: 3000 });
        }
    }


    /**
     * 初始化时间选择器
     */
    laydate.render({
        type: 'datetime',
        elem:"#updateDateTime",
        format:"yyyyMMdd HHmmss",
    });
    laydate.render({
        type: 'datetime',
        elem:"#addDateTime",
        format:"yyyyMMdd HHmmss",
    });


    form.render();
});