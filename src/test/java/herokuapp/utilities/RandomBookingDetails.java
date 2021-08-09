package herokuapp.utilities;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomBookingDetails {
    public static String setBookingDetails(){
        return    "{" +
                "    \"firstname\": \""+new Faker().name().firstName()+"\"," +
                "    \"lastname\": \""+new Faker().name().lastName()+"\"," +
                "    \"totalprice\": "+new Faker().number().digits(4)+"," +
                "    \"depositpaid\": "+new Random().nextBoolean() +"," +
                "    \"bookingdates\": {" +
                "        \"checkin\": \""+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\"," +
                "        \"checkout\": \""+LocalDate.now().plusWeeks(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\"" +
                "    }," +
                "    \"additionalneeds\": \""+new Faker().food().dish()+"\"" +
                "}";
    }

    public static String setBookingPartialDetails(){
        return    "{" +
                "    \"firstname\" : \""+new Faker().name().firstName()+"\"," +
                "    \"lastname\" : \""+new Faker().name().lastName()+"\"" +
                "}";
    }



}
