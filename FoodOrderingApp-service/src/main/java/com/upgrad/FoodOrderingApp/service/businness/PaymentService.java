package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();

    }

    //List all payments methods available in DB table
    public PaymentEntity getPaymentMethod(String paymentUuid) throws PaymentMethodNotFoundException {
        PaymentEntity paymentEntity = paymentDao.getMethodbyId(paymentUuid);
        if(paymentEntity==null){
            throw new PaymentMethodNotFoundException("PNF-002","No payment method found by this id");
        } else {
            return paymentEntity;
        }
    }
}