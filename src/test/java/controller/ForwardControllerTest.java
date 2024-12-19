package controller;

import http.HttpRequest;
import http.HttpResponse;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;

public class ForwardControllerTest {
    private String testDirPath = "src/test/resources/";

    private Controller controller;

    @Test
    public void testService() throws IOException {
        HttpRequest request = new HttpRequest(
                new FileInputStream(new File(testDirPath + "forward_controller_get_input.txt"))
        );
        HttpResponse response = new HttpResponse(
                new FileOutputStream(new File(testDirPath + "http_get_output.txt"))
        );
        controller = new ForwardController(
        );
        controller.service(request, response);
    }

    @Test
    public void testCSS() throws IOException {
        HttpRequest request = new HttpRequest(
                new FileInputStream(new File(testDirPath + "forward_controller_get_input_css.txt"))
        );
        HttpResponse response = new HttpResponse(
                new FileOutputStream(new File(testDirPath + "http_get_css_output.txt"))
        );
        controller = new ForwardController(
        );
        controller.service(request, response);
    }
}