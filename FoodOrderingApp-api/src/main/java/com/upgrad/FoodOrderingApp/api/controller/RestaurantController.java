package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.business.ItemService;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin(allowedHeaders = "*", origins = "*", exposedHeaders = ("access-token"))
@RestController
@RequestMapping("/")
public class RestaurantController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantList() {

        List<RestaurantEntity> listRestaurantEntity = restaurantService.restaurantsByRating();

        List<RestaurantList> listRestaurantList = getListRestaurantListFromListRestaurantEntity(listRestaurantEntity);

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(listRestaurantList);

        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    private List<RestaurantList> getListRestaurantListFromListRestaurantEntity(List<RestaurantEntity> listRestaurantEntity) {

        List<RestaurantList> listRestaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity : listRestaurantEntity) {

            List<CategoryEntity> listCategoryEntity = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            StringBuilder sbCategory = new StringBuilder();
            for (CategoryEntity c : listCategoryEntity) {
                sbCategory.append(c.getCategoryName() + ", ");
            }

            listRestaurantList.add(new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .averagePrice(restaurantEntity.getAvgPrice())
                    .categories(sbCategory.substring(0, sbCategory.length() - 2))
                    .address(getRestaurantDetailsResponseAddress(restaurantEntity))
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .photoURL(restaurantEntity.getPhotoUrl()));

        }
        return listRestaurantList;
    }

    private RestaurantDetailsResponseAddress getRestaurantDetailsResponseAddress(RestaurantEntity restaurantEntity) {
        return new RestaurantDetailsResponseAddress().id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo())
                .locality(restaurantEntity.getAddress().getLocality())
                .city(restaurantEntity.getAddress().getCity())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(new RestaurantDetailsResponseAddressState()
                        .id(UUID.fromString(restaurantEntity.getAddress().getState().getStateUuid()))
                        .stateName(restaurantEntity.getAddress().getState().getStateName()));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantListByCategory(
            @PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {

        List<RestaurantEntity> listRestaurantEntity = restaurantService.restaurantByCategory(categoryId);

        List<RestaurantList> listRestaurantList = getListRestaurantListFromListRestaurantEntity(listRestaurantEntity);

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(listRestaurantList);

        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

}