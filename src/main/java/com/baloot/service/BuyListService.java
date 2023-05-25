package com.baloot.service;

import com.baloot.model.*;
import com.baloot.model.id.BuyListId;
import com.baloot.repository.BuyListRepository;
import com.baloot.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void deleteById(BuyListId id){
        repo.deleteById(id);
    }
    public boolean existsById(BuyListId id){
        return repo.existsById(id);
    }
    public List<BuyList> findAll(){
        return repo.findAll();
    }


    public List<List<Object>>  getUserCommodities(User user) {
        return repo.getUserCommodities(user);
    }
}
