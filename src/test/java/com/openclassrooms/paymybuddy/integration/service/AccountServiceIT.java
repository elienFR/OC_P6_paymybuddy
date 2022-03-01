package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AccountServiceIT {

  @Autowired
  private AccountService accountService;



}
