<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="pepper" uri="http://pepper.com"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<span id="page-title" style="display: none"><c:choose>
		<c:when test="${action=='view'}">查看</c:when>
		<c:otherwise>修改</c:otherwise>
	</c:choose></span>

<div class="layui-fluid">
	<div class="layui-row layui-col-space15">
		<div class="layui-card">
			<div class="layui-card-header">
				<c:choose>
					<c:when test="${action=='view'}">查看</c:when>
					<c:otherwise>修改</c:otherwise>
				</c:choose>
			</div>
			<div class="layui-card-body">
				<form class="layui-form " action="${ctx}/console/supportArea/update"
					id="form-addOrUpdate" returnType="json">
					<input name="id" id="id" value="${supportArea.id}" type="hidden" />
					<div class="layui-form-item ">
						<label class="layui-form-label">地区名<span class="qi-req">*</span></label>
						<div class="layui-input-inline">
							<input type="text" name="areaName" id="areaName"
								lay-verify="areaName" autocomplete="off" placeholder="地区名"
								class="layui-input" value="${supportArea.areaName }" />
						</div>
						<div class="layui-form-mid layui-word-aux">请保持唯一，并且长度小于等于50个字符。</div>
					</div>
					<div class="layui-form-item ">
						<label class="layui-form-label">地区编号<span class="qi-req">*</span></label>
						<div class="layui-input-inline">
							<input type="text" name="code" id="code" lay-verify="code"
								autocomplete="off" placeholder="地区编号" class="layui-input"
								value="${supportArea.code }" />
						</div>
						<div class="layui-form-mid layui-word-aux">请保持唯一，只允许填入数字，并且长度小于等于20个字符。</div>
					</div>
					<div class="layui-form-item ">
						<label class="layui-form-label">地区类型<span class="qi-req">*</span></label>
						<div class="layui-input-inline">
							<pepper:Enum2Select required="true" documentId="level"
								enumClass="com.qicloud.model.console.common.enums.AreaLevelEnum"
								documentName="level" selectedValue="${supportArea.level }" />
						</div>
					</div>
					<div class="layui-form-item ">
						<label class="layui-form-label">父级编号<span class="qi-req">*</span></label>
						<div class="layui-input-inline">
							<input type="text" name="parentCode" id="parentCode"
								lay-verify="parentCode" autocomplete="off" placeholder="父级编号"
								class="layui-input" value="${supportArea.parentCode }" />
						</div>
						<div class="layui-form-mid layui-word-aux">请根据地区类型填写已存在的上级地区编码，如地区类型为"市"，必须填写当前存在的省级地区编码。只允许填入数字，并且长度小于等于20个字符。</div>
					</div>
					<div class="layui-inline" style="text-align: center;">
						<label class="layui-form-label"></label>
						<pepper:auth code="SYSTEM_SETTINGS_SUPPORT_AREA_UPDATE">
							<button class="layui-btn" lay-submit id="button_addOrUpdate"
								type="submit">保存</button>
						</pepper:auth>
						<button class="layui-btn layui-btn-normal" id="button_return"
							type="button">返回</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document)
			.ready(
					function() {
						//返回
						$("#button_return").click(function() {
							page.loadPage("${ctx}/console/supportArea/index")
						});

						//action=view时只读
						if ("view" == "${action}") {
							$("#button_addOrUpdate").remove();
							$("input, select, textarea").attr("disabled",
									"disabled");
							$("input, select, textarea").addClass("qc-cursor");
							layui.form.render();
						}

						//选择类型为省，那么的父级编号必须为0
						layui.form.on('select(level)', function(data) {
							if (data.value == 1) {
								$("#parentCode").val("0");
								$("#parentCode").attr("disabled", "disabled");
								$("#parentCode").addClass("qc-cursor");
							} else {
								$("#parentCode").removeAttr("disabled",
										"disabled");
								$("#parentCode").removeClass("qc-cursor");
							}
						});

						//校验
						form
								.verify({
									code : function(value, item) {
										value = $.trim(value);
										if ("${supportArea.code }" === value) {
											return false;
										}
										if (!value) {
											return '必填项不能为空';
										}
										if (!new RegExp("^\\d{1,20}$")
												.test(value)) {
											return '必须为数值，并且不能大于20位';
										}
										var exist = false;
										baseUtil
												.AJpost(
														"${ctx}/console/supportArea/checkSupportAreaExist",
														{
															code : value
														},
														function(d) {
															exist = d.data.exist;
														});
										if (exist) {
											return '地区编号已存在';
										}
									},
									areaName : function(value, item) {
										value = $.trim(value);
										if ("${supportArea.areaName }" === value) {
											return false;
										}
										if (!value) {
											return '必填项不能为空';
										}
										if (!new RegExp("^.{1,50}$")
												.test(value)) {
											return '长度不能大于50个字符';
										}
									},
									parentCode : function(value, item) {
										value = $.trim(value);
										if ("${supportArea.parentCode }" === value
												&& "${supportArea.parentCode }" == $(
														"#level").val()
														+ "") {
											return false;
										}
										if ($("#level").val() * 1 == 1) {
											return false;
										}
										var exist = false;
										baseUtil
												.AJpost(
														"${ctx}/console/supportArea/checkParentAreaExist",
														{
															parentCode : value,
															level : $("#level")
																	.val()
														},
														function(d) {
															exist = d.data.exist;
														});
										if (!exist) {
											return '父级编号不存在';
										}
									}
								});
					});
</script>