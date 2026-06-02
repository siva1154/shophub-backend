package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepo;

@Service
public class ProductService {

	@Autowired
	private ProductRepo repo;
	
	public List<Product> getAllProducts() {
	    return repo.findByActiveTrue();
	}

	public Product getProductById(int id) {
		return repo.findById(id).orElse(null);
	}

	public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
		product.setImageName(imageFile.getOriginalFilename());
		product.setImageType(imageFile.getContentType());
		product.setImageDate(imageFile.getBytes());
		return repo.save(product);
	}

	public Product updateProduct( int id,  Product updatedProduct,MultipartFile imageFile)
	        throws IOException {

	    Product existingProduct =   repo.findById(id) .orElseThrow();

	    existingProduct.setName( updatedProduct.getName());

	    existingProduct.setDescription( updatedProduct.getDescription());

	    existingProduct.setBrand(updatedProduct.getBrand());

	    existingProduct.setCategory(updatedProduct.getCategory());

	    existingProduct.setPrice(  updatedProduct.getPrice());

	    existingProduct.setStockQuantity(  updatedProduct.getStockQuantity());

	    existingProduct.setProductAvailable(  updatedProduct.isProductAvailable());

	    existingProduct.setActive(updatedProduct.isActive());

	    if (imageFile != null &&
	        !imageFile.isEmpty()) {

	        existingProduct.setImageName(  imageFile.getOriginalFilename());

	        existingProduct.setImageType(imageFile.getContentType());

	        existingProduct.setImageDate(  imageFile.getBytes());
	    }

	    return repo.save(existingProduct);
	}

	public void deleteProduct(int id) {

	    Product product = repo.findById(id)
	            .orElseThrow();

	    product.setActive(false);

	    repo.save(product);
	}

	public List<Product> searchProducts(String keyword) {
		return repo.searchProducts(keyword);
	}
	
	public List<Product> getAllProductsForAdmin() {
	    return repo.findAll();
	}

	public Product save(Product product) {
	    return repo.save(product);
	}
}
