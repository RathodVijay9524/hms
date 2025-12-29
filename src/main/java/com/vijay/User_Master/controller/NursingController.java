package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.nursing.*;
import com.vijay.User_Master.service.NursingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/nursing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NursingController {

    private final NursingService nursingService;

    @GetMapping("/wards")
    public ResponseEntity<List<WardDTO>> getWards() {
        return ResponseEntity.ok(nursingService.getWards());
    }

    @PostMapping("/wards")
    public ResponseEntity<WardDTO> createWard(@RequestBody WardDTO dto) {
        return ResponseEntity.ok(nursingService.createWard(dto));
    }

    @PutMapping("/wards/{id}")
    public ResponseEntity<WardDTO> updateWard(@PathVariable Long id, @RequestBody WardDTO dto) {
        return ResponseEntity.ok(nursingService.updateWard(id, dto));
    }

    @DeleteMapping("/wards/{id}")
    public ResponseEntity<Void> deleteWard(@PathVariable Long id) {
        nursingService.deleteWard(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assignments")
    public ResponseEntity<WardPatientDTO> assignPatient(@RequestBody AssignPatientRequestDTO dto) {
        return ResponseEntity.ok(nursingService.assignPatientToWard(dto));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<NursingDashboardDTO> getDashboard(
            @RequestParam Long wardId,
            @RequestParam String shift
    ) {
        return ResponseEntity.ok(nursingService.getDashboard(wardId, shift, LocalDateTime.now()));
    }

    @GetMapping("/patients")
    public ResponseEntity<List<WardPatientDTO>> getWardPatients(@RequestParam Long wardId) {
        return ResponseEntity.ok(nursingService.getWardPatients(wardId));
    }

    @PostMapping("/vitals/batch")
    public ResponseEntity<Void> saveBatchVitals(
            @RequestParam Long wardId,
            @RequestBody List<BatchVitalEntryDTO> entries
    ) {
        nursingService.saveBatchVitals(wardId, entries);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<NursingTaskDTO>> getTasks(
            @RequestParam Long wardId,
            @RequestParam String shift,
            @RequestParam(required = false) String date
    ) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(nursingService.getTasks(wardId, shift, d));
    }

    @PostMapping("/tasks")
    public ResponseEntity<NursingTaskDTO> createTask(@RequestBody CreateNursingTaskRequestDTO dto) {
        return ResponseEntity.ok(nursingService.createTask(dto));
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<NursingTaskDTO> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(nursingService.completeTask(id));
    }

    @GetMapping("/medications")
    public ResponseEntity<List<MedicationAdministrationDTO>> getMedications(
            @RequestParam Long wardId,
            @RequestParam(required = false) String date
    ) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(nursingService.getMedicationsForWard(wardId, d));
    }

    @PatchMapping("/medications/{id}/administer")
    public ResponseEntity<MedicationAdministrationDTO> administerMedication(
            @PathVariable Long id,
            @RequestBody AdministerMedicationRequestDTO dto
    ) {
        return ResponseEntity.ok(nursingService.administerMedication(id, dto));
    }

    @GetMapping("/handover")
    public ResponseEntity<NursingHandoverDTO> getHandover(
            @RequestParam Long wardId,
            @RequestParam(required = false) String date,
            @RequestParam String fromShift,
            @RequestParam String toShift
    ) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(nursingService.getHandover(wardId, d, fromShift, toShift));
    }

    @PostMapping("/handover")
    public ResponseEntity<NursingHandoverDTO> saveHandover(@RequestBody NursingHandoverDTO dto) {
        return ResponseEntity.ok(nursingService.saveHandover(dto));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<NursingAlertDTO>> getAlerts(@RequestParam Long wardId) {
        return ResponseEntity.ok(nursingService.getOpenAlerts(wardId));
    }

    @PatchMapping("/alerts/{id}/ack")
    public ResponseEntity<NursingAlertDTO> acknowledgeAlert(@PathVariable Long id) {
        return ResponseEntity.ok(nursingService.acknowledgeAlert(id));
    }
}
