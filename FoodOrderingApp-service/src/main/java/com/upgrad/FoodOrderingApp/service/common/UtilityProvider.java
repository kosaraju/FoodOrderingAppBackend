package com.upgrad.FoodOrderingApp.service.common;

import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This Class Provides various utilities.

@Component
public class UtilityProvider {





    //To Validate the Pincode
    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
    }


}