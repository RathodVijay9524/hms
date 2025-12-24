package com.vijay.User_Master.service.generics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GenericService<T, Req, Res, ID> {
    void create(Req request);
    Res getById(ID id);
    List<Res> getAll();
    Res update(ID id, Req request);
    void delete(ID id);

    // Additional methods
    T save(T entity, MultipartFile file) throws Exception;
    List<Res> findAll();
    Res findById(ID id) throws Exception;
    void softDelete(ID id) throws Exception;
    void restore(ID id) throws Exception;
    void hardDelete(ID id) throws Exception;
    List<Res> getRecycleBin();
    Res copy(ID id) throws Exception;
    Page<Res> findAll(Pageable pageable);
    Page<Res> search(String query, Pageable pageable);
}

