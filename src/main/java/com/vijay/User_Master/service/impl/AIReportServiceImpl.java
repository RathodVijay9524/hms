package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.ChatRequest;
import com.vijay.User_Master.dto.ChatResponse;
import com.vijay.User_Master.dto.lab.LabOrderDTO;
import com.vijay.User_Master.entity.LabOrder;
import com.vijay.User_Master.entity.LabResult;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.LabOrderRepository;
import com.vijay.User_Master.repository.LabResultRepository;
import com.vijay.User_Master.service.AIReportService;
import com.vijay.User_Master.service.ChatIntegrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIReportServiceImpl implements AIReportService {

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final ChatIntegrationService chatIntegrationService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LabOrderDTO generateAIReportSummary(Long orderId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", orderId));

        List<LabResult> results = labResultRepository.findByOrder_IdAndOwner_Id(orderId, ownerId);
        
        String prompt = constructPrompt(order, results);
        
        ChatRequest chatRequest = ChatRequest.builder()
                .message(prompt)
                .provider("google") // Default to Google Gemini if supported by ChatService
                .model("gemini-pro")
                .userId(ownerId.toString())
                .build();
        
        ChatResponse chatResponse = chatIntegrationService.sendMessage(chatRequest);
        
        String summary = chatResponse.getResponse();
        if (chatResponse.getError() != null) {
            summary = "Error generating AI summary: " + chatResponse.getError();
        }

        order.setAiSummary(summary);
        labOrderRepository.save(order);

        return modelMapper.map(order, LabOrderDTO.class);
    }

    private String constructPrompt(LabOrder order, List<LabResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a clinical lab assistant for a Hospital Management System. ");
        sb.append("Analyze the following laboratory results and provide a summary of findings. ");
        sb.append("RULES: 1. DO NOT DIAGNOSE. 2. Explain results in simple, patient-friendly language. ");
        sb.append("3. Highlight abnormalities clearly. 4. Provide lifestyle or follow-up suggestions. ");
        sb.append("5. ALWAYS include a disclaimer that this is NOT a medical diagnosis.\n\n");

        sb.append("Patient Details: Age: ").append(calculateAge(order.getPatient().getDateOfBirth()))
          .append(", Gender: ").append(order.getPatient().getGender()).append("\n");
        
        sb.append("Lab Order: ").append(order.getOrderNumber()).append("\n");
        sb.append("Results:\n");

        for (LabResult res : results) {
            sb.append("- ").append(res.getParameter().getName()).append(": ")
              .append(res.getResultValue()).append(" ").append(res.getParameter().getUnit())
              .append(" (Ref: ");
            
            // Add reference ranges for context (simplification: just list them)
            res.getParameter().getReferenceRanges().stream()
                .filter(range -> range.getGender().name().equals("BOTH") || range.getGender().name().equals(order.getPatient().getGender().name()))
                .findFirst()
                .ifPresent(r -> sb.append(r.getLowerLimit()).append(" - ").append(r.getUpperLimit()));
            
            sb.append(")\n");
        }
        
        return sb.toString();
    }

    private int calculateAge(java.time.LocalDate dob) {
        if (dob == null) return 0;
        return java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
    }
}
