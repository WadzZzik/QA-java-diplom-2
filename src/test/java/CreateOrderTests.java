import com.example.diplom_2.CreateOrder;
import com.example.diplom_2.CreateUser;
import com.example.diplom_2.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.example.diplom_2.OrderController.executeMakeOrder;
import static com.example.diplom_2.UserController.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateOrderTests {
    private static CreateUser createUser;
    private static String token;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void successCreateOrderWithAuthAndWithIngredientsTest() {
        createUser = new CreateUser("Ryan_Gosling@yandex.ru","password1337o_O","Ryan Gosling");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
        CreateOrder createOrder = new CreateOrder(new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .body("name", notNullValue())
                .and()
                .body("order.number", notNullValue())
                .and()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void successCreateOrderWithoutAuthAndWithIngredientsTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{"61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa73"});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .body("name", notNullValue())
                .and()
                .body("order.number", notNullValue())
                .and()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }
    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    public void successCreateOrderWithAuthAndWithoutIngredientsTest() {
        createUser = new CreateUser("Ryan_Gosling@yandex.ru","password1337o_O","Ryan Gosling");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
        CreateOrder createOrder = new CreateOrder(new String[]{});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void successCreateOrderWithoutAuthAndWithoutIngredientsTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хэшем ингредиентов")
    public void successCreateOrderWithAuthAndWithWrongIngredientsTest() {
        createUser = new CreateUser("Ryan_Gosling@yandex.ru","password1337o_O","Ryan Gosling");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
        CreateOrder createOrder = new CreateOrder(new String[]{"61WRONGHASHc0c5a71d1f82001bdaaa76"});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и неверным хэшем ингредиентов")
    public void successCreateOrderWithoutAuthAndWithWrongIngredientsTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{"61c0c5a71dASJDHWH1f82001bdaaa6c", "61wronghashwrong76"});
        Response response = executeMakeOrder(createOrder);
        response.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
    @After
    public void deleteChanges() {
        executeDelete(token);
    }
}
