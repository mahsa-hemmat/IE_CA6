package com.baloot.service;

import com.baloot.exception.InvalidDiscountException;
import com.baloot.model.Discount;
import com.baloot.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {
    private final DiscountRepository repo;
    @Autowired
    public DiscountService(DiscountRepository discountRepository){
        this.repo = discountRepository;
    }
    public Discount getDiscountById(String discount_code) throws InvalidDiscountException {
        Optional<Discount> discount = repo.findById(discount_code);
        if(discount.isPresent()){
            return discount.get();
        }
        throw new InvalidDiscountException(discount_code);
    }
    public void save(Discount discount){
        repo.save(discount);
    }
    public void saveAll(List<Discount> discount){
        repo.saveAll(discount);
    }
    public List<Discount> findAll(){
        return repo.findAll();
    }

}
