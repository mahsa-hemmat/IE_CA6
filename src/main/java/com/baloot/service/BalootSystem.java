package com.baloot.service;

import com.baloot.exception.InValidInputException;
import com.baloot.exception.ProviderNotFoundException;
import com.baloot.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BalootSystem {
    private static BalootSystem instance;  // can be removed later

    private final ProviderService providerService;
    private final CommodityService commodityService;
    private final CategoryService categoryService ;


    private DataBase db = new DataBase();
    //private BalootSystem() {}

    @Autowired
    public BalootSystem(ProviderService ps, CommodityService cs, CategoryService cas) {
        this.providerService = ps;
        this.commodityService = cs;
        this.categoryService = cas;
    }
    /*public static BalootSystem getInstance(){
        if(instance == null) {
            instance = new BalootSystem();
            instance.readDataFromServer();
        }
        return instance;
    }*/

    /*public DataBase getDataBase(){
        return db;
    }*/
    public User getLoggedInUser() throws InValidInputException {
        return db.getLoggedInUser();
    }
    @PostConstruct
    public void readDataFromServer() {
        try {
            System.out.println("Importing data ...");
            //importUsers();
            importProviders();
            importCommodities();
            //importComments();
            //importDiscounts();
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void importDiscounts() throws Exception{
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/discount");
        ObjectMapper mapper = new ObjectMapper();
        List<Discount> discounts = mapper.readValue(strJsonData, new TypeReference<List<Discount>>(){});
        db.addDiscount(discounts);
    }

    private void importComments() throws Exception{
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/comments");
        ObjectMapper mapper = new ObjectMapper();
        List<Comment> comments = mapper.readValue(strJsonData, new TypeReference<List<Comment>>(){});
        db.addComment(comments);
    }

    private void importCommodities() throws Exception{
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/v2/commodities");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(strJsonData);
        List<Commodity> commodities = new ArrayList<>();
        if(jsonNode.isArray()){
            for (JsonNode commodityNode : jsonNode) {
                Commodity commodity = new Commodity();
                commodity.setId(commodityNode.get("id").asInt());
                commodity.setName(commodityNode.get("name").asText());
                int providerId = commodityNode.get("providerId").asInt();
                commodity.setProvider(providerService.getProviderById(providerId));
                commodity.setPrice(commodityNode.get("price").asInt());
                commodity.setRating(commodityNode.get("rating").asDouble());
                commodity.setInStock(commodityNode.get("inStock").asInt());
                commodity.setImage(commodityNode.get("image").asText());
                JsonNode categoriesNode = commodityNode.get("categories");
                Set<Category> categories = new HashSet<>();
                if (categoriesNode.isArray()) {
                    for (JsonNode categoryNode : categoriesNode) {
                        Category category = categoryService.getCategoryByType(categoryNode.asText());
                        if (category == null) {
                            category = new Category();
                            category.setType(categoryNode.asText());
                            categoryService.save(category);
                        }
                        categories.add(category);
                    }
                }
                commodity.setCategories(categories);
                commodities.add(commodity);
                addCommodityToProvider(providerId, commodity);
            }
        }
        for (Commodity co:commodities) {
            commodityService.save(co);
        }
    }

    public void addCommodityToProvider(int providerId, Commodity commodity) {
        try {
            Provider provider = providerService.getProviderById(providerId);
            provider.addNewCommodity(commodity);
            providerService.save(provider);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void importProviders() throws Exception{
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/v2/providers");
        ObjectMapper mapper = new ObjectMapper();
        List<Provider> providers = mapper.readValue(strJsonData, new TypeReference<>() {});
        for (Provider provider:providers)
            providerService.save(provider);
    }

    private void importUsers() throws Exception {
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/users");
        ObjectMapper mapper = new ObjectMapper();
        List<User> users = mapper.readValue(strJsonData, new TypeReference<List<User>>(){});
        db.addUser(users);
    }

    private String HTTPRequestHandler(String users_url) throws URISyntaxException, IOException {
        URL url =  new URI(users_url).toURL();
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int status = urlConnection.getResponseCode();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder lines = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            lines.append(line);
        }
        String strJsonData = lines.toString();
        bufferedReader.close();
        return strJsonData;
    }
    public Boolean hasAnyUserLoggedIn(){
        return db.hasAnyUserLoggedIn();
    }

    public Boolean isUserValid(String username) throws Exception{
        return db.getUserById(username) != null;
    }
    public void loginInUser(String username, String password) throws Exception{
        db.setLoggedInUser(username, password);
    }

    //public void logOutUser() throws Exception {db.logOutUser();}

    public Provider getProvider(int id) throws ProviderNotFoundException {
        return providerService.getProviderById(id);
    }
    public void increaseCredit(int credit){db.increaseCredit(credit);}
    //public Provider getProvider(int id) throws ProviderNotFoundException { return db.getProviderById(id); }
}
