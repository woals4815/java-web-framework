package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

            while(!line.equals("")) {
                log.debug("line: {}", line);
                line = bufferedReader.readLine();

                if(line.contains("Content-Length")) {
                    HttpRequestUtils.Pair keyValue=  HttpRequestUtils.parseHeader(line);
                    contentLength = Integer.parseInt(keyValue.getValue());
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
                log.debug("new User {}", newUser.toString());
            }


            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

            response200Header(dos, body.length);
            responseBody(dos, body);
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
