package com.example.vendingmachine.service;

import com.example.vendingmachine.model.User;
import com.example.vendingmachine.repository.ProductRepository;
import com.example.vendingmachine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    @InjectMocks
    private MarketService marketService;

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;


    @Test
    void deposit_ShouldDeposit() {
        var user = TestMock.generateBuyerUser("username");
        user.setDeposit(0);
        when(userService.getUserByUsername(any())).thenReturn(user);
        int expected = 5;
        int actual = marketService.deposit(expected);
        assertEquals(expected, actual);
    }

    @Test
    void deposit_ShouldDepositOverExistingValue() {
        User user = TestMock.generateBuyerUser("username");
        user.setDeposit(5);
        when(userService.getUserByUsername(any())).thenReturn(user);
        int coin = 5;
        var expected = user.getDeposit() + coin;
        int actual = marketService.deposit(coin);
        assertEquals(expected, actual);
    }

    @Test
    void deposit_ShouldNotDepositWithInvalidValue() {
        when(userService.getUserByUsername(any())).thenReturn(TestMock.generateBuyerUser("username"));
        int expected = 3;
        var exception = assertThrows(ResponseStatusException.class,
                ()-> marketService.deposit(expected));
        assertEquals("You can only introduce 5, 10, 20, 50 and 100 coins", exception.getReason());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
    }


    @Test
    void buy_ProductNotFound(){
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        var exception = assertThrows(ResponseStatusException.class,
                ()-> marketService.buy(1,2));
        assertEquals("Product not found", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
        void buy_NotEnoughProduct(){
        var amount = 5;
        when(productRepository.findById(any())).thenReturn(Optional.of(TestMock.generateProduct()));
        var exception = assertThrows(ResponseStatusException.class,
                ()-> marketService.buy(1, amount));
        assertEquals("The amount of products you requested is not in stock", exception.getReason());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
    }

    @Test
    void buy_UserNotEnoughMoney(){
        var product = TestMock.generateProduct();
        product.setCost(10);
        var user = product.getUser();
        user.setDeposit(5);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(userService.getUserByUsername(any())).thenReturn(user);

        var exception = assertThrows(ResponseStatusException.class,
                ()-> marketService.buy(1, 1));

        assertEquals("You dont have enough money to purchase that", exception.getReason());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
    }


    @Test
    void buy_SuccessfulTransactionWithAmount1(){
        var product = TestMock.generateProduct();
        product.setCost(5);
        var user = product.getUser();
        user.setDeposit(10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(userService.getUserByUsername(any())).thenReturn(user);

        var response =  marketService.buy(1, 1);

        assertEquals(product.getName(), response.getProductName());
        assertEquals(product.getCost(), response.getTotalSpent());
        assertEquals(1, response.getChange().size());
        assertEquals(5, response.getChange().get(0));
    }

    @Test
    void buy_SuccessfulTransactionWithBiggerAmount(){
        var amount = 4;
        var product = TestMock.generateProduct();
        product.setAmountAvailable(amount);
        product.setCost(5);
        var user = product.getUser();
        user.setDeposit(20);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(userService.getUserByUsername(any())).thenReturn(user);

        var response =  marketService.buy(1, amount);

        assertEquals(product.getName(), response.getProductName());
        assertEquals(product.getCost() * amount, response.getTotalSpent());
    }

    @Test
    void buy_SuccessfulTransactionWithLotsOfChange(){
        var product = TestMock.generateProduct();
        product.setCost(5);
        var user = product.getUser();
        user.setDeposit(200);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(userService.getUserByUsername(any())).thenReturn(user);

        var response =  marketService.buy(1, 1);

        assertEquals(product.getName(), response.getProductName());
        assertEquals(product.getCost(), response.getTotalSpent());
        assertEquals(5, response.getChange().size());
        assertEquals(100, response.getChange().get(0));
        assertEquals(50, response.getChange().get(1));
        assertEquals(20, response.getChange().get(2));
        assertEquals(20, response.getChange().get(3));
        assertEquals(5, response.getChange().get(4));

    }
}
