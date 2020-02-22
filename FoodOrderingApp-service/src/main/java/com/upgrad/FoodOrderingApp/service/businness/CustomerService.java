package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import java.time.ZonedDateTime;
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


  /** authenticate incoming login.
   * @param contactNumber contactNUmber
   * @param password password
   * @return CustomerAuthEntity
   * @throws AuthenticationFailedException AuthenticationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity authenticate(final String contactNumber, final String password)
      throws AuthenticationFailedException {
    CustomerEntity customerEntity = customerDAO.getCustomerByContactNumber(contactNumber);
    if (customerEntity == null) {
      throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
    }

    final String encryptedPassword = PasswordCryptographyProvider
        .encrypt(password, customerEntity.getSalt());
    if (encryptedPassword.equals(customerEntity.getPassword())) {
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
      CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
      customerAuthEntity.setCustomer(customerEntity);
      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(8);

      customerAuthEntity
          .setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
      customerAuthEntity.setUuid(customerEntity.getUuid());

      customerAuthEntity.setLoginAt(now);
      customerAuthEntity.setExpiresAt(expiresAt);
      customerAuthEntity.setLogoutAt(null);//case of relogin

      customerDAO.createAuthToken(customerAuthEntity);

      customerDAO.updateCustomer(customerEntity);
      return customerAuthEntity;
    } else {
      throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
    }
  }


  /**Logoff session.
   * @param acessToken access token
   * @return CustomerAuthEntity
   * @throws AuthorizationFailedException authorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity logout(final String acessToken)
      throws AuthorizationFailedException {
    CustomerAuthEntity customerAuthEntity = validateBearerAuthentication(acessToken);
    customerAuthEntity.setExpiresAt(ZonedDateTime.now());
    customerAuthEntity.setLogoutAt(ZonedDateTime.now());
    customerDAO.updateCustomerAuth(customerAuthEntity);
    return customerAuthEntity;
  }

  /**Service to validate Bearer authorization token.
   * @param accessToken accessToken
   * @return CustomerAuthEntity
   * @throws AuthorizationFailedException AuthorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity validateBearerAuthentication(final String accessToken)
      throws AuthorizationFailedException {
    CustomerAuthEntity customerAuthEntity = customerDAO.getCustomerByToken(accessToken);
    if (customerAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    } else if (customerAuthEntity.getLogoutAt() != null) {
      //This is good enough logic that makes the test cases pass
      throw new AuthorizationFailedException("ATHR-002",
          "Customer is logged out. Log in again to access this endpoint.");
    }
    if(ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) >= 0){
      throw new AuthorizationFailedException("ATHR-003",
          "Your session is expired. Log in again to access this endpoint.");
    }
    return customerAuthEntity;
  }

  /** Service to split authorization header to get Beare access token.
   * @param authorization authorization
   * @return beare access token
   * @throws AuthorizationFailedException authorizationFailedException
   */
  public String getBearerAccessToken(final String authorization)
      throws AuthorizationFailedException {

    String[] tokens = authorization.split("Bearer ");
    String accessToken = null;
    try {
      //If the request adheres to 'Bearer accessToken', above split would put token in index 1
      accessToken = tokens[1];
    } catch (IndexOutOfBoundsException ie) {
      //If the request doesn't adheres to 'Bearer accessToken', try to read token in index 0
      accessToken = tokens[0];
      //for scenarios where those users don't adhere to adding prefix of Bearer like test cases
      if (accessToken == null) {
        throw new AuthorizationFailedException("ATH-005", "Use format: 'Bearer accessToken'");
      }
    }

    return accessToken;
  }

  /**Update Customer.
   * @param
   * @return
   * @throws
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity updateCustomer(CustomerEntity customer) {
    customerDAO.updateCustomer(customer);
    return customer;
  }


  /**Get Customer.
   * @param
   * @return
   * @throws
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity getCustomer(final String acessToken)
      throws AuthorizationFailedException {
    CustomerAuthEntity customerAuthEntity = validateBearerAuthentication(acessToken);
    CustomerEntity customer = customerAuthEntity.getCustomer();
    return customer;
  }


}
