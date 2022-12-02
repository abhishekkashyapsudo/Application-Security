package com.nagp.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nagp.security.models.UserDto;
import com.nagp.security.util.CaptchaUtil;

import cn.apiclub.captcha.Captcha;

@Controller
@RequestMapping("/ui")

public class UIController {
	
	@GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
		Captcha captcha =  CaptchaUtil.createCaptcha(240, 70);
        request.getSession().removeAttribute("error");

        request.getSession().setAttribute("captcha", CaptchaUtil.encodeCaptcha(captcha));
        request.getSession().setAttribute("captcha_answer", captcha.getAnswer());
        
        return "login";
    }
	
	
	@GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
		Captcha captcha =  CaptchaUtil.createCaptcha(240, 70);
        request.getSession().removeAttribute("error");

        request.getSession().setAttribute("captcha", CaptchaUtil.encodeCaptcha(captcha));
        request.getSession().setAttribute("captcha_answer", captcha.getAnswer());
        return "register";
    }
	
	
	@GetMapping(path= "/welcome")
    public String welcome(Model model) {
        
        return "welcome";
    }
	
}
