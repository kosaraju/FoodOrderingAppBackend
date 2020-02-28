package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//This Class is created to access DB with respect to categoryItem entity

@Repository
public class CategoryItemDao {


    @PersistenceContext
    private EntityManager entityManager;

    //To get List of CategoryItemEntity by CategoryEntity if no result then null is returned
    public List<CategoryItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {
        try {
            List<CategoryItemEntity> categoryItemEntities = entityManager.createNamedQuery("getItemsByCategory",CategoryItemEntity.class).setParameter("category",categoryEntity).getResultList();
            return categoryItemEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}