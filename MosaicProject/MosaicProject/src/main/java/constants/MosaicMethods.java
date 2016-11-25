package constants;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.example.MosaicGenerator.Model.ColorModel;

public class MosaicMethods {

	//以下作成用関数


	// 材料の解析(素材リストの作成）
	public static Object[][] materialdetail(boolean isCuttype, int width, int height) {
		System.out.println("★materiarl_detail");
		//Generate元の画像リスト
		File matefile = new File(Properties.materialpath);
		File files[] = matefile.listFiles();

		BufferedImage mateimage[] = new BufferedImage[files.length] ;// 素材画像読み込み用
		Object images[][] = new Object[files.length][7];// 素材の0path、1R2G3B,4h5s6v


		for (int i = 0; i < files.length; i++) {
			try {
				int c = 0, r = 0, g = 0, b = 0;
				BufferedImage thismate = ImageIO.read(files[i]);
				mateimage[i] = thismate;

				int w = thismate.getWidth();
				int h = thismate.getHeight();

				//Cuttype=Cutの時は、配置用のトリミングと合わせて分析範囲を変更
				int x_start = 0;
				int y_start= 0;
				int h_2 = h;
				int w_2 = w;

				if(isCuttype ){
					//素材サイズ
					//モザイクと素材の比率一致
					double mate_rate = w/h;
					double mosaic_rate = width/height;

					if( mosaic_rate != mate_rate){
						double scale_d = 0;

						//縦の余りを切る
						if( mosaic_rate >mate_rate ){
							scale_d = (w*1.0)/(width*1.0);
							if(scale_d * height > h){
								scale_d = (h*1.0)/(height*1.0);
							}
						}
						//横の余りを切る
						else{
							scale_d = (h*1.0)/(height*1.0);
							if(scale_d * width > w){
								scale_d = ((w*1.0)/(width*1.0));
							}
						}
						h_2 = (int)Math.floor(height * scale_d);
						w_2 = (int)Math.floor(width * scale_d);

						if(w_2 != w){
							x_start = (w - w_2)/2;
						}
						if(h_2 != h){
							y_start = (h - h_2)/2;
						}
						w_2 += x_start;
						h_2 += y_start;
					}
				}
				for (int y = y_start; y < h_2  ; y++) {
					for (int x = x_start ; x < w_2  ; x++) {
						try{
							c = thismate.getRGB(x, y);
							r += c >> 16 & 0xff;
							g += c >> 8 & 0xff;
							b += c & 0xff;

						}catch(Exception e){
							System.out.println(x +"," +y );
							e.printStackTrace();
							break;
						}
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

	//RGBからHSVに変換
	public static int[] RGB_to_HSV(int r,int g,int b){
		int[] hsv = new int[3];

		double r_d = (double)r/255;
		double g_d = (double)g/255;
		double b_d = (double)b/255;

		double max =  CommonMethods.maxValue(r_d,g_d,b_d);
		double min =  CommonMethods.minValue(r_d,g_d,b_d);
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
		return hsv;
	}

	//近似距離計算（HSV）
	public static int Diff_HSV(int[] c1,int[] c2){
		int diff = 0;
		int hue = 0;
		if(c1[0]>c2[0]){
			hue = CommonMethods.minValue(c1[0] - c2[0],c2[0]-c1[0] + 360);
		}else{
			hue =  CommonMethods.minValue(c2[0] - c1[0],c1[0]-c2[0] + 360);
		}

		diff = (int) ( Math.pow(hue,2) + Math.pow((c1[1]-c2[1]), 2) + Math.pow((c1[2]-c2[2]),2)  );
		diff = (int) Math.sqrt(diff);

		return diff;
	}

	// 指定されたピクセル*ピクセルサイズに変更し保存（変換後のパスを返す）
	public static String resize(int w, int h , File file) {
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
	public static ColorModel divide(ColorModel mosaic,int mosaic_w , int mosaic_h,int w, int h ) {
		System.out.println("★divide");
		int red = 0;
		int green = 0;
		int blue = 0;
		int[][] mozaicred = null;
		int[][] mozaicgreen = null;
		int[][] mozaicblue = null;
		ColorModel res = new ColorModel();

		try{
			mozaicred = new int[w / mosaic_w][h / mosaic_h];
			mozaicgreen = new int[w / mosaic_w][h / mosaic_h];
			mozaicblue = new int[w / mosaic_w][h / mosaic_h];

			int ycount = 1;
			int xcount = 1;
			for (int f = 0; f < w-1; f = f + mosaic_w) {
				for (int i = 0; i < h-1; i = i + mosaic_h) {
					for (int y = i; y <  (ycount * mosaic_h)-1; y++) {
						for (int x = f; x <( xcount * mosaic_w)-1; x++) {
							try{
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
						mozaicred[xcount-1][ycount-1] = red / (mosaic_h * mosaic_w);
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

		}catch(Exception e){
			e.printStackTrace();
		}
		res.setBlue(mozaicblue);
		res.setGreen(mozaicgreen);
		res.setRed(mozaicred);

		return res;

	}


}
