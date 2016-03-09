package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import dao.ImageInfoDao;
import bean.ImageInfo;

@Controller
public class RootController {

	@Autowired
	ImageInfoDao dao;
	@RequestMapping("/")
	public String welcome(@ModelAttribute("model") ModelMap model){
		List<ImageInfo> infos=new ArrayList<ImageInfo>();
		infos=dao.getPresentImageInfos(0);
		model.addAttribute("infos",infos);
		return "index";
	}
}
