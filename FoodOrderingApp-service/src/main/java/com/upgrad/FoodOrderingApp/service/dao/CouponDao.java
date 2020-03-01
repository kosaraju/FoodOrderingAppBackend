package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CouponDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Retrieves the Coupon Entity matched with the coupon name
   *
   * @param couponName The coupon name to be searched in Database
   * @return The Coupon Entity matched with the coupon name
   */
  public CouponEntity getCouponByCouponName(String couponName) {
    try {
      // Reading as List, if there are several records matching with the same coupon, take the first coupon record
      List<CouponEntity> couponList = entityManager
          .createNamedQuery("couponByCouponName", CouponEntity.class)
          .setParameter("couponName", couponName).getResultList();
      if (couponList != null && !couponList.isEmpty()) {
        return couponList.get(0);
      } else {
        return null;
      }
    } catch (NoResultException e) {
      return null;
    }
  }

  /**
   * Retrieves the Coupon from the database by matching the uuid of coupon
   *
   * @param couponUUID The uuid of the Coupon to retrieve
   * @return The Coupon Entity with the details populated if matched, null otherwise
   */
  public CouponEntity getCouponByUUID(String couponUUID) {
    try {
      return entityManager.createNamedQuery("couponByUUID", CouponEntity.class)
          .setParameter("couponUUID", couponUUID).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}