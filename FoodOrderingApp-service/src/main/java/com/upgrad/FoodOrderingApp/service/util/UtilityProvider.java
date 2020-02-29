package com.upgrad.FoodOrderingApp.service.util;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

//This Class Provides various utilities.

@Component
public class UtilityProvider {


  /**
   * Checks if the input string is an invalid String (null or empty) Mainly used to validate the
   * request input elements
   *
   * @param value The field to be checked for validation
   * @return true if value is null or empty, false otherwise
   */
  public static Boolean isInValid(String value) {
    return (value == null || value.isEmpty());
    }

    //To validate the ContactNo
    public boolean isContactValid(String contactNumber){
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    //To validate the email
    public boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    //To Validate the Pincode
    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
    }

    //To validate the Signuprequest
    public boolean isValidSignupRequest (CustomerEntity customerEntity)throws SignUpRestrictedException{
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""){
             throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if(customerEntity.getPassword() == null||customerEntity.getPassword() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getEmail() == null||customerEntity.getEmail() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getContactnumber() == null||customerEntity.getContactnumber() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        return true;
    }

    //To validate the Authorization format
    public boolean isValidAuthorizationFormat(String authorization)throws AuthenticationFailedException{
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedAuth = new String(decoded);
            String[] decodedArray = decodedAuth.split(":");
            String username = decodedArray[0];
            String password = decodedArray[1];
            return true;
        }catch (ArrayIndexOutOfBoundsException exc){
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
    }

    //To validate Customer update request
    public boolean isValidUpdateCustomerRequest (String firstName)throws UpdateCustomerException {
        if (firstName == null || firstName == "") {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return true;
    }

    //To validate the password Update Request.
    public boolean isValidUpdatePasswordRequest(String oldPassword,String newPassword) throws UpdateCustomerException{
        if (oldPassword == null || oldPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (newPassword == null || newPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        return true;
    }

    //To validate the Customer rating
    public boolean isValidCustomerRating(String cutomerRating){
        if(cutomerRating.equals("5.0")){
            return true;
        }
        Pattern p = Pattern.compile("[1-4].[0-9]");
        Matcher m = p.matcher(cutomerRating);
        return (m.find() && m.group().equals(cutomerRating));
    }

    //To sort the HashMap by values.
    public Map<String,Integer> sortMapByValues(Map<String,Integer> map){

        // Create a list from elements of itemCountMap
        List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        //Creating the Sorted HashMap
        Map<String, Integer> sortedByValueMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> item : list) {
            sortedByValueMap.put(item.getKey(), item.getValue());
        }

        return sortedByValueMap;
    }

  //To validate the password as per given conditions,1Uppercase,1Lowercase,1Number,1SpecialCharacter and atleast 8 characters.
  public boolean isValidPassword(String password) {
    Boolean lowerCase = false;
    Boolean upperCase = false;
    Boolean number = false;
    Boolean specialCharacter = false;

    if (password.length() < 8) {
      return false;
    }

    if (password.matches("(?=.*[0-9]).*")) {
      number = true;
    }

    if (password.matches("(?=.*[a-z]).*")) {
      lowerCase = true;
    }
    if (password.matches("(?=.*[A-Z]).*")) {
      upperCase = true;
    }
    if (password.matches("(?=.*[#@$%&*!^]).*")) {
      specialCharacter = true;
    }

    if (lowerCase && upperCase) {
      return specialCharacter && number;
    } else {
      return false;
    }
  }

}

