package org.sashvin.frontend;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class HomeResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/home")
          .then()
             .statusCode(200)
             .body(is("Sashvin"));
    }

}