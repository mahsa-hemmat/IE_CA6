package com.baloot.controller;

import com.baloot.exception.*;
import com.baloot.info.*;
import com.baloot.service.BalootSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baloot.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.*;


@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final BalootSystem balootSystem;
    @Autowired
    public UserController(BalootSystem balootSystem) {
        this.balootSystem = balootSystem;
    }

    @GetMapping("")
    public ResponseEntity<Object> getUser() {
        try {
            UserInfo userInfo = new UserInfo(balootSystem.getLoggedInUser());
            return ResponseEntity.status(HttpStatus.OK).body(userInfo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/buylist")
    public ResponseEntity<Object> getBuyList() {
        if(!balootSystem.hasAnyUserLoggedIn()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in. Please login first");
        }
        try {
            List<List<Object>> results = balootSystem.getBuyList();
            List<CartCommodity> commodities = new ArrayList<>();
            for (List<Object> re:results) {
                CartCommodity c = new CartCommodity((Integer) re.get(0), (String) re.get(1), (Integer) re.get(5), (Integer) re.get(2),
                        (Set<Category>) re.get(7), (Double) re.get(3), (Integer) re.get(4), (String) re.get(6));
                commodities.add(c);
            }
            return ResponseEntity.status(HttpStatus.OK).body(commodities);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/buylist")
    public ResponseEntity<Object> addToBuyList(@RequestParam(value = "commodityId") int commodityId) {
        if(!balootSystem.hasAnyUserLoggedIn()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in. Please login first");
        }
        try {
            balootSystem.addToBuyList(commodityId);
            return ResponseEntity.status(HttpStatus.OK).body("Commodity is added to buylist successfully.");
        } catch (OutOfStockException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (CommodityNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (InValidInputException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @DeleteMapping("/buylist")
    public ResponseEntity<Object> removeFromBuyList(@RequestParam(value = "commodityId") int commodityId) {
        if(!balootSystem.hasAnyUserLoggedIn()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in. Please login first");
        }
        try {
            balootSystem.removeCommodityFromBuyList(commodityId);
            return ResponseEntity.status(HttpStatus.OK).body("Commodity is removed from buy list successfully.");
        } catch (CommodityNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getHistory() {
        if(!balootSystem.hasAnyUserLoggedIn()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in. Please login first");
        }
        try {
            List<List<Object>> results = balootSystem.getHistoryList();
            List<CartCommodity> commodities = new ArrayList<>();
            for (List<Object> re:results) {
                CartCommodity c = new CartCommodity((Integer) re.get(0), (String) re.get(1), (Integer) re.get(5), (Integer) re.get(2),
                    (Set<Category>) re.get(7), (Double) re.get(3), (Integer) re.get(4), (String) re.get(6));
                commodities.add(c);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(commodities);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /*@PostMapping("/credit")
    public ResponseEntity<Object> addCredit(@RequestParam(value = "credit") int credit) {
        try {
            BalootSystem.getInstance().getLoggedInUser();
            if (credit < 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed. Credit Cannot Be Negative");
            }
            BalootSystem.getInstance().increaseCredit(credit);
            return ResponseEntity.status(HttpStatus.OK).body("Credit added successfully.");
        } catch (InValidInputException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PutMapping ("/buylist")
    public ResponseEntity<Object> getPaymentData(@RequestParam(value = "commodityId") int commodityId,
                                                 @RequestParam(value = "quantity") int quantity) {
        try {
            User user = BalootSystem.getInstance().getLoggedInUser();
            BalootSystem baloot = BalootSystem.getInstance();
            user.getBuyList().getCommodities().get(commodityId).setQuantity(quantity);
            return ResponseEntity.status(HttpStatus.OK).body("quantity set successfully");
        } catch (InValidInputException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
    @GetMapping ("/payment")
    public ResponseEntity<Object> getPaymentData() {
        try {
            User user = BalootSystem.getInstance().getLoggedInUser();
            BalootSystem baloot = BalootSystem.getInstance();
            Map<String, Integer> prices = baloot.getLoggedInUser().getBuyList().getTotalCost();
            return ResponseEntity.status(HttpStatus.OK).body(prices.get("originalPrice"));
        } catch (InValidInputException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<Object> makePayment() {
        try {
            BalootSystem.getInstance().getLoggedInUser();
            BalootSystem.getInstance().getDataBase().purchase();
            return ResponseEntity.status(HttpStatus.OK).body("payment done successfully.");
        } catch (InValidInputException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (OutOfStockException| BuyListIsEmptyException | NotEnoughCreditException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (CommodityNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/discount")
    public ResponseEntity<Object> applyDiscountCode(@RequestParam(value = "discountcode", required = false) String discount) {
        try {
            BalootSystem baloot = BalootSystem.getInstance();
            baloot.getLoggedInUser().submitDiscount(discount);
            Map<String, Integer> prices = baloot.getLoggedInUser().getBuyList().getTotalCost();
            return ResponseEntity.status(HttpStatus.OK).body(prices.get("discountedPrice"));
        } catch (InValidInputException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (InvalidDiscountException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DiscountHasExpiredException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }*/


}
