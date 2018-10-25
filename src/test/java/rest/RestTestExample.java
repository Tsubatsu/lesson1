package rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestTestExample {



    @BeforeTest
    public static void setSpecifications() {
        requestSpecification = new RequestSpecBuilder().
                setBaseUri("https://overpass.kumi.systems"). // !!!  to property or constants class
                setBasePath("/api/interpreter").// !!!  to property or constants class
                setAccept(ContentType.JSON).
                setContentType(ContentType.JSON).
                log(LogDetail.ALL).
                addFilter(new ResponseLoggingFilter()).
                build();

        responseSpecification = new ResponseSpecBuilder().
                expectStatusCode(HttpStatus.SC_OK).
                build();

    }

    @Test(description = "We have to be sure about a pub near here")
    public void findThePubTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:400,59.93823555,30.2668659740719)[amenity=pub];out;").
                when().
                      get().
                then().
                      body("elements", hasSize(greaterThanOrEqualTo(1)));
        //around meters
    }

    @Test(description = "Searching the transport")
    public void searchForTransportTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:1000,59.980041,30.3364678)[public_transport~\".*\"];out;").
                when().
                      get().
                then()
                      .body("elements.tags.name", hasItem("станция метро «Лесная»"));
        //around meters
    }

    @Test
    public void findPublicTransportTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:5000,59.93823555,30.2668659740719)" +
                                "[public_transport~\".*\"];out;").
                when().
                       get().
                then().
                       rootPath("elements.tags").
                       body("highway", hasItem("bus_stop"),
                        "name", hasItem("станция метро «Невский проспект»"));
    }

    @Test
    public void findCafeTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:400,59.93823555,30.2668659740719)[cafe~\".*\"];out;").
                when().
                       get().
                then().
                       rootPath("elements.tags").
                       body("name", notNullValue());
    }

}
