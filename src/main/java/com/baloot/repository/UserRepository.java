package com.baloot.repository;

import com.baloot.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT u FROM User u WHERE u.email = ?1")
    User userExistsByEmail(String email);

    @Query(value = "UPDATE User set credit=:credit where username=:loggedInUser")
    public void increaseCredit(@Param("loggedInUser") String loggedInUser,@Param("credit") int credit);
}
