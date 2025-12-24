package com.vijay.User_Master.service.generics;

import com.vijay.User_Master.dto.PageableResponse;
import org.springframework.data.domain.Pageable;


public interface AdvancedCrudService<Res, ID> {
    Res findById(ID id) throws Exception;
    void softDelete(ID id) throws Exception;// findById(id); setIsDeleted(true);  setDeletedOn(LocalDateTime.now());
    void restore(ID id) throws Exception;//findById(id) setIsDeleted(false);setDeletedOn(null);
    void hardDelete(ID id) throws Exception;
    void emptyRecycleBin(Pageable pageable);  //findByCreatedByAndIsDeletedTrue
    PageableResponse<Res> getRecycleBin(Pageable pageable);  //CommonUtil.getLoggedInUser().getId();findByCreatedByAndIsDeletedTrue(userId);
    Res copy(ID id) throws Exception;
    PageableResponse<Res> findAll(Pageable pageable);
    PageableResponse<Res> searchItemsWithDynamicFields(String query, Pageable pageable);
}
