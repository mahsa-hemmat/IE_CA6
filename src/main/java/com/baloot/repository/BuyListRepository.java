package com.baloot.repository;

import com.baloot.model.BuyList;
import com.baloot.model.id.BuyListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyListRepository extends JpaRepository<BuyList, BuyListId> {
}
