package com.example.ProjectA;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.ProjectA.Model.ColorModel;

@Controller
public class MosaicController {

	ColorModel cm = new ColorModel();
	BufferedImage mateimage[] = null;// 素材画像読み込み用
	Object image[][];// 素材のpath、RGB
	int[][] red;// モザイク赤(1pixel)[縦][横]
	int[][] green;// モザイク緑(1pixel)[縦][横]
	int[][] blue;// モザイク青(1pixel)[縦][横]
	int[][] mozaicred;// モザイク指定コマ用[縦][横]
	int[][] mozaicgreen;// モザイク指定コマ用[縦][横]
	int[][] mozaicblue;// モザイク指定コマ用[縦][横]
	int mozaicsizeH;// 最終モザイク縦
	int mozaicsizeW;// 最終モザイク横
	int minpixH;// 1コマ最小ピクセル縦
	int minpixW;// 1コマ最小ピクセル横
	File file;// 画像read用
	File tempfile;// 画像read用
	String temppath = "C:/Users/Junji Kodama/Documents/ProjectA/MosaicProject/src/main/webapp/resources/images/temp/";// アップ画像保存場所 変更可
	String materialpath = "C:/Users/Junji Kodama/Documents/ProjectA/MosaicProject/src/main/webapp/resources/images/material/";// 素材画像場所 変更可
	String mozaicfolder = "C:/Users/Junji Kodama/Documents/ProjectA/MosaicProject/src/main/webapp/resources/mozaic/";
	String tempimagepath;
	String mozaicpath; // 戻り値画像パス

