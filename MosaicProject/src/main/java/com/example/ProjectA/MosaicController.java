package com.example.ProjectA;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MosaicController {
	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String tomosaic(Locale locale, Model model) {
		System.out.println("test");
		return "mosaic";
	}


	@RequestMapping(value = "/mosaic_generate", method = RequestMethod.POST)
	@ResponseBody
    public String generate(@RequestBody String body) {

		System.out.println("jsから送られた引数：" + body);

		return "testMessage";
    }

}
