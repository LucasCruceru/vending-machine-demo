package com.example.vendingmachine.service;

import com.example.vendingmachine.dto.BuyResponseDto;
import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.model.User;
import com.example.vendingmachine.repository.ProductRepository;
import com.example.vendingmachine.repository.UserRepository;
import com.example.vendingmachine.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MarketService {

    private final static List<Integer> COIN_TYPES = Arrays.asList(100, 50, 20, 10, 5);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('BUYER')")
    public int deposit(int coin){
        User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());

        if (!COIN_TYPES.contains(coin))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You can only introduce 5, 10, 20, 50 and 100 coins");

        user.setDeposit(user.getDeposit() + coin);
        userRepository.save(user);
        return user.getDeposit();
    }

    @PreAuthorize("hasAuthority('BUYER')")
    public BuyResponseDto buy(long productId, int amount){
        User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());

        var optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        
        var product = optionalProduct.get();
        validateProductInStock(product.getAmountAvailable(), amount);

        var transactionCost = product.getCost() * amount;
        validateUserHasEnoughMoney(transactionCost, user.getDeposit());

        var remainingUserBalance = substractFunds(user, transactionCost);

        substractProductStock(product, amount);

        return createBuyResponseDto(product.getName(), transactionCost, remainingUserBalance);

    }

    @PreAuthorize("hasAuthority('BUYER')")
    public int reset(){
        User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());

        user.setDeposit(0);
        userRepository.save(user);
        return user.getDeposit();
    }

    private BuyResponseDto createBuyResponseDto(String name, int transactionCost, int remainingUserBalance) {
        var dto = new BuyResponseDto();
        dto.setProductName(name);
        dto.setTotalSpent(transactionCost);
        dto.setChange(getChange(remainingUserBalance));

        return dto;
    }

    private List<Integer> getChange(final int remainingUserBalance) {
        var change = new ArrayList<Integer>();
        var amountLeft = remainingUserBalance;
        for (int coinType : COIN_TYPES) {
            while (amountLeft >= coinType) {
                change.add(coinType);
                amountLeft = amountLeft - coinType;
            }
        }
        return change;
    }

    private void substractProductStock(Product product, int amount) {
        product.setAmountAvailable(product.getAmountAvailable() - amount);
        productRepository.save(product);
    }

    private int substractFunds(User user, int transactionCost) {
        var remainingUserBalance = user.getDeposit() - transactionCost;
        user.setDeposit(remainingUserBalance);
        userRepository.save(user);
        return remainingUserBalance;
    }

    private void validateUserHasEnoughMoney(int transactionCost, int userDeposit) {
        if (transactionCost > userDeposit)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You dont have enough money to purchase that");
    }

    private void validateProductInStock(int amountAvailable, int amount) {
        if(amount > amountAvailable)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The amount of products you requested is not in stock");
    }

}
