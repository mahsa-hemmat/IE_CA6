package com.baloot.model;

import com.baloot.model.id.CommentId;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "comment")
public class Comment {

    @EmbeddedId
    private CommentId id;

    @ManyToOne
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @MapsId("commodity_id")
    private Commodity commodity;
    private String text;
    private LocalDate date;
    public Comment(){};
    public Comment(User user, Commodity commodity, String text){
        this.id = new CommentId(user.getUsername(), commodity.getId());
        this.user = user;
        this.commodity = commodity;
        this.text = text;
        this.date = LocalDate.now();
    }

    public CommentId getId() {
        return id;
    }

    public void setId(CommentId id) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
