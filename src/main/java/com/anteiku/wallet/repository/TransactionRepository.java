package com.anteiku.wallet.repository;

import com.anteiku.wallet.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByType(Transaction.TransactionType type);

    List<Transaction> findByCategory(String category);
}