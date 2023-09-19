package com.example.teafarmanager;

public class User {
    private String username;
    private double totalWeight;
    private double totalOwedAmount;
    private double totalAmountPaid;
    private double balance;

    public User(String username, double totalWeight, double totalOwedAmount, double totalAmountPaid, double balance) {
        this.username = username;
        this.totalWeight = totalWeight;
        this.totalOwedAmount = totalOwedAmount;
        this.totalAmountPaid = totalAmountPaid;
        this.balance = balance;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
    public String getUsername() {
        return username;
    }

    public double getTotalOwedAmount() {
        return totalOwedAmount;
    }
    public double getTotalAmountPaid() {return totalAmountPaid;}
    public double getBalance() {return balance;}
}
