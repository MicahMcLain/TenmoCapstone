package com.techelevator.tenmo.model;

import javax.validation.constraints.*;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int sendingUserId;
    @NotNull
    private int receivingUserId;
    @NotNull
    @Positive
    @Max(10000000)
    private double amount;
    private Timestamp date;

    private int status;

    public Transaction(int id, int sendingUserId, int receivingUserId, double amount, Timestamp date, int status) {
        this.id = id;
        this.sendingUserId = sendingUserId;
        this.receivingUserId = receivingUserId;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSendingUserId() {
        return sendingUserId;
    }

    public void setSendingUserId(int sendingUserId) {
        this.sendingUserId = sendingUserId;
    }

    public int getReceivingUserId() {
        return receivingUserId;
    }

    public void setReceivingUserId(int receivingUserId) {
        this.receivingUserId = receivingUserId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}