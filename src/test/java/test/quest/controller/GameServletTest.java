package test.quest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quest.controller.GameServlet;
import quest.model.QuestStep;
import service.QuestService;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import java.lang.reflect.Method;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private QuestService questService;

    @InjectMocks
    private GameServlet gameServlet;

    private Method doGetMethod;
    private Method doPostMethod;

    @BeforeEach
    void setUp() throws Exception {
        // Используем рефлексию для доступа к protected методам
        doGetMethod = GameServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        doGetMethod.setAccessible(true);
        
        doPostMethod = GameServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPostMethod.setAccessible(true);
        
        // Настройка mocked QuestService
        QuestStep startStep = new QuestStep("start", "Ты потерял память. Принять вызов НЛО?", 
                "Принять вызов", "Отклонить вызов", "bridge", "lose1");
        when(questService.getStep(eq("start"))).thenReturn(startStep);
        
        QuestStep bridgeStep = new QuestStep("bridge", "Ты принял вызов. Поднимаешься на мостик к капитану?", 
                "Подняться на мостик", "Отказаться", "captain", "lose2");
        when(questService.getStep(eq("bridge"))).thenReturn(bridgeStep);
        
        QuestStep captainStep = new QuestStep("captain", "Ты поднялся на мостик. Ты кто?", 
                "Рассказать правду о себе", "Солгать о себе", "win", "lose3");
        when(questService.getStep(eq("captain"))).thenReturn(captainStep);
        
        QuestStep winStep = new QuestStep("win", "Тебя вернули домой. Победа!", null, null, null, null);
        when(questService.getStep(eq("win"))).thenReturn(winStep);
        
        QuestStep lose1Step = new QuestStep("lose1", "Ты отклонил вызов. Поражение!", null, null, null, null);
        when(questService.getStep(eq("lose1"))).thenReturn(lose1Step);
        
        when(questService.isFinalStep(anyString())).thenAnswer(invocation -> {
            String stepId = invocation.getArgument(0);
            return stepId != null && (stepId.equals("win") || 
                                   stepId.equals("lose1") || 
                                   stepId.equals("lose2") || 
                                   stepId.equals("lose3"));
        });
    }

    @Test
    @DisplayName("GET запрос с параметром step=start должен отобразить начальный шаг")
    void doGetWithStartStep() throws Exception {
        // Given
        when(request.getParameter("step")).thenReturn("start");
        
        // When
        doGetMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(request).setAttribute(eq("step"), any(QuestStep.class));
        verify(request).getRequestDispatcher(eq("/game.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET запрос без параметра step должен использовать start по умолчанию")
    void doGetWithoutStepParameter() throws Exception {
        // Given
        when(request.getParameter("step")).thenReturn(null);
        
        // When
        doGetMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(request).setAttribute(eq("step"), any(QuestStep.class));
        verify(request).getRequestDispatcher(eq("/game.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET запрос с несуществующим шагом должен вернуть 404")
    void doGetWithNonExistentStep() throws Exception {
        // Given
        when(request.getParameter("step")).thenReturn("non-existent");
        when(questService.getStep(eq("non-existent"))).thenReturn(null);
        
        // When
        doGetMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(response).sendError(eq(404));
    }

    @Test
    @DisplayName("POST запрос с ответом 1 должен перенаправить к следующему шагу")
    void doPostWithAnswer1() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn("1");
        when(request.getParameter("currentStep")).thenReturn("start");
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("POST запрос с ответом 2 должен перенаправить к следующему шагу")
    void doPostWithAnswer2() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn("2");
        when(request.getParameter("currentStep")).thenReturn("start");
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("POST запрос с финальным шагом должен сохранить результат и перенаправить на result.jsp")
    void doPostWithFinalStep() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn("1");
        when(request.getParameter("currentStep")).thenReturn("captain"); // captain -> win
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(session).setAttribute(eq("result"), eq("Тебя вернули домой. Победа!"));
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("POST запрос без параметра answer должен обработать ошибку")
    void doPostWithoutAnswer() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn(null);
        when(request.getParameter("currentStep")).thenReturn("start");
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(request).setAttribute(eq("message"), eq("Выберите вариант"));
        verify(request).setAttribute(eq("step"), any(QuestStep.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST запрос без параметра currentStep должен обработать ошибку")
    void doPostWithoutCurrentStep() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn("1");
        when(request.getParameter("currentStep")).thenReturn(null);
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(response).sendError(eq(400));
    }

    @Test
    @DisplayName("POST запрос с некорректным ответом должен обработать ошибку")
    void doPostWithInvalidAnswer() throws Exception {
        // Given
        when(request.getParameter("answer")).thenReturn("3");
        when(request.getParameter("currentStep")).thenReturn("start");
        
        // When
        doPostMethod.invoke(gameServlet, request, response);
        
        // Then
        verify(request).setAttribute(eq("message"), eq("Некорректный выбор"));
        verify(request).setAttribute(eq("step"), any(QuestStep.class));
        verify(requestDispatcher).forward(request, response);
    }
}