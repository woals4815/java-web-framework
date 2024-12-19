package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private String url;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters= new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        this.parseRequestLine(line);

        while(!line.equals("")) {
            line = bufferedReader.readLine();
            if (line == null || line.equals("")) {
                break;
            }
            this.setHeader(line);
        }
        this.setParameters(bufferedReader);
    }
    public String getUrl() {
        return url;
    }
    public String getMethod() {
        return method;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public Map<String, String> getParameters() {
        return parameters;
    }
    private void parseRequestLine(String line) {
        String[] tokens = line.split(" ");
        this.method = tokens[0];

        String url = tokens[1];

        int index = url.indexOf("?");

        if (index != -1) {
            String[] urlTokens = url.split(Pattern.quote("?"), 2);
            this.url = urlTokens[0];
            String queryPart = urlTokens[1];
            Map<String, String> queryKeyValues = HttpRequestUtils.parseQueryString(queryPart);
            this.parameters.putAll(queryKeyValues);
            return;
        }
        this.url = tokens[1];
    }

    private void setHeader(String line) {
        String[] tokens = line.split(":", 2);
        String key = tokens[0].trim();
        String value = tokens[1].trim();
        this.headers.put(key, value);
    }

    private void setParameters(BufferedReader bufferedReader) throws IOException {
        if (this.headers.get("Content-Length") != null) {
            String parameters = IOUtils.readData(bufferedReader, Integer.parseInt(this.headers.get("Content-Length")));
            Map<String, String> keyValues = HttpRequestUtils.parseQueryString(parameters);
            this.parameters.putAll(keyValues);
        }
    }
}
