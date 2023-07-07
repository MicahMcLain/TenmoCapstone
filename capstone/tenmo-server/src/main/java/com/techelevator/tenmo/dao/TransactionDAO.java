package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.awt.*;
import java.util.List;

public interface TransactionDAO {

    List<Transaction> getTransactionsByUserId(int id);
    //Get transactions by user id
    Transaction getTransactionsByUserIdAndTransactionId(int userId, int transactionId);

    void createTransaction(Transaction transaction);
    //Get transactions by transaction id
    Transaction update(Transaction transaction);
    void startTransaction(Transaction transaction);
}
