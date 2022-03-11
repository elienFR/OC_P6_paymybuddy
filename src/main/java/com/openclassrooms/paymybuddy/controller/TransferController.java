package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransferController {

  private static final Logger LOGGER = LogManager.getLogger(TransferController.class);

  @Autowired
  private UserService userService;

  @GetMapping("/transfer")
  public String getTransfer(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
    @RequestParam(value = "size", required = false, defaultValue = "3") int size,
    Model model, Principal principal) throws Exception {
    LOGGER.info("Fetching transfer page...");

    User userFromDB = userService.getUserFromPrincipal(principal);

    List<String> stringListOfBeneficiaries = new ArrayList<>();
    userFromDB.getUserBeneficiaries().forEach(
      userBeneficiary -> {
        stringListOfBeneficiaries.add(
          userBeneficiary.getBeneficiary().getFirstName()
        );
      }
    );

    Paged<Transaction> transactionsPaged = userService
      .getAllPagedTransactionFromUser(pageNumber, size, userFromDB);

    model.addAttribute("beneficiaries", stringListOfBeneficiaries);
    model.addAttribute("transactionPages", transactionsPaged);
    model.addAttribute("userEmail",userFromDB.getEmail());

    return "transfer";
  }
}
