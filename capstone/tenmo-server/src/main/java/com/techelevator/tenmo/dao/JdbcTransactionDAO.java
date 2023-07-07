package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transactions;
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

    private Transactions mapRowToTransaction(SqlRowSet results) {
        Transactions transaction = new Transactions();
        transaction.setDate(results.getTimestamp("date_time"));
        transaction.setAmount(results.getInt("amount"));
        transaction.setId(results.getInt("transactions_id"));
        transaction.setSendingUserId(results.getInt("sending_user_id"));
        transaction.setReceivingUserId(results.getInt("receiving_user_id"));
        transaction.setStatus(results.getInt("status"));
        return transaction;
    }

    @Override
    public Transactions getTransactionsByUserId(int id) {
        Transactions transaction = null;
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
    public Transactions getTransactionsByUserIdAndTransactionId(int userId, int transactionId) {
        Transactions transaction = null;
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
    public Transactions createTransaction(Transactions transaction) {
        Transactions newTransaction = null;
        String create = "UPDATE tenmo_user SET balance = balance + ? WHERE user_id = ?; " +
                "UPDATE tenmo_user SET balance = balance - ? WHERE user_id = ?; " +
                "INSERT INTO transactions VALUES (?,?) RETURNING transactions_id;";
        try {
            int newTransactionId = jdbcTemplate.queryForObject(create, int.class, transaction.getAmount(),
                    transaction.getReceivingUserId(), transaction.getAmount(), transaction.getSendingUserId(),
                    transaction.getAmount(), transaction.getReceivingUserId());
            newTransaction = getTransactionsByUserIdAndTransactionId(transaction.getSendingUserId(), newTransactionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        finally {
            System.out.println();
        }
        return newTransaction;
    }
}
