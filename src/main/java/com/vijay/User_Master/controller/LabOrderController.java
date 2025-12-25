package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.lab.LabOrderDTO;
import com.vijay.User_Master.dto.lab.LabResultDTO;
import com.vijay.User_Master.entity.LabOrder.OrderStatus;
import com.vijay.User_Master.service.AIReportService;
import com.vijay.User_Master.service.LabOrderService;
import com.vijay.User_Master.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab/orders")
@RequiredArgsConstructor
public class LabOrderController {

    private final LabOrderService labOrderService;
    private final AIReportService aiReportService;
    private final PdfExportService pdfExportService;

    @PostMapping
    public ResponseEntity<LabOrderDTO> createOrder(@RequestBody LabOrderDTO labOrderDTO) {
        LabOrderDTO createdOrder = labOrderService.createOrder(labOrderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabOrderDTO> getOrderById(@PathVariable Long id) {
        LabOrderDTO order = labOrderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<LabOrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LabOrderDTO> orders = labOrderService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabOrderDTO>> getOrdersByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(labOrderService.getOrdersByPatient(patientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LabOrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        LabOrderDTO updatedOrder = labOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{orderId}/results")
    public ResponseEntity<List<LabResultDTO>> getResults(@PathVariable Long orderId) {
        List<LabResultDTO> results = labOrderService.getResultsByOrderId(orderId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{orderId}/results")
    public ResponseEntity<List<LabResultDTO>> enterResults(
            @PathVariable Long orderId,
            @RequestBody List<LabResultDTO> results) {
        List<LabResultDTO> savedResults = labOrderService.enterResults(orderId, results);
        return ResponseEntity.ok(savedResults);
    }

    @PostMapping("/{orderId}/verify")
    public ResponseEntity<LabOrderDTO> verifyOrder(
            @PathVariable Long orderId,
            @RequestParam String doctorRemarks) {
        LabOrderDTO verifiedOrder = labOrderService.verifyOrder(orderId, doctorRemarks);
        return ResponseEntity.ok(verifiedOrder);
    }

    @PostMapping("/{orderId}/ai-summary")
    public ResponseEntity<LabOrderDTO> generateAISummary(@PathVariable Long orderId) {
        LabOrderDTO summary = aiReportService.generateAIReportSummary(orderId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{orderId}/report")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long orderId) {
        java.io.ByteArrayOutputStream outputStream = pdfExportService.generateLabReportPdf(orderId);
        byte[] pdfBytes = outputStream.toByteArray();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "LabReport-" + orderId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
