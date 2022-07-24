package manager;

import api.DurationAdapter;
import api.HttpTaskServer;
import api.KVServer;
import api.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static tasks.Status.NEW;

class HttpTaskManagerTest extends FileBackedTasksManagerTest {
    private Gson gson;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;
    private SubTask subTask4;
    private SubTask subTask5;
    private KVServer kvServer;

    @BeforeEach
    @Override
    public void init() {
        manager = Managers.getHttpTaskManager("http://localhost:8080");
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        httpClient = HttpClient.newHttpClient();
        task1 = new Task(manager.getId(), "Задача 1", "Описание задачи 1", NEW,
                LocalDateTime.of(2022, 7, 18, 11, 35, 0), Duration.ofMinutes(15));
        task2 = new Task(manager.getId(), "Задача 2", "Описание задачи 2", NEW,
                LocalDateTime.of(2022, 7, 24, 12, 45, 0), Duration.ofMinutes(15));
        epic1 = new Epic(manager.getId(), "Эпик 1", "Описание эпика 1", NEW);
        subTask1 = new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW,
                LocalDateTime.of(2022, 6, 28, 12, 5, 0), Duration.ofMinutes(120), 2);
        task3 = new Task(manager.getId(), "Задача 2", "Описание задачи 2", NEW,
                LocalDateTime.of(2022, 6, 18, 16, 55, 0), Duration.ofMinutes(15));
        subTask2 = new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", NEW,
                LocalDateTime.of(2022, 6, 28, 12, 5, 0), Duration.ofMinutes(120), 2);
        epic2 = new Epic(manager.getId(), "Эпик 2", "Описание эпика 2", NEW);
        subTask3 = new SubTask(manager.getId(), "Подзадача 3", "Описание подзадачи 3", NEW,
                LocalDateTime.of(2022, 7, 12, 12, 5, 0), Duration.ofMinutes(120), 6);
        subTask4 = new SubTask(manager.getId(), "Подзадача 4", "Описание подзадачи 4", NEW,
                LocalDateTime.of(2022, 8, 2, 12, 5, 0), Duration.ofMinutes(120), 2);
        subTask5 = new SubTask(manager.getId(), "Подзадача 5", "Описание подзадачи 5", NEW,
                LocalDateTime.of(2022, 8, 12, 12, 5, 0), Duration.ofMinutes(120), 6);

        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer("http://localhost:8080");
            httpTaskServer.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при запуске сервера");
        }
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }

}