	// 画面遷移
	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		// System.out.println("test");
		return "mosaic";
	}

	// データ受け取り
	@RequestMapping(value = "/data", method = RequestMethod.POST)
	@ResponseBody
	public String data(@RequestBody String postdata) {
		String[] data = postdata.split("/");
		cm.setheight(Integer.parseInt(data[0]));
		cm.setwidth(Integer.parseInt(data[1]));
		cm.setminpix(Integer.parseInt(data[2]));
		return "true";
	}

	// アップロード処理オリジナル画像
	@RequestMapping(value = "/uporigin", method = RequestMethod.POST)
	public String uploadorigin(@RequestParam("file") MultipartFile imagefile, Model model) throws Exception {

		this.mozaicsizeH = cm.getheight();
		this.mozaicsizeW = cm.getwidth();

		this.minpixH = cm.getminpix();
		this.minpixW = cm.getminpix();

		// this.minpixH = 2;
		// this.minpixW = 2;

		// this.mozaicsizeW = 300;
		// this.mozaicsizeH = 300;

		BufferedImage readImage = null;

		this.file = new File(this.temppath + imagefile.getOriginalFilename());

		// 保存
		imagefile.transferTo(this.file);

		this.resize(this.mozaicsizeH, this.mozaicsizeW);

		this.tempfile = new File(this.tempimagepath);

		try {
			readImage = ImageIO.read(this.tempfile);
			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦
			this.red = new int[w][h];
			this.green = new int[w][h];
			this.blue = new int[w][h];
			int c;
			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {
					c = readImage.getRGB(x, y);
					this.red[x][y] = (c >> 16) & 0xFF;
					this.green[x][y] = (c >> 8) & 0xFF;
					this.blue[x][y] = c & 0xFF;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}
		this.generate();

		// return "mosaic";
		return this.mozaicpath;
	}

	// アップロード処理デフォルト
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	@ResponseBody
	public String upload(@RequestBody String path) {

		this.mozaicsizeH = cm.getheight();
		this.mozaicsizeW = cm.getwidth();

		this.minpixH = cm.getminpix();
		this.minpixW = cm.getminpix();

		// this.minpixH = 2;
		// this.minpixW = 2;

		// this.mozaicsizeW = 300;
		// this.mozaicsizeH = 300;

		BufferedImage readImage = null;

		this.file = new File(this.materialpath + path);

		this.resize(this.mozaicsizeH, this.mozaicsizeW);

		this.tempfile = new File(this.tempimagepath);

		try {
			readImage = ImageIO.read(this.tempfile);
			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦
			this.red = new int[w][h];
			this.green = new int[w][h];
			this.blue = new int[w][h];
			int c;
			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {
					c = readImage.getRGB(x, y);
					this.red[x][y] = (c >> 16) & 0xFF;
					this.green[x][y] = (c >> 8) & 0xFF;
					this.blue[x][y] = c & 0xFF;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}
		this.generate();

		return this.mozaicpath;
	}

	public void generate() {

		// 素材の画像データ解析
		this.materialdetail();

		// 指定されたぷくセルサイズが1コマとなるようにする
		this.divide(this.minpixH, this.minpixW);

		// 画像のマッチング
		this.matching();
	}

	// 材料の解析
	public void materialdetail() {
		File matefile = new File(materialpath);
		File files[] = matefile.listFiles();

		this.image = new Object[files.length][4];
		this.mateimage = new BufferedImage[files.length];

		for (int i = 0; i < files.length; i++) {
			try {
				int c = 0, r = 0, g = 0, b = 0;
				this.mateimage[i] = ImageIO.read(files[i]);
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
				this.image[i][0] = files[i];
				this.image[i][1] = r / (w * h);
				this.image[i][2] = g / (w * h);
				this.image[i][3] = b / (w * h);
			} catch (Exception e) {
				e.printStackTrace();
				mateimage = null;
			}
		}
	}


	// マッチング処理
	public void matching() {
		BufferedImage readImage = null;
		BufferedImage img = null;

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(this.tempfile);

			int w = readImage.getWidth() / this.minpixW; // 横
			int h = readImage.getHeight() / this.minpixH; // 縦
			int max = 255;
			int num = 0;

			img = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics grph = img.getGraphics();

			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {

					int r1 = this.mozaicred[x][y];
					int g1 = this.mozaicgreen[x][y];
					int b1 = this.mozaicblue[x][y];

					for (int i = 0; i < this.image.length; i++) {
						int r2 = (Integer) this.image[i][1];
						int g2 = (Integer) this.image[i][2];
						int b2 = (Integer) this.image[i][3];
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
					grph.drawImage(this.mateimage[num], x * this.minpixW, y * this.minpixH, this.minpixW, this.minpixH,
							null);
					max = 255;
				}
			}
			this.mozaicpath = this.mozaicfolder + "\\mozaic.png";
			ImageIO.write(img, "png", new File(this.mozaicpath));
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}
	}


	// 指定されたピクセル*ピクセルサイズに変更し保存
	public void resize(int w, int h) {
		BufferedImage readImage = null;
		BufferedImage img = null;

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(this.file);

			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics grph = img.getGraphics();

			grph.drawImage(readImage, 1, 1, w, h, null);

			this.tempimagepath = this.temppath + "temp.png";
			ImageIO.write(img, "png", new File(this.tempimagepath));
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}
	}


	// 指定されたピクセルサイズが1コマとなるようにする
	public void divide(int h, int w) {
		int red = 0;
		int green = 0;
		int blue = 0;
		this.mozaicred = new int[this.mozaicsizeW / w][this.mozaicsizeH / h];
		this.mozaicgreen = new int[this.mozaicsizeW / w][this.mozaicsizeH / h];
		this.mozaicblue = new int[this.mozaicsizeW / w][this.mozaicsizeH / h];

		int ycount = 1;
		int xcount = 1;

		// x方向に終わらせる
		for (int f = 0; f < this.mozaicsizeW; f = f + w) {

			// y方向に終わらせる
			for (int i = 0; i < this.mozaicsizeH; i = i + h) {

				for (int y = i; y < ycount * h; y++) {

					for (int x = f; x < xcount * w; x++) {
						red += this.red[x][y];
						green += this.green[x][y];
						blue += this.blue[x][y];
					}
				}
				this.mozaicred[xcount - 1][ycount - 1] = red / (h * w);
				this.mozaicgreen[xcount - 1][ycount - 1] = green / (h * w);
				this.mozaicblue[xcount - 1][ycount - 1] = blue / (h * w);
				red = 0;
				green = 0;
				blue = 0;
				ycount++;
			}
			ycount = 1;
			xcount++;
		}
	}

}
