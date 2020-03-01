package com.upgrad.FoodOrderingApp.service.util;

import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
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


  private static final String BEARER_TOKEN = "Bearer ";

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

    //To Validate the Pincode
    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
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

  /**
   * Decode the Bearer Authorization Token
   *
   * @param authorization The Bearer authorization Token from the headers
   * @return The decoded access Token
   * @throws AuthorizationFailedException If the authorization token is not in valid format (missing
   *                                      Bearer prefix) throw an error message as not logged in
   */
  public static String decodeBearerToken(String authorization) throws AuthorizationFailedException {
    try {
      String[] bearerToken = authorization.split(UtilityProvider.BEARER_TOKEN);
      if (bearerToken != null && bearerToken.length > 1) {
        String accessToken = bearerToken[1];
        return accessToken;
      } else {
        throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    }
  }
}

