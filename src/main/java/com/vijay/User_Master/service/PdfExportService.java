package com.vijay.User_Master.service;

import java.io.ByteArrayOutputStream;

public interface PdfExportService {
    /**
     * Generates a PDF report for a given lab order.
     * 
     * @param orderId The ID of the lab order.
     * @return Byte array output stream containing the PDF data.
     */
    ByteArrayOutputStream generateLabReportPdf(Long orderId);

    /**
     * Generates a PDF prescription for a given doctor visit.
     */
    ByteArrayOutputStream generatePrescriptionPdf(Long visitId);

    /**
     * Generates a PDF visit summary for a given doctor visit.
     */
    ByteArrayOutputStream generateVisitSummaryPdf(Long visitId);

    /**
     * Generates a PDF dispensing receipt for a given dispensing ID.
     */
    ByteArrayOutputStream generateDispensingReceipt(Long dispensingId);
}
