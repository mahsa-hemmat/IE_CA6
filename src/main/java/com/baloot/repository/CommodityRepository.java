package com.baloot.repository;

import com.baloot.model.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Integer>{
    @Query(value = "select c from Commodity c where c.name like '%:name%' ")
    public List<Commodity> filterByName(@Param("name") String name);

//    @Query(value= "select c from Commodity c, Category ca where ca.type=:category and ca.id = any(c.categories)")
//    public List<Commodity> filterByCategory(@Param("category") String category);

    @Query(value= "select c from Commodity c inner join Provider p on c.provider.id=p.id where p.name=:name ")
    public List<Commodity> filterByProviderName(@Param("name") String name);
}
