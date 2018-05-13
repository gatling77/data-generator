package it.gatling77.mldemo.datagenerator;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by gatling77 on 3/17/18.
 */




public class UserGenerator{

    static LocalDateTime begin = LocalDateTime.of(2017,1,1,12,0,0);

    static PositionGenerator aroundMendrisioNarrowPositionGenerator = new PositionGenerator(3000,7000, 45.882372, 8.983121);
    static PositionGenerator aroundLuganoNarrowPositionGenerator = new PositionGenerator(2000,6000, 46.013778, 8.9614260);
    static PositionGenerator aroundLuganoWidePositionGenerator = new PositionGenerator(10000,20000, 46.013778, 8.9614260);
    static PositionGenerator aroundBellinzonaWidePositionGenerator = new PositionGenerator(10000,15000, 46.198628, 9.023575);

    static PresentationModeGenerator fewPresentationMode = new PresentationModeGenerator(3);
    static PresentationModeGenerator manyPresentationMode = new PresentationModeGenerator(10);

    static MerchantGenerator forAbitueeMerchantGenerator = new MerchantGenerator(10);
    static MerchantGenerator casualShopperMerchantGenerator = new MerchantGenerator(100);

    static AmountGenerator smallTransactionsAmountGenerator = new AmountGenerator(10, 50,20);
    static AmountGenerator mediumTransactionsAmountGenerator = new AmountGenerator(30,100, 225);
    static AmountGenerator largeTransactionsAmountGenerator = new AmountGenerator(80,500, 75);

    static TimeGenerator aCoupleADayTimeGenerator= new TimeGenerator(begin, 8000, 15000, 4000);
    static TimeGenerator oncePerWeekTimeGenerator= new TimeGenerator(begin, 10080,20160, 5000);
    static TimeGenerator twicePerMonthTimeGenerator= new TimeGenerator(begin, 15600,30000, 10000);

    public UserGenerator(Generator<LocalDateTime> timeGenerator,
                         Generator<Integer> presentationModeGenerator,
                         Generator<Position> positionGenerator,
                         Generator<String> merchantGenerator,
                         Generator<Long> amountGenerator) {
        this.timeGenerator = timeGenerator;
        this.presentationModeGenerator = presentationModeGenerator;
        this.positionGenerator = positionGenerator;
        this.merchantGenerator = merchantGenerator;
        this.amountGenerator = amountGenerator;
    }

        Generator<LocalDateTime> timeGenerator;
        Generator<Integer> presentationModeGenerator;
        Generator<Position> positionGenerator;
        Generator<String> merchantGenerator;
        Generator<Long> amountGenerator;


        User generate(long id, int transactionCount, boolean isFraud){

            renewGenerators();

            User user = new User(id,this);

            Set<Transaction> transactions = IntStream.range(1,transactionCount+1).parallel().mapToObj(i->{
                LocalDateTime time = timeGenerator.generate();
                double amount = amountGenerator.generate();
                Position position = positionGenerator.generate();
                String merchant = merchantGenerator.generate();
                int presentationMode = presentationModeGenerator.generate();
                return new Transaction(user,
                        time.toEpochSecond(ZoneOffset.UTC),
                        amount,
                        position.latitude,
                        position.longitude,
                        merchant,
                        presentationMode,
                        isFraud);
            }).collect(Collectors.toSet());
            user.setTransactions(transactions);
            return user;
        }

    private void renewGenerators(){
        timeGenerator=timeGenerator.spawnChild();
        presentationModeGenerator = presentationModeGenerator.spawnChild();
        positionGenerator = positionGenerator.spawnChild();
        merchantGenerator = merchantGenerator.spawnChild();
        amountGenerator = amountGenerator.spawnChild();
    }

}

class Generators{



}

interface Generator<T>{

    T generate();

    Generator<T> spawnChild();


}
class Position{
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public double latitude, longitude;
}

class PositionGenerator implements Generator<Position>{
    Random _random=new Random();
    private int minRadius;
    private int maxRadius;
    int radius;
    double lat;
    double lon;

