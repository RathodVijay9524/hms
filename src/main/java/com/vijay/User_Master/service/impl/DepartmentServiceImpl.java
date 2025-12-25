package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.DepartmentDTO;
import com.vijay.User_Master.entity.Department;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.DepartmentRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        Department department = modelMapper.map(departmentDTO, Department.class);
        department.setOwner(owner);

        Department saved = departmentRepository.save(department);
        return modelMapper.map(saved, DepartmentDTO.class);
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return departmentRepository.findByOwnerId(ownerId).stream()
                .map(d -> modelMapper.map(d, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Department department = departmentRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Department not found or access denied"));

        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());

        Department updated = departmentRepository.save(department);
        return modelMapper.map(updated, DepartmentDTO.class);
    }

    @Override
    public void deleteDepartment(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Department department = departmentRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Department not found or access denied"));
        departmentRepository.delete(department);
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Department department = departmentRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Department not found or access denied"));
        return modelMapper.map(department, DepartmentDTO.class);
    }
}
