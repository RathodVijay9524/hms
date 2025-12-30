package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.NursingAlert;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Ward;
import com.vijay.User_Master.repository.NursingAlertRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor/alerts")
@RequiredArgsConstructor
public class DoctorAlertController {

    private final NursingAlertRepository nursingAlertRepository;
    private final WardRepository wardRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOpenAlerts() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        List<Map<String, Object>> alerts = nursingAlertRepository
                .findByOwnerIdAndIsAcknowledgedFalseOrderByCreatedDateDesc(ownerId)
                .stream()
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeAlert(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Long userId = CommonUtils.getLoggedInUser().getId();
        
        NursingAlert alert = nursingAlertRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        alert.setIsAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(userId);
        
        nursingAlertRepository.save(alert);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Alert acknowledged successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllAlerts() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Long userId = CommonUtils.getLoggedInUser().getId();
        LocalDateTime now = LocalDateTime.now();
        
        List<NursingAlert> openAlerts = nursingAlertRepository
                .findByOwnerIdAndIsAcknowledgedFalseOrderByCreatedDateDesc(ownerId);
        
        openAlerts.forEach(alert -> {
            alert.setIsAcknowledged(true);
            alert.setAcknowledgedAt(now);
            alert.setAcknowledgedBy(userId);
        });
        
        nursingAlertRepository.saveAll(openAlerts);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cleared", openAlerts.size());
        return ResponseEntity.ok(response);
    }

    // Test endpoint to create sample alerts for development testing
    @PostMapping("/test/create")
    public ResponseEntity<Map<String, Object>> createTestAlert() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElse(null);
        
        // Get any available ward for this owner
        List<Ward> wards = wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
        Ward ward = wards.isEmpty() ? null : wards.get(0);
        
        if (ward == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "No ward found. Create a ward first in the IPD module.");
            return ResponseEntity.ok(response);
        }
        
        // Create sample critical alert
        NursingAlert alert1 = new NursingAlert();
        alert1.setWard(ward);
        alert1.setOwner(owner);
        alert1.setAlertType(NursingAlert.AlertType.VITAL_ABNORMAL);
        alert1.setSeverity(NursingAlert.Severity.CRITICAL);
        alert1.setMessage("Patient oxygen saturation dropped below 88% for more than 5 minutes. Nursing team notified, awaiting doctor's intervention.");
        alert1.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        alert1.setIsAcknowledged(false);
        nursingAlertRepository.save(alert1);

        // Create sample lab alert
        NursingAlert alert2 = new NursingAlert();
        alert2.setWard(ward);
        alert2.setOwner(owner);
        alert2.setAlertType(NursingAlert.AlertType.LAB_RESULT);
        alert2.setSeverity(NursingAlert.Severity.WARNING);
        alert2.setMessage("Serum Potassium reported at 6.2 mEq/L (Ref: 3.5 - 5.1). Urgent clinical correlation required.");
        alert2.setCreatedDate(LocalDateTime.now().minusMinutes(15));
        alert2.setIsAcknowledged(false);
        nursingAlertRepository.save(alert2);

        // Create sample medication alert
        NursingAlert alert3 = new NursingAlert();
        alert3.setWard(ward);
        alert3.setOwner(owner);
        alert3.setAlertType(NursingAlert.AlertType.MEDICATION_DUE);
        alert3.setSeverity(NursingAlert.Severity.WARNING);
        alert3.setMessage("Scheduled medication (Insulin 10 units) was not administered within the expected window. Please confirm administration status.");
        alert3.setCreatedDate(LocalDateTime.now().minusMinutes(30));
        alert3.setIsAcknowledged(false);
        nursingAlertRepository.save(alert3);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Created 3 test alerts. Refresh the alerts page to see them.");
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toDetailedDTO(NursingAlert alert) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", alert.getId());
        dto.put("alertType", alert.getAlertType().name());
        dto.put("severity", alert.getSeverity().name());
        dto.put("message", alert.getMessage());
        dto.put("createdDate", alert.getCreatedDate());
        dto.put("isAcknowledged", alert.getIsAcknowledged());
        
        // Add patient info if available
        if (alert.getAssignment() != null && alert.getAssignment().getPatient() != null) {
            dto.put("patientName", alert.getAssignment().getPatient().getName());
            dto.put("patientId", alert.getAssignment().getPatient().getId());
            dto.put("patientUhid", alert.getAssignment().getPatient().getUhid());
        }
        
        // Add ward info
        if (alert.getWard() != null) {
            dto.put("wardName", alert.getWard().getName());
            dto.put("wardId", alert.getWard().getId());
        }
        
        return dto;
    }
}

