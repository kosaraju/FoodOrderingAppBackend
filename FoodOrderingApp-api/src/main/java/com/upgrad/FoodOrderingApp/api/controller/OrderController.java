package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.api.model.OrderList;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddress;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddressState;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.OrderListCustomer;
import com.upgrad.FoodOrderingApp.api.model.OrderListPayment;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.business.AddressService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.business.ItemService;
import com.upgrad.FoodOrderingApp.service.business.OrderService;
import com.upgrad.FoodOrderingApp.service.business.PaymentService;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class OrderController {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private AddressService addressService;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private RestaurantService restaurantService;

  @Autowired
  private ItemService itemService;

  /**
   * Validate customer session and retrieves the coupon details based on the coupon name Throw error
   * message when the access token is invalid/expired/not present in Database If no coupon name
   * matches in the database, throw error message as coupon not found If the coupon name is empty,
   * throw error message as the field should not be empty
   *
   * @param authorization The Bearer authorization token from the headers
   * @param couponName    The Coupon name for which the details has to be retrieved
   * @return The coupon details matched with the coupon name
   * @throws AuthorizationFailedException If the token is invalid or expired or not present in
   *                                      Database
   * @throws CouponNotFoundException      If the Coupon name is invalid or not found in Database
   */
  @RequestMapping(method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = {"/order/coupon",
      "/order/coupon/{coupon_name}"})
  public ResponseEntity<OrderListCoupon> couponsByCouponName(
      @RequestHeader("authorization") final String authorization,
      @PathVariable(name = "coupon_name", required = false) String couponName)
      throws AuthorizationFailedException, CouponNotFoundException {
    // Validate customer session
    customerService.getCustomer(customerService.getBearerAccessToken(authorization));
    CouponEntity coupon = orderService.getCouponByCouponName(couponName);
    OrderListCoupon orderListCoupon = new OrderListCoupon();
    orderListCoupon.id(UUID.fromString(coupon.getUuid())).couponName(coupon.getCouponName())
        .percent(coupon.getPercent());
    return new ResponseEntity<OrderListCoupon>(orderListCoupon, HttpStatus.OK);

  }

  /**
   * Validates customer session and saves the order to the database Requests the coupon, payment,
   * address, restaurant, item details which are ordered by customer Throw error message when the
   * access token is invalid/expired/not present in Database Error message will be thrown if any of
   * the coupon,  payment, address, restaurant, item information is not available in the database
   * coupon is optional, validation takes place only if request has coupon id
   *
   * @param authorization    The Bearer authorization token from the headers
   * @param saveOrderRequest The Request holding all the details of order
   * @return The response with the created order uuid and success message
   * @throws AuthorizationFailedException   If the token is invalid or expired or not present in
   *                                        Database
   * @throws CouponNotFoundException        If the Coupon uuid passed doesn't match with Database
   *                                        records
   * @throws AddressNotFoundException       If the Coupon uuid passed doesn't match with Database
   *                                        records
   * @throws PaymentMethodNotFoundException If the Payment uuid passed doesn't match with Database
   *                                        records
   * @throws RestaurantNotFoundException    If the Restaurant uuid passed doesn't match with
   *                                        Database records
   * @throws ItemNotFoundException          If the Items uuid passed doesn't match with Database
   *                                        records
   */
  @RequestMapping(method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/order")
  public ResponseEntity<SaveOrderResponse> saveOrder(
      @RequestHeader("authorization") final String authorization,
      @RequestBody SaveOrderRequest saveOrderRequest)
      throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
      PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

    // Validate customer session
    CustomerEntity loggedInCustomer = customerService
        .getCustomer(customerService.getBearerAccessToken(authorization));
    // Check if the coupon is valid or not, in case it is passed
    CouponEntity coupon = null;
    PaymentEntity payment = null;
    AddressEntity address = null;
    RestaurantEntity restaurant = null;
    if (saveOrderRequest.getCouponId() != null) {
      coupon = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
    }
    if (saveOrderRequest.getPaymentId() != null) {
      payment = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
    } else {
      // If the input doesn't pass the payment uuid in request
      throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
    }
    if (saveOrderRequest.getAddressId() != null) {
      address = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), loggedInCustomer);
    } else {
      // If the input doesn't pass the address uuid in request
      throw new AddressNotFoundException("ANF-003", "No address by this id");
    }
    if (saveOrderRequest.getRestaurantId() != null) {
      restaurant = restaurantService
          .restaurantByUUID(saveOrderRequest.getRestaurantId().toString());
    } else {
      // If the input doesn't pass the restaurant uuid in request
      throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
    }

    // Populate the order details and save to Database
    OrderEntity order = new OrderEntity();
    order.setCustomer(loggedInCustomer);
    order.setCoupon(coupon);
    order.setAddress(address);
    order.setPayment(payment);
    order.setRestaurant(restaurant);
    order.setBill(saveOrderRequest.getBill() != null ?
        saveOrderRequest.getBill().doubleValue() : null);
    order.setDiscount(saveOrderRequest.getDiscount() != null ?
        saveOrderRequest.getDiscount().doubleValue() : null);
    final OrderEntity savedOrder = orderService.saveOrder(order);

    List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
    // Iterate all the items and save to database
    if (itemQuantities != null && !itemQuantities.isEmpty()) {
      for (ItemQuantity itemQuantity : itemQuantities) {
        ItemEntity item = itemService.getItemByUUID(String.valueOf(itemQuantity.getItemId()));
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setItem(item);
        orderItem.setOrder(savedOrder);
        orderItem.setQuantity(itemQuantity.getQuantity());
        orderItem.setPrice(itemQuantity.getPrice());
        orderService.saveOrderItem(orderItem);
      }
    }

    SaveOrderResponse response = new SaveOrderResponse();
    response.id(savedOrder.getUuid()).status("ORDER SUCCESSFULLY PLACED");
    return new ResponseEntity<SaveOrderResponse>(response, HttpStatus.CREATED);
  }

  /**
   * Validate customer session and retrieves the list of past orders placed by logged in user throws
   * error when customer access token is invalid/expired/logged out
   *
   * @param authorization The Bearer authorization token from the headers
   * @return The List of orders along with the items ordered
   * @throws AuthorizationFailedException If the token is invalid or expired or not present in
   *                                      Database
   */
  @RequestMapping(method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/order")
  public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {
    // Validate customer session
    CustomerEntity customer = customerService
        .getCustomer(customerService.getBearerAccessToken(authorization));

    // Get all the past orders by customer uuid
    List<OrderEntity> pastOrders = orderService.getOrdersByCustomers(customer.getUuid());
    CustomerOrderResponse response = new CustomerOrderResponse();
    if (pastOrders != null && !pastOrders.isEmpty()) {
      pastOrders.forEach(pastOrder -> {
        OrderList orderList = new OrderList();
        // Set the bill details
        orderList.id(UUID.fromString(pastOrder.getUuid()))
            .bill(BigDecimal.valueOf(pastOrder.getBill()))
            .discount(BigDecimal.valueOf(pastOrder.getDiscount()))
            .date(pastOrder.getDate().toString());

        CouponEntity pastOrderCoupon = pastOrder.getCoupon();
        // Set the coupon details if any applied while placing the order
        if (pastOrderCoupon != null) {
          OrderListCoupon coupon = new OrderListCoupon();
          coupon.id(UUID.fromString(pastOrderCoupon.getUuid()));
          coupon.couponName(pastOrderCoupon.getCouponName());
          coupon.percent(pastOrderCoupon.getPercent());
          orderList.coupon(coupon);
        }

        // Set the payment information of the order used while placing it
        OrderListPayment payment = new OrderListPayment();
        payment.id(UUID.fromString(pastOrder.getPayment().getUuid()))
            .paymentName(pastOrder.getPayment().getPaymentName());

        // Set the customer details for each order in the response
        OrderListCustomer orderListCustomer = new OrderListCustomer();
        CustomerEntity orderCustomer = pastOrder.getCustomer();
        orderListCustomer.id(UUID.fromString(orderCustomer.getUuid()))
            .firstName(orderCustomer.getFirstName())
            .lastName(orderCustomer.getLastName()).emailAddress(orderCustomer.getEmail())
            .contactNumber(orderCustomer.getContactnumber());

        orderList.payment(payment).customer(orderListCustomer);

        // Set the Address details in the response for which order is placed
        OrderListAddress orderListAddress = new OrderListAddress();
        AddressEntity orderAddress = pastOrder.getAddress();
        orderListAddress.id(UUID.fromString(orderAddress.getUuid()))
            .flatBuildingName(orderAddress.getFlatBuilNo())
            .locality(orderAddress.getLocality()).city(orderAddress.getCity())
            .pincode(orderAddress.getPincode());

        // Set the State information of address in the response
        OrderListAddressState orderListAddressState = new OrderListAddressState();
        StateEntity orderState = orderAddress.getState();
        orderListAddressState.id(UUID.fromString(orderState.getStateUuid()))
            .stateName(orderState.getStateName());
        orderListAddress.state(orderListAddressState);

        orderList.address(orderListAddress);

        // Fetch all the items by using the order id value
        List<OrderItemEntity> orderItems = orderService.getOrderItemsByOrderId(pastOrder.getId());
        if (orderItems != null) {
          orderItems.stream().forEach(orderItem -> {
            // Populate each item detail in the response
            ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
            itemQuantityResponse.quantity(orderItem.getQuantity()).price(orderItem.getPrice());
            ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
            itemQuantityResponseItem.id(UUID.fromString(orderItem.getItem().getUuid()))
                .itemName(orderItem.getItem().getItemName()).itemPrice(orderItem.getPrice())
                .type(ItemQuantityResponseItem.TypeEnum
                    .fromValue(orderItem.getItem().getType().getValue()));
            itemQuantityResponse.item(itemQuantityResponseItem);
            orderList.addItemQuantitiesItem(itemQuantityResponse);
          });
        }

        // If in case the items are empty, set an empty list in the response
        if (orderList.getItemQuantities() == null) {
          orderList.setItemQuantities(new ArrayList<ItemQuantityResponse>());
        }
        response.addOrdersItem(orderList);
      });
    }
    // If the user has no previous order, set an empty list
    if (response.getOrders() == null) {
      response.setOrders(new ArrayList<OrderList>());
    }
    return new ResponseEntity<CustomerOrderResponse>(response, HttpStatus.OK);
  }
}