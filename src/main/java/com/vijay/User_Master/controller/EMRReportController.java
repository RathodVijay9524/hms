package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/emr/reports")
@RequiredArgsConstructor
public class EMRReportController {

    private final PdfExportService pdfExportService;

    @GetMapping("/prescription/{visitId}")
    public ResponseEntity<byte[]> downloadPrescription(@PathVariable Long visitId) {
        ByteArrayOutputStream outputStream = pdfExportService.generatePrescriptionPdf(visitId);
        byte[] pdfBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Prescription-" + visitId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/visit-summary/{visitId}")
    public ResponseEntity<byte[]> downloadVisitSummary(@PathVariable Long visitId) {
        ByteArrayOutputStream outputStream = pdfExportService.generateVisitSummaryPdf(visitId);
        byte[] pdfBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "VisitSummary-" + visitId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
