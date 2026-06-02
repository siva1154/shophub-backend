package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({
    "profile",
    "addresses",
    "password"
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @Column(length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({
    	"profile",
        "addresses",
        "password"
})
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({
        "reviews"
    })
    private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

    // Getters & Setters
    
}