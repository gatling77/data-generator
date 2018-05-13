package it.gatling77.mldemo.datagenerator;


import static java.lang.System.out;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by gatling77 on 3/17/18.
 */
public class DataGenerator {
    public static void main(String ... args) throws IOException {

        class UserGeneratorSelector{
            private Random random=new Random();
            private UserGenerator[] userGenerators;
            UserGeneratorSelector(UserGenerator[] userGenerators){
                this.userGenerators=userGenerators;
            }

            UserGenerator pickOne(){
                return pickOne(userGenerators);
            }
            private UserGenerator pickOne(UserGenerator[] list){
                return list[random.nextInt(list.length)];
            }

            UserGenerator pickAnother(UserGenerator u){
                for (int i=0; i<userGenerators.length;i++){
                    if(userGenerators[i]==u){
                        return userGenerators[(i+1)%userGenerators.length];
                    }else {
                       return pickOne();
                    }
                }
                return pickOne();
            }

        }


        if (args.length <4) {
            out.println("Missing parameters");
            out.println("Usage: java it.gatling77.mldemo.datagenerator.DataGenerator [numberOfUsers] [transactionsPerUser] [fraudPerUsers] [outFIle]");
        }
        int numberOfUsers = Integer.parseInt(args[0]);
        int transactionsPerUser = Integer.parseInt(args[1]);
        int fraudPerUsers = Integer.parseInt(args[2]);

        String file = args[3];

        UserGenerator frequentSmallTransactionsAroundMendrisio = new UserGenerator(
                UserGenerator.aCoupleADayTimeGenerator,
                UserGenerator.fewPresentationMode,
                UserGenerator.aroundMendrisioNarrowPositionGenerator,
                UserGenerator.forAbitueeMerchantGenerator,
                UserGenerator.smallTransactionsAmountGenerator
        );

        UserGenerator intermittentLargeTransactionsAroundLugano = new UserGenerator(
                UserGenerator.twicePerMonthTimeGenerator,
                UserGenerator.manyPresentationMode,
                UserGenerator.aroundLuganoWidePositionGenerator,
                UserGenerator.casualShopperMerchantGenerator,
                UserGenerator.largeTransactionsAmountGenerator
        );

        UserGenerator frequentMediumTransactionsAroundBellinzona = new UserGenerator(
                UserGenerator.oncePerWeekTimeGenerator,
                UserGenerator.fewPresentationMode,
                UserGenerator.aroundBellinzonaWidePositionGenerator,
                UserGenerator.forAbitueeMerchantGenerator,
                UserGenerator.mediumTransactionsAmountGenerator
        );


        UserGeneratorSelector userGeneratorSelector= new UserGeneratorSelector(new UserGenerator[]{frequentSmallTransactionsAroundMendrisio,
               intermittentLargeTransactionsAroundLugano,
               frequentMediumTransactionsAroundBellinzona
        });


        Random random=new Random();
        Stream<String> transactions =  IntStream.range(1,numberOfUsers+1).parallel()
                // first of all, I create some rightfull transactions...
                .mapToObj(i-> userGeneratorSelector.pickOne().generate(i,transactionsPerUser, false))
                //then i create some frauds by creating transactions with a different generator and get the transactions out of the user
                .map(u->u.mergeUser(userGeneratorSelector.pickAnother(u.generator).generate(u.id,fraudPerUsers,true)).getTransactions())
                .flatMap(l->l.stream()).map(t->t.toCSV());


        out.println(transactions.count()+" generated, writing them to file");
        // Awfully creative way to write an header, I know, but I'm too lazy to search for a better way :-)
        Path p = Paths.get(file);
        Files.deleteIfExists(p);
        Files.write(p,new ArrayList<String>(){{this.add(Transaction.header());}},StandardOpenOption.CREATE_NEW);
        Files.write(p,(Iterable<String>)transactions::iterator,StandardOpenOption.APPEND);
        out.println("Sample written");
        //transactions.forEach(System.out::println);
    }


}
