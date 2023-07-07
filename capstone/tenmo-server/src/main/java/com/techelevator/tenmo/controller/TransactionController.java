package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.DaoException;
import com.techelevator.tenmo.dao.TransactionDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.awt.*;
import java.security.Principal;
import java.util.List;

@RestController
public class TransactionController {
    private final TransactionDAO transactionDAO;
    private final UserDao userDao;

    public TransactionController(TransactionDAO transactionDAO, UserDao userDao) {
        this.transactionDAO = transactionDAO;
        this.userDao = userDao;
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public List<Transaction> getTransactions(Principal principal) {
        User userFrom = userDao.findByUsername(principal.getName());
        return transactionDAO.getTransactionsByUserId(userFrom.getId());
    }

    @RequestMapping(value = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransactionsById(Principal principal, @PathVariable int id) {
        User userFrom = userDao.findByUsername(principal.getName());
        int userId = userFrom.getId();
        Transaction transaction = transactionDAO.getTransactionsByUserIdAndTransactionId(userId,id);
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Not Found");
        } else {
            return transaction;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
    //make sendMoney default mapping and possible request to request money
    //set status code to pending to 1 (pending) if request
    //dont make changes if status is 1
    public void sendMoney(@RequestParam (defaultValue = "") String request, @RequestParam (defaultValue= "") String respond,
                          Principal principal, @Valid @RequestBody Transaction transaction) throws Exception {
        //need a second path parameter for respond
        int pending = 1;
        int accepted = 2;
        int declined = 3;
        User userFrom = userDao.findByUsername(principal.getName());
        transaction.setSendingUserId(userFrom.getId());
        if(request == null) {
            if (transaction.getAmount() > userFrom.getBalance()) {
                throw new Exception("You can't send more money than you have!");
            }
            if (transaction.getAmount() < 0.01) {
                throw new Exception("You must send a valid amount");
            }
            transactionDAO.createTransaction(transaction);
        } else if(request != null){
            transaction.setStatus(pending);
            transactionDAO.startTransaction(transaction);
        } else if(respond != null){
            if(transaction.getStatus() == accepted){
                transaction.setStatus(transaction.getStatus());
                //delete old transaction and post new transaction
                transactionDAO.createTransaction(transaction);
            }
            if(transaction.getStatus() == declined){
                //delete transaction?
            }
        }

    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/transactions/{id}/respond", method = RequestMethod.POST)
    public Transaction acceptOrDecline(int statusNum, @Valid @RequestBody Transaction transaction, @PathVariable int id){
        transaction.setId(id);
        //where are we getting the status number from?
        transaction.setStatus(statusNum);
        try {
            if(statusNum == 2 || statusNum == 3) {
                transaction.setStatus(statusNum);
                Transaction updated = transactionDAO.update(transaction);
                return updated;
            }else {
                throw new Exception("You can only accept (2) or decline (3) a request");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
