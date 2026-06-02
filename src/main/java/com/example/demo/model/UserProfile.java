package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String address;

    private String profileImage;
    private Boolean orderNotifications = true;
    private Boolean promotionalEmails = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
	    return "UserProfile [id=" + id + ", name=" + name + ", phone=" + phone + ", address=" + address + "]";
	}

	public Boolean getOrderNotifications() {
		return orderNotifications;
	}

	public void setOrderNotifications(Boolean orderNotifications) {
		this.orderNotifications = orderNotifications;
	}

	public Boolean getPromotionalEmails() {
		return promotionalEmails;
	}

	public void setPromotionalEmails(Boolean promotionalEmails) {
		this.promotionalEmails = promotionalEmails;
	}
    
	

}