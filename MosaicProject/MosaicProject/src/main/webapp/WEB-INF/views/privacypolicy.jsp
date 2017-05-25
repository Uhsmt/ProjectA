<%--
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>

<link href="<c:url value="/resources/css/common.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/jquery-ui.css" />" rel="stylesheet">
<link rel="shortcut icon" href="resources/images/mosaic_logo.png">

<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"/>"></script>
<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.19/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/js/common.js" />"></script>
<script src='http://connect.facebook.net/ja_JP/sdk.js'></script>


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
.left_in2{
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
.baseimage8_tdspan{
    width: 200px;
    height: 35px;
    display: block;
    font-size: 14px;
    }


</style>

<title>MosaicAppli</title>
</head>

<body style="background-color: #9e9e9e;">
<div class="sp_part_top">
	<div class="sp_definition_list0">
	<table border="0" cellspacing="0" cellpadding="0" class="formTable800" style="margin-top: 10px;">
	<tr>
	<td colspan="2" align="center">当社はご利用者の氏名・メールアドレス等の個人情報の保護に関し、以下の取組みを実施しております。<br><br></td>
	</tr>
	<tr>
	<td class="ppTdLeft">1.</td>
	<td class="ppTdRight">当社は、個人情報に関する法令およびその他の規範を遵守し、ご利用者の大切な個人情報の保護に万全を尽くします。</td>
	</tr>
	<tr>
	<td class="ppTdLeft">2.</td>
	<td class="ppTdRight">当社は、ご利用者の個人情報については、下記の目的の範囲内で適正に取り扱いさせていただきます。</td>
	</tr>
	<tr>
	<td class="ppTdLeft">&nbsp;</td>
	<td class="ppTdRight"><table border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td class="ppTxtDot">・</td>
	<td>当サイト提供のコンテンツ、ツールを利用するために必要な手続きなどを行うこと</td>
	</tr>
	<tr>
	<td class="ppTxtDot">・</td>
	<td>電子メール、電話、郵送等各種媒体により、当社のサービスに関する販売推奨、アンケート調査および景品等の送付を行うこと</td>
	</tr>
	<tr>
	<td class="ppTxtDot">・</td>
	<td>当社のサービスの改善又は新たなサービスの開発を行うこと</td>
	</tr>
	<tr>
	<td class="ppTxtDot">・</td>
	<td>お問合せ、ご相談にお答えすること。なお、上記利用目的の他、サービス・アンケート等により個別に利用目的を定める場合があります。</td>
	</tr>
	</table></td>
	</tr>
	<tr>
	<td class="ppTdLeft">3.</td>
	<td class="ppTdRight">当社は、ご利用者の個人情報を適正に取扱うため、社内規程および社内管理体制の整備、従業員の教育、並びに、個人情報への不正アクセスや個人情報の紛失、破壊、改ざんおよび漏洩等防止に関する適切な措置を行い、また、その見直しを継続して図ることにより、個人情報の保護に努めてまいります。</td>
	</tr>
	<tr>
	<td class="ppTdLeft">4.</td>
	<td class="ppTdRight">当社は、ご利用者の個人情報については、上記利用目的を達成するため、業務委託先又は提携先に預託する場合があります。その場合は、個人情報の保護が十分に図られている企業を選定し、個人情報保護の契約を締結する等必要かつ適切な処置を実施いたします。なお、法令等に基づき裁判所・警察機関などの公的機関から開示の要請があった場合については、当該公的機関に提供することがあります。</td>
	</tr>
	<tr>
	<td class="ppTdLeft">5.</td>
	<td class="ppTdRight">当社では、ご利用者の個人情報の保護を図るために、また、法令その他の規範の変更に対応するために、プライバシーポリシーを改定する事があります。改定があった場合は当サイトにてお知らせいたします。</td>
	</tr>
	<tr>
	<td class="ppTdLeft">6.</td>
	<td class="ppTdRight">個人情報保護に関してご不明な点などございましたら、<a href="contact.html">お問い合わせページ</a>からご連絡いただくか、下記の当社窓口までご連絡いただければ、合理的な範囲で速やかに対応いたします。</td>
	</tr>
	</table>
	<br>
	</div><!-- sp_definition_list0 -->
	<img src="images/privacypolicy/privacypolicy_tel.jpg" class="sp_image" alt="個人情報保護に関するお問い合わせ：株式会社ラヴィコ（個人情報お問い合わせ窓口）03-3861-3932" /><br>
</div><!-- sp_part_top -->
v>
</body>
</html>