package com.baloot.service;

import com.baloot.exception.CommodityNotFoundException;
import com.baloot.model.Commodity;
import com.baloot.repository.CommodityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CommodityService {
    private final CommodityRepository repo;
    @Autowired
    public CommodityService(CommodityRepository commodityRepository) {
        this.repo = commodityRepository;
    }
    public Commodity getCommodityById(Integer id) throws CommodityNotFoundException {
        Optional<Commodity> result = repo.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new CommodityNotFoundException(id);
    }

    public List<Commodity> getCommodities(){
        return repo.findAll();
    }
//    public List<Commodity> filterByCategory(String category){
//        return repo.f
//    }

    public List<Commodity> filterByName(String name){
        return repo.filterByName(name);
    }

    public List<Commodity> filterByProviderName(String name){
        return repo.filterByProviderName(name);
    }
    public void save(Commodity commodity) {
        repo.save(commodity);
    }

    public Set<String> getCategoriesForCommodity(int commodity_id){
        return repo.getCategoriesForCommodity(commodity_id);
    }
}
