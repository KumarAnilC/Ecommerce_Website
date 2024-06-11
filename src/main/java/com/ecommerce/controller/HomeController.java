package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.UserDetails;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	

	@GetMapping("/")
	public String index() {
		return "Index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "Login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "Register";
	}
	
	@GetMapping("/products")
	public String products(Model model,@RequestParam(value = "category",defaultValue = "") String category) {
		System.out.println("category"+category);
		List<Category> categories = categoryService.getAllActiveCategories();
		List<Product> products = productService.getAllActiveProducts(category);
		model.addAttribute("categories", categories);
		model.addAttribute("products", products);
		model.addAttribute("paramValue", category);
		return "Product";
	}
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id,Model model) {
		Product productById = productService.getProductById(id);
		model.addAttribute("product",productById);
		return "ViewProduct";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDetails user,@RequestParam("img") MultipartFile file,HttpSession session) throws IOException {
		
		String imageName=file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDetails saveUser = userService.saveUser(user);
		if(!ObjectUtils.isEmpty(saveUser)){
			if(!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Saved Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}
		
		
		return "redirect:/register";
	}
}
