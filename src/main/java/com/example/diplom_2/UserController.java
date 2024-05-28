package com.example.diplom_2;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserController {
    private final static String apiCreateUser = "/api/auth/register";
    private final static String apiLoginUser = "/api/auth/login ";
    private final static String apiDeleteUser = "/api/auth/user";
    private final static String apiChangeUserData = "/api/auth/user";

    public static Response executeCreate(CreateUser createUser) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(createUser)
                        .when()
                        .post(apiCreateUser);
        return response;
    }

    public static Response executeDelete(LoginUser loginUser) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(getUserToken(loginUser))
                        .when()
                        .delete(apiDeleteUser);
        return response;
    }

    public static Response executeDelete(String token) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(token)
                        .when()
                        .delete(apiDeleteUser);
        return response;
    }



    public static String getUserToken(LoginUser loginUser) {
        Response response = executeLogin(loginUser);
        String accessToken = response.jsonPath().get("accessToken");
        return accessToken.split(" ")[1]; // разбили строку на 2 значения, разделитель Пробел. Выбрали второе значение (токен)
    }
    public static Response executeLogin(LoginUser loginUser) {
        return
                given()
                        .header("Content-type", "application/json")
                        .body(loginUser)
                        .when()
                        .post(apiLoginUser);
    }

    public static Response executeChangeUserData(LoginUser userBefore, ChangeUser changeUser, boolean useAuth) {
        String token;
        Response response;
        if (useAuth) {
            token = getUserToken(userBefore);
            response = given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(token)
                    .body(changeUser)
                    .when()
                    .patch(apiChangeUserData);
        } else {
            response = given()
                    .header("Content-type", "application/json")
                    .body(changeUser)
                    .when()
                    .patch(apiChangeUserData);
        }

        return response;
    }
}
