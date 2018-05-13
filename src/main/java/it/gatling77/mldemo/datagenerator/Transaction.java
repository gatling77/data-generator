package it.gatling77.mldemo.datagenerator;

/**
 * Created by gatling77 on 3/17/18.
 */
public class Transaction {
    private User user;
    private long time;
    private double amount;
    private double latitude;
    private double longitude;
    private String merchant;
    private int presentationMode;
    private boolean isFraud=false;

    public Transaction(User user, long time, double amount, double latitude, double longitude, String merchant, int presentationMode, boolean isFraud) {
        this.user=user;
        this.time = time;
        this.amount = amount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.merchant = merchant;
        this.presentationMode = presentationMode;
        this.isFraud = isFraud;
    }

    public String toCSV(){
        return user.toCSV()+","+
                time+","+
                amount+","+
                latitude+","+
                longitude+","+
                merchant+","+
                presentationMode+","+
                isFraud;
    }

    public static String header(){
        return "user,time,amount,lat,lon,merchant,presentationMode,isFraud";
    }


}


