package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransactionDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class TransactionController {
    private final TransactionDAO transactionDAO;
    private final UserDao userDao;

    public TransactionController(TransactionDAO transactionDAO, UserDao userDao) {
        this.transactionDAO = transactionDAO;
        this.userDao = userDao;
    }
    //request mapping goes here
    //Get transactions by user id

    //Get transactions by transaction id and user id

    //post new transaction
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
    public void sendMoney(Principal principal, @Valid @RequestBody Transaction transaction) throws Exception {
        User userFrom = userDao.findByUsername(principal.getName());
        transaction.setSendingUserId(userFrom.getId());
        if (transaction.getAmount() > userFrom.getBalance()) {
            throw new Exception("You can't send more money than you have!");
        }
        if(transaction.getAmount() < 0.01){
            throw new Exception("You must send a valid amount");
        }
        transactionDAO.createTransaction(transaction);
    }
}
