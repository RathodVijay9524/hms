package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.Helper;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.FavouriteEntryResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.FavouriteEntry;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.FavouriteEntryRepo;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.WorkerUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class WorkerUserServiceImpl implements WorkerUserService {

    private final WorkerRepository workerRepository;
    private final ModelMapper mapper;
    private final FavouriteEntryRepo favouriteEntryRepo;

    // find user by id ... for Worker Entity
    @Override
    public WorkerResponse findById(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        return mapper.map(worker, WorkerResponse.class);
    }

    // You can delete Item ... it saves at recycle bin.
    @Override
    public void softDelete(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) {
            throw new IllegalArgumentException("Worker with ID " + id + " is already deleted.");
        }
        worker.setDeleted(true);
        worker.setDeletedOn(LocalDateTime.now());
        // Set accountStatus to inactive
        AccountStatus accountStatus = worker.getAccountStatus();
        accountStatus.setIsActive(false);
        worker.setAccountStatus(accountStatus);
        workerRepository.save(worker);
    }

    // You can restore Item form recycle bin
    @Override
    public void restore(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) { // Worker is deleted, so restore it
            worker.setDeleted(false);
            worker.setDeletedOn(null);

            AccountStatus accountStatus = worker.getAccountStatus();
            accountStatus.setIsActive(true); // Set accountStatus to Active
            worker.setAccountStatus(accountStatus);

            workerRepository.save(worker); // Save the restored worker
        } else {
            throw new IllegalArgumentException("Worker with ID " + id + " is already present.");
        }
    }

    // You can delete Item form recycle bin - deleting permanently
    @Override
    public void hardDelete(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) {
            workerRepository.delete(worker); // deleting form recycle bin
        } else {
            throw new IllegalArgumentException("Sorry You can't hard delete Directly");
        }
    }

    @Override
    public WorkerResponse copy(Long aLong) throws Exception {
        return null;
    }

    // find all User from Worker user Entity
    @Override
    public PageableResponse<WorkerResponse> findAll(Pageable pageable) {
        Page<Worker> pages = workerRepository.findAll(pageable);
        return Helper.getPageableResponse(pages, WorkerResponse.class);
    }

    @Override
    public PageableResponse<WorkerResponse> searchItemsWithDynamicFields(String query, Pageable pageable) {
        Specification<Worker> spec = (root, criteriaQuery, criteriaBuilder) -> {
            String likePattern = "%" + query + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), likePattern),
                    criteriaBuilder.like(root.get("username"), likePattern),
                    criteriaBuilder.like(root.get("email"), likePattern),
                    criteriaBuilder.like(root.get("phoNo"), likePattern),
                    criteriaBuilder.like(root.get("accountStatus").get("isActive").as(String.class), likePattern));
        };
        Page<Worker> workerPage = workerRepository.findAll(spec, pageable);
        return Helper.getPageableResponse(workerPage, WorkerResponse.class);
    }

    @Override
    public PageableResponse<WorkerResponse> getAllActiveUserWithSortingSearching(int pageNumber, int pageSize, String sortBy, String sortDir) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long loggedInUserId = loggedInUser.getId();
        log.info("Logged in user ID: {}", loggedInUserId);
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Worker> allPages = workerRepository.findByCreatedByAndIsDeletedFalse(loggedInUserId, pageable);
        return Helper.getPageableResponse(allPages, WorkerResponse.class);
    }


    @Override
    public void emptyRecycleBin(Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Page<Worker> pages = workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable);
        if (pages.isEmpty()) {
            throw new ResourceNotFoundException("Recycle Bin", "Workers", "No deleted workers found for the current user.");
        }
        if (!ObjectUtils.isEmpty(pages)) {
            workerRepository.deleteAll(pages);
        }
    }

    // find all only Active users by superuser id or loggedInUser userId
    @Override
    public List<WorkerResponse> findAllActiveUsers() {
        //CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        List<Worker> userLists = workerRepository.findAll();
        return userLists.stream()
                .map((worker -> mapper.map(worker, WorkerResponse.class)))
                .collect(Collectors.toList());
    }


    // find all only Deleted users by superuser id or loggedInUser userId
    @Override
    public PageableResponse<WorkerResponse> getRecycleBin(Pageable pageable) { // restore delete item from RecycleBin
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Page<Worker> users = workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable);

        if (workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable).isEmpty()) {
            throw new ResourceNotFoundException("Recycle Bin", "Workers", "No deleted workers found for the current user.");
        }
        return Helper.getPageableResponse(users, WorkerResponse.class);
    }

    @Override
    public void favoriteWorkerUser(Long workerId) throws Exception {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", workerId));
        FavouriteEntry favouriteEntry = FavouriteEntry.builder()
                .worker(worker)
                .userId(loggedInUser.getId())
                .build(); // Assuming the user who favorites is the worker's associated user .build();
        favouriteEntryRepo.save(favouriteEntry);
    }

    @Override
    public void unFavoriteWorkerUser(Long id) throws Exception {
        FavouriteEntry favouriteEntry = favouriteEntryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "ID", id));
        favouriteEntryRepo.delete(favouriteEntry);
    }

    @Override
    public List<FavouriteEntryResponse> getUserFavoriteWorkerUsers() throws Exception {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        List<FavouriteEntry> favouriteWorkers = favouriteEntryRepo.findByUserId(loggedInUser.getId());
        return favouriteWorkers.stream()
                .map((worker) -> mapper.map(worker, FavouriteEntryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageableResponse<WorkerResponse> getWorkersBySuperUserId(Long superUserId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();

        if (!superUserId.equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to access this user's workers.");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Worker> workerPage = workerRepository.findByUser_Id(superUserId, pageable);

        return Helper.getPageableResponse(workerPage, WorkerResponse.class);
    }

    @Override
    public PageableResponse<WorkerResponse> getWorkersBySuperUserWithFilter(Long superUserId, String filter, Pageable pageable) {
        Page<Worker> page = switch (filter.toLowerCase()) {
            case "active" ->
                    workerRepository.findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveTrue(superUserId, pageable);
            case "deleted" -> workerRepository.findByUser_IdAndIsDeletedTrue(superUserId, pageable);
            case "expired" ->
                    workerRepository.findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveFalse(superUserId, pageable);
            default -> workerRepository.findByUser_Id(superUserId, pageable);
        };

        return Helper.getPageableResponse(page, WorkerResponse.class);
    }

    @Override
    public Page<WorkerResponse> getWorkersWithFilter(Long superUserId, Boolean isDeleted, Boolean isActive, String keyword, Pageable pageable) {
        Page<Worker> workers;

        if (StringUtils.hasText(keyword)) {
            workers = workerRepository.searchWorkersBySuperUserWithKeyword(keyword, isDeleted, isActive, superUserId, pageable);
        } else {
            if (isDeleted != null && isActive != null) {
                workers = workerRepository.findByUser_IdAndIsDeletedAndAccountStatus_IsActive(superUserId, isDeleted, isActive, pageable);
            } else if (isDeleted != null) {
                workers = workerRepository.findByUser_IdAndIsDeleted(superUserId, isDeleted, pageable);
            } else if (isActive != null) {
                workers = workerRepository.findByUser_IdAndAccountStatus_IsActive(superUserId, isActive, pageable);
            } else {
                workers = workerRepository.findByUser_Id(superUserId, pageable);
            }
        }

        return workers.map(worker -> mapper.map(worker, WorkerResponse.class));
    }

    @Override
    public void updateAccountStatus(Long userId, Boolean isActive) {

        Worker worker = workerRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        AccountStatus accountStatus = worker.getAccountStatus();
        if (accountStatus == null) {
            accountStatus = new AccountStatus(); // New status
        }

        accountStatus.setIsActive(isActive); // update the status
        worker.setAccountStatus(accountStatus); // assign to user

        workerRepository.save(worker); // cascade should handle persist/update

    }


}
                   