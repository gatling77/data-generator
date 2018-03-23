package it.gatling77.mldemo.datagenerator;

import com.github.javafaker.Faker;
import org.junit.Test;

import java.util.stream.IntStream;

/**
 * Created by gatling77 on 3/18/18.
 */
public class TestUserGenerator {

    @Test
    public void testPositionGenerator(){
        PositionGenerator sut = new PositionGenerator(1000,45.882372, 8.983121);
        IntStream.range(0,100).forEach(
            i -> {
                System.out.println(sut.generate().toString());
            }
        );
    }

    @Test
    public void generateMerchant(){
        Faker faker = new Faker();
        IntStream.range(0,100).forEach(
                i -> {
                    System.out.println("\""+faker.gameOfThrones().house()+" "+faker.gameOfThrones().character()+" store"+"\",");
                }
        );
    }
}
