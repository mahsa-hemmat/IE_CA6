package com.baloot.service;

import com.baloot.exception.*;
import com.baloot.model.*;
import com.baloot.model.id.BuyListId;
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
import java.util.*;

@Component
public class BalootSystem {
    private static BalootSystem instance;  // can be removed later

    private final ProviderService providerService;
    private final CommodityService commodityService;
    private final CategoryService categoryService ;
    private final UserService userService;
    private final DiscountService discountService;
    private final BuyListService buyListService;
    private final HistoryListService historyListService;
    private String loggedInUser;

    private DataBase db = new DataBase();
    //private BalootSystem() {}

    @Autowired
    public BalootSystem(ProviderService ps, CommodityService cs, CategoryService cas, UserService us,
                        DiscountService ds, BuyListService buys, HistoryListService hist) {
        this.providerService = ps;
        this.commodityService = cs;
        this.categoryService = cas;
        this.userService = us;
        this.discountService = ds;
        this.buyListService = buys;
        this.historyListService = hist;
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
                double rating = (double) commodityNode.get("rating").asDouble();
                commodity.setRating(rating);
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
    public void increaseCredit(int credit){db.increaseCredit(credit);}
    //public Provider getProvider(int id) throws ProviderNotFoundException { return db.getProviderById(id); }

    @Transactional
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
        buyListService.save(buyList);
        user.getBuyList().add(buyList);
        commodity.getBuyList().add(buyList);
        userService.save(user);
        commodityService.save(commodity);
    }
    public void removeCommodityFromBuyList(String userId,int commodityId) throws CommodityNotFoundException, UserNotFoundException {
        BuyListId buyListId = new BuyListId(userId, commodityId);
        if(!buyListService.existsById(buyListId))
            throw new CommodityNotFoundException(commodityId);
        buyListService.deleteById(buyListId);
        User user = userService.findUserById(userId);
        List<BuyList> buyLists = user.getBuyList();
        Iterator<BuyList> iterator = buyLists.iterator();
        while (iterator.hasNext()) {
            BuyList currentBuyList = iterator.next();
            if (currentBuyList.getId().equals(buyListId)) {
                iterator.remove();
                break;
            }
        }
        userService.save(user);
    }

    public List<Commodity> getHistoryList() {
        if(historyListService.findAll().isEmpty())
            return null;
        List<HistoryList> historyLists = historyListService.findAll();
        List<Commodity> commodities = new ArrayList<>();
        for(HistoryList historyList:historyLists)
            commodities.add(historyList.getCommodity());
        return commodities;
    }
    public List<List<Object>> getHistoryList(String username) throws UserNotFoundException {
        User user = userService.findUserById(username);
        List<List<Object>> results = historyListService.getUserCommodities(user);
        if(results.isEmpty())
            return null;
        for (List<Object> lo:results) {
            Set<String> categories = commodityService.getCategoriesForCommodity((Integer) lo.get(0));
            lo.add(categories);
        }
        return results;
    }

    public List<List<Object>> getBuyList(String username) throws UserNotFoundException {
        User user = userService.findUserById(username);
        List<List<Object>> results = buyListService.getUserCommodities(user);
        if(results.isEmpty())
            return null;
        for (List<Object> lo:results) {
            Set<String> categories = commodityService.getCategoriesForCommodity((Integer) lo.get(0));
            lo.add(categories);
        }
        return results;
    }
}
