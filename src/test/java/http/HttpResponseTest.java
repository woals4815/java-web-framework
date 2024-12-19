package http;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class HttpResponseTest {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseTest.class);
    private String testDirPath = "src/test/resources";

    @Test
    public void testForward() throws IOException {
        HttpResponse response = new HttpResponse(
                createOutputStream("Http_forward.txt")
        );
        response.forward("/index.html");
    }


    @Test
    public void responseCookie() throws IOException {
        HttpResponse response = new HttpResponse(
                createOutputStream("cookie.txt")
        );

        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }


    private OutputStream createOutputStream(String filename) throws IOException {
        return new FileOutputStream(new File(testDirPath + "/" + filename));
    }
}