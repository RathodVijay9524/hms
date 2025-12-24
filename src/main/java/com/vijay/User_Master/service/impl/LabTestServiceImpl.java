package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.lab.LabTestDTO;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LabTestDTO createLabTest(LabTestDTO labTestDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        LabTest labTest = modelMapper.map(labTestDTO, LabTest.class);
        labTest.setOwner(owner);
        labTest.setActive(true);

        // Map parameters and reference ranges if present
        if (labTest.getParameters() != null) {
            labTest.getParameters().forEach(param -> {
                param.setLabTest(labTest);
                if (param.getReferenceRanges() != null) {
                    param.getReferenceRanges().forEach(range -> range.setParameter(param));
                }
            });
        }

        LabTest savedTest = labTestRepository.save(labTest);
        return modelMapper.map(savedTest, LabTestDTO.class);
    }

    @Override
    public List<LabTestDTO> getAllLabTests() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        List<LabTest> tests = labTestRepository.findByOwnerIdAndActiveTrue(ownerId);
        return tests.stream()
                .map(test -> modelMapper.map(test, LabTestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LabTestDTO getLabTestById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabTest labTest = labTestRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
        return modelMapper.map(labTest, LabTestDTO.class);
    }

    @Override
    @Transactional
    public LabTestDTO updateLabTest(Long id, LabTestDTO labTestDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabTest labTest = labTestRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));

        labTest.setName(labTestDTO.getName());
        labTest.setCode(labTestDTO.getCode());
        labTest.setDescription(labTestDTO.getDescription());
        labTest.setCategory(labTestDTO.getCategory());
        labTest.setBasePrice(labTestDTO.getBasePrice());
        labTest.setActive(labTestDTO.isActive());

        // Note: For simplicity, update parameters/reference ranges might need a more complex merge logic.
        // For now, we update the basic test details.

        LabTest updatedTest = labTestRepository.save(labTest);
        return modelMapper.map(updatedTest, LabTestDTO.class);
    }

    @Override
    @Transactional
    public void deleteLabTest(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LabTest labTest = labTestRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
        labTest.setActive(false); // Soft delete
        labTestRepository.save(labTest);
    }
}
