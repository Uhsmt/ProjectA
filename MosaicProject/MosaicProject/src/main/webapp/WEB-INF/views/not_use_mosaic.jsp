<!doctype html>
<%--
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta charset="utf-8">
<link href="<c:url value="/resources/css/common.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/jquery-ui.css" />" rel="stylesheet">

<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"/>"></script>
<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.19/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/js/common.js" />"></script>

<!-- 初期動作 -->
<script>
	var image_exist = false;
	var def_width;
	var def_height;
	var minpix;

	$(function() {
		//デフォルト設定
		def_width = $("#mosaic_create_window").width();//IDがmosaic_create_window横
		def_height = $("#mosaic_create_window").height();//IDがmosaic_create_windowの縦
		minpix = $("#mosaic_sqare").val();
		$("#height_input").val(def_height);//IDがheight_inputに値をセット
		$("#width_input").val(def_width);//IDがwidth_inputに値をセット

		//classのphoto_sizeが操作されたら
		$(".photo_size").change(function() {
			//スライダー変動
			var val = $(this).val();//値を取得
			var thisid = $(this).attr("id");//idを取得
			var persentage = Number(val);//%に置き換える

			var change_width = def_width * persentage / 100;
			var change_height = def_height * persentage / 100;

			//枠を変更する
			if (thisid == "range_height") {
				$("#mosaic_create_window").css("height", change_height);
				$("#height_input").val(Math.round(change_height));
				def_height = $(this).val();
			} else if (thisid == "range_width") {
				$("#mosaic_create_window").css("width", change_width);
				$("#width_input").val(Math.round(change_width));
				def_width = $(this).val();
			} else {
				$("#mosaic_sqare_input").val(val);
				minpix = $(this).val();
			}

			//値が変わるたびにサーバへ送信
			var data  = def_height + "/" + def_width +"/"+ minpix;
			$.ajax({
				type : "POST",
				url : "data",
				data : data,
				contentType : false,
				mimeType : 'application',
				dataType : "text",
				cache : false,
				success : function(data, status, xhr) {
				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("失敗しました。");
				}
			});

		})

		//classのphoto_size_inputが操作されたら
		$(".photo_size_input").change(function() {
			//入力値変動
			var val = $(this).val();
			var thisid = $(this).attr("id");

			var range_width = Math.round(100 * val / def_width);
			var range_height = Math.round(100 * val / def_height);
			var range_mosaic = val;
			var this_max = $(this).attr("max");
			if (Number(val) > Number(this_max)) {
				val = this_max;
				$(this).val(this_max);
			}

			if (thisid == "height_input") {
				$("#mosaic_create_window").css("height", val);
				$("#range_height").val(range_height);
				def_height = $(this).val();
			} else if (thisid == "width_input") {
				$("#mosaic_create_window").css("width", val);
				$("#range_width").val(range_width);
				def_width = $(this).val();
			} else {
				$("#mosaic_sqare").val(val);
				minpix = $(this).val();
			}

			//値が変わるたびにサーバへ送信
			var data  = def_height + "/" + def_width +"/"+ minpix;
			$.ajax({
				type : "POST",
				url : "data",
				data : data,
				contentType : false,
				mimeType : 'application',
				dataType : "text",
				cache : false,
				success : function(data, status, xhr) {
				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("失敗しました。");
				}
			});
		})
	});
</script>

<!-- モザイクイメージ作成関数 -->
<script>
	function generate() {
		var origin = new Boolean(false);
		var mozaicpath;
		// ラジオのid取得
		var selected_id = $("input[name='imageselect_radio']:checked").attr("id");
		// フォームデータを取得
		var formdata = new FormData($('#my_form').get(0));

		if (selected_id == null || selected_id == undefined || selected_id == "") {
			alert("ラジオボタンが選択されていません。");
			return false;
		} else {
			if (selected_id == "baseimage8") {
				var input_file = document.getElementById("select_file").value;
				if (input_file == "") {
					alert("オリジナル画像が選択されていません。");
					return false;
				} else {
					origin = true;
				}
			}
		}
		//var data  = selected_id+"/"+ def_height + "/" + def_width +"/"+ minpix;
		//オリジナル画像の場合
		if (origin == true) {
			$.ajax({
				url : "uporigin",
				type : "POST",
				data : formdata,
				cache : false,
				contentType : false,
				processData : false,
				dataType : "html",
			}).done(function(data, status, jqXHR) {
				image_exist = true;
				mozaicpath = data;
				alert("モザイクフォトが完成しました。" + data);
			}).fail(function(XMLHttpRequest, status, errorThrown) {
				alert("失敗しました。");
			});
		//デフォルト画像の場合
		} else {
			$.ajax({
				type : "POST",
				url : "up",
				data : selected_id,
				contentType : false,
				mimeType : 'application',
				dataType : "text",
				cache : false,
				success : function(data, status, xhr) {
					image_exist = true;
					mozaicpath = data;
					alert("モザイクフォトが完成しました。" + data);
				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("失敗しました。");
				}
			});
		}

		/*
		var selected_id = $("input[name='imageselect_radio']:checked").attr("id");
		console.log(selected_id);
		if (selected_id == null || selected_id == undefined	|| selected_id == "") {
			alert("画像が選択されていません。");
			return false;
		} else {
			if (selected_id == "baseimage8") {
				var formdata = new FormData($('#my_form').get(0));
			}
		}

		if (selected_id == "baseimage8") {
			selected_id = $("input[name='image_file[]']").attr("id");
			selected_id += "_img";
			var imgurl = $("#" + selected_id).attr("src");
			if (imgurl == null || selected_id == undefined || selected_id == "") {
				alert("オリジナル画像が選択されていません。");
			}
		}

		selected_id += "_img";
		var imgurl = $("#" + selected_id).attr("src");

		//コントローラ側に送る引数(素材フォルダ)
		var materialpath = "C:\\Material";
		//send_date = JSON.stringify(send_data);

		$.ajax({
			type : "POST",
			url : "mosaic_generate",//URL
			data : imgurl,
			contentType : 'application/json; charset=UTF-8',
			mimeType : 'application/json',
			dataType : "text",
			cache : false,
			success : function(data, status, xhr) {
				alert("success:" + data);
				image_exist = true;
			},
			error : function(XMLHttpRequest, status, errorThrown) {
				alert("データの取得に失敗しました" + status);
			}
		});
		*/
	}
