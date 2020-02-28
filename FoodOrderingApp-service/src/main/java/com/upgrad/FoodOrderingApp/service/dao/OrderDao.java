package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
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

    //To save Order in the db
    public OrdersEntity saveOrder(OrdersEntity ordersEntity){
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

    //To get List of order from the db Corresponding to Customers
    public List<OrdersEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByCustomers",OrdersEntity.class).setParameter("customer",customerEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    //To get list of OrdersEntity by the restaurant if no result then null is returned
    public List<OrdersEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity){
        try{
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant",OrdersEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

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