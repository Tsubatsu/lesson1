package homework;

import com.jayway.jsonpath.JsonPath;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.RestAssured.responseSpecification;
import static io.restassured.matcher.ResponseAwareMatcherComposer.and;
import static io.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestRest {
    @BeforeTest
    public static void setSpecifications() {
        requestSpecification = new RequestSpecBuilder().
                setBaseUri("https://overpass.kumi.systems").
                setBasePath("/api/interpreter").
                setAccept(ContentType.JSON).
                setContentType(ContentType.JSON).
                log(LogDetail.ALL).
                addFilter(new ResponseLoggingFilter()).
                build();

        responseSpecification = new ResponseSpecBuilder().
                expectStatusCode(HttpStatus.SC_OK).
                build();

    }

    @Test
    public void findCafeTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:1500, 59.979930, 30.336104)[cafe~\".*\"];out;").
                when().
                get().
                then().
                rootPath("elements.tags").
                body("cafe", hasSize(greaterThanOrEqualTo(3)));
    }

    @Test(description = "Searching for metro stations")
    public void findTheMetroTest() {
        Response subways = given().
                queryParam("data",
                        "[out:json];node(around:1500, 59.979930, 30.336104)" +
                                "[railway~\".*\"];out;").
                when().
                get();
        String body = subways.body().asString();
        List<String> name =  JsonPath.read(body, "elements.*.tags[?(@.transport == 'subway')].name");
        assertThat(name, Matchers.<String>hasSize(2));
    }

    @Test(description = "Looking for missing Jager")
    public void findTheJagerTest() {
        given().
                queryParam("data",
                        "[out:json];node(around:500,59.93823555,30.2668659740719)[amenity=pub];out;").
                when().
                get().
                then().
                rootPath("elements.tags").
                body("name", not(hasItem("jagermeister")));
        //around meters
    }

}