</script>

<!-- 保存用関数 -->
<script>
	function save() {
		if (!image_exist) {
			alert("イメージが作成されていません。");
			return false;
		} else {
			var send_date = "";//コントローラ側に送る引数

			$.ajax({
				type : "POST",
				url : "/mosaic_save",//URL
				data : send_date,
				contentType : 'application/json; charset=UTF-8',
				mimeType : 'application/json',
				dataType : "json",
				cache : false,
				success : function(data, status, xhr) {
					alert("success:" + data);

				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("データの取得に失敗しました");
				}
			});
		}
	}
</script>

<script>
	//リセット用関数
	function clear_image() {
		$(function() {
			$("body").scrollTop(0);

		});
	}
	/*
	function create_mosaic() {
		//createされるたびにlistは初期化
		basemosaic_list = [];
		var selected_id = $("input[name='imageselect_radio']:checked").attr(
				"id");
		console.log(selected_id);
		if (selected_id == null || selected_id == undefined
				|| selected_id == "") {
			alert("画像が選択されていません。");
			return false;
		}
		selected_id += "_img";
		var imgurl = $("#" + selected_id).attr("src");
		var img = new Image();
		img.src = imgurl;

		var canvas = document.getElementById("canvas");
		var imgWidth = canvas.width = img.width;
		var imgHeight = canvas.height = img.height;
		var context = canvas.getContext("2d");
		context.drawImage(img, 0, 0);
		var size = new Number($("#mosaic_sqare").val());
		console.log(imgWidth + ":" + imgHeight);

		var y_count = 0;
		var x_count = 0;
		var cell_name = "";

		//縦方向ループ
		for (var y = 0; y < imgHeight; y += size) {
			y_count += 1;
			//条件式　(size <= imgHeight-y)が正だった場合はsize、そうでなければimgHeight-yを適用
			var h = (size <= imgHeight - y) ? size : imgHeight - y;

			//横方向ループ
			for (var x = 0; x < imgWidth; x += size) {
				var w = (size <= imgWidth - x) ? size : imgWidth - x;
				x_count += 1;

				var r = 0;
				var g = 0;
				var b = 0;

				var data = context.getImageData(x, y, w, h).data;
				var dataLength = data.length;

				for (var pixelIndex = 0; pixelIndex < dataLength; pixelIndex += 4) {
					r += data[pixelIndex];
					g += data[pixelIndex + 1];
					b += data[pixelIndex + 2];
				}

				var pixelCount = dataLength / 4;

				r = Math.floor(r / pixelCount);
				g = Math.floor(g / pixelCount);
				b = Math.floor(b / pixelCount);

				cell_name = "y" + y_count + "x" + x_count;
				//listに入れるオブジェクトを作成
				var cell = {
					cell_name : cell_name,
					x_address : x_count,
					y_address : y_count,
					Red : r,
					Green : g,
					Blue : b
				}
				basemosaic_list.push(cell);

				context.clearRect(x, y, w, h);
				context.fillStyle = 'rgb(' + r + ',' + g + ',' + b + ')';
				context.fillRect(x, y, w, h);
			}
		}
		console.log("タイル数：" + x_count * y_count);
	}
	 */

	/*
	function baselist_show() {
		console.log(basemosaic_list);
	}
	 */
</script>

<style>
</style>

<title>MosaicAppli</title>
</head>

