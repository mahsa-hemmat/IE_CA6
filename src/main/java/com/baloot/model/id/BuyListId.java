package com.baloot.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class BuyListId implements Serializable {
    @Column(name = "user_id")
    private String username;

    @Column(name = "commodity_id")
    private Integer commodityId;

    public BuyListId() {
    }

    public BuyListId(String username, Integer commodityId) {
        this.username = username;
        this.commodityId = commodityId;
    }
}

