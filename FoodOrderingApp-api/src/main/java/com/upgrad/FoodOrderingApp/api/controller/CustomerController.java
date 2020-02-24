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
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
  public ResponseEntity<SignupCustomerResponse> customerSignup(final SignupCustomerRequest signupCustomerRequest)
      throws SignUpRestrictedException {

    //Fetch details from signupCustomerRequest and set in CustomerEntity instance
    if(signupCustomerRequest==null) throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setUuid(UUID.randomUUID().toString());
    customerEntity.setFirstName(signupCustomerRequest.getFirstName());
    customerEntity.setLastName(signupCustomerRequest.getLastName());
    customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
    customerEntity.setPassword(signupCustomerRequest.getPassword());
    customerEntity.setContactnumber(signupCustomerRequest.getContactNumber());
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
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
        .contactNumber(customer.getContactnumber()).emailAddress(customer.getEmail())
        .message("LOGGED IN SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", customerAuthEntity.getAccessToken());
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
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<LogoutResponse> logout(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    //Get access token from authorization header
    String jwtToken = customerService.getBearerAccessToken(authorization);

    //Invoke business service to logoff
    CustomerAuthEntity userAuthEntity = customerService.logout(jwtToken);
    //Get Customer details who had logged off
    CustomerEntity customer = userAuthEntity.getCustomer();

    //Fill in Signout Response and return
    LogoutResponse logoutResponse = new LogoutResponse().id(customer.getUuid())
        .message("LOGGED OUT SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", userAuthEntity.getAccessToken());
    return new ResponseEntity<LogoutResponse>(logoutResponse, headers, HttpStatus.OK);
  }

  /**
   * Handler to Update customer.
   *
   * @param authorization access token
   * @return
   * @throws
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/customer",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdateCustomerResponse> update(
      @RequestHeader("authorization") final String authorization, UpdateCustomerRequest updateCustomerRequest)
      throws AuthorizationFailedException, UpdateCustomerException {

    //Get access token from authorization header
    String jwtToken = customerService.getBearerAccessToken(authorization);

    if(updateCustomerRequest.getFirstName()==null || updateCustomerRequest.getFirstName().trim().isEmpty()){
        throw new UpdateCustomerException("UCR-002","First name field should not be empty");
    }

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
   * Handler to Change customer password.
   *
   * @param authorization access token
   * @return
   * @throws
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/customer/password",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdatePasswordResponse> changePassword(
      @RequestHeader("authorization") final String authorization, UpdatePasswordRequest updatePasswordRequest)
      throws AuthorizationFailedException, UpdateCustomerException {

    //Get access token from authorization header
    String jwtToken = customerService.getBearerAccessToken(authorization);

    if(updatePasswordRequest.getNewPassword()==null
        || updatePasswordRequest.getNewPassword().isEmpty()
        || updatePasswordRequest.getOldPassword()==null
        || updatePasswordRequest.getOldPassword().isEmpty()
    ){
      throw new UpdateCustomerException("UCR-003","No field should be empty");
    }

    String oldPassword = updatePasswordRequest.getOldPassword();
    String newPassword = updatePasswordRequest.getNewPassword();
    //Call business service
    CustomerAuthEntity customerAuthEntity = customerService.validateBearerAuthentication(jwtToken);
    CustomerEntity customer = customerAuthEntity.getCustomer();
    customer = customerService.updateCustomerPassword(oldPassword, newPassword, customer);

    //Fill in Update Customer Response and return
    UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
        .id(customer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", jwtToken);
    return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, headers, HttpStatus.OK);
  }
}

