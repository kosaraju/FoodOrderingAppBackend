package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import com.upgrad.FoodOrderingApp.service.util.UtilityProvider;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  /**
   * Handler to signup for any prospective customer to get registered.
   *
   * @param signupCustomerRequest SignupCustomerRequest
   * @return SingupCustomerResponse
   * @throws SignUpRestrictedException SignUpRestrictedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/customer/signup",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupCustomerResponse> customerSignup(@RequestBody final SignupCustomerRequest signupCustomerRequest)
      throws SignUpRestrictedException {

    //Fetch details from signupCustomerRequest and set in CustomerEntity instance
    //Perform null check for mandatory fields
    if (signupCustomerRequest == null || signupCustomerRequest.getFirstName() == null
        || signupCustomerRequest.getContactNumber() == null
        || signupCustomerRequest.getEmailAddress() == null
        || signupCustomerRequest.getPassword() == null
        || signupCustomerRequest.getFirstName().isEmpty()
        || signupCustomerRequest.getEmailAddress().isEmpty() || signupCustomerRequest.getPassword()
        .isEmpty()
        || signupCustomerRequest.getContactNumber().isEmpty()
    ) {
      throw new SignUpRestrictedException("SGR-005",
          "Except last name all fields should be filled");
    }
    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setUuid(UUID.randomUUID().toString());
    customerEntity.setFirstName(signupCustomerRequest.getFirstName());
    customerEntity.setLastName(signupCustomerRequest.getLastName());
    customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
    customerEntity.setPassword(signupCustomerRequest.getPassword());
    customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
    customerEntity.setSalt("1234abc"); // will get overwritten in service class

    //Invoke business Service to signup & return SignupCustomerResponse
    final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
    SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid())
        .status("CUSTOMER SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
  }


  /**
   * Handler to login
   *
   * @param authorization access token
   * @return SigninResponse
   * @throws AuthenticationFailedException AuthenticationFailedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/customer/login",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<LoginResponse> login(
      @RequestHeader("authorization") final String authorization)
      throws AuthenticationFailedException {

    //Split authorization header to get username and password
    byte[] decode = null;
    String[] tokens = authorization.split("Basic ");

    try {
      decode = Base64.getDecoder().decode(tokens[1]);
    } catch (IllegalArgumentException ile) {
      throw new AuthenticationFailedException("ATH-003",
          "Incorrect format of decoded customer name and password");
    } catch (IndexOutOfBoundsException ie) {
      throw new AuthenticationFailedException("ATH-003",
          "Incorrect format of decoded customer name and password");
    }
    String decodedText = new String(decode);
    String[] decodedArray = decodedText.split(":");
    String contactNumber, password;
    try{
       contactNumber = decodedArray[0];
       password = decodedArray[1];

    }catch(Exception e){
      throw new AuthenticationFailedException("ATH-003",
          "Incorrect format of decoded customer name and password");

    }

    //Invoke Authentication Service
    CustomerAuthEntity customerAuthEntity = customerService
        .authenticate(contactNumber, password);

    //Get Customer details
    CustomerEntity customer = customerAuthEntity.getCustomer();

    //Fill LoginResponse and return
    LoginResponse loginResponse = new LoginResponse().id(customer.getUuid())
        .firstName(customer.getFirstName()).lastName(customer.getLastName())
        .contactNumber(customer.getContactNumber()).emailAddress(customer.getEmail())
        .message("LOGGED IN SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", customerAuthEntity.getAccessToken());
    List<String> header = new ArrayList<>();
    header.add("access-token");
    headers.setAccessControlExposeHeaders(header);
    return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
  }


  /**
   * Handler to logout.
   *
   * @param authorization access token
   * @return LogoutResponse
   * @throws AuthenticationFailedException AuthenticationFailedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/customer/logout",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<LogoutResponse> logout(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    //Get access token from authorization header
    String jwtToken = UtilityProvider.decodeBearerToken(authorization);

    //Invoke business service to logoff
    CustomerAuthEntity customerAuthEntity = customerService.logout(jwtToken);
    //Get Customer details who had logged off
    CustomerEntity customer = customerAuthEntity.getCustomer();

    //Fill in Signout Response and return
    LogoutResponse logoutResponse = new LogoutResponse().id(customer.getUuid())
        .message("LOGGED OUT SUCCESSFULLY");
    return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
  }

  /**
   * Handler to Update customer.
   *
   * @param authorization access token
   * @return
   * @throws
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/customer",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdateCustomerResponse> update(
      @RequestHeader("authorization") final String authorization, @RequestBody final UpdateCustomerRequest updateCustomerRequest)
      throws AuthorizationFailedException, UpdateCustomerException {

    if (updateCustomerRequest.getFirstName() == null || updateCustomerRequest.getFirstName().trim()
        .isEmpty()) {
      throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
    }

    //Get access token from authorization header
    String jwtToken = UtilityProvider.decodeBearerToken(authorization);

    //Invoke business service to update
    CustomerEntity customer = customerService.getCustomer(jwtToken);
    customer.setFirstName(updateCustomerRequest.getFirstName());
    customer.setLastName(updateCustomerRequest.getLastName());
    customerService.updateCustomer(customer);

    //Fill in Update Customer Response and return
    UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
        .id(customer.getUuid()).firstName(customer.getFirstName())
        .lastName(customer.getLastName()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", jwtToken);
    return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, headers, HttpStatus.OK);
  }


  /**
   * Updates the Customer Password with the one provided by the customer after validating the Bearer authorization
   * token with the Database records.
   * Throw error message when the access token is invalid/expired/not present in Database
   * Checks whether the old password matches with the one in Database before updating the new password
   *
   * @param authorization         The Bearer authorization token from the headers
   * @param updatePasswordRequest The request object which has the old and new passwords
   * @return The uuid of the Customer after updating the password
   * @throws UpdateCustomerException      If the passed old/new password fields are empty or null
   * @throws AuthorizationFailedException If the token is invalid or expired or not present in Database
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/customer/password",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(
      @RequestHeader("authorization") final String authorization,
      @RequestBody UpdatePasswordRequest updatePasswordRequest)
      throws UpdateCustomerException, AuthorizationFailedException {
    // Check for empty field validation
    if (updatePasswordRequest.getOldPassword() == null || updatePasswordRequest.getOldPassword()
        .isEmpty()
        || updatePasswordRequest.getNewPassword() == null || updatePasswordRequest.getNewPassword()
        .isEmpty()) {
      throw new UpdateCustomerException("UCR-003", "No field should be empty");
    }
    CustomerEntity customerToUpdate = customerService
        .getCustomer(UtilityProvider.decodeBearerToken(authorization));
    CustomerEntity updatedCustomer = customerService
        .updateCustomerPassword(updatePasswordRequest.getOldPassword(),
            updatePasswordRequest.getNewPassword(), customerToUpdate);
    UpdatePasswordResponse response = new UpdatePasswordResponse();
    response.id(updatedCustomer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
    return new ResponseEntity<UpdatePasswordResponse>(response, HttpStatus.OK);
  }

}

