package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.lab.LabTestDTO;
import com.vijay.User_Master.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab/tests")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping
    public ResponseEntity<LabTestDTO> createLabTest(@RequestBody LabTestDTO labTestDTO) {
        LabTestDTO createdTest = labTestService.createLabTest(labTestDTO);
        return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LabTestDTO>> getAllLabTests() {
        List<LabTestDTO> tests = labTestService.getAllLabTests();
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestDTO> getLabTestById(@PathVariable Long id) {
        LabTestDTO test = labTestService.getLabTestById(id);
        return ResponseEntity.ok(test);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabTestDTO> updateLabTest(@PathVariable Long id, @RequestBody LabTestDTO labTestDTO) {
        LabTestDTO updatedTest = labTestService.updateLabTest(id, labTestDTO);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabTest(@PathVariable Long id) {
        labTestService.deleteLabTest(id);
        return ResponseEntity.noContent().build();
    }
}
