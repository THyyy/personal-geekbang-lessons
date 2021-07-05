package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

/**
 * 输出 “Hello,World” Controller
 */
@Path("/hello")
public class HelloWorldController implements PageController {

    @GET
    @POST
    @Path("/world") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }

    @POST
    @Path("/test") // /hello/test -> HelloWorldController
    public String testPost(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        BufferedReader br = request.getReader();

        String str;
        StringBuilder requestString = new StringBuilder();
        while((str = br.readLine()) != null){
            requestString.append(str);
        }
        response.setHeader("Content-type", "text/html;charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(requestString.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        System.out.println(requestString.toString());
        return requestString.toString();
    }
}
