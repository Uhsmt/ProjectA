<!doctype html>
<%--
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
 --%>
 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
	<head>
		<meta charset="utf-8">
		<link href="<c:url value="/resources/css/common.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/jquery-ui.css" />" rel="stylesheet">

		<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"/>"></script>
		<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.19/jquery-ui.min.js"/>"></script>

		<script src="<c:url value="/resources/js/common.js" />"></script>

		<script>
		//グローバル関数
		var image_exist = false;

		$(function(){
			var def_width=$("#mosaic_create_window").width();
			var def_height=$("#mosaic_create_window").height();

			$("#flow1").click(function(){
				console.log("clickcheck");
			});


			$(".photo_size").change(function(){
				var val = $(this).val();
				var thisid = $(this).attr("id");
				var persentage = Number(val);

				var change_width = def_width * persentage / 100;
				var change_height = def_height * persentage /100;

				if(thisid == "range_height"){
					$("#height_size").text(change_height);
					$("#mosaic_create_window").css("height",change_height);
				}else if(thisid == "range_width"){
					$("#width_size").text(change_width);
					$("#mosaic_create_window").css("width",change_width);
				}
			})

		});

		//モザイクイメージ作成関数
		function generate(){
			var send_date;			//コントローラ側に送る引数
			//send_date = JSON.stringify(send_data);

	        $.ajax({
				type : "POST",
				url : "/mosaic_generate",//URL
				data: send_date,
				contentType: 'application/json; charset=UTF-8',
    			mimeType: 'application/json',
				dataType : "json",
				cache : false,
				success : function(data, status, xhr) {
					alert("success:"+data);
					image_exist = true;
				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("データの取得に失敗しました");
				}
	        });
		}


		//保存用関数
		function save(){
			if(!image_exist){
				alert("イメージが作成されていません。");
				return false;
			}else{
				var send_date = "";//コントローラ側に送る引数

				$.ajax({
					type : "POST",
					url : "/mosaic_save",//URL
					data: send_date,
					contentType: 'application/json; charset=UTF-8',
	    			mimeType: 'application/json',
					dataType : "json",
					cache : false,
					success : function(data, status, xhr) {
						alert("success:"+data);

					},
					error : function(XMLHttpRequest, status, errorThrown) {
						alert("データの取得に失敗しました");
					}
		        });
			}
		}

		//リセット用関数
		function clear_image(){
			$(function(){
				$("body").scrollTop(0);

			});
		}


		</script>
		<style>
		</style>
		<title>MosaicAppli</title>
	</head>

	<body style="background-color: #9e9e9e;">
		<div id = "wrapper" >
			<div id="mosaic_header">
				<div class="mosaic_header_tab">
				<img class="mt8 ml10" src="resources/images/mosaic_logo.png" width="30px">
				<span>MosaicGenerator</span>
				</div>
			</div>
			<div id="mosaic_wrapper">
				<div id="flow1">
					<p class="mosaic_flow_title"><span class=mosaic_title_sqare></span>完成イメージを選択</p>
					<table class="mosaic_select_temple">
						<tr>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage1">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage1">
									<img src="resources/images/base-01.png">
								</label>
							</td>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage2">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage2">
									<img src="resources/images/base-02.png">
								</label>
							</td>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage3">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage3">
									<img src="resources/images/base-03.png">
								</label>

							</td>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage4">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage4">
									<img src="resources/images/base-04.png">
								</label>

							</td>
						</tr>
						<tr>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage5">
							</td>
							<td>
								<label for="baseimage5" class="mosaic_baseimages">
									<img src="resources/images/base-05.png">
								</label>
							</td>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage6">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage6">
									<img src="resources/images/base-06.png">
								</label>
							</td>
							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage7">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage7">
									<img src="resources/images/base-07.png">
								</label>
							</td>

							<td class="td_check">
								<input type="radio" name="imageselect_radio" id="baseimage8">
							</td>
							<td>
								<label class="mosaic_baseimages" for="baseimage8">
									<span class="baseimage_none"><p style="padding:10px;">オリジナル画像を<br>フォルダから選択</p></span>
								</label>
							</td>
						</tr>
					</table>
				</div>
				<div id="flow2" class="mt20">
					<p class="mosaic_flow_title"><span class=mosaic_title_sqare></span>素材選択</p>
					<input type="file" name="image_file[]" multiple="multiple" accept="image/*" id="upload_images">
					<div>
						<div id="mosaic_drop_material" ><font color="gray">このエリアにファイルをドロップしてください。</font></div>
					</div>
					<div class="mt20">▼選択中の素材
						<div id="mosaic_drop_image_show" ></div>
					</div>
				</div>

				<div id="flow3"  class="mt20">
					<p class="mosaic_flow_title"><span class=mosaic_title_sqare></span>カラーパターン選択</p>

				</div>
				<div id="flow4"  class="mt20">
					<p class="mosaic_flow_title"><span class=mosaic_title_sqare></span>仕上がりサイズとモザイク密度指定</p>
					<div>
						縦:
						<input type="range" name="range_height" id="range_height" min=0 max=100 class="photo_size" value="100">
						<input type="number" min=0 id="height_input"> px
					</div>
					<div>
						横:
						<input type="range" name="range_width" id="range_width" min=0 max=100  class="photo_size" value="100">
						<input type="number" min=0 id="width_input"> px
					</div>
				</div>

				<div id="flow5">
					<div class="mt10" style="height:80px;margin-left: 345px;">
						<div class="btndiv_1" onclick="generate()">作成</div>
<!-- 						<div class="btndiv_1" onclick="generate_test()">テスト</div>-->
					</div>
					<div id="mosaic_create_window"></div>
					<div class="mt10 fr" style="height:80px; ">
						<div class="btndiv_1" onclick="save()">保存</div>
						<div class="btndiv_1" onclick="clear_image();">やり直し</div>
						<img src="resources/images/facebooklogo.png" alt="facebook" style="width:30px" onclick="alert('FaceBookで共有する');">
						<img src="resources/images/twitter.png" alt="twitter"style="width:30px" onclick="alert('Twitterで共有する');">
						<img src="resources/images/instagram.png" alt="instagram"style="width:30px" onclick="alert('Instagramで共有する');">
					</div>
					<div class="clr"></div>
				</div>
			</div>
		</div>
	</body>
</html>