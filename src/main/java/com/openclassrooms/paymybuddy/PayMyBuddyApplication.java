package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.repository.AuthorityRepository;
import com.openclassrooms.paymybuddy.repository.UserAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

  @Autowired
  private AuthorityRepository authorityRepository;

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  public static void main(String[] args) {
    SpringApplication.run(PayMyBuddyApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

  }
}
