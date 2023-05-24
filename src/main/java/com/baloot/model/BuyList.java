package com.baloot.model;

import com.baloot.model.id.BuyListId;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Entity
@Table(name = "buylist")
public class BuyList {
    @EmbeddedId
    private BuyListId id;

    @ManyToOne
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @MapsId("commodity_id")
    private Commodity commodity;

    @ColumnDefault("1")
    private Integer inCart;

    public BuyList(){}

    public BuyListId getId() {
        return id;
    }

    public void setId(BuyListId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public Integer getInCart() {
        return inCart;
    }

    public void setInCart(Integer inCart) {
        this.inCart = inCart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuyList)) return false;
        BuyList buylist = (BuyList) o;
        return Objects.equals(id, buylist.id) &&
                Objects.equals(user, buylist.user)&&
                Objects.equals(commodity, buylist.commodity) &&
                Objects.equals(inCart, buylist.inCart);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, user, commodity, inCart);
    }

}
