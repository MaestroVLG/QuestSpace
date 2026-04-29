package quest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/** Game servlet - класс обрабатывающий логику игры
 * Использует HPTTP Session для хранения состояния между запросами.
 * Проверяет ответы пользователя.
 * Устанавливает результат в сессию.
 * Используем sendRedirect для предотвращения повторной отправки формы.
 * */

@WebServlet("/game")
public class GameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HTTPSession session = request.getSession();
        String answer = request.getParameter("answer");

        if (answer == null){
            request.setAttribute("message", "Выберите вариант");
            request.getRequestDispatcher("/question.jsp").forward(request,response);
            return;
        }

        if ("accept".equals(answer)){
            session.setAttribute("result", "Победа! Вы приняли вызов");

        } else{
            session.setAttribute("result","Вы отказались");
        }

        response.sendRedirect(request.getContextPath() + "/result.jsp");
    }
}