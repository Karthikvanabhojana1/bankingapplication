package com.online.bank.model.dto;

import com.online.bank.model.AccountStatus;
import com.online.bank.model.AccountType;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BankAccount {

    private Long id;
    private String number;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal availableBalance;
    private BigDecimal actualBalance;
    private User user;

}
