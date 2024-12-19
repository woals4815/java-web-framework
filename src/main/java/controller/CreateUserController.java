package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class CreateUserController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = request.getParameters();
        User newUser = new User(
                parameters.get("userId"),
                parameters.get("password"),
                parameters.get("name"),
                parameters.get("email")
        );
        DataBase.addUser(newUser);
        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
