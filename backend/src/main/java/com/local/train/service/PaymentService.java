// PaymentService.java
package com.local.train.service;

import com.local.train.dto.*;
import com.local.train.entity.*;
import com.local.train.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            Wallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            
            // Check wallet balance if using wallet
            if (request.getMethod() == PaymentMethod.WALLET) {
                if (wallet.getBalance() < request.getAmount()) {
                    return PaymentResponse.builder()
                            .status(PaymentStatus.FAILED)
                            .message("Insufficient wallet balance")
                            .build();
                }
                wallet.setBalance(wallet.getBalance() - request.getAmount());
            }
            
            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setTransactionId(UUID.randomUUID().toString());
            transaction.setAmount(request.getAmount());
            transaction.setType(TransactionType.DEBIT);
            transaction.setMethod(request.getMethod());
            transaction.setStatus(PaymentStatus.PAID);
            transaction.setWallet(wallet);
            transaction.setDescription("Ticket booking payment");
            transaction.setCreatedAt(LocalDateTime.now());

            // Associate transaction with the booking
            if (request.getBookingId() != null) {
                Booking booking = bookingRepository.findById(request.getBookingId())
                        .orElseThrow(() -> new RuntimeException("Booking not found for payment processing: " + request.getBookingId()));
                transaction.setBooking(booking);
            }
            
            transactionRepository.save(transaction);
            walletRepository.save(wallet);
            
            return PaymentResponse.builder() // Changed from SUCCESSFUL to PAID
                    .status(PaymentStatus.PAID)
                    .transactionId(transaction.getTransactionId())
                    .message("Payment successful")
                    .build();
                    
        } catch (Exception e) {
            return PaymentResponse.builder()
                    .status(PaymentStatus.FAILED)
                    .message("Payment failed: " + e.getMessage())
                    .build();
        }
    }
    
    @Transactional
    public PaymentResponse processRefund(PaymentRefundRequest request) {
        try {
            Wallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            
            // Find the booking being refunded
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found for refund: " + request.getBookingId()));

            // Refund to wallet
            wallet.setBalance(wallet.getBalance() + request.getAmount());
            
            // Create refund transaction
            Transaction transaction = new Transaction();
            transaction.setTransactionId(UUID.randomUUID().toString());
            transaction.setAmount(request.getAmount());
            transaction.setType(TransactionType.CREDIT);
            transaction.setMethod(PaymentMethod.WALLET);
            transaction.setStatus(PaymentStatus.REFUNDED);
            transaction.setWallet(wallet);
            transaction.setBooking(booking);
            transaction.setDescription("Ticket cancellation refund");
            transaction.setCreatedAt(LocalDateTime.now());
            
            transactionRepository.save(transaction);
            walletRepository.save(wallet);
            
            return PaymentResponse.builder()
                    .status(PaymentStatus.REFUNDED)
                    .transactionId(transaction.getTransactionId())
                    .message("Refund processed successfully")
                    .build();
                    
        } catch (Exception e) {
            return PaymentResponse.builder()
                    .status(PaymentStatus.FAILED)
                    .message("Refund failed: " + e.getMessage())
                    .build();
        }
    }
}