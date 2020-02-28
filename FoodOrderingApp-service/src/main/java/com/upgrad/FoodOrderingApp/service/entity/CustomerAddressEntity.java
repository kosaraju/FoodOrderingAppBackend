package com.upgrad.FoodOrderingApp.service.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

//This Class represents the CustomerAddress table in the DB.

@Entity
@Table(name = "customer_address")
@NamedQueries({
        @NamedQuery(name = "getAllCustomerAddressByCustomer",query = "SELECT c from CustomerAddressEntity c where c.customer = :customer_entity AND c.address.active = :active"),
        @NamedQuery(name = "getCustomerAddressByAddress",query = "SELECT c from CustomerAddressEntity c where c.address = :address_entity")
})
public class CustomerAddressEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
