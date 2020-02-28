package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

//This Class handles all service related to the Item

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    @Autowired
    CategoryItemDao categoryItemDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    OrderDao orderDao;

    @Autowired
    UtilityProvider utilityProvider;


    /* This method is to get Items By Category And Restaurant and returns list of ItemEntity it takes restaurantUuid & categoryUuid as input.
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        //Calls getRestaurantByUuid of restaurantDao to get the  RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUUID(restaurantUuid);

        //Calls getCategoryByUuid of categoryDao to get the  CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        //Calls getItemsByRestaurant of restaurantItemDao to get the  list of RestaurantItemEntity
        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);

        //Calls getItemsByCategory of categoryItemDao to get the  list of CategoryItemEntity
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);

        //Creating list of item entity common to the restaurant and category.
        List<ItemEntity> itemEntities = new LinkedList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });

        return itemEntities;
    }


    /* This method is to get Items By Popularity and returns list of ItemEntity it takes restaurantEntity as input.
     */
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {

        //Calls getOrdersByRestaurant method of orderDao to get the  OrdersEntity
        List <OrdersEntity> ordersEntities = orderDao.getOrdersByRestaurant(restaurantEntity);

        //Creating list of ItemEntity which are ordered from the restaurant.
        List <ItemEntity> itemEntities = new LinkedList<>();

        //Looping in for each ordersEntity in ordersEntities to get the corresponding orders
        ordersEntities.forEach(ordersEntity -> {
            //Calls getItemsByOrders method of orderItemDao to get the  OrderItemEntity
            List <OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> { //Looping in to get each tem from the OrderItemEntity.
                itemEntities.add(orderItemEntity.getItem());
            });
        });

        //Creating a HashMap to count the frequency of the order.
        Map<String,Integer> itemCountMap = new HashMap<String,Integer>();
        itemEntities.forEach(itemEntity -> { //Looping in to count the frequency of Item ordered correspondingly updating the count.
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(),(count == null) ? 1 : count+1);
        });

        //Calls sortMapByValues method of uitilityProvider and get sorted map by value.
        Map<String,Integer> sortedItemCountMap = utilityProvider.sortMapByValues(itemCountMap);

        //Creating the top 5 Itementity list
        List<ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for(Map.Entry<String,Integer> item:sortedItemCountMap.entrySet()){
            if(count < 5) {
                //Calls getItemByUUID to get the Itemtentity
                sortedItemEntites.add(itemDao.getItemByUUID(item.getKey()));
                count = count+1;
            }else{
                break;
            }
        }

        return sortedItemEntites;
    }

    /* This method is to get Items By Category and returns list of ItemEntity it takes CategoryEntity as input.
     */
    public List<ItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {

        //Calls getItemsByCategory method of categoryItemDao to get the  CategoryItemEntity
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);
        List<ItemEntity> itemEntities = new LinkedList<>();
        categoryItemEntities.forEach(categoryItemEntity -> {
            ItemEntity itemEntity = categoryItemEntity.getItem();
            itemEntities.add(itemEntity);
        });
        return itemEntities;
    }

    public ItemEntity getItemByUUID(String itemUuid) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDao.getItemByUUID(itemUuid);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        }
        return itemEntity;
    }
}