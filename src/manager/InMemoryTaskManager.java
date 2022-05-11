package manager;

import util.Managers;
import tasks.Status;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    Integer id = 0;

    HistoryManager historyManager = Managers.getDefaultHistory();

    private TreeSet<Task> timePriorityTaskList = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return 1;
        } else if (o1.getStartTime() == null) {
            return 1;
        } else if (o2.getStartTime() == null) {
            return -1;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return 0;
        }
    });

    //Получить список задач, ранжированный по времени
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return timePriorityTaskList;
    }

    //Проверка пересечения времени задач
    @Override
    public <T extends Task> boolean timeValidation(T task) {
        boolean flag = false;
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        LocalDateTime tStart;
        LocalDateTime tEnd;
        if (taskStart == null || taskEnd == null) {
            return flag;
        }
        for (Task t : timePriorityTaskList) {
            tStart = t.getStartTime();
            tEnd = t.getEndTime();
            if (tStart != null & tEnd != null) {
                if (!((taskStart.isAfter(tEnd) || taskStart.equals(tEnd)) ||
                        (taskEnd.isBefore(tStart) || taskEnd.equals(tStart)))) {
                    flag = true;
                }
            }
        }
        if (flag) {
            --id;
        }
        return flag;
    }

    //Получить историю запросов
    @Override
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
            if (timeValidation(task)) {
                return;
            }
            if (!taskMap.containsKey(task.getId())) {
                timePriorityTaskList.add(task);
                taskMap.put(task.getId(), task);
            }
        }
    }

    //Обновить задачу
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (timeValidation(task)) {
                return;
            }
            if (taskMap.containsKey(task.getId())) {
                timePriorityTaskList.remove(task);
                timePriorityTaskList.add(task);
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
                    timePriorityTaskList.remove(taskMap.get(id));
                    taskMap.remove(id);
                    historyManager.remove(id);
                }
            }
        }
    }

    //Удалить все задачи
    @Override
    public void deleteAllTask() {
        Set<Integer> ids = taskMap.keySet();
        for (Integer id : ids) {
            timePriorityTaskList.remove(taskMap.get(id));
            historyManager.remove(id);
        }
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
                epicMap.put(epic.getId(), setEpicTime(epic));
            }
        }
    }

    //Удалить эпик
    @Override
    public void deleteEpic(Integer idEpic) {
        if (idEpic != null) {
            if (!epicMap.containsKey(idEpic)) {
                return;
            }
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
        for (Epic epic : epicMap.values()) {
            historyManager.remove(epic.getId());
        }
        epicMap.clear();
        subTaskMap.clear();
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

    //Рассчитать время эпика
    public Epic setEpicTime(Epic epic) {
        ArrayList<SubTask> subTasks = getSubTaskListOfEpic(epic.getId());
        if (subTasks == null) {
            return epic;
        }
        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = null;
        LocalDateTime subTaskStart;
        LocalDateTime subTaskEnd;
        Duration subTaskDuration;
        for (SubTask subTask : subTasks) {
            subTaskStart = subTask.getStartTime();
            subTaskEnd = subTask.getEndTime();
            subTaskDuration = subTask.getDuration();
            if (subTaskStart != null) {
                if (start == null) {
                    start = subTaskStart;
                } else {
                    if (subTaskStart.isBefore(start)) {
                        start = subTaskStart;
                    }
                }
            }
            if (subTaskEnd != null) {
                if (end == null) {
                    end = subTaskEnd;
                } else {
                    if (subTaskEnd.isAfter(end)) {
                        end = subTaskEnd;
                    }
                }
            }
            if (subTaskDuration != null) {
                if (duration != null) {
                    duration = duration.plus(subTaskDuration);
                } else {
                    duration = subTaskDuration;
                }
            }
        }
        if (start == null || end == null || duration == null) {
            return epic;
        } else {
            return new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(), start, end, duration);
        }
    }

    //Создать подзадачу
    @Override
    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            if (timeValidation(subTask)) {
                return;
            }
            if (!subTaskMap.containsKey(subTask.getId())) {
                if (epicMap.containsKey(subTask.getIdEpic())) {
                    timePriorityTaskList.add(subTask);
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
            if (timeValidation(subTask)) {
                return;
            }
            if (subTaskMap.containsKey(subTask.getId())) {
                if (epicMap.containsKey(subTask.getIdEpic())) {
                    subTaskMap.put(subTask.getId(), subTask);
                    epicMap.get(subTask.getIdEpic()).setSubTaskId(subTask);
                    determineStatus(subTask);
                    timePriorityTaskList.remove(subTask);
                    timePriorityTaskList.add(subTask);
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
                    historyManager.remove(id);
                    epicMap.get(subTaskMap.get(id).getIdEpic()).removeSubTaskId(subTaskMap.get(id));
                    determineStatus(subTaskMap.get(id));
                    timePriorityTaskList.remove(subTaskMap.get(id));
                    subTaskMap.remove(id);
                }
            }
        }
    }

    //Удалить все подзадачи
    @Override
    public void deleteAllSubTask() {
        Set<Integer> ids = subTaskMap.keySet();
        for (Integer id : ids) {
            timePriorityTaskList.remove(subTaskMap.get(id));
            historyManager.remove(id);
        }
        subTaskMap.clear();
        for (Integer idEpic : epicMap.keySet()) {
            epicMap.get(idEpic).removeAllSubTaskId();
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
    public List<Task> history() {
        return historyManager.getHistory();
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
