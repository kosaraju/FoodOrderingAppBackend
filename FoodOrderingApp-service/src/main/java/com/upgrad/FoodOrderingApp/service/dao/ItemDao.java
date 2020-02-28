package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

//This Class is created to access DB with respect to Item entity
@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get ItemEntity by its UUID if no result then null is returned.
    public ItemEntity getItemByUUID(String uuid) {
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("getItemByUUID",ItemEntity.class).setParameter("uuid",uuid).getSingleResult();
            return itemEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}