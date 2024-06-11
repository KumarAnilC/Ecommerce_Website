package com.ecommerce.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.entity.Product;

public interface ProductService {
	
	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts();
	
	public Boolean deleteProduct(int id);
	
	public Product getProductById(int id);
	
	public Product updateProduct(Product product,MultipartFile image);
	
	public List<Product> getAllActiveProducts(String category);
}
