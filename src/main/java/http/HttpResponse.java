package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private OutputStream out;
    private Map<String, String> headers = new HashMap<>();
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.out = out;
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

        setContentType(url);
        this.addHeader("Content-Length", String.valueOf(body.length));
        response200Header();


        responseBody(body);
    }

    public void forwardBody(String body) throws IOException {
        byte[] bodyBytes = body.getBytes();
        this.addHeader("Content-Length", String.valueOf(bodyBytes.length));
        this.addHeader("Content-Type", "text/html");
        response200Header();
        responseBody(bodyBytes);
    }

    private void setContentType(String url) {
        if (url.endsWith(".css")) {
            this.addHeader("Content-Type", "text/css");
        } else if (url.endsWith(".js")) {
            this.addHeader("Content-Type", "text/javascript");
        } else {
            this.addHeader("Content-Type", "text/html");
        }
    }

    private void writeHeaders() throws IOException {
        Set<String> keys = this.headers.keySet();
        for (String key : keys) {
            log.debug(key + ": " + this.headers.get(key));
            String value = headers.get(key);
            dos.writeBytes(key + ": " + value + " \r\n");
        }
        dos.writeBytes("\r\n");
    }

    public void sendRedirect(String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        this.addHeader("Location", url);
        writeHeaders();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.writeBytes("\r\n");
        dos.flush();
    }
    private void response200Header() throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        writeHeaders();
    }
}
