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
}
