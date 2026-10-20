package com.realestate.real_estate_management.repository;


import com.realestate.real_estate_management.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository
        extends JpaRepository<Property, Integer> {
}
