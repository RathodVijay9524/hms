package com.vijay.User_Master.service.generics;

import java.util.List;

public interface BasicCrudService<Req, Res, ID> {
    void create(Req request);
    Res getById(ID id);
    List<Res> getAll();
    Res update(ID id, Req request);
    void delete(ID id);
}

