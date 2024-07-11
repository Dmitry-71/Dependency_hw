package org.example.servlet;

import org.example.controller.PostController;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.example.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.example.config.JavaConfig;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private PostRepository repository;
    private final String PATH_POSTS = "/api/posts";
    private final String PATH_WITH_NUMBER_POST = PATH_POSTS + "/\\d+";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = context.getBean(PostController.class);

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals("GET") && path.equals(PATH_POSTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches(PATH_WITH_NUMBER_POST)) {
                // easy way
                final var id = findId(path);

                controller.getById(id, resp);

                return;
            }
            if (method.equals("POST") && path.equals(PATH_POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(PATH_WITH_NUMBER_POST)) {
                // easy way
                final var id = findId(path);

                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private long findId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}
