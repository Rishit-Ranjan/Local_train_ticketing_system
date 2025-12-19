// WalletService.java
package com.local.train.service;

import com.local.train.dto.AddFundsRequest;
import com.local.train.dto.WalletDto;
import com.local.train.entity.*;
import com.local.train.repository.TransactionRepository;
import com.local.train.repository.UserRepository;
import com.local.train.repository.WalletRepository;
import com.local.train.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    
    public WalletDto getWallet(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        return WalletDto.builder()
                .balance(wallet.getBalance())
                .userId(user.getId())
                .build();
    }
    
    @Transactional
    public WalletDto addFunds(String email, AddFundsRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setBalance(0.0);
                    return walletRepository.save(newWallet);
                });
        
        wallet.setBalance(wallet.getBalance() + request.getAmount());
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.CREDIT);
        transaction.setMethod(request.getPaymentMethod());
        transaction.setStatus(PaymentStatus.PAID);
        transaction.setWallet(wallet);
        transaction.setDescription("Funds added to wallet");
        transaction.setCreatedAt(LocalDateTime.now());
        
        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        
        return WalletDto.builder()
                .balance(wallet.getBalance())
                .userId(user.getId())
                .build();
    }
    public java.util.List<com.local.train.dto.TransactionResponse> getTransactions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(txn -> com.local.train.dto.TransactionResponse.builder()
                        .transactionId(txn.getTransactionId())
                        .amount(txn.getAmount())
                        .type(txn.getType())
                        .method(txn.getMethod())
                        .status(txn.getStatus())
                        .createdAt(txn.getCreatedAt())
                        .description(txn.getDescription())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }
}
