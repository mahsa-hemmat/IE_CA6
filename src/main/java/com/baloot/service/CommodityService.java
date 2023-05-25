package com.baloot.service;

import com.baloot.exception.CommodityNotFoundException;
import com.baloot.exception.ProviderNotFoundException;
import com.baloot.model.Commodity;
import com.baloot.model.Provider;
import com.baloot.repository.CommodityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public void save(Commodity commodity) {
        repo.save(commodity);
    }
}
