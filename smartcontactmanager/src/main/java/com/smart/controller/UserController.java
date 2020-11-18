package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepositoty;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepositoty contactRepository;

	// common data setting method
	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		System.out.println("username :" + principal.getName());
		User user = userRepo.getUserByUserName(principal.getName());
		System.out.println(user);
		model.addAttribute("user", user);

	}

	// dashborad home
	@RequestMapping("/index")
	public String userDashboard(Model model, Principal principal) {
		model.addAttribute("title", "DashBoard Home");
		return "normal/user_Dashborad";
	}

	// adding contact open from
	@GetMapping("/add-contact-form")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add-contact-form";
	}

	// proccessing add contact form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("imeage") MultipartFile file,
			HttpSession session, Model model) {

		try {
			System.out.println("shortcut user name:" + model.getAttribute("user"));
			System.out.println(contact);

			User user = (User) model.getAttribute("user");

			// processing and upload file

			if (file == null) {
				// if file is empty throw your message
				System.out.println("File is empty");

				// setting default image for contacts
				contact.setProfileImage("default.png");

			} else {
				// upload the file to folder and update the name to contact
				contact.setProfileImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/imeage").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContacts().add(contact);

			System.out.println("-----===================" + user);
			System.out.println("----------------------------" + contact);

			this.userRepo.save(user);

			System.out.println(user);

			System.out.println("user add contact details saved to database");
			// message success
			session.setAttribute("message", new Message("Contact Added successfully.Add more....!!!", "success"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR :" + e.getMessage());
			e.printStackTrace();
			// message failue
			session.setAttribute("message", new Message("Something Went wrong  try again....!!!", "danger"));

		}

		return "normal/add-contact-form";
	}

	// show contact handler
	@GetMapping("/show-contacts/{current}")
	public String showContacts(Model model, @PathVariable("current") Integer current) {
		model.addAttribute("title", "show Contacts");

		// fetching contacts using login user(email)
		User user = (User) model.getAttribute("user");

		// pagination....

		// curent page
		// no of contacts per page-5 details contain pagelble ke pas

		Pageable pageable = PageRequest.of(current, 6);

		Page<Contact> contacts = contactRepository.getAllContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", current);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show-contacts";
	}

	// showing perticular contact details
	@RequestMapping("/{cId}/contact")
	public String showingContactDetails(@PathVariable("cId") Integer cId, Model model) {

		System.out.println("cId  :" + cId);
		model.addAttribute("title", "ContactFullDetails");

		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		User user = (User) model.getAttribute("user");

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}
}
