package com.vijay.User_Master.service.generics;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface iCrudService<Req,Res,ID> {
    CompletableFuture<Res> create(Req request);
    CompletableFuture<Res> getById(ID id);
    CompletableFuture<Set<Res>> getAll();
    CompletableFuture<Res> update(ID id, Req request);
    CompletableFuture<Boolean> delete(ID id);

}