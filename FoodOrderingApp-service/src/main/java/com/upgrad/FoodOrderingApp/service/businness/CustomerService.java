package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class CustomerService {

  @Autowired
  private CustomerDAO customerDAO;

  @Autowired
  private PasswordCryptographyProvider passwordCryptographyProvider;

  /**signup business service.
   * @param customerEntity user entity
   * @return CustomerEntity
   * @throws SignUpRestrictedException SignUpRestrictedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {

    //Perform null check for mandatory fields
    if (customerEntity == null || customerEntity.getFirstName() == null || customerEntity.getContactnumber()==null
        || customerEntity.getEmail() == null
        || customerEntity.getPassword() == null
        || customerEntity.getFirstName().isEmpty()
        || customerEntity.getEmail().isEmpty() || customerEntity.getPassword().isEmpty()
        || customerEntity.getContactnumber().isEmpty()
    ) {
      throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
    }

    //If customer already exists with same contact number throw respective exceptions
    CustomerEntity existingUser1 = customerDAO.getCustomerByContactNumber(customerEntity.getContactnumber());
    if (existingUser1 != null) {
      throw new SignUpRestrictedException("SGR-001",
          "This contact number is already registered! Try other contact number.");
    }

    //(SGR-002) and message (Invalid email-id format!).
    if(!isValidEmail(customerEntity.getEmail())){
      throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
    }

    // (SGR-003) and message (Invalid contact number!)
    if(!isValidContactNumber(customerEntity.getContactnumber())){
      throw new SignUpRestrictedException("SGR-003", "Invalid contact number");
    }

    // (SGR-004) and message (Weak password!)
    if(!isWeakPassword(customerEntity.getPassword())){
      throw new SignUpRestrictedException("SGR-004", "Weak password!");
    }

    String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
    customerEntity.setSalt(encryptedText[0]);
    customerEntity.setPassword(encryptedText[1]);
    return customerDAO.createCustomer(customerEntity);
  }
  //valid email  determining logic
  private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Z0-9]+@[A-Z0-9]+\\.[A-Z0-9]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
    if (email == null)
      return false;
    return pat.matcher(email).matches();
  }

  // valid contact number determining logic
  private boolean isValidContactNumber(String contactNumber) {
    String contactNUmberRegex = "\\d{10}";

    Pattern pat = Pattern.compile(contactNUmberRegex);
    if (contactNumber == null)
      return false;
    return pat.matcher(contactNumber).matches();
  }

  //Strong password determining logic
  private boolean isWeakPassword(String password) {

    String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,}$";

    Pattern pat = Pattern.compile(passwordRegex);
    if (password == null)
      return false;
    return pat.matcher(password).matches();
  }


}
