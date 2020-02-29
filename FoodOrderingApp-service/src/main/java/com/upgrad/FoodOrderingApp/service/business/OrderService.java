package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.util.UtilityProvider;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  @Autowired
  private CouponDao couponDao;

  @Autowired
  private OrderDao orderDao;

  @Autowired
  private ItemDao itemDao;

  /**
   * Retrieve the Coupon Information matched with the Coupon name passed
   *
   * @param couponName The coupon name for which the coupon details has to be retrieved
   * @return The Coupon info matched with the coupon name
   * @throws CouponNotFoundException If the coupon name doesn't match with the Database records
   */
  public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
    // If coupon name is empty
    if (UtilityProvider.isInValid(couponName)) {
      throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
    }
    CouponEntity coupon = couponDao.getCouponByCouponName(couponName);
    // No match with the Database for the coupon name
    if (coupon == null) {
      throw new CouponNotFoundException("CPF-001", "No coupon by this name");
    }
    return coupon;
  }

  /**
   * Retrieves the coupon details from the database matched with the uuid throws error message when
   * the coupon uuid doesn't match with any record
   *
   * @param couponUUID The uuid of the coupon to be looked up in Database
   * @return The Coupon details pulled from Database
   * @throws CouponNotFoundException If the coupon uuid doesn't match with database
   */
  public CouponEntity getCouponByCouponId(String couponUUID) throws CouponNotFoundException {
    CouponEntity coupon = couponDao.getCouponByUUID(couponUUID);
    if (coupon == null) {
      throw new CouponNotFoundException("CPF-002", "No coupon by this id");
    }
    return coupon;
  }

  /**
   * Saves the order data to database, archives the address so that the address will be linked to
   * this particular order when request for delete of address, archived address as it is linked to
   * the order will not be deleted Sets the current time as order save date
   *
   * @param order The order Entity to be saved to Database
   * @return The Persisted order after updating to the Database
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public OrderEntity saveOrder(OrderEntity order) {
    order.setUuid(UUID.randomUUID().toString());
    order.setDate(new Date());
    // Set the status of address to archive
    order.getAddress().setActive(0);
    orderDao.saveOrderDetail(order);
    return order;
  }

  /**
   * Saves the order item to the database with all the details received
   *
   * @param orderItem The Item details while placing as order which is to be saved
   * @return The persisted order item after updating to the Database
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
    orderDao.saveOrderItem(orderItem);
    return orderItem;
  }

  /**
   * Retrieves the list of orders matched by the customer uuid
   *
   * @param customerUUID The uuid of the logged in customer
   * @return The list of Order details fetched from Dao (Database)
   */
  public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
    return orderDao.getPastOrdersByCustomerId(customerUUID);
  }

  /**
   * Retrieves the list of available items under a particular order using id of order
   *
   * @param orderId The id value of Order entity
   * @return The list of items placed with the order
   */
  public List<OrderItemEntity> getOrderItemsByOrderId(Integer orderId) {
    return itemDao.getItemsByOrderId(orderId);
  }
}