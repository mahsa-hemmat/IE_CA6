package com.baloot.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discount")
public class Discount {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "discount_code")
    private String discountCode;
    private Integer discountAmount;
    @Column(name = "used", columnDefinition = "boolean default false")
    private Boolean alreadyUsed;
    @ManyToMany(mappedBy = "discounts")
    private List<User> users = new ArrayList<>();

    public Discount(){}
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discount) {
        this.discountAmount = discount;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
