package constants;
//propertiesクラス

public class Properties {

	// privateコンストラクタでインスタンス生成を抑止
	private Properties(){}
	// 定数
	public static final String temppath =// "C:/Users/Hashimoto/Documents/ProjectA/MosaicProject/src/main/webapp/resources/images/temp/";//アップ画像保存場所 変更可
									"C://GIT/画像保存";
	public static final String materialpath = //"C:/Users/Hashimoto/Documents/ProjectA/MosaicProject/src/main/webapp/resources/images/material/";//素材画像場所 変更可
												"C://GIT/素材保管";
	public static final String templatepath = "C://mosaic/template/";
	public static final String mozaicfolder = //"C:/Users/Hashimoto/Documents/ProjectA/MosaicProject/src/main/webapp/resources/mozaic/";
	"C://mosaic/";

	public static final int hsv_rate_fix = 40;	//今のところ50 がベスト？


}