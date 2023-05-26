package com.baloot.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;


@Embeddable
public class VoteId implements Serializable {
    @Column(name = "vote_user_id")
    private String username;

    @Column(name = "comment_id")
    private CommentId commentId;

    public VoteId() {
    }

    public VoteId(String username, CommentId commentId) {
        this.username = username;
        this.commentId = commentId;
    }
}
