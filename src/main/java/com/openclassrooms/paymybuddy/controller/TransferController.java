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
    Model model, Principal principal
  ) throws Exception {
    LOGGER.info("GET : Fetching /transfer page...");

    User userFromDB = userService.getUserFromPrincipal(principal);

    List<UserBeneficiary> listOfUserBeneficiaries = userFromDB.getUserBeneficiaries();

    Paged<Transaction> transactionsPaged = userService
      .getAllPagedTransactionFromUser(pageNumber, size, userFromDB);

    model.addAttribute("accountId", userFromDB.getAccount().getId());
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

    LOGGER.info("POST : Starting payment /transfer/successful...");

    StringBuffer errorStatement = new StringBuffer();

    if (userBeneficiaryEmail == null) {
      LOGGER.warn("Beneficiary mail is null redirect to /transfer without making payment...");
      errorStatement.append("&nullBeneficiary");
    }

    if (amount <= 0) {
      LOGGER.warn("Cannot perform a zero or negative money transfer.");
      errorStatement.append("&zeroAmount");
    }

    if (errorStatement.toString().isBlank()) {
      LOGGER.info("Initiating payment -> from : "
        + userService.getUserFromPrincipal(principal).getEmail()
        + " to : " + userBeneficiaryEmail);

      LOGGER.info("Performing transfer of amount " + amount);

      // --------------- MODIFY THIS TO APPLY FEES OR NOT  ---------------
      boolean applyFees = true;
      // -----------------------------------------------------------------
      userService.makeATransaction(
        userService.getUserFromPrincipal(principal),
        userService.getUserByEmail(userBeneficiaryEmail).get(),
        descriptionOfTransaction,
        amount,
        applyFees
      );

      model.addAttribute("userEmail", userService.getUserFromPrincipal(principal).getEmail());
      model.addAttribute("userBeneficiaryEmail", userBeneficiaryEmail);
      model.addAttribute("amount", amount);
      model.addAttribute("currency", userService.getUserFromPrincipal(principal).getAccount().getCurrencyCode().toString());
      return "transferSuccessful";
    }

    return "redirect:/transfer?" + errorStatement;
  }

  @GetMapping("/addConnection")
  public String getAddConnectionPage(Principal principal, Model model) throws Exception {

    LOGGER.info("Fetching /transfer/addConnection page...");

    User userFromDB = userService.getUserFromPrincipal(principal);
    model.addAttribute("userEmail", userFromDB.getEmail());
    model.addAttribute("email", null);
    return "addConnection";
  }

  @Transactional
  @PostMapping("/addConnection")
  public String postAddConnectionPage(
    Principal principal,
    Model model,
    String connectionEmail
  ) throws Exception {
    LOGGER.info("Posting on /transfer/addConnection page...");
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
      } else {
        model.addAttribute("userInexistant", true);
      }
    } else {
      model.addAttribute("errorOwnEmail", true);
    }
    model.addAttribute("userEmail", userFromDB.getEmail());
    model.addAttribute("email", connectionEmail);
    return "addConnection";
  }

  @GetMapping("/bankTransfer")
  public String getBankTransfer(
    Principal principal,
    Model model)
    throws Exception {
    LOGGER.info("Fetching /transfer/bankTransfer page...");

    User userFromDB = userService.getUserFromPrincipal(principal);


    model.addAttribute("userEmail", userFromDB.getEmail());
    return "transferBankTransfer";
  }

  @Transactional
  @PostMapping("/bankTransfer/successful")
  public String postBankTransfer(Principal principal,
                                 Model model,
                                 String iban,
                                 String swiftCode,
                                 float amount,
                                 String description) throws Exception {
    LOGGER.info("POST : Starting bank transaction on /transfer/bankTransfer/successful...");

    User userFromDB = userService.getUserFromPrincipal(principal);
    StringBuffer errorStatement = new StringBuffer();

    if (amount <= 0) {
      errorStatement.append("&zeroAmount");
    }

    if ((userFromDB.getAccount().getBalance() - amount) < 0) {
      errorStatement.append("&balanceTooLow");
    }

    // length 16 is minimal and its is for belgium
    if (iban == null || iban.length() < 16) {
      errorStatement.append("&wrongIban");
    }

    if (swiftCode == null || swiftCode.length() < 8) {
      errorStatement.append("&wrongSwiftCode");
    }

    if (errorStatement.toString().isBlank()) {

      LOGGER.info("Initiating bank transfert -> from : "
        + userService.getUserFromPrincipal(principal).getEmail()
        + " to IBAN : " + iban + " swift code : " + swiftCode);

      LOGGER.info(
        "Performing transfer of amount " + amount + " "
          + userFromDB.getAccount().getCurrencyCode().toString()
      );

      // TODO : This is where the real bank transaction should be made. (SWIFT)

      userService.makeABankTransaction(
        userService.getUserFromPrincipal(principal),
        iban,
        swiftCode,
        description,
        amount
      );

      //This is not a beneficiary email but un IBAN so we construct it :
      String userBeneficiaryEmail = "IBAN : " + iban + ", BIC :" + swiftCode;

      model.addAttribute("userEmail", userService.getUserFromPrincipal(principal).getEmail());
      model.addAttribute("userBeneficiaryEmail", userBeneficiaryEmail);
      model.addAttribute("amount", amount);
      model.addAttribute("currency", userService.getUserFromPrincipal(principal).getAccount().getCurrencyCode().toString());
      return "transferSuccessful";
    } else {
      return "redirect:/transfer/bankTransfer?" + errorStatement;
    }
  }

  @GetMapping("/accountCredit")
  public String getAccountCredit(Principal principal, Model model) throws Exception {
    LOGGER.info("Fetching /transfer/accountCredit page...");

    User userFromDB = userService.getUserFromPrincipal(principal);

    model.addAttribute("userEmail", userFromDB.getEmail());

    return "transferAccountCredit";
  }


  @PostMapping("/accountCredit/successful")
  public String postAccountCredit(Principal principal,
                                  Model model,
                                  String creditCardNumber,
                                  String expirationDate,
                                  String crypto,
                                  float amount,
                                  String description) throws Exception {
    LOGGER.info("POST : Starting Credit account process /transfer/accountCredit/successful...");

    StringBuffer errorStatement = new StringBuffer();

    if (amount <= 0) {
      errorStatement.append("&zeroAmount");
    }

    // check ISO/IEC 7812
    // Can add this method to check card validity with luhn algo
    // LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(creditCardNumber);
    if (!(creditCardNumber.length() >= 8 && creditCardNumber.length() <= 19)) {
      errorStatement.append("&wrongCreditCardNumber");
    }

    if (crypto == null || (crypto.length() < 3 && crypto.length() > 4)) {
      errorStatement.append("&wrongCryptogram");
    }

    if (expirationDate == null || expirationDate.length() != 4) {
      errorStatement.append("&wrongExpirationDate");
    }

    if (errorStatement.toString().isBlank()) {

      //TODO : make real transaction here (SWIFT)
      //TODO : instantiate in database the credit_account transaction
      userService.makeAccountCredit(
        userService.getUserFromPrincipal(principal),
        amount,
        description,
        creditCardNumber,
        crypto,
        expirationDate
      );


      model.addAttribute("userEmail", "Your Credit Card");
      model.addAttribute("userBeneficiaryEmail", "Your PayMyBuddy Account");
      model.addAttribute("amount", amount);
      model.addAttribute("currency", userService.getUserFromPrincipal(principal).getAccount().getCurrencyCode().toString());
      return "transferSuccessful";
    }

    return "redirect:/transfer/accountCredit?" + errorStatement;
  }


}

