package controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MosaicController {

	@RequestMapping(value = "/mosaic", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		System.out.println("Controller.test");
		model.addAttribute("hello","こんにちは！");

		return "mosaic";
	}

}