<body style="background-color: #9e9e9e;">
	<div id="wrapper">
		<div id="mosaic_header">
			<div class="mosaic_header_tab">
				<img class="mt8 ml10" src="resources/images/mosaic_logo.png" width="30px"> <span>MosaicGenerator</span>
			</div>
		</div>
		<div id="mosaic_wrapper">
			<div id="flow1">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>完成イメージを選択
				</p>

				<table class="mosaic_select_temple">
					<tr>
						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-01.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage1">
								<img src="resources/images/base-01.png" id="baseimage1_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-02.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage2">
								<img src="resources/images/base-02.png" id="baseimage2_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-03.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage3">
								<img src="resources/images/base-03.png" id="baseimage3_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-04.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage4">
								<img src="resources/images/base-04.png" id="baseimage4_img">
						</label></td>
					</tr>

					<tr>
						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-05.png"></td>
						<td><label for="baseimage5" class="mosaic_baseimages">
								<img src="resources/images/base-05.png" id="baseimage5_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-06.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage6">
								<img src="resources/images/base-06.png" id="baseimage1_img6">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="base-07.png"></td>
						<td><label class="mosaic_baseimages" for="baseimage7">
								<img src="resources/images/base-07.png" id="baseimage7_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" id="baseimage8"></td>
						<td><label class="mosaic_baseimages" for="baseimage8">
<!-- 								<span class="baseimage_none"> -->
<!-- 									<p style="padding: 10px;">オリジナル画像を<br>フォルダから選択</p> -->
<!-- 								</span> -->
							<form id="my_form" enctype="multipart/form-data">
								<input type="file" name="file" id="select_file">
							</form>
						</label></td>

					</tr>
				</table>
			</div>

			<div id="flow2" class="mt20">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>素材選択
				</p>
				<form id="muiti_form" enctype="multipart/form-data">
<!-- 				<input type="file" name="image_file" multiple="multiple" accept="image/*" id="upload_images"> -->
					<input type="file" name="file" id="select_file">
					<div>
						<div id="mosaic_drop_material">
							<font color="gray">このエリアにファイルをドロップしてください。</font>
						</div>
					</div>
				</form>

				<div class="mt20">
					▼選択中の素材
					<div id="mosaic_drop_image_show"></div>
				</div>
			</div>

			<div id="flow3" class="mt20">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>カラーパターン選択
				</p>

			</div>

			<div id="flow4" class="mt20">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>仕上がりサイズとモザイク密度指定
				</p>
				<div>
					縦: <input type="range" name="range_height" id="range_height" min=10 max=100 class="photo_size" value="100">
						<input	type="number" min=0 max=510 id="height_input" class="photo_size_input"> px
				</div>
				<div>
					横: <input type="range" name="range_width" id="range_width" min=10 max=100 class="photo_size" value="100">
						<input	type="number" min=10 max=800 id="width_input" class="photo_size_input"> px
				</div>
				<div>
					モザイクサイズ: <input type="range" name="mosaic_square" id="mosaic_sqare"	min=5 max=100 class="photo_size" value="10">
									<input	type="number" min=5 max=100 id="mosaic_sqare_input"	class="photo_size_input" value=10> px四方
				</div>
			</div>

			<div id="flow5">
				<div class="mt10" style="height: 80px; margin-left: 345px;">
					<form id="my_form" enctype="multipart/form-data">
						<div class="btndiv_1" onclick="generate()">作成</div>
<!-- 						<div class="btndiv_1" onclick="generate_test()">テスト</div> -->
					</form>
				</div>
<!-- 				TODO ここにモザイク画像を表示------------------------------------------ -->
<!-- 				<div id="mosaic_create_window"> -->
					<div>
					<img id="mosaic_create_window">
<!-- 				</div> -->
					</div>
				<div class="mt10 fr" style="height: 80px;">

					<div class="btndiv_1" onclick="save()">保存</div>
					<div class="btndiv_1" onclick="clear_image();">やり直し</div>

					<img src="resources/images/facebooklogo.png" alt="facebook"	style="width: 30px" onclick="alert('FaceBookで共有する');">
					<img src="resources/images/twitter.png" alt="twitter" style="width: 30px" onclick="alert('Twitterで共有する');">
					<img src="resources/images/instagram.png" alt="instagram" style="width: 30px" onclick="alert('Instagramで共有する');">
				</div>
				<div class="clr"></div>
			</div>
		</div>
	</div>

	<!-- 	▼テスト検証スペース▼  -->
<!-- 	<div id="test_space" style="background-color: white"> -->
<!-- 		<h1>HTML5のcanvasでモザイク処理</h1> -->
<!-- 		<form> -->
<!-- 			モザイクのサイズとモザイクのベース画像はページ上部で選択 -->
<!-- 			<!--  		<select name="mosaicSize" id="mosaicSize"> -->
<!-- 		  <option value="5">5 x 5</option> -->
<!-- 		  <option value="10">10 x 10</option> -->
<!-- 		  <option value="30">30 x 30</option> -->
<!-- 		  <option value="50">50 x 50</option> -->
<!-- 		</select> -->
<!--  			-->
<!-- 			<input type="button" value="実行" onclick="create_mosaic()" /> -->
<!-- 		</form> -->
<%-- 		<canvas id="canvas"> --%>
<!-- 		canvasに対応したブラウザでなければ動作しません！ -->
<%-- 		</canvas> --%>
<!-- 		<p onclick="baselist_show()" -->
<!-- 			style="display: block; background: orange; color: white; width: 150px">リスト確認</p> -->
<!-- 	</div> -->
</body>
</html>