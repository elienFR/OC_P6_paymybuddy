package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransferController {

  @Autowired
  private UserService userService;

  @GetMapping("/transfer")
  public String getTransfer(Model model, Principal user) {
    String userEmail = user.getName();
    User userFromDB = userService.getUserByEmail(userEmail).get();

    List<String> stringListOfBeneficiaries = new ArrayList<>();
    userFromDB.getUserBeneficiaries().forEach(
      userBeneficiary -> {
        stringListOfBeneficiaries.add(
          userBeneficiary.getBeneficiary().getFirstName()
        );
      }
    );

    List<Transaction> transactionsList = userFromDB.getAccount().getTransactionsFromThisAccount();


    model.addAttribute("beneficiaries",stringListOfBeneficiaries);
    model.addAttribute("transactions", transactionsList);
    return "transfer";
  }
}
