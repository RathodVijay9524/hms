package com.vijay.User_Master.service;

public interface HomeService {

    public Boolean verifyAccount(Long uid,String verificationCode) throws Exception;
}