    public PositionGenerator(int minRadius,int maxRadius, double lat, double lon) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.radius = _random.nextInt(maxRadius-minRadius)+minRadius;
        this.lat = lat;
        this.lon = lon;
    }



    @Override
    public Position generate() {
        // Convert radius from meters to degrees
        double radiusInDegrees=radius/111000f;

        double u=_random.nextDouble();
        double v=_random.nextDouble();
        double w=radiusInDegrees*Math.sqrt(u);
        double t=2*Math.PI*v;
        double x=w*Math.cos(t);
        double y=w*Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x=x/Math.cos(Math.toRadians(lon));

        double foundLongitude=new_x+lon;
        double foundLatitude=y+lat;

        return new Position(foundLatitude,foundLongitude);
    }

    @Override
    public Generator<Position> spawnChild() {
        return new PositionGenerator(minRadius,maxRadius,lat,lon);
    }
}

class PresentationModeGenerator implements Generator<Integer>{
    private Random _random = new Random();
    private int max;
    public PresentationModeGenerator(int max) {
        this.max=max;
    }

    @Override
    public Integer generate() {
        return _random.nextInt(max);
    }

    @Override
    public Generator<Integer> spawnChild() {
        return this;
    }
}

class MerchantGenerator implements Generator<String>{

    private int selectFrom;

    private Random _random = new Random();
    private String[] _merchants = {"Zara","H&M",
            "Varner Nysterica store",
            "Yelshire Selwyn Tarth store",
            "Roote of Lord Harroway's Town Ambrode store",
            "Fenn Podrick Payne store",
            "Qorgyle of Sandstone Tumco Lho store",
            "Uffering Mullin store",
            "Frost Denys Darklyn store",
            "Beesbury of Honeyholt Jacelyn Bywater store",
            "Woolfield Sarra Frey store",
            "Redwyne of the Arbor Myrielle Lannister store",
            "Wade Tommen Baratheon store",
            "Piper of Pinkmaiden Jeyne Darry store",
            "Greyjoy of Pyke Bryan of Oldtown store",
            "Durrandon Melicent store",
            "Selmy of Harvest Hall Garin store",
            "Chelsted Gerold Grafton store",
            "Reyne of Castamere Mohor store",
            "Hamell Lomas Estermont store",
            "Borrell of Sweetsister Loren Lannister store",
            "Casterly of Casterly Rock Penny store",
            "Hook Ulrick Dayne store",
            "Volmark Aegon IV Targaryen store",
            "Merlyn of Pebbleton Briar store",
            "Bettley Jon Penrose store",
            "Shell Gwin Goodbrother store",
            "Farring Styr store",
            "Wull Elwood store",
            "Stark of Winterfell Ferrego Antaryon store",
            "Nayland of Hag's Mire Blane store",
            "Roxton of the Ring Tarber store",
            "Kenning of Kayce Matrice store",
            "Staunton of Rook's Rest Falia Flowers store",
            "Wendwater Tytos Blackwood store",
            "Hook Harlon Greyjoy store",
            "Lake Bryen Farring store",
            "Dargood Jon Penrose store",
            "Rambton Othor store",
            "Connington of Griffin's Roost Sarya Whent store",
            "Brune of Brownhollow Davos Seaworth store",
            "Bridges Azor Ahai store",
            "Longthorpe of Longsister Harmen Uller store",
            "Hayford of Hayford Beren Tallhart store",
            "Targaryen of King's Landing Tal Toraq store",
            "Borrell of Sweetsister Tarle store",
            "Chyttering Nissa Nissa store",
            "Blackfyre of King's Landing Elia Sand store",
            "Hamell Gallard store",
            "Cerwyn of Cerwyn Hugh store",
            "Mudd of Oldstones Redtusk store",
            "Errol of Haystack Hall Meizo Mahr store",
            "Graceford of Holyhall Clement store",
            "Bracken of Stone Hedge Orbelo store",
            "Blanetree Lysono Maar store",
            "Targaryen of King's Landing Harlen Tyrell store",
            "Goodbrother of Hammerhorn Aethan store",
            "Lowther Willam Wythers store",
            "Lannett Ketter store",
            "Shawney Denys Darklyn store",
            "Fisher Tyrek Lannister store",
            "Mudd of Oldstones Robin Hollard store",
            "Estermont of Greenstone Elron store",
            "Grey Theobald store",
            "Turnberry Larraq store",
            "Yew Harry Strickland store",
            "Fell of Felwood Genna Lannister store",
            "Jast Donnel Locke store",
            "Woolfield Hairy Hal store",
            "Arryn of Gulltown Bluetooth store",
            "Magnar of Kingshouse Jeor Mormont store",
            "Glenmore Unella store",
            "Farman of Faircastle Laena Velaryon store",
            "Greyiron of Orkmont Ghost of High Heart store",
            "Bolling Porther store",
            "Estren of Wyndhall Selmond Stackspear store",
            "Baelish of Harrenhal Tristifer V Mudd store",
            "Strong of Harrenhal Quellon Greyjoy store",
            "Drinkwater Sybassion store",
            "Ruttiger Edrick Stark store",
            "Sloane Victarion Greyjoy store",
            "Shett of Gulltown Sargon Botley store",
            "Cox of Saltpans Bass store",
            "Sunderland of the Three Sisters Salladhor Saan store",
            "Glenmore Ysilla store",
            "Hayford of Hayford Gyloro Dothare store",
            "Darklyn of Duskendale Dolf store",
            "Smallwood of Acorn Hall Galbart Glover store",
            "Connington of Griffin's Roost Gormond Goodbrother store",
            "Ironsmith Ryles store",
            "Reyne of Castamere Melessa Florent store",
            "Longwaters Monford Velaryon store",
            "Lannett Grenn store",
            "Liddle Stalwart Shield store",
            "Redbeard Lucias Vypren store",
            "Graceford of Holyhall Eleyna Westerling store",
            "Ryswell of the Rills Elbert Arryn store",
            "Estermont of Greenstone Marq Rankenfell store",
            "Vikary Rhaegar Frey store",
            "Chambers Tytos Blackwood store",
            "Redwyne of the Arbor Shyra Errol store",
            "Lantell Rusty Flowers store"
    };

