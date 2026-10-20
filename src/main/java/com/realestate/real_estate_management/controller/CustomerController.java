package com.realestate.real_estate_management.controller;

import com.realestate.real_estate_management.entity.Customer;
import com.realestate.real_estate_management.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Display all customers
    @GetMapping("/customers")
    public String listCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<Customer> customers = customerRepository.findAll();

        // Search by name, phone, or email
        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim().toLowerCase();

            customers = customers.stream()
                    .filter(c ->
                            (c.getName() != null &&
                                    c.getName().toLowerCase().contains(search))
                                    ||
                                    (c.getPhone() != null &&
                                            c.getPhone().toLowerCase().contains(search))
                                    ||
                                    (c.getEmail() != null &&
                                            c.getEmail().toLowerCase().contains(search))
                    )
                    .toList();
        }

        // Filter by customer role
        if (role != null && !role.isBlank()) {
            customers = customers.stream()
                    .filter(c -> role.equalsIgnoreCase(c.getRole()))
                    .toList();
        }

        // Pagination: 5 customers per page
        int pageSize = 5;
        int totalCustomers = customers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);

        // Keep the requested page within valid bounds
        if (totalPages == 0) {
            page = 0;
        } else {
            page = Math.max(0, Math.min(page, totalPages - 1));
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalCustomers);

        List<Customer> paginatedCustomers =
                customers.subList(start, end);

        model.addAttribute("customers", paginatedCustomers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCustomers", totalCustomers);

        return "customers";
    }

    // Display add-customer form
    @GetMapping("/customers/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    // Save a new customer
    @PostMapping("/customers/add")
    public String addCustomer(
            @ModelAttribute("customer") Customer customer,
            RedirectAttributes redirectAttributes) {

        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Customer added successfully!");

        return "redirect:/dashboard";
    }

    // Display edit-customer form
    @GetMapping("/customers/edit/{id}")
    public String showEditCustomerForm(
            @PathVariable Long id, Model model) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid customer ID: " + id));

        model.addAttribute("customer", customer);
        return "edit-customer";
    }

    // Update an existing customer
    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(
            @PathVariable Long id,
            @ModelAttribute("customer") Customer formCustomer,
            RedirectAttributes redirectAttributes) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid customer ID: " + id));

        existingCustomer.setName(formCustomer.getName());
        existingCustomer.setPhone(formCustomer.getPhone());
        existingCustomer.setEmail(formCustomer.getEmail());
        existingCustomer.setRole(formCustomer.getRole());

        customerRepository.save(existingCustomer);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Customer updated successfully!");

        return "redirect:/dashboard";
    }

    // Delete a customer
    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!customerRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Customer not found!");
            return "redirect:/customers";
        }

        customerRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Customer deleted successfully!");

        return "redirect:/customers";
    }
}