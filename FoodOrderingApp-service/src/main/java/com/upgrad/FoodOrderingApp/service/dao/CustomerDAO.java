package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDAO {


    @PersistenceContext
    private EntityManager entityManager;

    /**create a CustomerEntity.
     * @param customerEntity CustomerEntity
     * @return CustomerEntity
     */
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**Delete aCustomer.
     * @param customerEntity CustomerEntity
     * @return CustomerEntity
     */
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CustomerEntity deleteCustomer(CustomerEntity customerEntity) {
        entityManager.remove(customerEntity);
        return customerEntity;
    }

    /**Get CustomerEntity by email.
     * @param email email
     * @return CustomerEntity
     */
    public CustomerEntity getCustomerByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("customerByEmail", CustomerEntity.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**Get CustomerEntity by uuid.
     * @param uuid uuid
     * @return CustomerEntity
     */
    public CustomerEntity getCustomerByUUID(final String uuid) {
        try {
            return entityManager.createNamedQuery("customerByUUID", CustomerEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    /**Get CustomerEntity by id.
     * @param id id
     * @return CustomerEntity
     */
    public CustomerEntity getCustomerById(final String id) {
        try {
            return entityManager.createNamedQuery("customerById", CustomerEntity.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**Get CustomerEntity by contactNumber.
     * @param contactNumber contactNumber
     * @return CustomerEntity
     */
    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contactnumber", contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**create authentication access token.
     * @param customerAuthEntity CustomerAuthEntity
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    /**Get CustomerEntity by access token.
     * @param accessToken accessToken
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity getCustomerByToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**update CustomerEntity.
     * @param updatedCustomerEntity CustomerEntity
     */
    public void updateCustomer(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    /**update CustomerAuthEntity.
     * @param updatedCustomerAuthEntity CustomerAuthEntity
     */
    public void updateCustomerAuth(final CustomerAuthEntity updatedCustomerAuthEntity) {
        entityManager.merge(updatedCustomerAuthEntity);
    }

}
