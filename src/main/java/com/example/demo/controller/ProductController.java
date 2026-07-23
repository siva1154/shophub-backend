package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class ProductController {
    @Autowired
	private ProductService service;
	
	@RequestMapping("/")
	public String greet() {
		return "Hellow world";
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts(){
		return new ResponseEntity<>(service.getAllProducts(),HttpStatus.OK);
	}
	
	
	@GetMapping("/product/{id}")
	public ResponseEntity<Product> getProduct(  @PathVariable int id) {

	    Product product =  service.getProductById(id);

	    if (product == null) {

	        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
	    }

	    if (!product.isActive()) {

	        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
	    }

	    return new ResponseEntity<>(  product,   HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/product")
	public ResponseEntity<?> addProduct(@RequestPart Product product,@RequestPart MultipartFile imageFile) {
		try {
			Product product1=service.addProduct(product,imageFile);
			return new ResponseEntity<>(product1,HttpStatus.CREATED);
		}
		catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
		
	@GetMapping("/product/{productId}/image")
	public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId){
		Product product=service.getProductById(productId);
		 if (product == null || product.getImageDate() == null) {
		        return ResponseEntity.notFound().build();
		    }
		byte[] imageFile=product.getImageDate();
		return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType()))  // ✅ ADDED
                .body(imageFile);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/product/{id}")
	public ResponseEntity<String> updateProduct(@PathVariable int id,@RequestPart Product product,@RequestPart(required = false)
	MultipartFile imageFile){
		Product product1;
		try {
			product1 = service.updateProduct(id,product,imageFile);
		} catch (IOException e) {
			return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
		}
		if(product1!=null)
			return new ResponseEntity<>("Updated",HttpStatus.OK);
			else {
				return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
			}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/product/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable int id){
		Product product=service.getProductById(id);
		if(product!=null) {
			service.deleteProduct(id);
			return new ResponseEntity<>("Deleted",HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Product not found",HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/products/search")
	public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
		List<Product> products=service.searchProducts(keyword);
		return new ResponseEntity<>(products,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/product/{id}/restore")
	public ResponseEntity<String> restoreProduct( @PathVariable int id) {

	    Product product =   service.getProductById(id);

	    if (product == null) {

	        return new ResponseEntity<>( "Product not found",HttpStatus.NOT_FOUND  );
	    }

	    product.setActive(true);

	    service.save(product);

	    return new ResponseEntity<>(   "Product restored",  HttpStatus.OK  );
	}
	
	@GetMapping("/admin/product/{id}")
	public ResponseEntity<Product> getProductForAdmin(
	        @PathVariable int id) {

	    Product product =
	            service.getProductById(id);

	    if (product == null) {

	        return new ResponseEntity<>(
	                HttpStatus.NOT_FOUND
	        );
	    }

	    return new ResponseEntity<>(
	            product,
	            HttpStatus.OK
	    );
	}
	
}
