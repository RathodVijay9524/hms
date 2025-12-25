package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.lab.LabOrderDTO;
import com.vijay.User_Master.dto.lab.LabResultDTO;
import com.vijay.User_Master.entity.LabOrder.OrderStatus;
import org.springframework.data.domain.Page;
import java.util.List;

public interface LabOrderService {
    LabOrderDTO createOrder(LabOrderDTO labOrderDTO);
    LabOrderDTO getOrderById(Long id);
    Page<LabOrderDTO> getAllOrders(int page, int size);
    LabOrderDTO updateOrderStatus(Long id, OrderStatus status);
    List<LabResultDTO> enterResults(Long orderId, List<LabResultDTO> results);
    LabOrderDTO verifyOrder(Long orderId, String doctorRemarks);
    List<LabResultDTO> getResultsByOrderId(Long orderId);
    long getPendingOrderCount();
    long getReportsReadyCount();
    List<LabOrderDTO> getRecentOrders(int limit);
    List<LabOrderDTO> getOrdersByPatient(Long patientId);
}
