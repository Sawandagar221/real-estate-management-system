package com.realestate.real_estate_management.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.realestate.real_estate_management.repository.PropertyRepository;
import com.realestate.real_estate_management.repository.CustomerRepository;
import com.realestate.real_estate_management.repository.TransactionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class DashboardController {

    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public DashboardController(
            PropertyRepository propertyRepository,
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository) {

        this.propertyRepository = propertyRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {

        long totalProperties = propertyRepository.count();
        long totalCustomers = customerRepository.count();
        long totalTransactions = transactionRepository.count();

        BigDecimal totalTransactionValue = transactionRepository.findAll()
                .stream()
                .map(transaction -> transaction.getAmount())
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalProperties", totalProperties);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalTransactionValue", totalTransactionValue);
        model.addAttribute(
                "recentTransactions",
                transactionRepository.findAll(
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(Sort.Direction.DESC, "transactionId")
                        )
                ).getContent()
        );

        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());

        return "dashboard";
    }
}
