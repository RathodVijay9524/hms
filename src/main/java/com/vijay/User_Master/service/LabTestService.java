package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.lab.LabTestDTO;
import java.util.List;

public interface LabTestService {
    LabTestDTO createLabTest(LabTestDTO labTestDTO);
    List<LabTestDTO> getAllLabTests();
    LabTestDTO getLabTestById(Long id);
    LabTestDTO updateLabTest(Long id, LabTestDTO labTestDTO);
    void deleteLabTest(Long id);
    long getLabTestCount();
}
