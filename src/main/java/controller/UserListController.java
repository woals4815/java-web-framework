package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class UserListController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            String cookie = request.getHeaders().get("Cookie");

            if (cookie == null) {
                response.sendRedirect("/user/login.html");
                return;
            }

            Collection<User> users = DataBase.findAll();
            StringBuilder sb = new StringBuilder();
            sb.append("<table border='1'>");
            for(User user : users) {
                sb.append("<tr>");
                sb.append("<td>" + user.getUserId() + "</td>");
                sb.append("<td>" + user.getName() + "</td>");
                sb.append("<td>" + user.getEmail() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            response.forwardBody(sb.toString());
        }catch(IOException e) {
            log.error(e.getMessage());
        }

    }
}
