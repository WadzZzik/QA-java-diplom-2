package com.example.diplom_2;

import io.restassured.response.Response;

import static com.example.diplom_2.UserController.getUserToken;
import static io.restassured.RestAssured.given;

public class OrderController {
    private final static String apiOrders = "/api/orders";

    public static Response executeMakeOrder(CreateOrder createOrder) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(createOrder)
                        .when()
                        .post(apiOrders);
        return response;
    }

    public static Response executeGetOrder(LoginUser loginUser) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(getUserToken(loginUser))
                        .when()
                        .get(apiOrders);
        return response;
    }

    public static Response executeGetOrder(String token, boolean useAuth) {
        Response response;
        if (useAuth) {
            response = given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(token)
                    .when()
                    .get(apiOrders);
        } else {
            response = given()
                    .header("Content-type", "application/json")
                    .when()
                    .get(apiOrders);
        }
        return response;
    }
}
