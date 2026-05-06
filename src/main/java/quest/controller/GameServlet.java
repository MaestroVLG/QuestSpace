package quest.controller;

import quest.model.QuestStep;
import service.QuestService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Game servlet - класс, обрабатывающий логику игры
 * Использует HTTP Session для хранения состояния между запросами.
 * Обрабатывает как GET (отображение шага), так и POST (обработка ответа) запросы.
 * Перенаправляет на result.jsp после завершения игры.
 */
@WebServlet("/game")
public class GameServlet extends HttpServlet {

    private final QuestService questService = new QuestService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String stepId = request.getParameter("step");
        if (stepId == null || stepId.isEmpty()) {
            stepId = "start"; // начальный шаг по умолчанию
        }

        QuestStep step = questService.getStep(stepId);
        if (step == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("step", step);
        request.getRequestDispatcher("/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String answer = request.getParameter("answer");
        String currentStep = request.getParameter("currentStep");

        if (answer == null || currentStep == null) {
            request.setAttribute("message", "Выберите вариант");
            doGet(request, response); // используем doGet для отображения текущего шага
            return;
        }

        QuestStep current = questService.getStep(currentStep);
        if (current == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String nextStepId;
        if ("1".equals(answer)) {
            nextStepId = current.getNextStepId1();
        } else if ("2".equals(answer)) {
            nextStepId = current.getNextStepId2();
        } else {
            request.setAttribute("message", "Некорректный выбор");
            doGet(request, response);
            return;
        }

        if (questService.isFinalStep(nextStepId)) {
            // Игра завершена
            QuestStep finalStep = questService.getStep(nextStepId);
            session.setAttribute("result", finalStep.getText());
            response.sendRedirect(request.getContextPath() + "/result.jsp");
        } else {
            // Переход к следующему шагу
            response.sendRedirect(request.getContextPath() + "/game?step=" + nextStepId);
        }
    }
}