package com.vijay.User_Master.service;



import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.service.generics.iCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface UserService extends iCrudService<UserRequest, UserResponse,Long> {

    UserResponse updateUser(Long id, UserRequest request);
    UserResponse getCurrentUser();
    void updateAccountStatus(Long userId, Boolean isActive);
    UserResponse getByIdForUser(Long aLong);
    PageableResponse<UserResponse> getAllActiveUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<UserResponse> getAllDeletedUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PageableResponse<UserResponse> getUsersWithFilters(
            int pageNumber, int pageSize, String sortBy, String sortDir,
            Boolean isDeleted, Boolean isActive);

    Page<UserResponse> getUsersWithFilter(Boolean isDeleted, Boolean isActive, String keyword, Pageable pageable);

    void softDeleteUser(Long id);

    void permanentlyDelete(Long id);
    void restoreUser(Long id);



}
