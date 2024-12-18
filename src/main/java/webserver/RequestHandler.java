package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import db.DataBase;
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

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");

            String method = tokens[0];
            String url  = tokens[1];


            int contentLength = 0;

            Map<String, String> cookies = new HashMap<>();

            while(!line.equals("")) {
                log.debug("line: {}", line);
                line = bufferedReader.readLine();

                if(line.contains("Content-Length")) {
                    HttpRequestUtils.Pair keyValue=  HttpRequestUtils.parseHeader(line);
                    contentLength = Integer.parseInt(keyValue.getValue());
                }
                if (line.contains("Cookie")) {
                    cookies = HttpRequestUtils.parseCookies(line.split(":")[1].trim());
                }
            }

            if(url.startsWith("/user/create")) {
                String bodyMessage = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(bodyMessage);
                User newUser = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );
                DataBase.addUser(newUser);
                response302Redirect(out, "/index.html");
                return;
            }

            if (url.startsWith("/user/login") && method.equals("POST")) {
                String bodyMessage = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(bodyMessage);
                User user = DataBase.findUserById(params.get("userId"));
                if (user == null || !user.getPassword().equals(params.get("password")) ) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }
                responseRedirectLoginSuccess(out, "/index.html");
                return;
            }
            if (url.startsWith("/user/list")) {
                String cookie = cookies.get("logined");
                if (cookie == null) {
                    response302Redirect(out, "/user/login.html");
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
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);

                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }
            responseResource(out, url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(
            OutputStream out,
            String url
    ) {
        DataOutputStream dos = new DataOutputStream(out);
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseRedirectLoginSuccess(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("Set-Cookie: " + "logined=true; Path=/" + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Redirect(
            OutputStream out,
            String url
            ) {
        DataOutputStream dos = new DataOutputStream(out);
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