    public MerchantGenerator(int selectFrom){
        this.selectFrom = Math.min(selectFrom,_merchants.length-1);
    }

    @Override
    public String generate() {
        return this._merchants[_random.nextInt(selectFrom)];
    }

    @Override
    public Generator<String> spawnChild() {
        return this;
    }
}

class AmountGenerator implements Generator<Long>{

    private final long minAverage;
    private final long maxAverage;
    private final long average;
    private final long stdDev;
    private final Random _random = new Random();

    public AmountGenerator(long minAverage,long maxAverage, long stdDev) {
        this.average = _random.nextInt((int)(maxAverage-minAverage))+minAverage;
        this.stdDev = stdDev;
        this.minAverage = minAverage;
        this.maxAverage = maxAverage;
    }

    @Override
    public Long generate() {
        long result;
        do {
            result = (long)(average+ _random.nextGaussian()*(double)stdDev);
        }while(result<0);
        return result;
    }

    @Override
    public Generator<Long> spawnChild() {
        return new AmountGenerator(minAverage,maxAverage,stdDev);
    }


}

class TimeGenerator implements Generator<LocalDateTime> {


    private final long minOffsetInMinutes;
    private final long maxOffsetInMinutes;
    private final LocalDateTime startingPoint;
    private final long offsetInMinutes;
    private final long stdDev;

    private int _count = 0;
    private final Random _random = new Random();

    TimeGenerator(LocalDateTime startingPoint, long minOffsetInMinutes, long maxOffsetInMinutes, long stdDev) {
        this.startingPoint = startingPoint;
        this.offsetInMinutes = _random.nextInt((int)(maxOffsetInMinutes-minOffsetInMinutes))+minOffsetInMinutes;
        this.stdDev = stdDev;
        this.minOffsetInMinutes = minOffsetInMinutes;
        this.maxOffsetInMinutes = maxOffsetInMinutes;
    }

    @Override
    public LocalDateTime generate() {
        LocalDateTime res = startingPoint.plusMinutes((long) (offsetInMinutes * _count + _random.nextGaussian() * (double)stdDev));
        _count++;
        return res;
    }

    @Override
    public Generator<LocalDateTime> spawnChild() {
        return new TimeGenerator(startingPoint,minOffsetInMinutes,maxOffsetInMinutes,stdDev);
    }

}



