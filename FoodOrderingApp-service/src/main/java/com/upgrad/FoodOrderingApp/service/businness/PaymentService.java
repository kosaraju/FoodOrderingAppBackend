package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();

    }


    /**
     * Retrives the Payment details from the Database based on the uuid
     *
     * @param paymentUUID The uuid of the Payment to lookup in Database
     * @return The Payment Details retrieved from Database with the matched uuid
     * @throws PaymentMethodNotFoundException If the uuid doesn't match with any Database record
     */
    public PaymentEntity getPaymentByUUID(String paymentUUID)
        throws PaymentMethodNotFoundException {
        PaymentEntity payment = paymentDao.getMethodbyId(paymentUUID);
        if (payment != null) {
            return payment;
        }
        // If no payment method available with the uuid or when input doesn't have the uuid details
        throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
    }
}