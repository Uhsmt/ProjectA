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
<link rel="shortcut icon" href="resources/images/mosaic_logo.png">

<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"/>"></script>
<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.19/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/js/common.js" />"></script>

<script>

	var image_exist = false;
	var def_width;
	var def_height;
	var minpix;	//widthとheightの約数比でないとだめ
	var origin_path;
	var max_width = 800;
	var pix_list = new Array();

	$(function() {
		//デフォルト設定
		def_width = $("#baseimage1_img").width();
		def_height = $("#baseimage1_img").height();
		minpix = $("#mosaic_sqare").val();

		$("input[name='imageselect_radio']").change(function(){
			size_set();
			var this_rate = Math.round(max_width/def_width) * 10;
			$("#rate").val(this_rate).trigger("change");
		});

		$("#mosaic_sqare_input").change(function(){
			var thisval = $(this).val();
			var pix = pixcel_check(thisval);
			$("#mosaic_sqare_input").val(pix);
		});

		$("#rate").change(function(){
			var thisval = $(this).val();
			var rate ;
			if(thisval >= 1){
				rate = thisval/10;
			}else if(thisval <= -1){
				rate = 1/(-thisval/10);
			}else{
				rate = 1;
			}

			var height =Math.round(def_height * rate)
			var width =  Math.round(def_width * rate)
			$("#height_input").val(height);
			$("#width_input").val(width);
			$("#mosaic_create_window").css("width",width);
			$("#mosaic_create_window").css("height",height);

		}).dblclick(function(){
			$(this).val(1);
			$("#height_input").val( def_height);
			$("#width_input").val( def_width);
			$("#mosaic_create_window").css("width",def_width);
			$("#mosaic_create_window").css("height",def_height);

		});

 		//classのphoto_sizeが操作されたら
		$(".photo_size").change(function() {
			//スライダー変動
			var val = $(this).val();//値を取得
			$("#mosaic_sqare_input").val(val).trigger("change");
			minpix = $(this).val();
		});

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
			} else if (thisid == "width_input") {
				$("#mosaic_create_window").css("width", val);
				$("#range_width").val(range_width);
			} else {
				$("#mosaic_sqare").val(val);
				minpix = $(this).val();
			}
		})
	});

	function origin_image_send(frm){
		$(function(){
			// フォームで選択された全ファイルを取得
			var fileList = frm.files ;
			// 個数分の画像を表示する
			for( var i=0,l=fileList.length; l>i; i++ ) {
				// Blob URLの作成
				var blobUrl = window.URL.createObjectURL( fileList[i] ) ;
				// HTMLに書き出し (src属性にblob URLを指定)
				$("#output").html('<label for="baseimage8" ><img src="' + blobUrl + '" id="baseimage8_img"></label>') ;
			}
			//ラジオボタン
			//$('input[name=lang]:eq(0)').prop('checked', true);
			$('#baseimage8').prop('checked', true);

		});
		// フォームデータを取得
		var formdata = new FormData($('#my_form').get(0));
		var input_file = document.getElementById("select_file").value;
		if (input_file == "") {
			origin = false;
			return false;
		} else {
			origin = true;
		}
		$.ajax({
			url : "uporigin",
			type : "POST",
			data : formdata,
			cache : false,
			contentType : false,
			processData : false,
			dataType : "text",
		}).done(function(data, status, jqXHR) {
			if(data == "false"){
				alert("画像選択に失敗しました。" + data);
			}else{
				alert("オリジナル画像を使用します。");
				origin_path = data;
				size_set();
				$("#baseimage8").trigger("change");
			}
		}).fail(function(XMLHttpRequest, status, errorThrown) {
			alert("失敗しました。" + status);
		});
	}


	//モザイクイメージ作成関数
	function generate() {
		$("#mosaic_create_window").empty();
		var selected_id = $("input[name='imageselect_radio']:checked").attr("data-name");

		var isOrigin = false;
		// change hashi ピクセルサイズや幅は作成決定時に送信する
		if (selected_id == "baseimage8"){
			isOrigin = true;
			if(origin_path == "" || origin_path == undefined){
				alert("オリジナル画像が選択されていません。");
				return false;
			}
			selected_id = origin_path;
		}else if(selected_id == "" || selected_id == undefined){
			alert("画像が選択されていません。");
			return false;
		}

		dispLoading();

		var height = $("#height_input").val();
		var width = $("#width_input").val();
		var pixcel = $("#mosaic_sqare_input").val();
		var data  = selected_id+"/"+ height + "/" +width +"/"+ pixcel + "/" + isOrigin;
		$.ajax({
			type : "POST",
			url : "up",
			data : data,
			contentType : false,
			mimeType : 'application',
			dataType : "text",
			cache : false,
			success : function(data, status, xhr) {
				image_exist = true;
				mozaicpath = data;
				alert("モザイクフォトが完成しました。");
				var timestamp = new Date().getTime();
				$("#mosaic_create_window").html("<img src='file1?"+timestamp+"''>");
				$("#save_btn").html('<a href="file1" download="mosaic.png"><span class="btndiv_1" id="save_btn">保存</span></a>')


			},
			error : function(XMLHttpRequest, status, errorThrown) {
				alert("失敗しました。");
			},
			complete : function(data) {
                removeLoading();
            }
		});
	}

	//保存用関数
	function save() {
		if (!image_exist) {
			alert("イメージが作成されていません。");
			return false;
		} else {
			window.location.href =  "file1.png";
		}
	}

	function size_set(){
		console.log("size_set");

		$(function(){
			var thisid = $("input[name='imageselect_radio']:checked").attr("id");
			var imgwidth = $("#" + thisid + "_img").width();
			var imgheight = $("#" + thisid + "_img").height();

			def_width = imgwidth;
			def_height = imgheight;

			$("#height_input").val(imgheight);
			$("#width_input").val(imgwidth);

//			pix_list = getMinPix(imgheight,imgwidth);
			var pix = $("#mosaic_sqare_input").val();
			pix = pixcel_check(pix);
			$("#mosaic_sqare_input").val(pix);
/* 			var pix = $("#mosaic_sqare_input").val();
			var pix_ok = false;
			var pix_new ;
			console.log(pix_list);
			for(var i=0 ; i< pix_list.length ; i++){
				if(pix % pix_list[i] == 0){
					pix_ok = true;
					break;
				}else{
					if(pix < pix_list[i]){
						pix_new = pix_list[i];
					}
				}
			}
			if(!pix_ok){
				pix = pix_new;
			}
 *///			console.log(pix);
			//$("#mosaic_sqare_input").val(pix);

		});
	}

	//pixcelcheck
	function pixcel_check(pix){
		var width = $("#width_input").val();
		var height = $("#height_input").val();
		pix_list = getMinPix(height,width);

		var pix_ok = false;
		var pix_new = 0;
		for(var i=pix_list.length-1 ; i>=0; i--){
			if(pix_list[i]% pix == 0){
				pix_ok = true;
				break;
			}else{
				if(pix < pix_list[i]){
					pix_new = pix_list[i];
				}else{
					break;
				}
			}
		}
		if(pix_new == 0){
			pix_new  = pix_list[pix_list.length-1];
		}

		if(!pix_ok){
			pix = pix_new;
		}
		return pix;
	}

	//リセット用関数
	function clear_image() {
		location.reload();
		$(function() {
			$("body").scrollTop(0);
		});
	}

	//最小公倍数を求める関数
	function getMinPix(A,B){
		var Alist = divisor(A);
		var Blist = divisor(B);
		var commonList = new Array();

		var res = 1;
		for(var a=0; a<Alist.length ; a++){
			var anum = Alist[a];
			for(var b = 0 ; b< Blist.length ; b++){
				var bnum = Blist[b];
				if(anum == bnum){
					commonList.push(anum);
					break;
				}
			}
		}
		return commonList;

		//訳数一覧
		function divisor(num){
		    var results = [];
		    for(var i=1; i<=num; i++) {
		        if( (num%i == 0) & i!= 1 ) {
		            results.push(i);
		        }
		    }
		    return results;
		}
	}

	// Loadingイメージ表示関数
	function dispLoading(){
	    // ローディング画像が表示されていない場合のみ表示
	    if($("#loading").size() == 0){
	        $("body").append("<div id='loading'></div>");
	    }
	}

	// Loadingイメージ削除関数
	function removeLoading(){
	 $("#loading").remove();
	}


