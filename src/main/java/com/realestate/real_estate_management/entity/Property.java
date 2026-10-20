package com.realestate.real_estate_management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "Properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Integer propertyId;

    @Column(name = "title")
    private String title;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "location")
    private String location;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status")
    private String status;

    public Property() {
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
