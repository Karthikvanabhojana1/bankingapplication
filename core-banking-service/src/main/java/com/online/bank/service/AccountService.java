package com.online.bank.service;

import com.online.bank.exception.EntityNotFoundException;
import com.online.bank.model.dto.BankAccount;
import com.online.bank.model.dto.UtilityAccount;
import com.online.bank.model.entity.BankAccountEntity;
import com.online.bank.model.entity.UtilityAccountEntity;
import com.online.bank.model.mapper.BankAccountMapper;
import com.online.bank.model.mapper.UtilityAccountMapper;
import com.online.bank.repository.BankAccountRepository;
import com.online.bank.repository.UtilityAccountRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private BankAccountMapper bankAccountMapper = new BankAccountMapper();
    private UtilityAccountMapper utilityAccountMapper = new UtilityAccountMapper();

    private final BankAccountRepository bankAccountRepository;
    private final UtilityAccountRepository utilityAccountRepository;

    public BankAccount readBankAccount(String accountNumber) {
        BankAccountEntity entity = bankAccountRepository.findByNumber(accountNumber).orElseThrow(EntityNotFoundException::new);
        return bankAccountMapper.convertToDto(entity);
    }

    public UtilityAccount readUtilityAccount(String provider) {
        UtilityAccountEntity utilityAccountEntity = utilityAccountRepository.findByProviderName(provider).orElseThrow(EntityNotFoundException::new);
        return utilityAccountMapper.convertToDto(utilityAccountEntity);
    }

    public UtilityAccount readUtilityAccount(Long id) {
        return utilityAccountMapper.convertToDto(utilityAccountRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

}
