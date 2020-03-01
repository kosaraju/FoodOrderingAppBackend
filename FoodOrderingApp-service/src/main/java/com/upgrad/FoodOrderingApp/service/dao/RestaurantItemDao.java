package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//This Class is created to access DB with respect to RestaurantItem entity

@Repository
public class RestaurantItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get the list of RestaurantItemEntity using Restaurant entity if no results then returns null.
    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantItemEntity> restaurantItemEntities = entityManager.createNamedQuery("getItemsByRestaurant",RestaurantItemEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return restaurantItemEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}