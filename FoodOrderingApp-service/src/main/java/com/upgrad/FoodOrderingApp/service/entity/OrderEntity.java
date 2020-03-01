package com.upgrad.FoodOrderingApp.service.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "orders",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries({
    @NamedQuery(name = "pastOrdersByCustomerUUID", query = "select o from OrderEntity o where o.customer.uuid = :customerUUID order by o.date desc"),
    @NamedQuery(name = "getOrdersByCustomers", query = "SELECT o FROM OrderEntity o WHERE o.customer = :customer ORDER BY o.date DESC "),
    @NamedQuery(name = "getOrdersByRestaurant", query = "SELECT o FROM OrderEntity o WHERE o.restaurant = :restaurant"),
    @NamedQuery(name = "getOrdersByAddress", query = "SELECT o FROM OrderEntity o WHERE o.address = :address")
})
public class OrderEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

  @Column(name = "uuid", unique = true)
  @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "bill")
    @NotNull
    private Double bill;

  @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity coupon;

  // Set default value
  @Column(name = "discount")
  @ColumnDefault("0")
  private Double discount = 0.0;

    @Column(name = "date")
    @NotNull
    private Date date;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

  @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

  public OrderEntity() {
    }

  public OrderEntity(@NotNull @Size(max = 200) String uuid, @NotNull Double bill,
      CouponEntity coupon,
      Double discount, @NotNull Date date, PaymentEntity payment, CustomerEntity customer,
      AddressEntity address, RestaurantEntity restaurant) {
        this.uuid = uuid;
        this.bill = bill;
    this.coupon = coupon;
        this.discount = discount;
    this.date = date;
    this.payment = payment;
    this.customer = customer;
    this.address = address;
    this.restaurant = restaurant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getBill() {
        return bill;
    }

  public void setBill(Double bill) {
        this.bill = bill;
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

  public Date getDate() {
        return date;
    }

  public void setDate(Date date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}