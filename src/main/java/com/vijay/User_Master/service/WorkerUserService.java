package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.FavouriteEntryResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.UserResponse;

import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.service.generics.AdvancedCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface WorkerUserService extends AdvancedCrudService<WorkerResponse, Long> {

    
    List<WorkerResponse> findAllActiveUsers();
    PageableResponse<WorkerResponse> getAllActiveUserWithSortingSearching(int pageNumber, int pageSize, String sortBy, String sortDir);

    void favoriteWorkerUser(Long workerId) throws Exception;

    void unFavoriteWorkerUser(Long Id) throws Exception;

    List<FavouriteEntryResponse> getUserFavoriteWorkerUsers() throws Exception;

    PageableResponse<WorkerResponse> getWorkersBySuperUserId(Long superUserId, int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<WorkerResponse> getWorkersBySuperUserWithFilter(Long superUserId, String filter, Pageable pageable);

    Page<WorkerResponse> getWorkersWithFilter(Long superUserId, Boolean isDeleted, Boolean isActive, String keyword, Pageable pageable);
    void updateAccountStatus(Long userId, Boolean isActive);


}
