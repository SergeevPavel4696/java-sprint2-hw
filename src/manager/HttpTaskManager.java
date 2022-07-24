package manager;

import api.DurationAdapter;
import api.KVTaskClient;
import api.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        this.kvTaskClient = new KVTaskClient(url);
        load();
    }

    public void load() {
        try {
            String tasks = kvTaskClient.load("tasks/task");
            if (tasks != null) {
                ArrayList<Task> taskList = gson.fromJson(tasks, new TypeToken<ArrayList<Task>>() {
                }.getType());
                for (Task task : taskList) {
                    createTask(task);
                }
            }
            String epics = kvTaskClient.load("tasks/epic");
            if (epics != null) {
                ArrayList<Epic> epicList = gson.fromJson(epics, new TypeToken<ArrayList<Epic>>() {
                }.getType());
                for (Epic epic : epicList) {
                    createEpic(epic);
                }
            }
            String subtasks = kvTaskClient.load("tasks/subtask");
            if (subtasks != null) {
                ArrayList<SubTask> subtaskList = gson.fromJson(subtasks, new TypeToken<ArrayList<SubTask>>() {
                }.getType());
                for (SubTask subTask : subtaskList) {
                    createSubTask(subTask);
                }
            }
            String history = kvTaskClient.load("tasks/history");
            if (history != null) {
                ArrayList<Task> historyList = gson.fromJson(history, new TypeToken<List<Task>>() {
                }.getType());
                for (Task task : historyList) {
                    getTask(task.getId());
                    getEpic(task.getId());
                    getSubTask(task.getId());
                }
                for (Integer id : taskMap.keySet()) {
                    if (id > this.id) {
                        this.id = id;
                    }
                }
                for (Integer id : epicMap.keySet()) {
                    if (id > this.id) {
                        this.id = id;
                    }
                }
                for (Integer id : subTaskMap.keySet()) {
                    if (id > this.id) {
                        this.id = id;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при восстановлении менеджера задач.");
        }
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks/task", gson.toJson(taskMap));
        kvTaskClient.put("tasks/epic", gson.toJson(epicMap));
        kvTaskClient.put("tasks/subtask", gson.toJson(subTaskMap));
        kvTaskClient.put("tasks/history", gson.toJson(historyManager.getHistory()));
    }
}
