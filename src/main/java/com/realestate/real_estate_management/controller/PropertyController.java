
package com.realestate.real_estate_management.controller;

import com.realestate.real_estate_management.entity.Property;
import com.realestate.real_estate_management.repository.PropertyRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class PropertyController {

    private final PropertyRepository propertyRepository;

    // Constructor injection
    public PropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Display all properties
    @GetMapping("/properties")
    public String listProperties(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<Property> properties = propertyRepository.findAll();

        // Search by title or location
        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim().toLowerCase();

            properties = properties.stream()
                    .filter(p ->
                            (p.getTitle() != null &&
                                    p.getTitle().toLowerCase().contains(search))
                                    ||
                                    (p.getLocation() != null &&
                                            p.getLocation().toLowerCase().contains(search))
                    )
                    .toList();
        }

        // Filter by property type
        if (propertyType != null && !propertyType.isBlank()) {
            properties = properties.stream()
                    .filter(p -> propertyType.equalsIgnoreCase(p.getPropertyType()))
                    .toList();
        }

        // Filter by status
        if (status != null && !status.isBlank()) {
            properties = properties.stream()
                    .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                    .toList();
        }

        // Pagination: 5 properties per page
        int pageSize = 5;
        int totalProperties = properties.size();
        int totalPages = (int) Math.ceil((double) totalProperties / pageSize);

        // Keep the requested page within valid bounds
        if (totalPages == 0) {
            page = 0;
        } else {
            page = Math.max(0, Math.min(page, totalPages - 1));
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalProperties);

        List<Property> paginatedProperties =
                properties.subList(start, end);

        model.addAttribute("properties", paginatedProperties);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", propertyType);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProperties", totalProperties);

        return "properties";
    }

    // Display add-property form
    @GetMapping("/properties/add")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }

    // Save property to database
    @PostMapping("/properties/add")
    public String saveProperty(
            @ModelAttribute("property") Property property) {

        propertyRepository.save(property);

        return "redirect:/dashboard";
    }
    // Display the edit property form
    @GetMapping("/properties/edit/{id}")
    public String showEditPropertyForm(
            @PathVariable Long id, Model model) {

        Property property = propertyRepository.findById(id)
                .orElse(null);

        if (property == null) {
            return "redirect:/properties";
        }

        model.addAttribute("property", property);
        return "edit-property";
    }

    // Update an existing property
    @PostMapping("/properties/edit/{id}")
    public String updateProperty(
            @PathVariable Long id,
            @ModelAttribute("property") Property updatedProperty) {

        Property existingProperty = propertyRepository.findById(id)
                .orElse(null);

        if (existingProperty == null) {
            return "redirect:/dashboard";
        }

        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setPropertyType(updatedProperty.getPropertyType());
        existingProperty.setLocation(updatedProperty.getLocation());
        existingProperty.setPrice(updatedProperty.getPrice());
        existingProperty.setStatus(updatedProperty.getStatus());

        propertyRepository.save(existingProperty);

        return "redirect:/dashboard";
    }
    // Delete an existing property
    @PostMapping("/properties/delete/{id}")
    public String deleteProperty(@PathVariable Long id) {
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
        }

        return "redirect:/properties";
    }
}
