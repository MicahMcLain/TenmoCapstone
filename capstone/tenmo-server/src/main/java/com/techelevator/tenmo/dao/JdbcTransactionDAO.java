package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransactionDAO implements TransactionDAO {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Transaction mapRowToTransaction(SqlRowSet results) {
        Transaction transaction = new Transaction();
        transaction.setId(results.getInt("transactions_id"));
        transaction.setSendingUserId(results.getInt("sending_user_id"));
        transaction.setReceivingUserId(results.getInt("receiving_user_id"));
        transaction.setAmount(results.getInt("amount"));
        transaction.setDate(results.getTimestamp("date_time"));
        transaction.setStatus(results.getInt("status"));
        return transaction;
    }

    @Override
    public Transaction getTransactionsByUserId(int id) {
        Transaction transaction = null;
        String sql = "SELECT transactions_id, sending_user_id, receiving_user_id, amount, date_time, status " +
                "FROM transactions WHERE sending_user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
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
    public void createTransaction(Transaction transaction) {
        Transaction newTransaction = null;
        String create = "UPDATE tenmo_user SET balance = balance + ? WHERE user_id = ?; " +
                "UPDATE tenmo_user SET balance = balance - ? WHERE user_id = ?; " +
                "INSERT INTO transactions (amount, receiving_user_id, sending_user_id) VALUES (?,?,?);";
        try {
            jdbcTemplate.update(create, transaction.getAmount(),
                    transaction.getReceivingUserId(), transaction.getAmount(), transaction.getSendingUserId(),
                    transaction.getAmount(), transaction.getReceivingUserId(), transaction.getSendingUserId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
}
