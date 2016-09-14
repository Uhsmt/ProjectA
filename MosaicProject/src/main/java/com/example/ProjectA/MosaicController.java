package com.example.ProjectA;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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

	Object image[][]; 				// 素材
	Object mozaic[][]; 			// モザイク
	int[][] red; 					// モザイク赤
	int[][] green;					// モザイク緑
	int[][] blue;					// モザイク青

	String mozaicpath = null; 		// 戻り値画像パス

	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		System.out.println("test");
		return "mosaic";
	}

	@RequestMapping(value = "/mosaic_generate", method = RequestMethod.POST)
	@ResponseBody
	public String generate(@RequestBody String materialpath) {

		// 素材の画像データ配列処理
		this.materialdetail(materialpath);

		// 画像のマッチング処理
		this.matching(materialpath);

		// 作成されたモザイクフォトのパス
		return mozaicpath;
	}

	// 材料の処理
	public void materialdetail(String materialpath) {
//		Map<Object, Object> red = new HashMap<Object, Object>();
//		Map<Object, Object> green = new HashMap<Object, Object>();
//		Map<Object, Object> blue = new HashMap<Object, Object>();

		BufferedImage readImage = null;

		File file = new File(materialpath);
		File files[] = file.listFiles();

		// デバッグ用
//		for (int i = 0; i < files.length; i++) {
//			System.out.println(files[i]);
//			System.out.println("jsから送られた引数：" + materialpath);
//		}

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
//				red.put("red", r / (w * h));
//				green.put("green", g / (w * h));
//				blue.put("blue", b / (w * h));
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

	public void matching(String materialpath) {

		// TODOマッチング

		BufferedImage p1 = null;
		BufferedImage img = null;
		BufferedImage shrink = null;
		Graphics2D g2d = null;

		// 画像作成＆保存
		try {
			p1 = ImageIO.read(new FileInputStream(image[0][0].toString()));

			// 引数にコマ割り数横、コマ割り数縦を指定
			img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			g.drawImage(p1, 0, 0, null);
			g.drawImage(p1, 0, p1.getHeight(), null);
			g.drawImage(p1, p1.getWidth(), 0, null);
			g.drawImage(p1, p1.getWidth(), p1.getHeight(), null);

			// 元の大きさに戻す
			shrink = new BufferedImage(p1.getWidth(), p1.getHeight(), p1.getType());
			g2d = shrink.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g2d.drawImage(img, 0, 0, p1.getHeight(), p1.getHeight(), null);

			mozaicpath = materialpath + "\\mozaic.png";

			ImageIO.write(shrink, "png", new File(mozaicpath));
		} catch (Exception e) {
			e.printStackTrace();
			p1 = null;
			img = null;
		} finally {
			p1 = null;
			img = null;
		}
	}

	// アップロード処理
	@RequestMapping(value = "/up", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile imagefile, Model model) throws Exception {

		String mozaicpath = "C:/temp/";   //アップ画像保存場所
		BufferedImage readImage = null;

		// 保存
		imagefile.transferTo(new File(mozaicpath + imagefile.getOriginalFilename()));

		// モザイク解析
		File file = new File(mozaicpath);
		File files[] = file.listFiles();

		// デバッグ用
//		for (int i = 0; i < files.length; i++) {
//			System.out.println(files[i]);
//		}

//		mozaic = new Object[files.length][4];

		for (int i = 0; i < files.length; i++) {
			try {
				readImage = ImageIO.read(files[i]);
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
		}
		System.out.println("ファイル");
		return "mosaic";
	}
}
