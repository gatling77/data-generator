package it.gatling77.mldemo.datagenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by gatling77 on 3/17/18.
 */
public class DataGenerator {
    public static void main(String ... args) throws IOException {
        int numberOfUsers = 3;
        int transactionsPerUser = 10;
        String file = "/home/gatling77/dev/mldemo/datagenerator/target/data.csv";

        UserGenerator frequentSmallTransactionsAroundMendrision = new UserGenerator(
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


        UserGenerator[] userGenerators= {frequentSmallTransactionsAroundMendrision,
                intermittentLargeTransactionsAroundLugano,
                frequentMediumTransactionsAroundBellinzona
        };


        Random random=new Random();
        Stream<String> transactions = IntStream.range(1,numberOfUsers+1).parallel()
                .mapToObj(i-> userGenerators[random.nextInt(userGenerators.length)].generate(i,transactionsPerUser))
                .flatMap(s->s.stream()).map(t->t.toCSV());

         Files.write(Paths.get(file),(Iterable<String>)transactions::iterator);
        //transactions.forEach(System.out::println);
    }

}
