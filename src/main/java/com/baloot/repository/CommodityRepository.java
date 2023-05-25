package com.baloot.repository;

import com.baloot.model.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Integer>{
    @Query("SELECT ca.type FROM Commodity c JOIN c.categories ca WHERE c.id = ?1")
    Set<String> getCategoriesForCommodity(int commodityId);
}
