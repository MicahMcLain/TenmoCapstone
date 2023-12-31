package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDAO implements TransactionDAO {
    public enum transactionType{UNKNOWN, PENDING, APPROVED, REJECTED}
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Transaction mapRowToTransaction(SqlRowSet results) {
        Transaction transaction = new Transaction();
        transaction.setId(results.getInt("transactions_id"));
        transaction.setSendingUserId(results.getInt("sending_user_id"));
        transaction.setReceivingUserId(results.getInt("receiving_user_id"));
        transaction.setAmount(results.getDouble("amount"));
        transaction.setDate(results.getTimestamp("date_time"));
        transaction.setStatus(results.getInt("status"));
        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int id) {
        List<Transaction> transaction = new ArrayList<>();
        String sql = "SELECT transactions_id, sending_user_id, receiving_user_id, amount, date_time, status " +
                "FROM transactions WHERE sending_user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while (results.next()) {
                Transaction transactionResult = (mapRowToTransaction(results));
                transaction.add(transactionResult);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL Syntax Error", e);
        }
        return transaction;
    }

    @Override
    public Transaction getTransactionsByUserIdAndTransactionId(int userId, int transactionId) {
        Transaction transaction = null;
        String sql = "SELECT transactions_id, sending_user_id, receiving_user_id, amount, date_time, status " +
                "FROM transactions WHERE sending_user_id = ? AND transactions_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, transactionId);
            while (results.next()) {
                transaction = mapRowToTransaction(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL Syntax Error", e);
        }
        return transaction;
    }

    @Override
    public void transferMoney(Transaction transaction) throws Exception {
        Transaction newTransaction = null;
        if (transaction.getAmount() < 0.01) {
            throw new Exception("You must send a valid amount");
        }
        String create = "BEGIN TRANSACTION; UPDATE tenmo_user SET balance = balance + ? WHERE user_id = ?; " +
                "UPDATE tenmo_user SET balance = balance - ? WHERE user_id = ?; " +
                "INSERT INTO transactions (amount, receiving_user_id, sending_user_id) VALUES (?,?,?); COMMIT;";
        if(transaction.getReceivingUserId() == (transaction.getSendingUserId())) {
            throw new Exception("You cannot send money to yourself");
        }
            jdbcTemplate.update(create, transaction.getAmount(),
                    transaction.getReceivingUserId(), transaction.getAmount(), transaction.getSendingUserId(),
                    transaction.getAmount(), transaction.getReceivingUserId(), transaction.getSendingUserId());


    }

    @Override
    public Transaction update(Transaction transaction) {
        Transaction updated = null;
        //how are we going to get status? can only update to 2 or 3
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transaction.getId());

            if(numberOfRows ==0 ){
                throw new DaoException("0 rows affected. Expected at least one");
            } else {
                //may need to use principal to make a user and get the user id
                updated = getTransactionsByUserIdAndTransactionId(transaction.getSendingUserId(), transaction.getId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    @Override
    public void createPendingTransaction(Transaction transaction) {
        transaction.setStatus(transactionType.PENDING.ordinal());
        String create = "INSERT INTO transactions (amount, receiving_user_id, sending_user_id, status) VALUES (?,?,?,?);";
            jdbcTemplate.update(create, transaction.getAmount(), transaction.getReceivingUserId(),
                    transaction.getSendingUserId(), transaction.getStatus());
    }
    //approve transaction method
    @Override
    public void approveTransaction(Transaction transaction){
        transaction.setStatus(transactionType.APPROVED.ordinal());
        String approve = "UPDATE transaction SET status = 2 WHERE transaction_id = ?";
        jdbcTemplate.update(approve, transaction.getId());
    }
    //decline transaction method
    @Override
    public void rejectTransaction(Transaction transaction){
        transaction.setStatus(transactionType.REJECTED.ordinal());
        String reject = "UPDATE transaction SET status = 3 WHERE transaction_id = ?";
        jdbcTemplate.update(reject, transaction.getId());
    }
    public void declineTransaction(Transaction transaction){
        Transaction updated = null;
        //how are we going to get status? can only update to 2 or 3
        //add numberof rows updated
        String sql = "UPDATE transactions SET status = 3 WHERE transaction_id = ?;";
        jdbcTemplate.update(sql, transaction.getId());

    }
    //do we actually need to delete any of the transactions?
    @Override
    public int delete(int id) {
        int numberOfRows = 0;
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(sql, id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return numberOfRows;
    }
}
