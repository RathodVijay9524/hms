package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.emr.DepartmentDTO;
import java.util.List;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);
    void deleteDepartment(Long id);
    DepartmentDTO getDepartmentById(Long id);
}
