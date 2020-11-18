package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model m) {

		m.addAttribute("title", "home-Smart Contac Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {

		m.addAttribute("title", "about-Smart Contac Manager");
		return "about";
	}

	//handler for register form opening
	
	@RequestMapping("/singup")
	public String singup(Model m) {

		m.addAttribute("title", "Register-Smart Contac Manager");
		m.addAttribute("user", new User());

		return "singup";
	}

	// handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {

			if (!agreement) {
				System.out.println("you are not agreed to terms and conditions ");
				throw new Exception("you are not agreed to terms and conditions ");
			}

			if (bindingResult.hasErrors()) {
				System.out.println("ERRORS :" + bindingResult.toString());
				model.addAttribute("user", user);
				return "singup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImeageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println(agreement);
			System.out.println(user);

			User result = userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registred !!", "alert-success"));
			return "singup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong !!!" + e.getMessage(), "alert-error"));
			return "singup";
		}

	}
	
	@GetMapping("/singin")
	public String login(Model m) {
		m.addAttribute("title", "LogIn-Smart Contac Manager");
		return "login";
	}
}
