//package com.example.demo.model;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Date;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Lob;
//
//@Entity
//public class Product {
//
//    @Id
//    private int id;
//
//    private String name;
//
//    private String description;
//
//    private String brand;
//
//    private BigDecimal price;
//
//    @Column(name = "category")
//    private String category;
//
//    @Column(name = "release_date")
//    private Date releaseDate;
//
//    private boolean available;
//
//    private int quantity;
//
//    private String imageName;
//    private String imageType;
//
//    @Lob
////    @Column(name = "image_date", columnDefinition = "LONGBLOB")
//    private byte[] imageDate;
//
//
//    // ---------- Constructors ----------
//    public Product() {}
//
//    public Product(int id, String name, String description, String brand, BigDecimal price,
//                   String category, Date releaseDate, boolean available, int quantity,
//                   String imageName, String imageType, byte[] imageDate) {
//
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.brand = brand;
//        this.price = price;
//        this.category = category;
//        this.releaseDate = releaseDate;
//        this.available = available;
//        this.quantity = quantity;
//        this.imageName = imageName;
//        this.imageType = imageType;
//        this.imageDate = imageDate;
//    }
//
//
//    // ---------- Getters & Setters ----------
//    public int getId() {
//        return id;
//    }
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//    public void setDescription(String desc) {
//        this.description = desc;   // FIXED
//    }
//
//    public String getBrand() {
//        return brand;
//    }
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public Date getReleaseDate() {
//        return releaseDate;
//    }
//    public void setReleaseDate(Date releaseDate) {
//        this.releaseDate = releaseDate;
//    }
//
//    public boolean isAvailable() {
//        return available;
//    }
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public String getImageName() {
//        return imageName;
//    }
//    public void setImageName(String imageName) {
//        this.imageName = imageName;
//    }
//
//    public String getImageType() {
//        return imageType;
//    }
//    public void setImageType(String imageType) {
//        this.imageType = imageType;
//    }
//
//    public byte[] getImageDate() {
//        return imageDate;
//    }
//    public void setImageDate(byte[] imageDate) {
//        this.imageDate = imageDate;
//    }
//
//
//    @Override
//    public String toString() {
//        return "Product [id=" + id +
//                ", name=" + name +
//                ", description=" + description +
//                ", brand=" + brand +
//                ", price=" + price +
//                ", category=" + category +
//                ", releaseDate=" + releaseDate +
//                ", available=" + available +
//                ", quantity=" + quantity +
//                ", imageName=" + imageName +
//                ", imageType=" + imageType +
//                ", imageDate=" + Arrays.toString(imageDate) + "]";
//    }
//}

package com.example.demo.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private String brand;

    private BigDecimal price;
    
    private boolean active = true;

    @Column(name = "category")
    private String category;

    @Column(name = "release_date")
    private Date releaseDate;

    private boolean productAvailable;

    private int stockQuantity;
  private String imageName;
  private String imageType;
  
  @OneToMany( mappedBy = "product", cascade = CascadeType.ALL)
  @JsonIgnore
		private List<Review> reviews;

  @Lob
//  @Column(name = "image_date", columnDefinition = "LONGBLOB")
  private byte[] imageDate;
    
}