</script>

<style>
#baseimage8_img{
	width:150px;
}
.slidlabel{
	display:block;
	min-height:30px;
	width:130px;
	float:left;
}
.left_in{
    border-right: 10px double #b9b9b9;
    padding: 2px 10px;
    margin-right: 10px;
 }

 .btn_space{
   	height: 80px;
    margin-left: 200px;
    margin-top: 30px;
}

#loading{
    width:50px;
	height:50px;
    border:1px dashed #999;
    padding:15px;
    position: fixed;
    top:50%;
    left:50%;
    background-color:#FFF;
    filter: alpha(opacity=85);
    -moz-opacity:0.85;
    opacity:0.95;
    background-image:url("resources/images/load.gif");

}

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
							<input type="radio" name="imageselect_radio" data-name="base-01.png" id="baseimage1"></td>
						<td><label class="mosaic_baseimages" for="baseimage1">
								<img src="resources/images/base-01.png" id="baseimage1_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-02.png" id="baseimage2"></td>
						<td><label class="mosaic_baseimages" for="baseimage2">
								<img src="resources/images/base-02.png" id="baseimage2_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-03.png" id="baseimage3"></td>
						<td><label class="mosaic_baseimages" for="baseimage3">
								<img src="resources/images/base-03.png" id="baseimage3_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-04.png" id="baseimage4"></td>
						<td><label class="mosaic_baseimages" for="baseimage4">
								<img src="resources/images/base-04.png" id="baseimage4_img">
						</label></td>
					</tr>

					<tr>
						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-05.png" id="baseimage5"></td>
						<td><label for="baseimage5" class="mosaic_baseimages">
								<img src="resources/images/base-05.png" id="baseimage5_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-06.png" id="baseimage6"></td>
						<td><label class="mosaic_baseimages" for="baseimage6">
								<img src="resources/images/base-06.png" id="baseimage6_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="base-07.png" id="baseimage7"></td>
						<td><label class="mosaic_baseimages" for="baseimage7">
								<img src="resources/images/base-07.png" id="baseimage7_img">
						</label></td>

						<td class="td_check">
							<input type="radio" name="imageselect_radio" data-name="baseimage8" id="baseimage8"></td>
						<td>
							<form id="my_form" enctype="multipart/form-data">
								<input type="file" name="file" id="select_file" data-name="select_file" onchange="origin_image_send(this);">
							</form>

						<label class="mosaic_baseimages" for="baseimage8">
							</label>
							<div id="output"></div>
						</td>

					</tr>
				</table>
			</div>

