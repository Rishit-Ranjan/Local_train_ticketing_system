package com.local.train.controller;

import com.local.train.dto.AddFundsRequest;
import com.local.train.dto.TransactionResponse;
import com.local.train.dto.WalletDto;
import com.local.train.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDto> getWallet(Authentication authentication) {
        return ResponseEntity.ok(walletService.getWallet(authentication.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<WalletDto> addFunds(@RequestBody AddFundsRequest request, Authentication authentication) {
        return ResponseEntity.ok(walletService.addFunds(authentication.getName(), request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication) {
        return ResponseEntity.ok(walletService.getTransactions(authentication.getName()));
    }
}
