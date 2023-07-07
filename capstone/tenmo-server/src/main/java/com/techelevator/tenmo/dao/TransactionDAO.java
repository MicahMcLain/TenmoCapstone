package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

public interface TransactionDAO {

    Transaction getTransactionsByUserId(int id);
    //Get transactions by user id
    Transaction getTransactionsByUserIdAndTransactionId(int userId, int transactionId);

    void createTransaction(Transaction transaction);
    //Get transactions by transaction id
}
