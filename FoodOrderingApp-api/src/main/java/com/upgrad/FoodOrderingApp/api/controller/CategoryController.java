package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import java.util.ArrayList;
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

@CrossOrigin
@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //Lists call categories
    //No input
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path="/category" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories()
    {
        List<CategoryEntity> categoryEntityList = categoryService.getAllCategoriesOrderedByName();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        for(CategoryEntity categoryEntity : categoryEntityList)
        {
            CategoryListResponse categoryListResponse = new CategoryListResponse()
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());
            categoriesListResponse.addCategoriesItem(categoryListResponse);
        }

        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK );

    }

  /**
   * This method retrieves the category with all the items matching that category based on the uuid
   * of category No authorization required for this endpoint
   *
   * @param categoryUUID The uuid of the category to be retrieved
   * @return The category with all the items under it
   * @throws CategoryNotFoundException If the category uuid is not matched with any of the records
   *                                   in Database
   */
  @RequestMapping(method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/category/{category_id}")
  public ResponseEntity<CategoryDetailsResponse> getCategoryDetails(
      @PathVariable("category_id") String categoryUUID)
      throws CategoryNotFoundException {
    CategoryEntity category = categoryService.getCategoryById(categoryUUID);

    List<ItemEntity> categoryItems = category.getItems();
    List<ItemList> itemsList = new ArrayList<ItemList>();
    // If any items exists under a category, populate in the response
    if (categoryItems != null && !categoryItems.isEmpty()) {
      categoryItems.forEach(item -> {
        ItemList itemList = new ItemList();
        itemList.id(UUID.fromString(item.getUuid())).itemName(item.getItemName())
            .price(item.getPrice())
            .itemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
        itemsList.add(itemList);
      });
    }
    CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
    categoryDetailsResponse.itemList(itemsList);
    categoryDetailsResponse.id(UUID.fromString(category.getUuid()))
        .categoryName(category.getCategoryName());
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }

}
