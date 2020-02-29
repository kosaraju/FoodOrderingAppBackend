package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

  /**
   * Saves the Order Information in the Database
   *
   * @param order The order details to be saved
   * @return The persisted order with id value generated
   */
  public OrderEntity saveOrderDetail(OrderEntity order) {
    entityManager.persist(order);
    return order;
  }

  /**
   * Saves the Order Item Information in the Database
   *
   * @param orderItem The order item details to be saved
   * @return The persisted order item with id value generated
   */
  public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
    entityManager.persist(orderItem);
    return orderItem;
  }

  /**
   * Retrieves the list of previously placed orders ordered by the date placed descending
   *
   * @param customerUUID The uuid of the customer for which orders has to be retrieved
   * @return The Order details sorted in descending order of the data placed
   */
  public List<OrderEntity> getPastOrdersByCustomerId(String customerUUID) {
    return entityManager.createNamedQuery("pastOrdersByCustomerUUID", OrderEntity.class)
        .setParameter("customerUUID", customerUUID).getResultList();
    }

    //To get the
    public List<OrderItemEntity> getItemsByOrders(OrderEntity orderEntity) {
      try {
        List<OrderItemEntity> orderItemEntities = entityManager
            .createNamedQuery("getItemsByOrders", OrderItemEntity.class)
            .setParameter("ordersEntity",
                orderEntity).getResultList();
        return orderItemEntities;
      } catch (NoResultException nre) {
        return null;
      }
    }


    //To get list of OrdersEntity by the restaurant if no result then null is returned
    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
      try {
        List<OrderEntity> ordersEntities = entityManager
            .createNamedQuery("getOrdersByRestaurant", OrderEntity.class)
            .setParameter("restaurant", restaurantEntity).getResultList();
        return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    //To get all the order corresponding to the address
    public List<OrderEntity> getOrdersByAddress(AddressEntity addressEntity) {
      try {
        List<OrderEntity> ordersEntities = entityManager
            .createNamedQuery("getOrdersByAddress", OrderEntity.class)
            .setParameter("address", addressEntity).getResultList();
        return ordersEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }

}