package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    //List all payment methods available in DB
    public List<PaymentEntity> getAllPaymentMethods(){
        try{
            return this.entityManager.createNamedQuery("allPaymentMethods", PaymentEntity.class).getResultList();
        } catch(NoResultException nre){
            return null;
        }
    }

    //Return payment method by paymet UUID
    public PaymentEntity getMethodbyId(final String uuid){
        try{
          return entityManager.createNamedQuery("getMethodbyId", PaymentEntity.class)
              .setParameter("paymentUUID", uuid).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }
}