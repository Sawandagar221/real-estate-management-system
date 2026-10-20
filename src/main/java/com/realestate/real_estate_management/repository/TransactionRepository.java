package com.realestate.real_estate_management.repository;

import com.realestate.real_estate_management.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
}
