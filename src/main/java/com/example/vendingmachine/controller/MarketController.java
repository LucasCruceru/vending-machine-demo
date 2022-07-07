package com.example.vendingmachine.controller;

import com.example.vendingmachine.dto.BuyResponseDto;
import com.example.vendingmachine.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketController {

    @Autowired
    private MarketService service;

    @PutMapping(value = "deposit")
    public ResponseEntity<Integer> deposit(@RequestParam int coin) {
        return ResponseEntity.ok(service.deposit(coin));
    }

    @PostMapping(value = "buy")
    public ResponseEntity<BuyResponseDto> buy(long productId, int amount){
        return ResponseEntity.ok(service.buy(productId, amount));
    }

    @PutMapping(value = "reset")
    public ResponseEntity<String> reset(){
        return ResponseEntity.ok("Your balance has been reset to " + service.reset());
    }
}
