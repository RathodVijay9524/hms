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
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PdfExportServiceImpl.class);

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final TemplateEngine templateEngine;

    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateLabReportPdf(Long orderId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", orderId));

        List<LabResult> results = labResultRepository.findByOrder_IdAndOwner_Id(orderId, ownerId);
        log.info("PDF Generation: Found {} results for order ID: {}", results.size(), orderId);

        List<com.vijay.User_Master.dto.report.ReportResultDTO> reportResults = results.stream().map(r -> {
            List<String> ranges = r.getParameter().getReferenceRanges().stream()
                    .filter(range -> range.getGender() == com.vijay.User_Master.entity.LabReferenceRange.Gender.BOTH ||
                            range.getGender().name().equalsIgnoreCase(order.getPatient().getGender().name()))
                    .map(range -> range.getLowerLimit() + " - " + range.getUpperLimit())
                    .collect(java.util.stream.Collectors.toList());

            return new com.vijay.User_Master.dto.report.ReportResultDTO(
                    r.getParameter().getName(),
                    r.getResultValue(),
                    r.getParameter().getUnit(),
                    ranges
            );
        }).collect(java.util.stream.Collectors.toList());

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("results", reportResults);
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
