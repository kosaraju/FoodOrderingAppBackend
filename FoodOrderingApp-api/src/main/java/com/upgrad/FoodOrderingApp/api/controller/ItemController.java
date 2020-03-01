package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// ItemController Handles all  the Item related endpoints

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService; // Handles all the Service Related to Item.

    @Autowired
    RestaurantService restaurantService; // Handles all the Service Related to Restaurant.

    /* The method handles get Top Five Items By Popularity request & takes restaurant_id as the path variable
    & produces response in ItemListResponse and returns list of 5 items sold by restaurant on basis of popularity  with details from the db. If error returns error code and error message.
    */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTopFiveItemsByPopularity (@PathVariable(value = "restaurant_id")final String restaurantUuid) throws RestaurantNotFoundException {

        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        //Calls getItemsByPopularity method of itemService to get the ItemEntity.
        List<ItemEntity> itemEntities = itemService.getItemsByPopularity(restaurantEntity);

        //Creating the ItemListResponse details as required.
        ItemListResponse itemListResponse = new ItemListResponse();
        itemEntities.forEach(itemEntity -> {
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getItemName())
                    .price(itemEntity.getPrice())
                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
            itemListResponse.add(itemList);
        });

        return new ResponseEntity<ItemListResponse>(itemListResponse,HttpStatus.OK);
    }
}