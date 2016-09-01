package com.example.ProjectA;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ProjectA.Model.ColorModel;

@Controller
public class MosaicController {
	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		System.out.println("test");
		return "mosaic";
	}

	// 変数
	Object image[][];
	String mozaicpath = null;

	ColorModel CM = new ColorModel();

	@RequestMapping(value = "/mosaic_generate", method = RequestMethod.POST)
	@ResponseBody
	public String generate(@RequestBody String materialpath) {

		// 素材の画像データ配列処理
		this.materialdetail(materialpath);

		// 画像のマッチング処理
		this.matching();

		// 作成されたモザイクフォトのパス
		return mozaicpath;
	}

	//材料の処理
	public void materialdetail(String materialpath) {
		Map<Object, Object> red = new HashMap<Object, Object>();
		Map<Object, Object> green = new HashMap<Object, Object>();
		Map<Object, Object> blue = new HashMap<Object, Object>();

		BufferedImage readImage = null;

		File file = new File(materialpath);
		File files[] = file.listFiles();

		// デバッグ用
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i]);
			System.out.println("jsから送られた引数：" + materialpath);
		}
		image = new Object[files.length][4];
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
				red.put("red", r / (w * h));
				green.put("green", g / (w * h));
				blue.put("blue", b / (w * h));
				image[i][0] = files[i];
				image[i][1] = red;
				image[i][2] = green;
				image[i][3] = blue;
			} catch (Exception e) {
				e.printStackTrace();
				readImage = null;
			}
		}
	}

	public void matching() {

	}

}
