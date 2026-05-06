Ссылка на задание: https://javarush.com/quests/lectures/jru.module3.lecture04

Технологический стек
1) Сервлеты: Servlet API 4.0
2) JSP + JSTL - для динамических веб-страниц
3) Работа с сессиями - хранить состояние пользователя
4) Maven - управление зависимостями
5) Архитектура MVC (Model-View-Controller) в упрощенном виде
6) Тестирование: JUnit + Mockito
  
План
1) Настроена структура проекта
2) Подключены необходимые зависимости
3) Реализована игровая механика
4) Создан пользовательский интерфейс
5) Реализованы тесты для QuestService
   
Цель
   У нас должно быть полностью рабочее приложение, которое:
1) Принимает решение пользователя
2) Хранит состояние игры
3) Показывает результат
4) Соответствует поддерживаемой архитектуре
   
Структура каталогов
   src/main/java/quest - Java классы
   src/main/resources - ресурсы
   src/main/webapp/WEB-INF - веб-конфигурация
   src/main/webapp - JSP-страницы

   src/test/java/quest - тесты
   
Эндпоинты
   http://localhost:8081/HelloQuest/hello
   http://localhost:8081/HelloQuest/game
   http://localhost:8081/HelloQuest/start
   
Запуск приложения
   mvn compile tomcat7:run