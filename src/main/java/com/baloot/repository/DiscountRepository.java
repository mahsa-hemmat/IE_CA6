package com.baloot.repository;


import com.baloot.model.Discount;
import com.baloot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    @Query("select d from Discount d where d.discountCode = ?1")
    Optional<Discount> findById(String discount_code);
}
