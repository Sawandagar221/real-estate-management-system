package com.realestate.real_estate_management.controller;

import com.realestate.real_estate_management.entity.Transaction;
import com.realestate.real_estate_management.entity.Property;
import com.realestate.real_estate_management.entity.Customer;

import com.realestate.real_estate_management.repository.TransactionRepository;
import com.realestate.real_estate_management.repository.PropertyRepository;
import com.realestate.real_estate_management.repository.CustomerRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;

    public TransactionController(
            TransactionRepository transactionRepository,
            PropertyRepository propertyRepository,
            CustomerRepository customerRepository) {

        this.transactionRepository = transactionRepository;
        this.propertyRepository = propertyRepository;
        this.customerRepository = customerRepository;
    }

    // Display all transactions
    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        List<Transaction> transactions = transactionRepository.findAll();

        model.addAttribute("transactions", transactions);
        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());

        return "transactions";

    }

    // Display the add transaction form
    @GetMapping("/transactions/add")
    public String showAddTransactionForm(Model model) {

        model.addAttribute("transaction", new Transaction());
        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());

        return "add-transaction";
    }

    // Save a new transaction
    @PostMapping("/transactions/add")
    public String addTransaction(
            @ModelAttribute("transaction") Transaction transaction,
            RedirectAttributes redirectAttributes) {

        transactionRepository.save(transaction);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Transaction added successfully!");

        return "redirect:/dashboard";
    }
    @GetMapping("/transactions/edit/{id}")
    public String showEditTransactionForm(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            Model model) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid transaction ID: " + id));

        model.addAttribute("transaction", transaction);
        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());

        return "edit-transaction";
    }

    @PostMapping("/transactions/edit/{id}")
    public String updateTransaction(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @ModelAttribute("transaction") Transaction updatedTransaction) {

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid transaction ID: " + id));

        existingTransaction.setPropertyId(updatedTransaction.getPropertyId());
        existingTransaction.setCustomerId(updatedTransaction.getCustomerId());
        existingTransaction.setTransactionType(updatedTransaction.getTransactionType());
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());

        transactionRepository.save(existingTransaction);

        return "redirect:/dashboard";
    }
    @PostMapping("/transactions/delete/{id}")
    public String deleteTransaction(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Transaction deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Transaction not found!");
        }

        return "redirect:/transactions";
    }
}