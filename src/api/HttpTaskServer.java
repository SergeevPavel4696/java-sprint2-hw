package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final String allTasks = "/tasks";

    public HttpTaskServer(String url) throws IOException, InterruptedException {
        taskManager = Managers.getHttpTaskManager(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext(allTasks, this::handler);
    }

    private void handler(HttpExchange h) throws IOException {
        final String tasks = "/tasks/task";
        final String subtasks = "/tasks/subtask";
        final String epics = "/tasks/epic";
        final String history = "/tasks/history";
        final String subtasksOfEpic = "/tasks/subtask/epic";

        String path = h.getRequestURI().getPath();
        String query = h.getRequestURI().getQuery();
        if (path.contains(allTasks)) {
            System.out.println("Началась обработка запроса от клиента.");
            String method = h.getRequestMethod();
            switch (method) {
                case "GET":
                    if (path.endsWith(allTasks)) {
                        System.out.println("Началась обработка запроса получения списка задач по приоритету.");
                        h.sendResponseHeaders(200, 0);
                        String response = "Полный список задач по приоритету:\n"
                                + gson.toJson(taskManager.getPrioritizedTasks());
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (path.endsWith(tasks)) {
                        System.out.println("Началась обработка запроса получения списка задач.");
                        h.sendResponseHeaders(200, 0);
                        String response = "Список задач по id:\n" + gson.toJson(taskManager.getTaskList());
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (path.endsWith(epics)) {
                        System.out.println("Началась обработка запроса получения списка эпиков.");
                        h.sendResponseHeaders(200, 0);
                        String response = "Список эпиков по id:\n" + gson.toJson(taskManager.getEpicList());
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (path.endsWith(subtasks)) {
                        System.out.println("Началась обработка запроса получения списка подзадач.");
                        h.sendResponseHeaders(200, 0);
                        String response = "Список подзадач по id:\n" + gson.toJson(taskManager.getSubTaskList());
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (path.endsWith(history)) {
                        System.out.println("Началась обработка запроса получения истории запросов.");
                        h.sendResponseHeaders(200, 0);
                        String response = "История запросов:\n" + gson.toJson(taskManager.getHistory());
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        System.out.println("Началась обработка запроса поиска.");
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (path.endsWith(subtasksOfEpic + "/?id=" + id)) {
                            System.out.println("Началась обработка запроса поиска подзадач эпика по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            String response = "Подзадачи эпика по id = " + id + ":\n"
                                    + gson.toJson(taskManager.getSubTaskListOfEpic(id));
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(tasks + "/?id=" + id) && taskManager.getTaskMap().containsKey(id)) {
                            System.out.println("Началась обработка запроса поиска задачи по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            String response = "Задача по id = " + id + gson.toJson(taskManager.getTask(id));
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(epics + "/?id=" + id) && taskManager.getEpicMap().containsKey(id)) {
                            System.out.println("Началась обработка запроса поиска эпика по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            String response = "Эпик по id = " + id + gson.toJson(taskManager.getEpic(id));
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(subtasks + "/?id=" + id) && taskManager.getSubTaskMap().containsKey(id)) {
                            System.out.println("Началась обработка запроса поиска подзадачи по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            String response = "Подзадача по id = " + id + gson.toJson(taskManager.getSubTask(id));
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            System.out.println("Задача по id = " + id + " не существует.\n" +
                                    "Либо невозможно обработать запрос.");
                        }
                    }
                case "POST":
                    InputStream is = h.getRequestBody();
                    if (is != null) {
                        String responseBody = new String(is.readAllBytes(), CHARSET);
                        if (query == null) {
                            if (path.endsWith(tasks)) {
                                System.out.println("Началась обработка запроса создания задачи");
                                taskManager.createTask(gson.fromJson(responseBody, Task.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Задача добавлена";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else if (path.endsWith(epics)) {
                                System.out.println("Началась обработка запроса создания эпика");
                                taskManager.createEpic(gson.fromJson(responseBody, Epic.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Эпик добавлен";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else if (path.endsWith(subtasks)) {
                                System.out.println("Началась обработка запроса создания подзадачи");
                                taskManager.createSubTask(gson.fromJson(responseBody, SubTask.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Подзадача добавлена";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Невозможно обработать запрос.");
                            }
                        } else {
                            System.out.println("Началась обработка запроса обновления задачи");
                            int id = Integer.parseInt(query.split("=")[1]);
                            if (path.endsWith(tasks + "/?id=" + id)) {
                                System.out.println("Началась обработка запроса обновления задачи");
                                taskManager.updateTask(gson.fromJson(responseBody, Task.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Задача обновлена";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else if (path.endsWith(subtasks + "/?id=" + id)) {
                                System.out.println("Началась обработка запроса обновления эпика");
                                taskManager.updateEpic(gson.fromJson(responseBody, Epic.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Эпик обновлён";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else if (path.endsWith(epics + "/?id=" + id)) {
                                System.out.println("Началась обработка запроса обновления подзадачи");
                                taskManager.updateSubTask(gson.fromJson(responseBody, SubTask.class));
                                h.sendResponseHeaders(201, 0);
                                String response = "Подзадача обновлена";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                h.sendResponseHeaders(400, 0);
                                String response = "Некорректный запрос";
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            }
                        }
                    } else {
                        String response = "Тело запроса пусто";
                        h.sendResponseHeaders(400, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                case "DELETE":
                    if (query == null) {
                        if (path.endsWith(tasks)) {
                            System.out.println("Началась обработка запроса удалению списка задач.");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteAllTask();
                            String response = "Список задач удалён";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(epics)) {
                            System.out.println("Началась обработка запроса удалению списка эпиков.");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteAllEpic();
                            String response = "Список эпиков удалён";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(subtasks)) {
                            System.out.println("Началась обработка запроса удалению списка подзадач.");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteAllSubTask();
                            String response = "Список подзадач удалён";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            System.out.println("Невозможно обработать запрос.");
                        }
                    } else {
                        System.out.println("Началась обработка запроса удаления.");
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (path.endsWith(tasks + "/?id=" + id)) {
                            System.out.println("Началась обработка запроса удаления задачи по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteTask(id);
                            String response = "Задача по id = " + id + " удалена.";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(epics + "/?id=" + id)) {
                            System.out.println("Началась обработка запроса удаления эпика по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteEpic(id);
                            String response = "Задача по id = " + id + " удалена.";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else if (path.endsWith(subtasks + "/?id=" + id)) {
                            System.out.println("Началась обработка запроса удаления подзадачи по id = " + id + ".");
                            h.sendResponseHeaders(200, 0);
                            taskManager.deleteSubTask(id);
                            String response = "Задача по id = " + id + " удалена.";
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            System.out.println("Невозможно обработать запрос.");
                        }
                    }
                default:
                    String response = "Некорректный метод";
                    h.sendResponseHeaders(400, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write(response.getBytes());
                    }
            }
        } else {
            h.sendResponseHeaders(400, 0);
            String response = "Некорректный путь";
            try (OutputStream os = h.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public void start() {
        System.out.println("Сервер запущен на " + PORT + " порту!");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
