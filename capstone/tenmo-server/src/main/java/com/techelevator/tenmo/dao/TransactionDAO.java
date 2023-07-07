package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transactions;
import com.techelevator.tenmo.model.User;

import java.sql.SQLException;

public interface TransactionDAO {

    Transactions getTransactionsByUserId(int id);
    //Get transactions by user id
    Transactions getTransactionsByUserIdAndTransactionId(int userId, int transactionId);

    Transactions createTransaction(Transactions transaction);
    //Get transactions by transaction id
}
