package com.example.demo.model;

import jakarta.persistence.CascadeType;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long  id;
	
	private String name;
	private boolean active = true;
	private boolean banned = false;
	
	@Column(unique = true, nullable = false)
    private String email;
	
	 @Column(nullable = false)
	 @JsonIgnore
	private String password;
	 
	 private String role;
	 
	 @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	 @JsonIgnore
	    private UserProfile profile;
	 
	 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
	 @JsonIgnore
	 private List<Address> addresses;

	 public long getId() {
		 return id;
	 }

	 public void setId(long id) {
		 this.id = id;
	 }

	 
	 public String getName() {
		    return name;
		}

		public void setName(String name) {
		    this.name = name;
		}
		
		
		public boolean isActive() {
		    return active;
		}

		public void setActive(boolean active) {
		    this.active = active;
		}
		
		
		
	 public boolean isBanned() {
			return banned;
		}

		public void setBanned(boolean banned) {
			this.banned = banned;
		}

	 public String getEmail() {
		 return email;
	 }

	 public void setEmail(String email) {
		 this.email = email;
	 }

	 public String getPassword() {
		 return password;
	 }

	 public void setPassword(String password) {
		 this.password = password;
	 }

	 public String getRole() {
		 return role;
	 }

	 public void setRole(String role) {
		 this.role = role;
	 }
	 
	 

	 public UserProfile getProfile() {
		return profile;
	}

	 public void setProfile(UserProfile profile) {
		 this.profile = profile;
	 }

	 
	 public List<Address> getAddresses() {
		return addresses;
	}

	 public void setAddresses(List<Address> addresses) {
		 this.addresses = addresses;
	 }

	 @Override
	 public String toString() {
	     return "User [id=" + id + ", email=" + email + ", role=" + role + "]";
	 }
	



	 
	 
	
}
