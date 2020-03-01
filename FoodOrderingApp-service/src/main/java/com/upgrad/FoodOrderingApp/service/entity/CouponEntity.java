package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "coupon")
@NamedQueries({
    @NamedQuery(name = "couponByCouponName", query = "select c from CouponEntity c where c.couponName = :couponName"),
    @NamedQuery(name = "couponByUUID", query = "select c from CouponEntity c where c.uuid = :couponUUID")
})
public class CouponEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

  @Column(name = "uuid", unique = true)
    @NotNull
  @Size(max = 200)
    private String uuid;

    @Column(name = "coupon_name")
    @Size(max = 255)
    private String couponName;

    @Column(name = "percent")
    @NotNull
    private Integer percent;

  public CouponEntity() {
    }

  public CouponEntity(@NotNull @Size(max = 200) String uuid, @Size(max = 255) String couponName,
      @NotNull Integer percent) {
    this.uuid = uuid;
    this.couponName = couponName;
        this.percent = percent;
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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}