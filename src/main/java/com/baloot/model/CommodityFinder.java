package com.baloot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommodityFinder extends JpaRepository<Commodity, Long> {
    @Query("value= select c from Commodity c where c.name like '%:name%' ")
    public List<Commodity> filterByName(@Param("name") String name);

    @Query("value= select c from Commodity c innerjoin Category ca on ca.id=c.categories where ca.name=:category ")
    public List<Commodity> filterByCategory(@Param("category") String category);

    @Query("value= select c from Commodity c innerjoin provider p on c.provider_id=p.id where p.name=:name ")
    public List<Commodity> filterByProviderName(@Param("name") String name);
}
