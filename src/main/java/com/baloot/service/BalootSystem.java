package com.baloot.service;

import com.baloot.exception.*;
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
    private final UserService userService;
    private final DiscountService discountService;
    private final BuyListService buyListService;


    private DataBase db = new DataBase();
    //private BalootSystem() {}

    @Autowired
    public BalootSystem(ProviderService ps, CommodityService cs, CategoryService cas, UserService us,
                        DiscountService ds, BuyListService buys) {
        this.providerService = ps;
        this.commodityService = cs;
        this.categoryService = cas;
        this.userService = us;
        this.discountService = ds;
        this.buyListService = buys;
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
            importDiscounts();
            importUsers();
            importProviders();
            importCommodities();
            //importComments();
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void importDiscounts() throws Exception{
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/discount");
        ObjectMapper mapper = new ObjectMapper();
        List<Discount> discounts = mapper.readValue(strJsonData, new TypeReference<>() {});
        discountService.saveAll(discounts);
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
        if(jsonNode.isArray()){
            for (JsonNode commodityNode : jsonNode) {
                Commodity commodity = new Commodity(commodityNode.get("id").asInt(), commodityNode.get("name").asText(),
                        commodityNode.get("price").asInt(), commodityNode.get("rating").asDouble(), commodityNode.get("inStock").asInt(),
                        commodityNode.get("image").asText());
                int providerId = commodityNode.get("providerId").asInt();
                commodity.setProvider(providerService.getProviderById(providerId));
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
                addCommodityToProvider(providerId, commodity);
                commodityService.save(commodity);
            }
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
        providerService.saveAll(providers);

    }

    private void importUsers() throws Exception {
        String strJsonData = HTTPRequestHandler("http://5.253.25.110:5000/api/users");
        ObjectMapper mapper = new ObjectMapper();
        List<User> users = mapper.readValue(strJsonData, new TypeReference<>() {});
        for(User user:users){
            user.setDiscounts(discountService.findAll());
        }
        userService.saveAll(users);

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
    public User getUser(String username) throws UserNotFoundException {
        return userService.findUserById(username);
    }
    public Commodity getCommodity(int id) throws CommodityNotFoundException {
        return commodityService.getCommodityById(id);
    }

    public List<Commodity> getCommodities(){
        return commodityService.getCommodities();
    }
//    public List<Commodity> filterByCategory(String category){
//        return repo.f
//    }

    public List<Commodity> filterByName(String name){
        return commodityService.filterByName(name);
    }

    public List<Commodity> filterByProviderName(String name){
        return commodityService.filterByProviderName(name);
    }
    public void increaseCredit(int credit){db.increaseCredit(credit);}
    //public Provider getProvider(int id) throws ProviderNotFoundException { return db.getProviderById(id); }
    public void addToBuyList(String userId, int commodityId) throws UserNotFoundException, CommodityNotFoundException, InValidInputException, OutOfStockException {
        User user = userService.findUserById(userId);
        Commodity commodity = commodityService.getCommodityById(commodityId);
        if(commodity.getInStock() == 0)
            throw new OutOfStockException(commodityId);
        if(!user.getBuyList().isEmpty()){
            for(BuyList buyList:user.getBuyList()){
                if(buyList.getCommodity().equals(commodity))
                    throw new InValidInputException("Commodity is already added to buy list");
            }
        }
        BuyList buyList = new BuyList(user, commodity);
        user.getBuyList().add(buyList);
        userService.save(user);
    }


}
