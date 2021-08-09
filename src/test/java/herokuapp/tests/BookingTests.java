package herokuapp.tests;

import herokuapp.utilities.ConfigurationReader;
import herokuapp.utilities.RandomBookingDetails;
import io.restassured.response.*;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class BookingTests {

    Response response, tokenResponse;
    String requestBody;
    String token;
    String tokenRequestBody;
    List<Integer> bookingIds = Arrays.asList();

    @Before
    public void setUp() {
        baseURI = ConfigurationReader.get("baseUrl");
        response = given().get(ConfigurationReader.get("healthCheck"));
        Assert.assertEquals("verify end point working", 201, response.statusCode());

        tokenRequestBody = "{" +
                "    \"username\" : \"" + ConfigurationReader.get("username") + "\"," +
                "    \"password\" : \"" + ConfigurationReader.get("password") + "\"" +
                "}";
        tokenResponse = given().header("Content-Type", "application/json")
                .and().body(tokenRequestBody)
                .when().post(ConfigurationReader.get("createToken"));
        Assert.assertEquals("verify get token response", 200, tokenResponse.statusCode());
        token = tokenResponse.path("token");
    }

    @Test
    public void createBooking() {
        requestBody = RandomBookingDetails.setBookingDetails();
        response = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .and().body(requestBody)
                .when().post(ConfigurationReader.get("createBooking"));

        Assert.assertEquals(200, response.statusCode());
        Assert.assertTrue(requestBody.contains(response.path("booking.firstname")));
        Assert.assertTrue(requestBody.contains(response.path("booking.lastname")));
        Assert.assertTrue(requestBody.contains(response.path("booking.totalprice").toString()));
        Assert.assertTrue(requestBody.contains(response.path("booking.depositpaid").toString()));
        Assert.assertTrue(requestBody.contains(response.path("booking.bookingdates.checkin")));
        Assert.assertTrue(requestBody.contains(response.path("booking.bookingdates.checkout")));
        Assert.assertTrue(requestBody.contains(response.path("booking.additionalneeds")));
    }

    @Test
    public void getBookingIds() {
        response = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .when().get(ConfigurationReader.get("createBooking"));
//        bookingIds = response.path("bookingid");
        Assert.assertEquals(200, response.statusCode());
    }

    @Test
    public void getBookingById() {
        Response responseBookingIds = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .when().get(ConfigurationReader.get("createBooking"));
        bookingIds = responseBookingIds.path("bookingid");

        response = given().header("Accept", "application/json")
                .and().pathParam("id", bookingIds.get(new Random().nextInt(bookingIds.size())))
                .when().get(ConfigurationReader.get("createBooking") + ConfigurationReader.get("idPathParam"));
        Assert.assertEquals(200, response.statusCode());
    }

    @Test
    public void updateBooking() {
        requestBody = RandomBookingDetails.setBookingDetails();
        response = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .and().header("Cookie", "token=" + token)
                .and().pathParam("id", 5)
                .and().body(requestBody)
                .when().put(ConfigurationReader.get("createBooking") + ConfigurationReader.get("idPathParam"));
        Assert.assertTrue("Verify firstname update", requestBody.contains(response.path("lastname")));
        Assert.assertEquals(200, response.statusCode());
    }

    @Test
    public void partialUpdateBooking() {
        requestBody = RandomBookingDetails.setBookingPartialDetails();
        response = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .and().header("Cookie", "token=" + token)
                .and().pathParam("id", 5)
                .and().body(requestBody)
                .when().patch(ConfigurationReader.get("createBooking") + ConfigurationReader.get("idPathParam"));
        Assert.assertTrue("Verify firstname update", requestBody.contains(response.path("lastname")));
        System.out.println("response.prettyPrint() = " + response.prettyPrint());
        Assert.assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteBooking() {
        response = given().header("Accept", "application/json")
                .and().header("Content-Type", "application/json")
                .when().get(ConfigurationReader.get("createBooking"));
        bookingIds = response.path("bookingid");

        response = given().header("Content-Type", "application/json")
                .and().header("Cookie", "token=" + token)
                .and().pathParam("id", bookingIds.get(1))
                .when().delete(ConfigurationReader.get("createBooking") + ConfigurationReader.get("idPathParam"));
        Assert.assertEquals(201, response.statusCode());
    }
}
