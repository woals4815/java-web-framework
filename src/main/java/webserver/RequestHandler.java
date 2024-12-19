package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    private HttpRequest request;
    private HttpResponse response;
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            this.request = new HttpRequest(in);
            this.response = new HttpResponse(out);

            String method = request.getMethod();
            String url  = request.getUrl();
            Map<String, String> parameters = request.getParameters();

            Map<String, String> cookies = new HashMap<>();

            if(url.startsWith("/user/create")) {
                User newUser = new User(
                        parameters.get("userId"),
                        parameters.get("password"),
                        parameters.get("name"),
                        parameters.get("email")
                );
                DataBase.addUser(newUser);
                response.sendRedirect("/index.html");
                return;
            }

            if (url.startsWith("/user/login") && method.equals("POST")) {
                User user = DataBase.findUserById(parameters.get("userId"));
                if (user == null || !user.getPassword().equals(parameters.get("password")) ) {
                    response.forward("/user/login_failed.html");
                    return;
                }
                response.sendRedirect("/index.html");
                return;
            }
            if (url.startsWith("/user/list")) {
                String cookie = cookies.get("logined");
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
                return;
            }
            response.forward(url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
