package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = request.getParameters();
        User user = DataBase.findUserById(parameters.get("userId"));
        try {
            if (user == null || !user.getPassword().equals(parameters.get("password")) ) {
                response.forward("/user/login_failed.html");
                return;
            }
            response.addHeader("Set-Cookie", "logined=true; Path=/");
            response.sendRedirect("/");
        }catch (IOException e) {
            log.error(e.getMessage());
        }

    }
}
