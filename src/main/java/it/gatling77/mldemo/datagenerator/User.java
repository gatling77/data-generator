package it.gatling77.mldemo.datagenerator;

import java.util.Set;

/**
 * Created by gatling77 on 5/13/18.
 */
public class User {
    long id;
    UserGenerator generator;

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
    public User mergeUser(User u){
        this.transactions.addAll(u.getTransactions());
        return this;
    }
    Set<Transaction> transactions;

    public User(long id, UserGenerator generator) {
        this.id = id;
        this.generator = generator;
    }
    public String toCSV(){
        return ""+id;
    }

    public Set<Transaction> getTransactions(){
        return transactions;
    }
}
