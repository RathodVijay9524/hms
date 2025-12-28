package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.reception.*;
import com.vijay.User_Master.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reception")
@RequiredArgsConstructor
public class ReceptionRestController {

    private final ReceptionService receptionService;

    @GetMapping("/stats")
    public ResponseEntity<ReceptionStatsDTO> getStats() {
        return ResponseEntity.ok(receptionService.getDashboardStats());
    }

    @GetMapping("/enquiries")
    public ResponseEntity<List<EnquiryDTO>> getEnquiries() {
        return ResponseEntity.ok(receptionService.getAllEnquiries());
    }

    @PostMapping("/enquiries")
    public ResponseEntity<EnquiryDTO> createEnquiry(@RequestBody EnquiryDTO dto) {
        return ResponseEntity.ok(receptionService.createEnquiry(dto));
    }

    @PutMapping("/enquiries/{id}")
    public ResponseEntity<EnquiryDTO> updateEnquiry(@PathVariable Long id, 
                                                   @RequestParam String status, 
                                                   @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(receptionService.updateEnquiryStatus(id, status, notes));
    }

    @GetMapping("/visitors")
    public ResponseEntity<List<VisitorLogDTO>> getVisitors() {
        return ResponseEntity.ok(receptionService.getAllVisitorLogs());
    }

    @PostMapping("/visitors/check-in")
    public ResponseEntity<VisitorLogDTO> checkIn(@RequestBody VisitorLogDTO dto) {
        return ResponseEntity.ok(receptionService.checkInVisitor(dto));
    }

    @PutMapping("/visitors/{id}/check-out")
    public ResponseEntity<VisitorLogDTO> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(receptionService.checkOutVisitor(id));
    }

    @GetMapping("/tokens")
    public ResponseEntity<List<QueueTokenDTO>> getTokens() {
        return ResponseEntity.ok(receptionService.getTodayTokens());
    }

    @PostMapping("/tokens")
    public ResponseEntity<QueueTokenDTO> issueToken(@RequestBody QueueTokenDTO dto) {
        return ResponseEntity.ok(receptionService.issueToken(dto));
    }

    @PutMapping("/tokens/{id}/status")
    public ResponseEntity<QueueTokenDTO> updateTokenStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(receptionService.updateTokenStatus(id, status));
    }

    @GetMapping("/patients/search")
    public ResponseEntity<Object> searchPatients(@RequestParam String query) {
        return ResponseEntity.ok(receptionService.searchPatients(query));
    }
}
