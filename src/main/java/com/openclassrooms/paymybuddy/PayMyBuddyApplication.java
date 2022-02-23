package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.Roles;
import com.openclassrooms.paymybuddy.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.management.relation.Role;
import java.util.List;

@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

  @Autowired
  private AuthorityRepository authorityRepository;

  public static void main(String[] args) {
    SpringApplication.run(PayMyBuddyApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    authorityRepository.findAll().forEach(
      authority -> System.out.println(authority)
    );

  }
}
