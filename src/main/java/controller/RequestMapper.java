package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private static Map<String, Controller> controllers = new HashMap<>();

    private RequestMapper() {}

    static {
        controllers.put("/", new ForwardController());
        controllers.put("/user/form.html", new ForwardController());
        controllers.put("/user/login.html", new ForwardController());
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new UserListController());
    }

    static public Controller getController(String url) {
        return controllers.get(url);
    }
}
