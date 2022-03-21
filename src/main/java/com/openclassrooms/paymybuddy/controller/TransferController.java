package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

  private static final Logger LOGGER = LogManager.getLogger(TransferController.class);

  @Autowired
  private UserService userService;

  @GetMapping
  public String getTransfer(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
    @RequestParam(value = "size", required = false, defaultValue = "3") int size,
    Model model, Principal principal) throws Exception {
    LOGGER.info("Fetching transfer page...");

    User userFromDB = userService.getUserFromPrincipal(principal);

    List<UserBeneficiary> listOfUserBeneficiaries = userFromDB.getUserBeneficiaries();

    Paged<Transaction> transactionsPaged = userService
      .getAllPagedTransactionFromUser(pageNumber, size, userFromDB);

    model.addAttribute("userBeneficiaries", listOfUserBeneficiaries);
    model.addAttribute("transactionPages", transactionsPaged);
    model.addAttribute("userEmail", userFromDB.getEmail());

    return "transfer";
  }

  @Transactional
  @PostMapping("/successful")
  public String postTransferSuccessful(
    Principal principal,
    Model model,
    float amount,
    String descriptionOfTransaction,
    String userBeneficiaryEmail
  ) throws Exception {

    LOGGER.info("Starting payment /transfer/successful...");

    StringBuffer errorStatement = new StringBuffer();

    if (userBeneficiaryEmail == null) {
      LOGGER.warn("Beneficiary mail is null redirect to /transfer without making payment...");
      errorStatement.append("&nullBeneficiary");
    }

    if(amount<=0) {
      LOGGER.warn("Cannot perform a zero or negative money transfer.");
      errorStatement.append("&zeroAmount");
    }

    if(errorStatement.toString().isBlank()){
      LOGGER.info("Initiating payment -> from : "
        + userService.getUserFromPrincipal(principal).getEmail()
        + " to : " + userBeneficiaryEmail);

        LOGGER.info("Performing transfer of amount " + amount);
        userService.makeATransaction(
          userService.getUserFromPrincipal(principal),
          userService.getUserByEmail(userBeneficiaryEmail).get(),
          descriptionOfTransaction,
          amount
        );

        model.addAttribute("userEmail", userService.getUserFromPrincipal(principal).getEmail());
        model.addAttribute("userBeneficiaryEmail", userBeneficiaryEmail);
        model.addAttribute("amount",amount);
        model.addAttribute("currency",userService.getUserFromPrincipal(principal).getAccount().getCurrencyCode().toString());
        return "transferSuccessful";
      }

    return "redirect:/transfer?" + errorStatement;
  }

  @GetMapping("/addConnection")
  public String getAddConnectionPage(Principal principal, Model model) throws Exception {
    User userFromDB = userService.getUserFromPrincipal(principal);
    model.addAttribute("userEmail", userFromDB.getEmail());
    model.addAttribute("email", null);
    return "addConnection";
  }

  @PostMapping("/addConnection")
  public String postAddConnectionPage(
    Principal principal,
    Model model,
    String connectionEmail
  ) throws Exception {
    User userFromDB = userService.getUserFromPrincipal(principal);
    if (!userFromDB.getEmail().equals(connectionEmail)) {
      if (userService.existsByEmail(connectionEmail)) {
        UserBeneficiary userBeneficiary = new UserBeneficiary();
        userBeneficiary.setBeneficiary(
          userService.getUserByEmail(connectionEmail).get()
        );
        userFromDB.addUserBeneficiary(userBeneficiary);
        userService.save(userFromDB);
        return "redirect:/transfer?addConnectionSuccessful";
      }
    }
    model.addAttribute("errorOwnEmail", true);
    model.addAttribute("userEmail", userFromDB.getEmail());
    model.addAttribute("email", connectionEmail);
    return "addConnection";
  }
}
