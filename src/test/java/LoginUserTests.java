import com.example.diplom_2.CreateUser;
import com.example.diplom_2.LoginUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.example.diplom_2.UserController.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class LoginUserTests {
    private static CreateUser createUser;
    private static String token;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        createUser = new CreateUser("i_just_crashed_my_mclaren_few_times@yandex.ru","funnyMovies322","Bean Mister");
        executeCreate(createUser);
        token = getUserToken(new LoginUser(createUser.getEmail(), createUser.getPassword()));
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void successSingleTest() {
        LoginUser loginUser = new LoginUser("i_just_crashed_my_mclaren_few_times@yandex.ru","funnyMovies322");
        Response response = executeLogin(loginUser);
        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", startsWith("Bearer"))
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user.email", equalTo(loginUser.getEmail()))
                .and()
                .body("user.name", equalTo(createUser.getName()))
                .and()
                .statusCode(SC_OK);
    }

    public void failAuthWrongCredential(LoginUser credential) {
        Response response = executeLogin(credential);
        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
    @Test
    @DisplayName("Логин с неверным логином")
    public void failLoginWithWrongLoginTest() {
        LoginUser loginUserWithWrongLogin = new LoginUser("i_just_crashed_my_mclaren_once@yandex.ru","funnyMovies322");
        failAuthWrongCredential(loginUserWithWrongLogin);
    }
    @Test
    @DisplayName("Логин с неверным паролем")
    public void failLoginWithWrongPasswordTest() {
        LoginUser loginUserWithWrongPassword = new LoginUser("i_just_crashed_my_mclaren_few_times@yandex.ru","funnyMovies1234");
        failAuthWrongCredential(loginUserWithWrongPassword);
    }

    @After
    public void deleteChanges() {
        executeDelete(token);
    }
}
