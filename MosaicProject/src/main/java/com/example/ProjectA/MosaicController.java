package com.example.ProjectA;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

	File file;

	// アップ画像保存場所 変更可
	String originpath = "C:/temp/";
	// 素材画像場所 変更可
	String materialpath = "C:/Material/";

	String mozaicpath = null; // 戻り値画像パス

	// 画面遷移
	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		System.out.println("test");
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
			red = new int[w][h];
			green = new int[w][h];
			blue = new int[w][h];
			int c;
			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {
					c = readImage.getRGB(x, y);

					red[x][y] = c >> 16 & 0xff;
					green[x][y] = c >> 8 & 0xff;
					blue[x][y] = c & 0xff;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}

		this.generate();

		System.out.println(mozaicpath);
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

					red[x][y] = c >> 16 & 0xff;
					green[x][y] = c >> 8 & 0xff;
					blue[x][y] = c & 0xff;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		} finally {
			readImage = null;
		}

		this.generate();

		System.out.println("mozaicpath");
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
		BufferedImage readImage = null;
		File matefile = new File(materialpath);
		File files[] = matefile.listFiles();

		this.image = new Object[files.length][4];
		for (int i = 0; i < files.length; i++) {
			try {
				int c = 0, r = 0, g = 0, b = 0;
				readImage = ImageIO.read(files[i]);
				int w = readImage.getWidth();
				int h = readImage.getHeight();
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						c = readImage.getRGB(x, y);
						r += c >> 16 & 0xff;
						g += c >> 8 & 0xff;
						b += c & 0xff;
					}
				}
				// 画像一枚の平均色
				image[i][0] = files[i];
				image[i][1] = r / (w * h);
				image[i][2] = g / (w * h);
				image[i][3] = b / (w * h);
			} catch (Exception e) {
				e.printStackTrace();
				readImage = null;
			} finally {
				readImage = null;
			}
		}
	}

	// マッチング処理
	public void matching() {
		BufferedImage readImage = null;
		BufferedImage tempimage = null;
		BufferedImage img = null;
		BufferedImage shrink = null;
		Graphics2D g2d = null;

		// 画像作成＆保存
		try {
			readImage = ImageIO.read(this.file);

			int w = readImage.getWidth(); // 横
			int h = readImage.getHeight(); // 縦

			// 引数にコマ割り数横、コマ割り数縦を指定
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			Graphics g = img.getGraphics();

			// 縦
			for (int y = 0; y < h; y++) {
				// 横
				for (int x = 0; x < w; x++) {

					int temp = red[x][y];
					int temp1 = green[x][y];
					int temp2 = blue[x][y];

					//tempimage = ImageIO.read();//近似値の画像パス
							//近似値の画像
					g.drawImage(tempimage, y, x, null);

				}
			}
			g.drawImage(readImage, 0, readImage.getHeight(), null);
			g.drawImage(readImage, readImage.getWidth(), 0, null);
			g.drawImage(readImage, readImage.getWidth(), readImage.getHeight(), null);

			// 元の大きさに戻す
			shrink = new BufferedImage(readImage.getWidth(), readImage.getHeight(), readImage.getType());
			g2d = shrink.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g2d.drawImage(img, 0, 0, readImage.getHeight(), readImage.getHeight(), null);

			mozaicpath = materialpath + "\\mozaic.png";

			ImageIO.write(shrink, "png", new File(mozaicpath));
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
			img = null;
			shrink = null;
			g2d = null;
		} finally {
			readImage = null;
			img = null;
			shrink = null;
			g2d = null;
		}
	}
}
