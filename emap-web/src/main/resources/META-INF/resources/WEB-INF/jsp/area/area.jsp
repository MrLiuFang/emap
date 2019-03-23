<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="pepper" uri="http://pepper.com"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="layui-fluid">
	<div class="layui-row layui-col-space15">
		<div class="layui-card">
			<form class="layui-form layui-form-pane" action="" id="form-query">
				<div class="layui-form-item">
					<div class="layui-inline ">
						<label class="layui-form-label">地区名</label>
						<div class="layui-input-inline">
							<input type="text" name="areaName" id="areaName"
								placeholder="地区名" class="layui-input">
						</div>
					</div>
					<div class="layui-inline ">
						<label class="layui-form-label">地区编号</label>
						<div class="layui-input-inline">
							<input type="text" name="code" id="code" placeholder="地区编号"
								class="layui-input">
						</div>
					</div>
					<div class="layui-inline ">
						<label class="layui-form-label">地区类型</label>
						<div class="layui-input-inline" style="width: 190px;">
							<pepper:Enum2Select documentId="level"
								enumClass="com.qicloud.model.console.common.enums.AreaLevelEnum"
								documentName="level" />
						</div>
					</div>
					<div class="layui-inline ">
						<label class="layui-form-label">父级编号</label>
						<div class="layui-input-inline">
							<input type="text" name="parentCode" id="parentCode"
								placeholder="父级编号" class="layui-input">
						</div>
					</div>
				</div>
				<div class="layui-form-item qc-list-bar">
					<div class="layui-inline">
						<button class="layui-btn" id="button_query" type="button"
							data-type="reload">查询</button>
						<pepper:auth code="SYSTEM_SETTINGS_SUPPORT_AREA_TOADD">
							<button class="layui-btn layui-btn-normal" id="button_add"
								type="button">新增</button>
						</pepper:auth>
					</div>
				</div>
			</form>
		</div>

		<div class="layui-card layui-form">
			<table class="layui-hide" id="mainTable" lay-filter="mainTable"></table>
		</div>
	</div>
</div>

<script type="text/html" id="barDemo">
	<pepper:auth code="SYSTEM_SETTINGS_SUPPORT_AREA_TOVIEW">
		<a class="layui-btn layui-btn-xs" lay-event="view">查看</a>
	</pepper:auth>
	<pepper:auth code="SYSTEM_SETTINGS_SUPPORT_AREA_TOEDIT">
		<a class="layui-btn layui-btn-xs" lay-event="update">编辑</a>
	</pepper:auth>
	<pepper:auth code="SYSTEM_SETTINGS_SUPPORT_AREA_DELETE">
		<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
	</pepper:auth>
</script>

<script type="text/javascript">
	$(function() {
		var $ = layui.$;
		var table = layui.table;
		//表格渲染
		table.render({
			elem : '#mainTable',
			url : '${ctx}/console/supportArea/list?sort_level=asc',
			method : "post",
			cols : [ [ {
				field : 'areaName',
				title : '地区名'
			}, {
				field : 'code',
				title : '地区编号'
			}, {
				field : 'levelStr',
				title : '地区类型'
			}, {
				field : 'parentCode',
				title : '父级编号'
			}, {
				fixed : 'right',
				width : 200,
				toolbar : '#barDemo',
				title : '操作'
			} ] ],
			request : {
				pageName : 'pageNo',
				limitName : 'pageSize'
			},
			response : {
				statusName : 'status',
				statusCode : 200,
				msgName : 'message',
				countName : 'totalRow',
				dataName : 'results'
			},
			page : {
				layout : [ 'count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip' ]
			}
		});

		//点击“搜索”
		$('#button_query').on('click', function() {
			table.reload('mainTable', {
				page : {
					curr : 1
				},
				where : {
					"search_EQ_level" : $('#level').val(),
					"search_LIKE_areaName" : $('#areaName').val(),
					"search_LIKE_code" : $('#code').val(),
					"search_LIKE_parentCode" : $('#parentCode').val()
				}
			});
		});

		//监听工具条
		table.on('tool(mainTable)', function(obj) { //注：tool是工具条事件名，mainTable是table原始容器的属性 lay-filter="对应的值"
			var data = obj.data; //获得当前行数据
			var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
			var tr = obj.tr; //获得当前行 tr 的DOM对象
			if (layEvent === 'view') { //查看
				page.loadPage("${ctx}/console/supportArea/toView?id=" + data["id"])
			} else if (layEvent === 'del') { //删除
				layui.layer.confirm('确定删除该行？', function(index) {
					obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
					layui.layer.close(index);
					//向服务端发送删除指令
					common.deleteData('${ctx}/console/supportArea/delete', data["id"]);
					table.reload('mainTable');
				});
			} else if (layEvent === 'update') { //修改
				page.loadPage("${ctx}/console/supportArea/toEdit?id=" + data["id"]);
			}
		});

		//新增跳转
		$("#button_add").click(function() {
			page.loadPage("${ctx}/console/supportArea/toAdd");
		})

	});
</script>


