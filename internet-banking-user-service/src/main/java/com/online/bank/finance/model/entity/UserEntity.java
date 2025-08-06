package com.online.bank.finance.model.entity;

import com.online.bank.finance.model.dto.AuditAware;
import com.online.bank.finance.model.dto.Status;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity extends AuditAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authId;

    private String identification;

    @Enumerated(EnumType.STRING)
    private Status status;

}
