package com.online.bank.finance.service.rest;

import com.online.bank.finance.model.rest.response.UserResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "core-banking-service")
public interface BankingCoreRestClient {

    @GetMapping("/api/v1/user/{identification}")
    UserResponse readUser(@PathVariable("identification") String identification);

}
