package controller;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ForwardController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(ForwardController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            String url = request.getUrl();
            response.forward(processUrl(url));
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String processUrl(String url) {
        if (url.equals("/")) {
            return "/index.html";
        }
        return url;
    }
}
