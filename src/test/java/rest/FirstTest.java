package rest;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;


public class FirstTest {
    @Test
    public void test()
    {
        given().log().all().
                baseUri("https://overpass.kumi.systems/api/interpreter").
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                queryParam("data",
                "[out:json];node(around:1000,59.980041,30.3364678)[public_transport~\".*\"];out;").
                when().get().
                then().log().all().statusCode(200).body("elements.tags.name", hasItem("станция метро «Лесная»"));

    }
}
