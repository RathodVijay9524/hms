package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.lab.LabOrderDTO;
import com.vijay.User_Master.dto.lab.LabResultDTO;
import com.vijay.User_Master.dto.lab.LabTestDTO;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.LabOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabOrderServiceImpl implements LabOrderService {

    private final LabOrderRepository labOrderRepository;
    private final PatientRepository patientRepository;
    private final LabTestRepository labTestRepository;
    private final LabResultRepository labResultRepository;
    private final LabParameterRepository labParameterRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LabOrderDTO createOrder(LabOrderDTO labOrderDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Patient patient = patientRepository.findByIdAndOwnerId(labOrderDTO.getPatientId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", labOrderDTO.getPatientId()));

        List<LabTest> tests = labTestRepository.findAllById(labOrderDTO.getTestIds());
        // Verify all tests belong to the owner
        tests.forEach(test -> {
            if (!test.getOwner().getId().equals(ownerId)) {
                throw new RuntimeException("Unauthorized test access");
            }
        });

        LabOrder order = LabOrder.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .patient(patient)
                .tests(tests)
                .status(LabOrder.OrderStatus.ORDERED)
                .owner(owner)
                .build();

        LabOrder savedOrder = labOrderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public LabOrderDTO getOrderById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", id));
        
        return convertToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<LabOrderDTO> getAllOrders(int page, int size) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<LabOrder> orders = labOrderRepository.findByOwnerId(ownerId, pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public LabOrderDTO updateOrderStatus(Long id, LabOrder.OrderStatus status) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", id));

        order.setStatus(status);
        if (status == LabOrder.OrderStatus.SAMPLE_COLLECTED) {
            order.setCollectionDate(LocalDateTime.now());
        }

        LabOrder updatedOrder = labOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public List<LabResultDTO> enterResults(Long orderId, List<LabResultDTO> resultDTOs) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", orderId));

        User owner = userRepository.findById(ownerId).get();

        List<LabResult> savedResults = resultDTOs.stream().map(dto -> {
            LabParameter parameter = labParameterRepository.findById(dto.getParameterId())
                    .orElseThrow(() -> new ResourceNotFoundException("LabParameter", "id", dto.getParameterId()));

            LabResult result = LabResult.builder()
                    .order(order)
                    .parameter(parameter)
                    .resultValue(dto.getResultValue())
                    .technicianNotes(dto.getTechnicianNotes())
                    .owner(owner)
                    .build();
            return labResultRepository.save(result);
        }).collect(Collectors.toList());

        order.setStatus(LabOrder.OrderStatus.RESULT_ENTERED);
        labOrderRepository.save(order);

        return savedResults.stream()
                .map(r -> modelMapper.map(r, LabResultDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LabOrderDTO verifyOrder(Long orderId, String doctorRemarks) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabOrder order = labOrderRepository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", orderId));

        order.setDoctorVerified(true);
        order.setDoctorRemarks(doctorRemarks);
        order.setStatus(LabOrder.OrderStatus.VERIFIED);
        
        LabOrder verifiedOrder = labOrderRepository.save(order);
        return convertToDTO(verifiedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabResultDTO> getResultsByOrderId(Long orderId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        List<LabResult> results = labResultRepository.findByOrder_IdAndOwner_Id(orderId, ownerId);
        return results.stream().map(r -> {
            LabResultDTO dto = modelMapper.map(r, LabResultDTO.class);
            dto.setParameterId(r.getParameter().getId());
            dto.setParameterName(r.getParameter().getName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public long getPendingOrderCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return labOrderRepository.countByOwnerIdAndStatusIn(ownerId, 
            java.util.List.of(LabOrder.OrderStatus.ORDERED, LabOrder.OrderStatus.SAMPLE_COLLECTED, LabOrder.OrderStatus.IN_PROCESS));
    }

    @Override
    public long getReportsReadyCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return labOrderRepository.countByOwnerIdAndStatusIn(ownerId, 
            java.util.List.of(LabOrder.OrderStatus.VERIFIED, LabOrder.OrderStatus.REPORT_READY));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<LabOrderDTO> getRecentOrders(int limit) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit, org.springframework.data.domain.Sort.by("id").descending());
        return labOrderRepository.findByOwnerId(ownerId, pageable)
                .map(this::convertToDTO).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderDTO> getOrdersByPatient(Long patientId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return labOrderRepository.findByPatientIdAndOwnerIdOrderByCreatedOnDesc(patientId, ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LabOrderDTO convertToDTO(LabOrder order) {
        LabOrderDTO dto = modelMapper.map(order, LabOrderDTO.class);
        dto.setPatientId(order.getPatient().getId());
        dto.setPatientName(order.getPatient().getName());
        
        dto.setTests(order.getTests().stream()
                .map(t -> modelMapper.map(t, LabTestDTO.class))
                .collect(Collectors.toList()));

        if (order.getCreatedOn() != null) {
            dto.setCreatedAt(order.getCreatedOn().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        return dto;
    }
}
