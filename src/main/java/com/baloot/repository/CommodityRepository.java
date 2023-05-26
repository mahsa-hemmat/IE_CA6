package com.baloot.repository;

import com.baloot.model.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Integer>{
    @Query("SELECT ca.type FROM Commodity c JOIN c.categories ca WHERE c.id = ?1")
    Set<String> getCategoriesForCommodity(int commodityId);
    
    @Query(value = "select c from Commodity c where c.name LIKE %:name% ")
    public List<Commodity> filterByName(@Param("name") String name);

    @Query(value= "select c from Commodity c JOIN Category ca where ca.type LIKE %:category%")
    public List<Commodity> filterByCategory(@Param("category") String category);

    @Query(value = "SELECT c FROM Commodity c JOIN FETCH c.provider p WHERE p.name LIKE %:name%")
    public List<Commodity> filterByProviderName(@Param("name") String name);
    @Query(value = "SELECT c1 FROM Commodity c1,Commodity c2 WHERE c2.id=:id ORDER BY c1.rating desc limit 4")
    public List<Commodity>recommenderSystem(@Param("id") int commodityId);
    @Query(value = "UPDATE Commodity SET rating=:score WHERE id=:id")
    void rateCommodity(@Param("id") Integer commodityId,@Param("score") int score);
}
