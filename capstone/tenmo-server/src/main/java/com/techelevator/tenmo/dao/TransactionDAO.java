package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDAO {

    List<Transaction> getTransactionsByUserId(int id);
    //Get transactions by user id
    Transaction getTransactionsByUserIdAndTransactionId(int userId, int transactionId);

    void transferMoney(Transaction transaction) throws Exception;
    //Get transactions by transaction id
    Transaction update(Transaction transaction);
    void createPendingTransaction(Transaction transaction);

    //approve transaction method
    void approveTransaction(Transaction transaction);

    //decline transaction method
    void rejectTransaction(Transaction transaction);

    int delete(int id);
}