<!-- 			<div id="flow2" class="mt20">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>素材選択
				</p>
				<form id="muiti_form" enctype="multipart/form-data">
				<input type="file" name="image_file" multiple="multiple" accept="image/*" id="upload_images">
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

 -->			<div id="flow4" class="mt20">
				<p class="mosaic_flow_title">
					<span class=mosaic_title_sqare></span>仕上がりサイズとモザイク密度指定
				</p>
				<div class="fl">
					<span class="slidlabel mt20">作成画像サイズ</span>
				</div>
				<div class="fl left_in">
						<input type="range" name="image_rate" id="rate"	min=-100 max=100  value="0" style="width:200px">
						<div>
						縦<input	type="number" min=10  id="height_input" class="photo_size_input ml15" > px
						横<input	type="number" min=10 id="width_input" class="photo_size_input ml15" > px
						</div>
				</div>
				<div class="fl mt20" >
					<span class="slidlabel">モザイクサイズ</span> <input type="range" name="mosaic_square" id="mosaic_sqare"	min=5 max=100 class="photo_size" value="5">
						<input	type="number" min=5 max=100 id="mosaic_sqare_input"	class="photo_size_input" value="5"> px四方
				</div>
			</div>
			<div style="clear:both"></div>
			<div id="flow5">
				<div class="btn_space">
					<form id="my_form" enctype="multipart/form-data">
						<span class="btndiv_1" onclick="generate()">作成</span>
					</form>
					<span class="btndiv_1" onclick="clear_image();">やり直し</span>
					<span id="save_btn"></span>
				</div>
				<div id="mosaic_create_window"></div>
				<div class="mt10 fr" style="height: 80px;">
					<img src="resources/images/facebooklogo.png" alt="facebook"	style="width: 30px" onclick="alert('FaceBookで共有する');">
					<img src="resources/images/twitter.png" alt="twitter" style="width: 30px" onclick="alert('Twitterで共有する');">
					<img src="resources/images/instagram.png" alt="instagram" style="width: 30px" onclick="alert('Instagramで共有する');">
				</div>
				<div class="clr"></div>
			</div>
		</div>
	</div>

</body>
</html>