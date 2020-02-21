package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;

import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//This Class is created to access DB with respect to Order entity

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get all the order corresponding to the address
    public List<OrdersEntity> getOrdersByAddress(AddressEntity addressEntity) {
        try{
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByAddress",OrdersEntity.class).setParameter("address",addressEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }
}