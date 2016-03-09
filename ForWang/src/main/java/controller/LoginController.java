package controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import dao.LoginDao;

@Controller
public class LoginController {
	//必须
	@Autowired
	LoginDao dao;
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@RequestParam String username,@RequestParam String password,HttpServletRequest request){
		
		int tag=dao.login(username, password);
		if(tag>-1)
		{
			request.getSession().setAttribute("user_role", tag);
			request.getSession().setAttribute("user_name", username);
			return "redirect:/index";
		}
		
		return "login";
	}
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(){
		
		return "login";
	}
}
