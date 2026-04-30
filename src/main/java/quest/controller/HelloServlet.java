package quest.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Стартовый сервлет
 * Который перенаправляет на главную страницу
 * Используем аннотацию @WebServlet вместо конфигурации в web.xml
 * Метод doGet() обрабатывает HTTP GET-запросы
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
