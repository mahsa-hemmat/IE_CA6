package com.baloot.model;

import com.baloot.model.id.VoteId;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "vote")
public class Vote {
    @EmbeddedId
    private VoteId id;
    @ManyToOne
    @MapsId("vote_user_id")
    private User user;
    @ManyToOne
    @MapsId("comment_id")
    private Comment comment;
    @Column(name = "like_count")
    @ColumnDefault("0")
    private int likeCount;

    @Column(name = "dislike_count")
    @ColumnDefault("0")
    private int dislikeCount;

    public Vote(){

    }
    public Vote(User user, Comment comment){
        this.id = new VoteId(user.getUsername(), comment.getId());
        this.user = user;
        this.comment = comment;
    }

    public VoteId getId() {
        return id;
    }

    public void setId(VoteId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
