package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.pharmacy.DispensingDTO;
import com.vijay.User_Master.dto.pharmacy.DispensingRequest;
import com.vijay.User_Master.dto.pharmacy.DispensedItemDTO;
import com.vijay.User_Master.dto.pharmacy.PharmacyDashboardDTO;

import java.util.List;

public interface PharmacyDispensingService {
    
    // Get pending prescriptions for dispensing
    List<DispensingDTO> getPendingPrescriptions();
    
    // Get prescription details with stock availability
    DispensingDTO getPrescriptionForDispensing(Long prescriptionId);
    
    // Process dispensing (full or partial)
    DispensingDTO dispensePrescription(DispensingRequest request);
    
    // Get dispensing history for a patient
    List<DispensingDTO> getDispensingHistory(Long patientId);
    
    List<DispensingDTO> getAllDispensings();
    
    // Cancel dispensing
    void cancelDispensing(Long dispensingId, String reason);
    
    long getPendingCount();
    PharmacyDashboardDTO getDashboardStats();
    java.io.ByteArrayOutputStream getDispensingReceipt(Long dispensingId);
}
