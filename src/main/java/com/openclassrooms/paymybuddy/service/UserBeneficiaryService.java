package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.repository.UserBeneficiaryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserBeneficiaryService {

  private static final Logger LOGGER = LogManager.getLogger(UserBeneficiaryService.class);

  @Autowired
  private UserBeneficiaryRepository userBeneficiaryRepository;

  /**
   * This method saves an userbeneficiary object into the database.
   *
   * @param userBeneficiary is the userbeneficiary to save.
   * @return the saved userbeneficiary with its real id from db.
   */
  public UserBeneficiary save(UserBeneficiary userBeneficiary) {
    LOGGER.info("Saving user beneficiary into db...");
    return userBeneficiaryRepository.save(userBeneficiary);
  }

  /**
   * This method deletes an userbeneficiary instance from the database.
   *
   * @param userBeneficiary is the userbeneficiary to delete.
   */
  public void delete(UserBeneficiary userBeneficiary) {
    LOGGER.info("Deleting user beneficiary into db...");
    userBeneficiaryRepository.delete(userBeneficiary);
  }

}
