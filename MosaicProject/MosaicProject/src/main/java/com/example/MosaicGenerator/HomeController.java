package com.example.MosaicGenerator;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.MosaicGenerator.Model.ColorModel;

import constants.MosaicMethods;
import constants.Properties;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	private String image_file_name ;

	//main_get
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		return "home";
	}

	// アップロード処理オリジナル画像：非同期
	@RequestMapping(value = "/uporigin", method = RequestMethod.POST)
	@ResponseBody
	public String uploadorigin(@RequestParam("file") MultipartFile imagefile, Model model) throws Exception {
		String res = "false";

		try{
			String temppath = Properties.temppath;
			res = imagefile.getOriginalFilename();
			String[] arr =  res.split("\\.");

			String kakucho = arr[(arr.length-1)];
			res = "origin_up." + kakucho;

			File file = new File(temppath + res);
			// 保存
			imagefile.transferTo(file);

		}catch(Exception e){
			res = "false";
			e.printStackTrace();
		}
		System.out.println("res:" + res);
		return  res;
	}

	// モザイク作成：非同期→作成後のファイルは/fileに作成
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	@ResponseBody
	public String upload(HttpServletResponse res,@RequestBody String body)throws IOException {
		String[] data = body.split("/");
		int height = 0;
		int width = 0;
		int h_pix = 0 ;
		int w_pix = 0 ;

		String selectid = null;
		String isOrigin = "false";

		int diff_fix = 0;
		//▼add m.hagihara 2017/02/19 重ね処理用追加
		int wrap_fix = 0;
		//▲add m.hagihara 2017/02/19 重ね処理用追加
		boolean isCuttype = false;

		try{
			selectid = data[0];
			height = Integer.parseInt(data[1]);
			width = Integer.parseInt(data[2]);
			h_pix = Integer.parseInt(data[3]);
			w_pix = Integer.parseInt(data[4]);
			isOrigin = data[5];
			diff_fix = Integer.parseInt(data[6]);
			if(data[7].equals("cut")){
				isCuttype = true;
			}
			//▼add m.hagihara 2017/02/19 重ね処理用追加
			wrap_fix = Integer.parseInt(data[8]);
			//▲add m.hagihara 2017/02/19 重ね処理用追加
		}catch(Exception e){
			e.printStackTrace();
		}

		BufferedImage readImage = null;
		File file = null;
		try{
			if(isOrigin.equals("true")){
				file = new File(Properties.temppath + selectid);
			}else{
				file = new File(Properties.templatepath + selectid);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		//確認用ログ
/*		System.out.println("height:" + height);
		System.out.println("width:" + width);
		System.out.println("heightpix:" + h_pix);
		System.out.println("widthpix:" + w_pix);
		System.out.println("selectid:" + selectid);
		System.out.println("diff_fix:" + diff_fix);
		System.out.println("cut:" + isCuttype);
*/
		//完成想定図の分析
		String return_path = "";
		//ファイルをリサイズ
		String resized_path = MosaicMethods.resize(width, height , file);

		//図のカラー情報
		ColorModel color = new ColorModel();

		int[][] red = null;
		int[][] green = null;
		int[][] blue = null;

		int[][] H = null;
		int[][] S = null;
		int[][] V = null;

		File tempfile = new File(resized_path);
		try {
			readImage = ImageIO.read(tempfile);
			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦
			red = new int[w][h];
			green = new int[w][h];
			blue = new int[w][h];

			H = new int[w][h];
			S = new int[w][h];
			V = new int[w][h];

			int c=0;
			int[] hsv = new int[3];
			int r=0,g=0,b=0;
			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {
					c = readImage.getRGB(x, y);

					r = (c >> 16) & 0xFF;
					g = (c >> 8) & 0xFF;
					b = c & 0xFF;
					red[x][y] = (c >> 16) & 0xFF;
					green[x][y] = (c >> 8) & 0xFF;
					blue[x][y] = c & 0xFF;
					hsv = MosaicMethods.RGB_to_HSV(r,g,b);
					H[x][y] = hsv[0];
					S[x][y] = hsv[1];
					V[x][y] = hsv[2];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}
		color.setRed(red);
		color.setGreen(green);
		color.setBlue(blue);
		color.setH(H);
		color.setS(S);
		color.setV(V);

		// 素材の画像データ解析ColorModel color = new ColorModel();
		Object[][] materiars =  MosaicMethods.materialdetail(isCuttype,w_pix,h_pix);

		// 完成図の1マスごとのカラー解析
		color = MosaicMethods.divide(color, w_pix, h_pix , width ,height);

		// 画像のマッチング
		return_path = matching_hsv(tempfile,w_pix,h_pix,color,materiars,diff_fix,isCuttype);

		//▼add m.hagihara 2017/02/19 重ね処理用追加
		if(wrap_fix > 0){
			Boolean success = MosaicMethods.wrap(tempfile, color, return_path, wrap_fix);
		}
		//▲add m.hagihara 2017/02/19 重ね処理用追加

		// 20170207 mod kodama
		// 画像を重ねる transparentは画面から取得する透過率。とりあえず50％
		//Boolean success = MosaicMethods.wrap(tempfile, color, return_path, 50);

		//はぎー追加 // 20170207 mod kodama
		/*
		if(data[8].equals("wrap")){

		}
		//元画像読み込み
		readImage = ImageIO.read(tempfile);
		int w_3 = readImage.getWidth();
		int h_3 = readImage.getHeight();
		BufferedImage writeImage = new BufferedImage(w_3, h_3, BufferedImage.TYPE_INT_ARGB);
		int r = 0, g = 0, b = 0, a = 0;
		int c=0;
		for (int y = 0; y < h_3; y++) {
			    for (int x = 0; x < w_3; x++) {
			    c = readImage.getRGB(x, y);
			    // イメージのアルファ値を元イメージの半分にする。
			    a = (c >> 24) & 0xff;
				r = (c >> 16) & 0xff;
				g = (c >> 8) & 0xff;
				b = c & 0xff;
			    int rgb = color.argb( a/2, r, g, b);
			    writeImage.setRGB(x, y, rgb);
			    }
		}
		String created_path_test = ""; //透過した画像の保存先
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHss");
		Date date = new Date();
		String str_date = sdf.format(date);
		created_path_test = Properties.mozaicfolder + "mosaic_test"+str_date+".png";
		ImageIO.write(writeImage, "png", new File(created_path_test));

	       画像の重ね合わせ
		BufferedImage img = ImageIO.read(new File(return_path)); // モザイク画像
		BufferedImage img2 = ImageIO.read(new File(created_path_test)); // 透過画像
		Graphics2D gr = img.createGraphics();
		gr.drawImage(img2,0,0,null);
		gr.dispose();
		//String created_path_wrap = "";
		//created_path_wrap = Properties.mozaicfolder + "mosaic_test"+str_date+".png";
		//ImageIO.write(img, "png", new File(created_path_wrap));
		// モザイク画像を上書き
		ImageIO.write(img, "png", new File(return_path));
		//this.image_file_name = created_path_test;
		//return_path = created_path_wrap;
		//はぎー追加
*/
		file1(res) ;
		return return_path;
	}

	//作成後ファイル
	@RequestMapping("/file1")
	@ResponseBody
    public void file1(HttpServletResponse res) throws IOException {
    	String IMAGE_FILE = "C:/mosaic/mosaic.png";
    	IMAGE_FILE =  this.image_file_name ;

    	System.out.println("file1:" + IMAGE_FILE);
    	File file = new File(IMAGE_FILE);
        res.setContentLength((int) file.length());
        res.setContentType(MediaType.ALL_VALUE);

        FileCopyUtils.copy(new FileInputStream(file), res.getOutputStream());
    }




	//以下使用メソッド

	// マッチング処理 HSV判定
	public String  matching_hsv(File file, int width ,int height ,ColorModel color ,Object[][] materiars, int diff_fix ,boolean isCuttype) {
		System.out.println("★matching2★" + width +"," + height );
		BufferedImage readImage = null;
		BufferedImage img = null;
		File matefile = new File(Properties.materialpath);
		File files[] = matefile.listFiles();
		String created_path = "";

		BufferedImage[] mateimage = new BufferedImage[files.length];

		for (int i = 0; i < files.length; i++) {
			try {
				mateimage[i] = ImageIO.read(files[i]);
			} catch (Exception e) {
				e.printStackTrace();
				mateimage = null;
			}
		}

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(file);
			//System.out.println( "file_path:" + file.getAbsolutePath());

			int w = readImage.getWidth() / width; // 横
			int h = readImage.getHeight() / height; // 縦
			int min = 0;
			int num = 0;
			//int roop = 0;
			img = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);

			//１マス
			Graphics grph = img.getGraphics();

			int last_roop = 0;
			// 縦
			int diff=0;
			for (int y = 0; y < h; y++) {
				// 横

				for (int x = 0; x < w; x++) {

					int r1 = color.getRed()[x][y];
					int g1 = color.getGreen()[x][y];
					int b1 = color.getBlue()[x][y];
					min = 0;

					//System.out.println(roop + ":");
					for (int i = last_roop; i < materiars.length + last_roop; i++) {
						int i2= i;
						if(i2 >= materiars.length){
							i2 = i-materiars.length;
						}

						int[] hsv_1 = MosaicMethods.RGB_to_HSV(r1,g1,b1);
						int[] hsv_2 = new int[3];
						hsv_2[0] = (Integer) materiars[i2][4];
						hsv_2[1] = (Integer) materiars[i2][5];
						hsv_2[2] = (Integer) materiars[i2][6];

						diff = MosaicMethods.Diff_HSV(hsv_1,hsv_2);

						if(min == 0){
							min = diff;
							num = i2;
							last_roop = i2;
						}

						else if(diff_fix!= 0 && diff<diff_fix){
							min = diff;
							num = i2;
							last_roop = i2;
							break;
						}else if(diff < min){
							min = diff;
							num = i2;
							last_roop = i2;
						}
					}
					BufferedImage thismate = mateimage[num];

					//カット指定の場合
					if(isCuttype){
						//素材サイズ
						int MH = thismate.getHeight();
						int MW = thismate.getWidth();

						//モザイクと素材の比率一致
						if(width / height  * MH == MW){
							grph.drawImage(thismate, x * width, y * height, width, height,null);
						}else{
							double scale_d = 0;
							double mosaic_rate = width/height;
							double mate_rate = MH/MW;

							//縦の余りを切る
							if( mosaic_rate >mate_rate ){
								scale_d = ((MW*1.0)/(width*1.0));
								if(scale_d * height > MH){
									scale_d = ((MH*1.0)/(height*1.0));
								}
							}
							//横の余りを切る
							else{
								scale_d = ((MH*1.0)/(height*1.0));
								if(scale_d * width > MW){
									scale_d = ((MW*1.0)/(width*1.0));
								}
							}
							int MH_2 = (int)Math.floor(height * scale_d);
							int MW_2 = (int)Math.floor(width * scale_d);

							int dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2;
							dx1 = x * width;
							dy1 = y * height;
							dx2 = dx1 + width;
							dy2 = dy1 + height;
							sx1 = 0;
							sy1 = 0;

							if(MW_2 != MW){
								sx1 = (MW-MW_2)/2;
							}
							if(MH_2 != MH){
								sy1 =  (MH-MH_2)/2;
							}
							sx2 = sx1 + MW_2;
							sy2 = sy1 + MH_2;
							grph.drawImage(thismate,dx1, dy1,dx2,dy2,sx1, sy1,sx2,sy2,null);
						}
					}
					//縮小指定の場合
					else{
						grph.drawImage(thismate, x * width, y * height, width, height,null);
					}
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHss");
			Date date = new Date();
			String str_date = sdf.format(date);
			created_path = Properties.mozaicfolder + "mosaic"+str_date+".png";
			ImageIO.write(img, "png", new File(created_path));
			this.image_file_name = created_path;
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}
		System.out.println("matching_end:" + created_path);

		return created_path;

	}

	// マッチング処理 RGB判定(未使用）
	public String  matching_rgb(File file, int width ,int height ,ColorModel color ,Object[][] materiars) {
		System.out.println("★matching");
		BufferedImage readImage = null;
		BufferedImage img = null;
		File matefile = new File(Properties.materialpath);
		File files[] = matefile.listFiles();
		String created_path = "";

		BufferedImage[] mateimage = new BufferedImage[files.length];

		for (int i = 0; i < files.length; i++) {
			try {
				mateimage[i] = ImageIO.read(files[i]);
//				System.out.println( "\t" +  files[i].getPath());
			} catch (Exception e) {
				e.printStackTrace();
				mateimage = null;
			}
		}

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(file);
			System.out.println( "file_path:" + file.getAbsolutePath());
			System.out.println(readImage.getWidth() + "/" + width);
			System.out.println(readImage.getHeight() + "/" + height);


			int w = readImage.getWidth() / width; // 横
			int h = readImage.getHeight() / height; // 縦
			int max = 255;
			int num = 0;

			img = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics grph = img.getGraphics();

			System.out.println("h:" + h);
			System.out.println("w:" + w );


			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {

					int r1 = color.getRed()[x][y];
					int g1 = color.getGreen()[x][y];
					int b1 = color.getBlue()[x][y];

					for (int i = 0; i < materiars.length; i++) {
						int r2 = (Integer) materiars[i][1];
						int g2 = (Integer) materiars[i][2];
						int b2 = (Integer) materiars[i][3];
						int dif = ((r2 - r1) ^ 2 + (g2 - g1) ^ 2 + (b2 - b1) ^ 2) ^ 1 / 2;
						int temp = dif;
						if (temp < 0) {
							temp = temp * -1;
						}
						if (temp < max) {
							max = temp;
							num = i;
						}
					}
					grph.drawImage(mateimage[num], x * width, y * height, width, height,
							null);
					max = 255;
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHss");
			Date date = new Date();
			String str_date = sdf.format(date);
			created_path = Properties.mozaicfolder + "mosaic"+str_date+".png";
			ImageIO.write(img, "png", new File(created_path));
			this.image_file_name = created_path;
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}
		System.out.println("matching_end:" + created_path);

		return created_path;

	}

}
