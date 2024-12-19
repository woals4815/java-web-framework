package http;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestTest extends TestCase {
    private String testDirPath = "src/test/resources";
    private HttpRequest request;

    @Before
    public void setUp() throws Exception {
        FileInputStream in = new FileInputStream(new File(testDirPath + "/http_get.txt"));
        request = new HttpRequest(in);
    }

    @Test
    public void testGetUrl() {
        assertEquals("/user/create", request.getUrl());
    }

    @Test
    public void testGetMethod() {
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void testGetHeaders() {
        Map<String, String> expected = new HashMap<>();
        expected.put("Host", "localhost:8080");
        assertEquals(expected.get("Host"), request.getHeaders().get("Host"));
    }

    @Test
    public void testGetParameters() {
        Map<String, String> expected = new HashMap<>();
        expected.put("userId", "유저아이디");
        assertEquals(expected, request.getParameters());
    }

    @Test
    public void testPost() throws IOException {
        FileInputStream in = new FileInputStream(new File(testDirPath + "/http_post.txt"));
        HttpRequest postRequest = new HttpRequest(in);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("Host", "localhost:8080");
        expectedHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        expectedHeaders.put("Accept", "*/*");

        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put("userId", "유저아이디");
        assertEquals("/user/create", postRequest.getUrl());
        assertEquals(expectedHeaders.get("Host"), postRequest.getHeaders().get("Host"));
        assertEquals(expectedParams, postRequest.getParameters());
    }
}