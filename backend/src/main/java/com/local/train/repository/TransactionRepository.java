// TransactionRepository.java
package com.local.train.repository;

import com.local.train.entity.Transaction;
import com.local.train.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findByWalletUserId(Long userId);
    List<Transaction> findByType(TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.id = :userId " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.type = :type " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    Double sumAmountByTypeAndDateRange(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}