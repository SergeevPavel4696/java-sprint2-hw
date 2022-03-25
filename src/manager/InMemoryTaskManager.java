package manager;

import util.Managers;
import tasks.Status;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private Integer id = 0;

    private HistoryManager historyManager = Managers.getDefaultHistory();

    //ПОлучить историю запросов
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    //Получить следующий id
    @Override
    public Integer getId() {
        return ++id;
    }

    //Создать задачу
    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (!taskMap.containsKey(task.getId())) {
                taskMap.put(task.getId(), task);
            }
        }
    }

    //Обновить задачу
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (taskMap.containsKey(task.getId())) {
                taskMap.put(task.getId(), task);
            }
        }
    }

    //Удалить задачу
    @Override
    public void deleteTask(Integer id) {
        if (id != null) {
            if (taskMap.containsKey(id)) {
                if (taskMap.get(id) != null) {
                    taskMap.remove(id);
                    historyManager.remove(id);
                }
            }
        }
    }

    //Удалить все задачи
    @Override
    public void deleteAllTask() {
        taskMap.clear();
    }

    //Получить задачу
    @Override
    public Task getTask(Integer id) {
        if (id != null) {
            if (taskMap.containsKey(id)) {
                historyManager.linkLast(taskMap.get(id));
                return taskMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //Получить список задач
    @Override
    public ArrayList<Task> getTaskList() {
        if (!taskMap.isEmpty()) {
            return new ArrayList<>(taskMap.values());
        } else {
            return null;
        }
    }

    //Создать эпик
    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            if (!epicMap.containsKey(epic.getId())) {
                epicMap.put(epic.getId(), epic);
            }
        }
    }

    //Обновить эпик
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            if (epicMap.containsKey(epic.getId())) {
                epicMap.put(epic.getId(), epic);
            }
        }
    }

    //Удалить эпик
    @Override
    public void deleteEpic(Integer idEpic) {
        if (idEpic != null) {
            for (Integer id : epicMap.get(idEpic).getSubTaskIdMap().keySet()) {
                subTaskMap.remove(id);
                historyManager.remove(id);
            }
            epicMap.remove(idEpic);
            historyManager.remove(idEpic);
        }
    }

    //Удалить все эпики
    @Override
    public void deleteAllEpic() {
        epicMap.clear();
    }

    //Получить эпик
    @Override
    public Epic getEpic(Integer id) {
        if (id != null) {
            if (epicMap.containsKey(id)) {
                historyManager.linkLast(epicMap.get(id));
                return epicMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //Получить список эпиков
    @Override
    public ArrayList<Epic> getEpicList() {
        if (!epicMap.isEmpty()) {
            return new ArrayList<>(epicMap.values());
        } else {
            return null;
        }
    }

    //проверка и обновление статуса
    @Override
    public void determineStatus(SubTask subTask) {
        Epic temp = epicMap.get(subTask.getIdEpic());
        if (temp.getSubTaskIdMap().containsValue(Status.IN_PROGRESS)
                || (temp.getSubTaskIdMap().containsValue(Status.NEW)
                && temp.getSubTaskIdMap().containsValue(Status.DONE))) {
            updateEpic(new Epic(temp.getId(), temp.getTitle(), temp.getDescription(), Status.IN_PROGRESS));
            epicMap.get(subTask.getIdEpic()).setSubTaskIdMap(temp.getSubTaskIdMap());
        } else if (!temp.getSubTaskIdMap().containsValue(Status.NEW)) {
            updateEpic(new Epic(temp.getId(), temp.getTitle(), temp.getDescription(), Status.DONE));
            epicMap.get(subTask.getIdEpic()).setSubTaskIdMap(temp.getSubTaskIdMap());
        }
    }

    //Создать подзадачу
    @Override
    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            if (!subTaskMap.containsKey(subTask.getId())) {
                if (epicMap.containsKey(subTask.getIdEpic())) {
                    subTaskMap.put(subTask.getId(), subTask);
                    epicMap.get(subTask.getIdEpic()).setSubTaskId(subTask);
                    determineStatus(subTask);
                }
            }
        }
    }

    //Обновить подзадачу
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            if (subTaskMap.containsKey(subTask.getId())) {
                if (epicMap.containsKey(subTask.getIdEpic())) {
                    subTaskMap.put(subTask.getId(), subTask);
                    epicMap.get(subTask.getIdEpic()).setSubTaskId(subTask);
                    determineStatus(subTask);
                }
            }
        }
    }

    //Удалить подзадачу
    @Override
    public void deleteSubTask(Integer id) {
        if (id != null) {
            if (subTaskMap.containsKey(id)) {
                if (subTaskMap.get(id) != null) {
                    subTaskMap.remove(id);
                    historyManager.remove(id);
                    epicMap.get(subTaskMap.get(id).getIdEpic()).removeSubTaskId(subTaskMap.get(id));
                }
            }
        }
    }

    //Удалить все подзадачи
    @Override
    public void deleteAllSubTask() {
        subTaskMap.clear();
        for (Integer epic : epicMap.keySet()) {
            epicMap.get(epic).removeAllSubTaskId();
        }
    }

    //Получить подзадачу
    @Override
    public SubTask getSubTask(Integer id) {
        if (id != null) {
            if (subTaskMap.containsKey(id)) {
                historyManager.linkLast(subTaskMap.get(id));
                return subTaskMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //Получить список подзадач
    @Override
    public ArrayList<SubTask> getSubTaskList() {
        if (!subTaskMap.isEmpty()) {
            return new ArrayList<>(subTaskMap.values());
        } else {
            return null;
        }
    }

    //Получить список подзадач эпика
    @Override
    public ArrayList<SubTask> getSubTaskListOfEpic(Integer idEpic) {
        if (idEpic != null) {
            ArrayList<SubTask> subTaskListOfEpic = new ArrayList<>();
            if (epicMap.containsKey(idEpic)) {
                if (!epicMap.get(idEpic).getSubTaskIdMap().isEmpty()) {
                    for (Integer id : epicMap.get(idEpic).getSubTaskIdMap().keySet()) {
                        subTaskListOfEpic.add(subTaskMap.get(id));
                    }
                }
            }
            if (!subTaskListOfEpic.isEmpty()) {
                return new ArrayList<>(subTaskListOfEpic);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    //Получить карту задач
    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    //Получить карту эпиков
    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    //Получить карту подзадач
    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }
}
