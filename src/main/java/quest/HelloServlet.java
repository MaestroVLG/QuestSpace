package quest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;


/** Стартовый сервлет
 * Который перенаправляет на главную страницу
 * Используем аннотацию @WebServlet вместо конфигурации в web.xml
 * Метод doGet() обрабатывает HTTP GET-запросы
 */

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(path:"/index.jsp").forward(request,response);

    }

}
