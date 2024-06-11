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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String index() {
		return "/admin/Index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model model) {
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		return "/admin/AddProduct";
	}

	@GetMapping("/category")
	public String category(Model model) {
		model.addAttribute("categories", categoryService.getAllCategory());
		return "/admin/Category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());
		if (existCategory) {
			session.setAttribute("errorMsg", "Category name already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
			} else {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("succMsg", "Saved Successfully");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model model) {
		model.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/EditCategory";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		Category updateCategory = categoryService.saveCategory(oldCategory);
		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Ctegory Updated Sucessfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());

		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {
			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ image.getOriginalFilename());

			System.out.println(path);

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Saved Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "admin/Products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		model.addAttribute("categories", categoryService.getAllCategory());
		return "admin/EditProduct";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, HttpSession session,
			@RequestParam("file") MultipartFile image, Model model) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product Updated Successfully");
			} else {
				session.setAttribute("errorMsg", "Something Went Wrong");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

}
