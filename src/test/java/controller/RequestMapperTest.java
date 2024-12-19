package controller;

import http.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class RequestMapperTest {

    private static final Logger log = LoggerFactory.getLogger(RequestMapperTest.class);
    private String testDirPath = "src/test/resources/";



    @Test
    public void getForwardController() throws IOException {
        FileInputStream in = new FileInputStream(new File(testDirPath + "http_get.txt"));
        HttpRequest request = new HttpRequest(in);
        String url = request.getUrl();
        Controller controller = RequestMapper.getController(url);
        assertTrue(controller instanceof CreateUserController);
    }
}