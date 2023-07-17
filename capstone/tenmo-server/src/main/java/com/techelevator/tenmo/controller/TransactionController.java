package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransactionDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
    public void sendMoney(@RequestParam (required = false) String action,
                          Principal principal, @Valid @RequestBody Transaction transaction) throws Exception {
//        request = "/request";
//        respond = "/respond";

        //need a second path parameter for respond
        User userFrom = userDao.findByUsername(principal.getName());
        transaction.setSendingUserId(userFrom.getId());
        if(action == null) {
            
            transactionDAO.transferMoney(transaction);
        } else if(action.equals("request")){
            transactionDAO.createPendingTransaction(transaction);
        } else if("respond" != null){
            if(transaction.getStatus() == 3){
                transactionDAO.rejectTransaction(transaction);
            }
            if(transaction.getStatus() == 2){
                transactionDAO.approveTransaction(transaction);
            }
        }

    }
}
