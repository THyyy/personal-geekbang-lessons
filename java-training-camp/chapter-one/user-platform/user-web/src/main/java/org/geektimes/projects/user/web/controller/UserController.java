package org.geektimes.projects.user.web.controller;

import org.geektimes.Context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.impl.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Set;

/**
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/1
 */
@Path("")
public class UserController implements PageController {

    private static final UserService userService;

    private static final Validator validator;

    static {
        userService = ComponentContext.getInstance().getComponent("bean/UserService");
        validator = ComponentContext.getInstance().getComponent("bean/Validator");
    }

    @GET
    @Path("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("====进入登录/注册页面====");
        return "login-form.jsp";
    }

    @POST
    @Path("/register")
    public String register(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("====进行注册====");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phoneNumber");
        User user = new User();
        user.setName("yuancome");
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);

        // 通过目前的委派校验器处理，校验失败原因返回给用户提示信息需要额外处理，后续优化
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            return "failed.jsp";
        }

        boolean resutl = userService.register(user);
        if (resutl) {
            return "success.jsp";
        }
        return "failed.jsp";
    }

    @GET
    @Path("/user/list")
    public String list(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("====查询所有用户====");
        Collection<User> users = userService.queryAllUser();
        System.out.println(users);
        return "index.jsp";
    }

    @GET
    @Path("/user/init")
    public String init(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("====初始化 User 数据表====");
        userService.initUserTable();
        return "index.jsp";
    }
}
