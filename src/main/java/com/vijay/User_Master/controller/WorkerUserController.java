package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.FavouriteEntryResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.service.WorkerUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/workers")
@AllArgsConstructor
public class WorkerUserController {

    private final WorkerUserService workerUserService;

    /**
     * 🛠️ Worker Filtering API by SuperUser ID
     *
     * ✅ Get all workers created by a specific SuperUser:
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter
     *
     * ✅ Get only deleted workers:
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?isDeleted=true
     *
     * ✅ Get only active workers:
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?isActive=true
     *
     * ✅ Search workers by keyword (name, email, username, phone):
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?keyword=karan
     *
     * ✅ Combine filters: active + keyword search:
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?isActive=true&keyword=manoj
     *
     * ✅ View expired workers (not deleted but inactive):
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?isDeleted=false&isActive=false
     *
     * ✅ Support pagination (default page=0, size=10):
     *     GET /api/v1/workers/superuser/{superUserId}/advanced-filter?page=1&size=5
     */
    @GetMapping("/superuser/{superUserId}/advanced-filter")
    public ResponseEntity<?> getWorkersWithAdvancedFilter(
            @PathVariable Long superUserId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());
        Page<WorkerResponse> response = workerUserService.getWorkersWithFilter(
                superUserId, isDeleted, isActive, keyword, pageable
        );
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get filtered workers created by a specific super user.
     *
     * @param superUserId ID of the super user (creator of workers)
     * @param type Filter type: all, active, deleted, expired
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     * @return Paginated filtered worker responses
     *
     * 📌 URLs:
     * GET /api/v1/workers/superuser/{superUserId}/filter?type=all
     * GET /api/v1/workers/superuser/{superUserId}/filter?type=active
     * GET /api/v1/workers/superuser/{superUserId}/filter?type=deleted
     * GET /api/v1/workers/superuser/{superUserId}/filter?type=expired
     */
    @GetMapping("/superuser/{superUserId}/filter")
    public ResponseEntity<PageableResponse<WorkerResponse>> getFilteredWorkersBySuperUser(
            @PathVariable Long superUserId,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageableResponse<WorkerResponse> response =
                workerUserService.getWorkersBySuperUserWithFilter(superUserId, type, pageable);
        return ResponseEntity.ok(response);
    }

    // 🔐 Get workers created by a super user (with pagination + sorting)
    @GetMapping("/superuser/{superUserId}")
    public ResponseEntity<PageableResponse<WorkerResponse>> getWorkersBySuperUser(
            @PathVariable Long superUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageableResponse<WorkerResponse> response = workerUserService
                .getWorkersBySuperUserId(superUserId, page, size, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getWorkerUserById(@PathVariable Long id) throws Exception {
        return ExceptionUtil.createBuildResponse(workerUserService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/soft/{id}")
    public ResponseEntity<?> softDeleteUserById(@PathVariable Long id) throws Exception {
        workerUserService.softDelete(id);
        return ExceptionUtil.createBuildResponseMessage("User Deleted, User Available in RecycleBin bean", HttpStatus.OK);
    }

    @GetMapping("/restore/{id}")
    public ResponseEntity<?> restoreDeleteUserById(@PathVariable Long id) throws Exception {
        workerUserService.restore(id);
        return ExceptionUtil.createBuildResponseMessage("User Restored Successfully,", HttpStatus.OK);
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<?> deletingUserByIdFromRecycleBin(@PathVariable Long id) throws Exception {
        workerUserService.hardDelete(id);
        return ExceptionUtil.createBuildResponseMessage("User Deleted Permanently,", HttpStatus.OK);
    }*/

    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveWorkerUser() {
        return ExceptionUtil.createBuildResponse(workerUserService.findAllActiveUsers(), HttpStatus.OK);
    }

    @GetMapping("/recycle")
    public ResponseEntity<?> getSoftDeletedUser(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        PageableResponse<WorkerResponse> pages = workerUserService.getRecycleBin(pageable);
        return ExceptionUtil.createBuildResponse(pages, HttpStatus.OK);
    }

    @DeleteMapping("/recycle/delete-all")
    public ResponseEntity<?> deleteAllUserSuperUserId(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        workerUserService.emptyRecycleBin(pageable);
        return ExceptionUtil.createBuildResponse("Deleted All item form Recycle Bin..! now Empty Recycle Bin. !", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        PageableResponse<WorkerResponse> pages = workerUserService.findAll(pageable);
        return ExceptionUtil.createBuildResponse(pages, HttpStatus.OK);
    }

    @GetMapping("/pageable")
    public ResponseEntity<?> getActiveUserPageableWithSortAndSearch(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<WorkerResponse> listUsers = workerUserService.getAllActiveUserWithSortingSearching(pageNumber, pageSize, sortBy, sortDir);
        return ExceptionUtil.createBuildResponse(listUsers, HttpStatus.OK);
    }

    // http://localhost:9091/api/v1/workers/search?query=ajay&page=0&size=10&sort=name,asc
    @GetMapping("/search")
    public ResponseEntity<?> searchWorkers(@RequestParam("query") String query, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        try {
            PageableResponse<WorkerResponse> PageableResponse = workerUserService.searchItemsWithDynamicFields(query, pageable);
            return ExceptionUtil.createBuildResponse(PageableResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/favorite-list")
    public ResponseEntity<?> getAllFavoriteWorkerUser() throws Exception {
        List<FavouriteEntryResponse> userFavoriteWorkerUsers = workerUserService.getUserFavoriteWorkerUsers();
        return ExceptionUtil.createBuildResponse(userFavoriteWorkerUsers, HttpStatus.OK);
    }

    @PostMapping("/favorite/{workerId}")
    public ResponseEntity<?> favoriteWorkerUser(@PathVariable Long workerId) throws Exception {
        workerUserService.favoriteWorkerUser(workerId);
        return ExceptionUtil.createBuildResponseMessage("Worker added to favorites successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<?> unFavoriteWorkerUser(@PathVariable Long id) throws Exception {
        workerUserService.unFavoriteWorkerUser(id);
        return ExceptionUtil.createBuildResponseMessage("Worker removed from favorites successfully!", HttpStatus.OK);

    }

    // 🔁 Soft delete (move to recycle bin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteWorker(@PathVariable Long id) {
        log.info("Soft deleting worker ID: {}", id);
        try {
            workerUserService.softDelete(id);
            return ExceptionUtil.createBuildResponse("Worker soft-deleted", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during soft delete for worker ID: {}", id, e);
            return ExceptionUtil.createBuildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ♻️ Restore from recycle bin
    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreWorker(@PathVariable Long id) {
        log.info("Restoring worker ID: {}", id);
        try {
            workerUserService.restore(id);
            return ExceptionUtil.createBuildResponse("Worker restored", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during restore for worker ID: {}", id, e);
            return ExceptionUtil.createBuildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ❌ Hard delete (permanently delete)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteWorker(@PathVariable Long id) {
        log.info("Permanently deleting worker ID: {}", id);
        try {
            workerUserService.hardDelete(id);
            return ExceptionUtil.createBuildResponse("Worker permanently deleted", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error during hard delete for worker ID: {}", id, e);
            return ExceptionUtil.createBuildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateWorkerStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive
    ) {
        log.info("Updating account status for worker ID: {} to {}", id, isActive);
        try {
            workerUserService.updateAccountStatus(id, isActive);
            return ExceptionUtil.createBuildResponse("Worker account status updated", HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            log.error("Worker not found: {}", id);
            return ExceptionUtil.createBuildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Error updating account status for worker ID: {}", id, ex);
            return ExceptionUtil.createBuildResponse("Failed to update worker status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
