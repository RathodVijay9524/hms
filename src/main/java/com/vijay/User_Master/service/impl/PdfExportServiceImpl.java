package com.vijay.User_Master.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.LabOrder;
import com.vijay.User_Master.entity.LabResult;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.LabOrderRepository;
import com.vijay.User_Master.repository.LabResultRepository;
import com.vijay.User_Master.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final TemplateEngine templateEngine;

    @Override
    public ByteArrayOutputStream generateLabReportPdf(Long orderId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", orderId));

        List<LabResult> results = labResultRepository.findByOrderIdAndOwnerId(orderId, ownerId);

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("results", results);
        context.setVariable("hospitalName", order.getOwner().getName());
        context.setVariable("hospitalAddress", order.getOwner().getAbout()); // Assuming 'about' is used for address/info
        context.setVariable("hospitalPhone", order.getOwner().getPhoNo());

        String htmlContent = templateEngine.process("reports/lab-report", context);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(target);
            builder.run();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Could not generate PDF report", e);
        }

        return target;
    }
}
