package it.gatling77.mldemo.datagenerator;

/**
 * Created by gatling77 on 3/17/18.
 */
public class Transaction {
    private long user;
    private long time;
    private double amount;
    private double latitude;
    private double longitude;
    private String merchant;
    private int presentationMode;
    private boolean isFraud=false;

    public Transaction(long user, long time, double amount, double latitude, double longitude, String merchant, int presentationMode) {
        this.user = user;
        this.time = time;
        this.amount = amount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.merchant = merchant;
        this.presentationMode = presentationMode;
        this.isFraud = isFraud;
    }

    public void markAsFraud(){
        this.isFraud=true;
    }
    public boolean isFraud(){
        return isFraud;
    }
    public String toCSV(){
        return user+","+
                time+","+
                amount+","+
                latitude+","+
                longitude+","+
                merchant+","+
                presentationMode+","+
                isFraud;
    }

}


