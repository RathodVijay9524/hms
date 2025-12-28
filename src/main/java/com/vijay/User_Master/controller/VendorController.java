package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Vendor;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/vendors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendorController {
    
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    
    // Dashboard APIs
    @GetMapping("/dashboard/active-count")
    public ResponseEntity<Long> getActiveVendorsCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Long count = vendorRepository.getActiveVendorCount(ownerId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/dashboard/reliable")
    public ResponseEntity<List<Vendor>> getReliableVendors() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        List<Vendor> vendors = vendorRepository.findReliableVendors(ownerId);
        return ResponseEntity.ok(vendors);
    }
    
    // Vendor Management APIs
    @GetMapping
    public ResponseEntity<Page<Vendor>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Vendor> vendors = vendorRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable);
        return ResponseEntity.ok(vendors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Vendor vendor = vendorRepository.findByIdAndOwnerId(id, ownerId)
                .filter(v -> !v.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return ResponseEntity.ok(vendor);
    }
    
    @PostMapping
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        vendor.setOwner(owner);
        Vendor created = vendorRepository.save(vendor);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendorDetails) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Vendor vendor = vendorRepository.findByIdAndOwnerId(id, ownerId)
                .filter(v -> !v.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        vendor.setName(vendorDetails.getName());
        vendor.setContactPerson(vendorDetails.getContactPerson());
        vendor.setEmail(vendorDetails.getEmail());
        vendor.setPhoneNumber(vendorDetails.getPhoneNumber());
        vendor.setAddress(vendorDetails.getAddress());
        vendor.setPaymentTerms(vendorDetails.getPaymentTerms());
        vendor.setTaxNumber(vendorDetails.getTaxNumber());
        vendor.setIsActive(vendorDetails.getIsActive());
        
        Vendor updated = vendorRepository.save(vendor);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Vendor vendor = vendorRepository.findByIdAndOwnerId(id, ownerId)
                .filter(v -> !v.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        vendor.setIsDeleted(true);
        vendorRepository.save(vendor);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Vendor>> searchVendors(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Vendor> vendors = vendorRepository.searchVendors(ownerId, keyword, pageable);
        return ResponseEntity.ok(vendors);
    }
}
