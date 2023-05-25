package com.baloot.service;

import com.baloot.model.BuyList;
import com.baloot.model.Provider;
import com.baloot.repository.BuyListRepository;
import com.baloot.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyListService {
    private final BuyListRepository repo;

    @Autowired
    public BuyListService(BuyListRepository buyListRepository) {
        this.repo = buyListRepository;
    }
    public void save(BuyList buyList) {
        repo.save(buyList);
    }
    public void delete(BuyList buyList){
        repo.delete(buyList);
    }
}
