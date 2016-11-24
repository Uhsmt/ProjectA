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

import com.example.MosaicGenerator.Model.MosaicModel;

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



	// アップロード処理オリジナル画像
	@RequestMapping(value = "/uporigin", method = RequestMethod.POST)
	@ResponseBody
	public String uploadorigin(@RequestParam("file") MultipartFile imagefile, Model model) throws Exception {
		System.out.println("uporigin::\r\n");

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

	// モザイク作成
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	@ResponseBody
	public String upload(HttpServletResponse res,@RequestBody String body)throws IOException {
		System.out.println("up://" + body);

		String[] data = body.split("/");
		int height = 0;
		int width = 0;
		int h_pix = 0 ;
		int w_pix = 0 ;

		String selectid = null;
		String isOrigin = "false";

		int diff_fix = 0;
		boolean isCuttype = false;

		try{
			selectid = data[0];
			height = Integer.parseInt(data[1]);
			width = Integer.parseInt(data[2]);
			h_pix = Integer.parseInt(data[3]);
			w_pix = Integer.parseInt(data[4]);
			isOrigin = data[5];
			diff_fix = Integer.parseInt(data[6]);
			if(data[7] == "cut"){
				isCuttype = true;
			}
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
			System.out.println(file.getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("height:" + height);
		System.out.println("width:" + width);
		System.out.println("heightpix:" + h_pix);
		System.out.println("widthpix:" + w_pix);
		System.out.println("selectid:" + selectid);
		System.out.println("diff_fix:" + diff_fix);
		System.out.println("cut:" + isCuttype);

		String return_path = "";
		String resized_path = resize(width, height , file);
		System.out.println("resized_path:" + resized_path);
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
//					System.out.println("\t" +x + "-"+ y+ ":" + r + "," + g + "," +b);
					red[x][y] = (c >> 16) & 0xFF;
					green[x][y] = (c >> 8) & 0xFF;
					blue[x][y] = c & 0xFF;
					hsv = RGB_to_HSV(r,g,b);
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

		//モザイクカラー情報
		MosaicModel mosaic = new MosaicModel();
		mosaic.setRed(red);
		mosaic.setGreen(green);
		mosaic.setBlue(blue);
		mosaic.setH(H);
		mosaic.setS(S);
		mosaic.setV(V);

		// 素材の画像データ解析
		Object[][] materiars =  materialdetail();

		// 1マスごとのカラー解析
		mosaic = divide(mosaic, w_pix, h_pix , width ,height);
		// 画像のマッチング
		return_path = matching2(tempfile,w_pix,h_pix,mosaic,materiars,diff_fix);

		file1(res) ;
		return return_path;
	}



	//以下作成用関数


	// 材料の解析(素材リストの作成）
	public Object[][] materialdetail() {
		System.out.println("★materiarl_detail");
		//Generate元の画像リスト
		File matefile = new File(Properties.materialpath);
		File files[] = matefile.listFiles();

		BufferedImage mateimage[] = new BufferedImage[files.length] ;// 素材画像読み込み用
		Object images[][] = new Object[files.length][7];// 素材の0path、1R2G3B,4h5s6v


		for (int i = 0; i < files.length; i++) {
			try {
				int c = 0, r = 0, g = 0, b = 0;
				mateimage[i] = ImageIO.read(files[i]);
				int w = mateimage[i].getWidth();
				int h = mateimage[i].getHeight();

				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						c = mateimage[i].getRGB(x, y);
						r += c >> 16 & 0xff;
						g += c >> 8 & 0xff;
						b += c & 0xff;
					}
				}
				// 画像一枚の平均色
				images[i][0] = files[i];
				r = r / (w * h);
				g = g / (w * h);
				b = b / (w * h);

				images[i][1] = r ;
				images[i][2] = g ;
				images[i][3] = b ;
				int[] hsv = RGB_to_HSV(r,g,b);
/*				System.out.print( "\t" + r + "," + g + "," + b  + "⇒");
				System.out.println(  hsv[0] + "," + hsv[1] + "," + hsv[2]);
*/
				images[i][4] = hsv[0];
				images[i][5] = hsv[1];
				images[i][6] = hsv[2];

			} catch (Exception e) {
				e.printStackTrace();
				mateimage = null;
			}
		}
		return images;
	}

	public int[] RGB_to_HSV(int r,int g,int b){
		int[] hsv = new int[3];
//		System.out.print( "\t" + r + "," + g + "," + b  + "⇒");

		double r_d = (double)r/255;
		double g_d = (double)g/255;
		double b_d = (double)b/255;

		double max = maxValue(r_d,g_d,b_d);
		double min = minValue(r_d,g_d,b_d);
		int H = 0;
		int S = 0;
		int V = 0;

		if(max == min){
			H = 0;
		}else if(max == r_d){
			H =  (int)(60 * (g_d-b_d)/(max-min)+360);
			H = H-(H/360);
		}else if(max == g_d){
			H = (int) (60*(b_d-r_d)/(max-min));
			H = H + 120;
		}else if(max == b_d){
			H = (int) (60*(r_d-g_d)/(max-min));
			H = H + 240;
		}
		if(max==0){
			S = 0;
		}else{
			S = (int) ((1- (min/max))*100);
		}
		V = (int) (max * 100);

		hsv[0] = H;
		hsv[1] = S;
		hsv[2] = V;

//		System.out.println(  H + "," + S + "," + V);
		return hsv;
	}


	// マッチング処理
	public String  matching(File file, int width ,int height ,MosaicModel mosaic ,Object[][] materiars) {
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

					int r1 = mosaic.getMozaicred()[x][y];
					int g1 = mosaic.getMozaicgreen()[x][y];
					int b1 = mosaic.getMozaicblue()[x][y];

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

	// マッチング処理2 HSV判定
	public String  matching2(File file, int width ,int height ,MosaicModel mosaic ,Object[][] materiars, int diff_fix) {
		System.out.println("★matching2★");
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
			int min = 0;
			int num = 0;
			//int roop = 0;
			img = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics grph = img.getGraphics();

			System.out.println("h:" + h);
			System.out.println("w:" + w );


			int last_roop = 0;
			// 縦
			int diff=0;
			for (int y = 0; y < h; y++) {
				// 横

				for (int x = 0; x < w; x++) {

					int r1 = mosaic.getMozaicred()[x][y];
					int g1 = mosaic.getMozaicgreen()[x][y];
					int b1 = mosaic.getMozaicblue()[x][y];
					min = 0;

					//System.out.println(roop + ":");
					for (int i = last_roop; i < materiars.length + last_roop; i++) {
						int i2= i;
						if(i2 >= materiars.length){
							i2 = i-materiars.length;
						}

						int[] hsv_1 = RGB_to_HSV(r1,g1,b1);
						int[] hsv_2 = new int[3];
						hsv_2[0] = (Integer) materiars[i2][4];
						hsv_2[1] = (Integer) materiars[i2][5];
						hsv_2[2] = (Integer) materiars[i2][6];

						diff = Diff_HSV(hsv_1,hsv_2);

						if(min == 0){
							min = diff;
							num = i2;
							last_roop = i2;
							//System.out.println("\t\t○○min==0");
						}

						else if(diff_fix!= 0 && diff<diff_fix){
							min = diff;
							num = i2;
							last_roop = i2;
							//System.out.println("\t\t"+num+"★★" + diff + "<=" + min);
							break;
						}else if(diff < min){
							min = diff;
							num = i2;
							last_roop = i2;
							//System.out.println("\t\t"+num+"○○" + diff + "<=" + min);
						}else{
							//System.out.println("\t\t××" + diff + ">=" + min);
						}
						//roop += 1;
					}

					//System.out.println(roop + ":" + num + "("+materiars[num][0]+") \t min:" + min );
					grph.drawImage(mateimage[num], x * width, y * height, width, height,
							null);
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

	public int Diff_HSV(int[] c1,int[] c2){
		int diff = 0;
		int hue = 0;
		//folding "H" dimension
		if(c1[0]>c2[0]){
			hue = minValue(c1[0] - c2[0],c2[0]-c1[0] + 360);
		}else{
			hue = minValue(c2[0] - c1[0],c1[0]-c2[0] + 360);
		}

		diff = (int) ( Math.pow(hue,2) + Math.pow((c1[1]-c2[1]), 2) + Math.pow((c1[2]-c2[2]),2)  );

		diff = (int) Math.sqrt(diff);

//		System.out.println("\t\t" + c1[0] + "," + c1[1] + "," + c1[2] + ":" + c2[0] + "," + c2[1] + "," + c2[2] + "⇒" + diff);

		return diff;
	}

	// 指定されたピクセル*ピクセルサイズに変更し保存
	public String resize(int w, int h , File file) {
		System.out.println("★resize");
		BufferedImage readImage = null;
		BufferedImage img = null;

		String res = "";

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(file);

			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics grph = img.getGraphics();

			grph.drawImage(readImage, 1, 1, w, h, null);

			String tempimagepath = Properties.temppath + "temp.png";
			ImageIO.write(img, "png", new File(tempimagepath));
			res = tempimagepath;
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}

		return res;
	}


	// 指定されたピクセルサイズが1コマとなるようにする
	public MosaicModel divide(MosaicModel mosaic,int mosaic_w , int mosaic_h,int w, int h ) {
		System.out.println("★divide");
		int red = 0;
		int green = 0;
		int blue = 0;
		int[][] mozaicred = null;
		int[][] mozaicgreen = null;
		int[][] mozaicblue = null;
		MosaicModel res = new MosaicModel();

/*		System.out.println("mosaic_h:" + mosaic_h + ":(i=0 = i=;)" + h );
		System.out.println("mosaic_w:" + mosaic_w + ":(f=0 ～、f=+)"+w);
		System.out.println("mosaic_length:" + mosaic.getRed().length + "," + mosaic.getRed()[0].length);
*/
		try{
			mozaicred = new int[w / mosaic_w][h / mosaic_h];
			mozaicgreen = new int[w / mosaic_w][h / mosaic_h];
			mozaicblue = new int[w / mosaic_w][h / mosaic_h];

			int ycount = 1;
			int xcount = 1;
//			System.out.println("---roop_count----");
			// x方向に終わらせる
			for (int f = 0; f < w-1; f = f + mosaic_w) {
//				System.out.println("f:" + f + "(0～"+w+"),f+"+mosaic_w+")");

				for (int i = 0; i < h-1; i = i + mosaic_h) {
//					System.out.println("\ti:" + i + "(0～"+h+"),i+"+mosaic_h+")");

					for (int y = i; y <  (ycount * mosaic_h)-1; y++) {
//						System.out.println("\t\ty:" + y + "("+i+"～"+(ycount * mosaic_h -1)+"),y++)");

						for (int x = f; x <( xcount * mosaic_w)-1; x++) {
//							System.out.println("\t\t\tx:" + x + "("+f+"～"+(xcount * mosaic_w -1 )+")-1,x++)");

							try{
//								System.out.println("\t\t\tred:x:" + x +",y:" + y);
								red += mosaic.getRed()[x][y];
								green += mosaic.getGreen()[x][y];
								blue += mosaic.getBlue()[x][y];
							}catch(Exception e){
								System.out.println("x:" + x +",y:" + y);
								e.printStackTrace();

							}
						}
					}
					try{
/*						System.out.println("\t\t\tred\t:["+xcount+"-1]["+ycount+"-1]="+red+"/("+mosaic_h+"* "+mosaic_w+")" );
						System.out.println("\t\t\tgreen\t:["+xcount+"-1]["+ycount+"-1]="+green+"/("+mosaic_h+"* "+mosaic_w+")" );
						System.out.println("\t\t\tblue\t:["+xcount+"-1]["+ycount+"-1]="+blue+"/("+mosaic_h+"* "+mosaic_w+")" );
*/						mozaicred[xcount-1][ycount-1] = red / (mosaic_h * mosaic_w);
						mozaicgreen[xcount-1 ][ycount-1] = green / (mosaic_h * mosaic_w);
						mozaicblue[xcount-1][ycount-1] = blue / (mosaic_h * mosaic_w);
					}catch(Exception e){
						e.printStackTrace();
					}
					red = 0;
					green = 0;
					blue = 0;
					ycount++;
				}
				ycount = 1;
				xcount++;
			}
//			System.out.println("---roop_count----");


		}catch(Exception e){
			e.printStackTrace();
		}
		res.setMozaicblue(mozaicblue);
		res.setMozaicgreen(mozaicgreen);
		res.setMozaicred(mozaicred);

		return res;

	}

	public static int maxValue(int a, int b, int c) {
		int max = a;
		if (max < b) max = b;
		if (max < c) max = c;
		return max;
	}

	public static int minValue(int a, int b, int c) {
		int min = a;
		if (min > b) min = b;
		if (min > c) min = c;

		return min;
	}


	public static double maxValue(double a, double b, double c) {
		double max = a;
		if (max < b) max = b;
		if (max < c) max = c;
		return max;
	}

	public static double minValue(double a, double b,double c) {
		double min = a;
		if (min > b) min = b;
		if (min > c) min = c;

		return min;
	}


	public static int maxValue(int a, int b) {
		int max = a;
		if (max < b) max = b;
		return max;
	}

	public static int minValue(int a, int b) {
		int min = a;
		if (min > b) min = b;
		return min;
	}


}
