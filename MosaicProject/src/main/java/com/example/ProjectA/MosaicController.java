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

@Controller
public class MosaicController {

	Object image[][]; // 素材
	Object mozaic[][]; // モザイク
	int[][] red; // モザイク赤
	int[][] green; // モザイク緑
	int[][] blue; // モザイク青
	BufferedImage mateimage[] = null;

	File file;

	// アップ画像保存場所 変更可
	String originpath = "C:/temp/";
	// 素材画像場所 変更可
	String materialpath = "C:/Material/";

	String mozaicpath = null; // 戻り値画像パス

	// 画面遷移
	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		//System.out.println("test");
		return "mosaic";
	}

	// アップロード処理オリジナル画像
	@RequestMapping(value = "/uporigin", method = RequestMethod.POST)
	public String uploadorigin(@RequestParam("file") MultipartFile imagefile, Model model) throws Exception {

		BufferedImage readImage = null;

		this.file = new File(this.originpath + imagefile.getOriginalFilename());

		// 保存
		imagefile.transferTo(this.file);

		try {
			readImage = ImageIO.read(this.file);
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
					this.blue[x][y] = c  & 0xFF;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}

		this.generate();

		//System.out.println(mozaicpath);
		return "mosaic";
	}

	// アップロード処理デフォルト
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	@ResponseBody
	public String upload(@RequestBody String path) {

		BufferedImage readImage = null;

		this.file = new File(this.materialpath + path);

		try {
			readImage = ImageIO.read(this.file);
			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦
			red = new int[w][h];
			green = new int[w][h];
			blue = new int[w][h];
			int c;
			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {
					c = readImage.getRGB(x, y);

					red[x][y] = (c >> 16) & 0xFF;
					green[x][y] = (c >> 8) & 0xFF;
					blue[x][y] = c  & 0xFF;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}

		this.generate();

		//System.out.println("mozaicpath");
		return "mosaic";
	}

	public void generate() {

		// 素材の画像データ解析
		this.materialdetail();

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
			readImage = ImageIO.read(this.file);

			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦
			int max = 255;
			int num = 0;

			img = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics grph = img.getGraphics();

			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {

					int r1 = this.red[x][y];
					int g1 = this.green[x][y];
					int b1 = this.blue[x][y];

					for (int i = 0; i < this.image.length; i++){
						int r2 = (Integer) this.image[i][1];
						int g2 = (Integer) this.image[i][2];
						int b2 = (Integer) this.image[i][3];
						int dif = ((r2-r1)^2 + (g2-g1)^2 + (b2-b1)^2)^1/2;
						int temp = dif;
						if (temp < 0){
							temp = temp * -1;
						}
						if (temp < max){
							max = temp;
							num = i;
						}
					}
					grph.drawImage(this.mateimage[num], x , y,1,1, null);
					max = 255;
				}
			}
			mozaicpath = materialpath + "\\mozaic.png";
			ImageIO.write(img, "png", new File(mozaicpath));
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
		} finally {
			readImage = null;
			img = null;
		}
	}
}
