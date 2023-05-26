package com.baloot.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RatingId implements Serializable {
    @Column(name = "user_id")
    private String username;

    @Column(name = "commodity_id")
    private Integer commodityId;

    public RatingId() {
    }

    public RatingId(String username, Integer commodityId) {
        this.username = username;
        this.commodityId = commodityId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(username,commodityId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingId ratingId)) return false;
        return Objects.equals(username, ratingId.username) &&
                Objects.equals(commodityId, ratingId.commodityId);
    }
}
