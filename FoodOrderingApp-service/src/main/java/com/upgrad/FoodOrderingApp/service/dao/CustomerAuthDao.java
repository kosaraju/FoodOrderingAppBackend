package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

//This Class is created to access DB with respect to CustomerAuth Entity

@Repository
public class CustomerAuthDao {

    @PersistenceContext
    private EntityManager entityManager;


    //To get Customer Auth By AccessToken if no results return null
    public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken){
        try{
            CustomerAuthEntity customerAuthEntity = entityManager.createNamedQuery("getCustomerAuthByAccessToken",CustomerAuthEntity.class).setParameter("access_Token",accessToken).getSingleResult();
            return customerAuthEntity;
        }catch (NoResultException nre){
            return null;
        }

    }

    //To save CustomerAuthEntity in the DB
    public CustomerAuthEntity createCustomerAuth (CustomerAuthEntity customerAuthEntity){
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    //To upadte CustomerAuthEntity in the DB
    public CustomerAuthEntity customerLogout (CustomerAuthEntity customerAuthEntity){
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }

}
