package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasks.Status.DONE;
import static tasks.Status.IN_PROGRESS;
import static tasks.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager manager;

    @Test
    void getPrioritizedTasksTest() {
        manager.createTask(new Task(8, "Задача 1", "Описание задачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 15, 0), Duration.ofMinutes(15)));
        manager.createTask(new Task(10, "Задача 2", "Описание задачи 2", NEW));
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2"));
        manager.createSubTask(new SubTask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 45, 0),
                Duration.ofMinutes(15), 1));
        manager.createSubTask(new SubTask(4, "Подзадача 2", "Описание подзадачи 2", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 45, 0),
                Duration.ofMinutes(15), 1));
        manager.createSubTask(new SubTask(9, "Подзадача 2", "Описание подзадачи 2", NEW, 1));
        manager.createSubTask(new SubTask(4, "Подзадача 2", "Описание подзадачи 2", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 0, 0),
                Duration.ofMinutes(15), 1));
        manager.createSubTask(new SubTask(7, "Подзадача 5", "Описание подзадачи 5", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 30, 0),
                Duration.ofMinutes(15), 2));

        List<Task> taskList = new LinkedList<>();
        taskList.add(new SubTask(4, "Подзадача 2", "Описание подзадачи 2", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 0, 0),
                Duration.ofMinutes(15), 1));
        taskList.add(new Task(8, "Задача 1", "Описание задачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 15, 0), Duration.ofMinutes(15)));
        taskList.add(new SubTask(7, "Подзадача 5", "Описание подзадачи 5", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 30, 0),
                Duration.ofMinutes(15), 2));
        taskList.add(new SubTask(3, "Подзадача 1", "Описание подзадачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 45, 0),
                Duration.ofMinutes(15), 1));
        taskList.add(new SubTask(9, "Подзадача 2", "Описание подзадачи 2", NEW, 1));
        taskList.add(new Task(10, "Задача 2", "Описание задачи 2", NEW));
        int i = 0;
        for (Task task : manager.getPrioritizedTasks()) {
            assertEquals(task, taskList.get(i));
            i++;
        }
    }

    @Test
    void timeValidationTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 12, 0, 0),
                Duration.ofMinutes(60), 1));
        manager.createTask(new Task(3, "Задача 1", "Описание задачи 1", NEW,
                LocalDateTime.of(2022, 5, 10, 14, 0, 0), Duration.ofMinutes(60)));
        manager.createTask(new Task(3, "Задача 2", "Описание задачи 2", NEW));
        SubTask subTask1 = new SubTask(4, "Подзадача 2", "Описание подзадачи 2", NEW,
                LocalDateTime.of(2022, 5, 10, 11, 45, 0),
                Duration.ofMinutes(30), 1);
        SubTask subTask2 = new SubTask(5, "Подзадача 3", "Описание подзадачи 3", NEW,
                LocalDateTime.of(2022, 5, 10, 11, 30, 0),
                Duration.ofMinutes(30), 1);
        SubTask subTask3 = new SubTask(6, "Подзадача 4", "Описание подзадачи 4", NEW,
                LocalDateTime.of(2022, 5, 10, 13, 15, 0),
                Duration.ofMinutes(30), 1);
        SubTask subTask4 = new SubTask(7, "Подзадача 5", "Описание подзадачи 5", NEW,
                LocalDateTime.of(2022, 5, 10, 13, 45, 0),
                Duration.ofMinutes(30), 1);
        SubTask subTask5 = new SubTask(8, "Подзадача 6", "Описание подзадачи 6", NEW,
                LocalDateTime.of(2022, 5, 10, 15, 0, 0),
                Duration.ofMinutes(30), 1);
        assertTrue(manager.timeValidation(subTask1));
        assertFalse(manager.timeValidation(subTask2));
        assertFalse(manager.timeValidation(subTask3));
        assertTrue(manager.timeValidation(subTask4));
        assertFalse(manager.timeValidation(subTask5));
    }

    @Test
    void getIdTest() {
        assertEquals(manager.getId(), 1);
        assertEquals(manager.getId(), 2);
    }

    @Test
    void createTaskTest() {
        manager.createTask(null);
        assertNull(manager.getTaskList());
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", NEW));
        assertEquals(new Task(1, "Задача 1", "Описание задачи 1", NEW), manager.getTask(1));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 2", IN_PROGRESS));
        assertEquals(new Task(2, "Задача 2", "Описание задачи 2", IN_PROGRESS), manager.getTask(2));
        manager.createTask(new Task(2, "Задача 3", "Описание задачи 3", DONE));
        assertEquals(new Task(2, "Задача 2", "Описание задачи 2", IN_PROGRESS), manager.getTask(2));
    }

    @Test
    void updateTaskTest() {
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", NEW));
        manager.updateTask(new Task(1, "Задача 2", "Описание задачи 2", DONE));
        assertEquals(new Task(1, "Задача 2", "Описание задачи 2", DONE), manager.getTask(1));
    }

    @Test
    void deleteTaskTest() {
        manager.deleteTask(1);
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS));
        manager.deleteTask(1);
        assertNull(manager.getTaskList());
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", NEW));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        manager.deleteTask(null);
        assertEquals(2, manager.getTaskList().size());
        manager.getTask(1);
        manager.getTask(2);
        assertEquals(2, manager.getHistory().size());
        manager.deleteTask(1);
        assertEquals(1, manager.getTaskList().size());
        manager.deleteTask(2);
        assertNull(manager.getTaskList());
        assertNull(manager.getHistory());
    }

    @Test
    void deleteAllTaskTest() {
        manager.deleteAllTask();
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 1", NEW));
        manager.createTask(new Task(3, "Задача 3", "Описание задачи 2", DONE));
        manager.getTask(1);
        manager.getTask(2);
        assertEquals(2, manager.getHistory().size());
        manager.deleteAllTask();
        assertNull(manager.getTaskList());
        assertNull(manager.getHistory());
    }

    @Test
    void getTaskTest() {
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        assertEquals(new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS), manager.getTask(1));
        assertEquals(new Task(2, "Задача 2", "Описание задачи 2", DONE), manager.getTask(2));
        assertNull(manager.getTask(null));
        assertNull(manager.getTask(3));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void getTaskListTest() {
        assertNull(manager.getTaskList());
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        Task[] taskArray1 = {new Task(1, "Задача 1", "Описание задачи 1", IN_PROGRESS),
                new Task(2, "Задача 2", "Описание задачи 2", DONE)};
        Task[] taskArray2 = manager.getTaskList().toArray(new Task[0]);
        assertArrayEquals(taskArray1, taskArray2);
    }

    @Test
    void createEpicTest() {
        manager.createEpic(null);
        assertNull(manager.getEpicList());
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        assertEquals(new Epic(1, "Эпик 1", "Описание эпика 1"), manager.getEpic(1));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2", DONE));
        assertEquals(new Epic(2, "Эпик 2", "Описание эпика 2", DONE), manager.getEpic(2));
        manager.createEpic(new Epic(2, "Эпик 3", "Описание эпика 3", IN_PROGRESS));
        assertEquals(new Epic(2, "Эпик 2", "Описание эпика 2", DONE), manager.getEpic(2));
    }

    @Test
    void updateEpicTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.updateEpic(new Epic(1, "Эпик 2", "Описание эпика 2", DONE));
        assertEquals(new Epic(1, "Эпик 2", "Описание эпика 2", DONE), manager.getEpic(1));
    }

    @Test
    void deleteEpicTest() {
        manager.deleteEpic(1);
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.deleteEpic(1);
        assertNull(manager.getEpicList());
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createSubTask(new SubTask(2, "Подадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(3, "Подадача 2", "Описание подзадачи 2", NEW, 1));
        manager.createSubTask(new SubTask(4, "Подадача 3", "Описание подзадачи 3", NEW, 1));
        manager.createEpic(new Epic(5, "Эпик 12", "Описание эпика 2", DONE));
        manager.deleteEpic(null);
        assertEquals(2, manager.getEpicList().size());
        assertEquals(3, manager.getSubTaskList().size());
        manager.getEpic(1);
        manager.getEpic(5);
        assertEquals(2, manager.getHistory().size());
        manager.deleteEpic(1);
        assertEquals(1, manager.getHistory().size());
        assertNull(manager.getSubTaskList());
        assertEquals(1, manager.getEpicList().size());
        manager.deleteEpic(5);
        assertNull(manager.getEpicList());
        assertNull(manager.getHistory());
    }

    @Test
    void deleteAllEpicTest() {
        manager.deleteAllEpic();
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2", DONE));
        manager.createEpic(new Epic(3, "Эпик 3", "Описание эпика 3", IN_PROGRESS));
        manager.getEpic(1);
        manager.getEpic(2);
        assertEquals(2, manager.getHistory().size());
        manager.createSubTask(new SubTask(4, "Подзадача 1", "Описание подзадачи 4", NEW, 1));
        manager.createSubTask(new SubTask(5, "Подзадача 1", "Описание подзадачи 5", IN_PROGRESS, 2));
        manager.createSubTask(new SubTask(6, "Подзадача 1", "Описание подзадачи 6", NEW, 1));
        manager.createSubTask(new SubTask(7, "Подзадача 1", "Описание подзадачи 7", DONE, 3));
        manager.createSubTask(new SubTask(8, "Подзадача 1", "Описание подзадачи 8", IN_PROGRESS, 2));
        manager.deleteAllEpic();
        assertNull(manager.getEpicList());
        assertNull(manager.getSubTaskList());
        assertNull(manager.getHistory());
    }

    @Test
    void getEpicTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание задачи 1", IN_PROGRESS));
        assertEquals(new Epic(1, "Эпик 1", "Описание задачи 1", IN_PROGRESS), manager.getEpic(1));
        assertNull(manager.getEpic(null));
        assertNull(manager.getEpic(3));
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void getEpicListTest() {
        assertNull(manager.getEpicList());
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2", DONE));
        Epic[] epicArray1 = {new Epic(1, "Эпик 1", "Описание эпика 1"),
                new Epic(2, "Эпик 2", "Описание эпика 2", DONE)};
        Epic[] epicArray2 = manager.getEpicList().toArray(new Epic[0]);
        assertArrayEquals(epicArray1, epicArray2);
    }

    @Test
    void determineStatusTest() {
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        assertEquals(NEW, manager.getEpic(1).getStatus());
        manager.createSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        assertEquals(NEW, manager.getEpic(1).getStatus());
        manager.createSubTask(new SubTask(3, "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, 1));
        assertEquals(IN_PROGRESS, manager.getEpic(1).getStatus());
        manager.createSubTask(new SubTask(4, "Подзадача 3", "Описание подзадачи 3", DONE, 1));
        assertEquals(IN_PROGRESS, manager.getEpic(1).getStatus());
        manager.updateSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        assertEquals(IN_PROGRESS, manager.getEpic(1).getStatus());
        manager.updateSubTask(new SubTask(3, "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        assertEquals(DONE, manager.getEpic(1).getStatus());
        manager.createSubTask(new SubTask(5, "Подзадача 4", "Описание подзадачи 4", NEW, 1));
        assertEquals(IN_PROGRESS, manager.getEpic(1).getStatus());
    }

    @Test
    void createSubTaskTest() {
        manager.createSubTask(null);
        assertNull(manager.getSubTaskList());
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        manager.createSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        assertEquals(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", DONE, 1),
                manager.getSubTask(2));
        assertEquals(DONE, manager.getEpic(1).getSubTaskIdMap().get(2));
    }

    @Test
    void updateSubTaskTest() {
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        manager.createSubTask(new SubTask(2, "Подадача 1", "Описание подзадачи 1", NEW, 1));
        assertEquals(new SubTask(2, "Подадача 1", "Описание подзадачи 1", NEW, 1),
                manager.getSubTask(2));
        manager.updateSubTask(new SubTask(2, "Подадача 1", "Описание подзадачи 1", DONE, 1));
        assertEquals(new SubTask(2, "Подадача 1", "Описание подзадачи 1", DONE, 1),
                manager.getSubTask(2));
    }

    @Test
    void deleteSubTaskTest() {
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        manager.deleteSubTask(2);
        manager.createSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1));
        manager.deleteSubTask(2);
        assertNull(manager.getSubTaskList());
        manager.createSubTask(new SubTask(2, "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(3, "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        manager.deleteSubTask(null);
        assertEquals(2, manager.getSubTaskList().size());
        manager.getSubTask(2);
        manager.getSubTask(3);
        assertEquals(2, manager.getHistory().size());
        manager.deleteSubTask(2);
        assertEquals(1, manager.getSubTaskList().size());
        manager.deleteSubTask(3);
        assertNull(manager.getTaskList());
        assertNull(manager.getHistory());
        assertEquals(0, manager.getEpic(1).getSubTaskIdMap().size());
    }

    @Test
    void deleteAllSubTaskTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2"));
        manager.createSubTask(new SubTask(3, "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(4, "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, 2));
        manager.createSubTask(new SubTask(5, "Подзадача 3", "Описание подзадачи 3", DONE, 2));
        assertEquals(3, manager.getSubTaskList().size());
        manager.deleteAllSubTask();
        assertNull(manager.getSubTaskList());
    }

    @Test
    void getSubTaskTest() {
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        manager.createSubTask(new SubTask(2, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(3, "Задача 2", "Описание задачи 2", DONE, 1));
        assertEquals(new SubTask(2, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1),
                manager.getSubTask(2));
        assertEquals(new SubTask(3, "Задача 2", "Описание задачи 2", DONE, 1),
                manager.getSubTask(3));
        assertNull(manager.getSubTask(null));
        assertNull(manager.getSubTask(4));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void getSubTaskListTest() {
        manager.createEpic(new Epic(1, "Эпик", "Описание эпика"));
        assertNull(manager.getSubTaskList());
        manager.createSubTask(new SubTask(2, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(3, "Задача 2", "Описание задачи 2", DONE, 1));
        SubTask[] subTaskArray1 = {new SubTask(2, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1),
                new SubTask(3, "Задача 2", "Описание задачи 2", DONE, 1)};
        SubTask[] subTaskArray2 = manager.getSubTaskList().toArray(new SubTask[0]);
        assertArrayEquals(subTaskArray1, subTaskArray2);
    }

    @Test
    void getSubTaskListOfEpicTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2"));
        manager.createSubTask(new SubTask(3, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(4, "Задача 2", "Описание задачи 2", DONE, 1));
        manager.createSubTask(new SubTask(5, "Задача 3", "Описание задачи 3", NEW, 2));
        manager.createSubTask(new SubTask(6, "Задача 4", "Описание задачи 4", DONE, 2));
        manager.createSubTask(new SubTask(7, "Задача 5", "Описание задачи 5", IN_PROGRESS,
                LocalDateTime.of(2022, 5, 10, 12, 15, 0), Duration.ofMinutes(10), 2));
        manager.createSubTask(new SubTask(8, "Задача 6", "Описание задачи 6", IN_PROGRESS,
                LocalDateTime.of(2022, 5, 10, 12, 0, 0), Duration.ofMinutes(10), 2));
        manager.createSubTask(new SubTask(9, "Задача 7", "Описание задачи 7", IN_PROGRESS,
                LocalDateTime.of(2022, 5, 10, 12, 30, 0), Duration.ofMinutes(10), 2));
        assertNull(manager.getSubTaskListOfEpic(null));
        assertNull(manager.getSubTaskListOfEpic(3));
        SubTask[] subTaskArray1 = {new SubTask(5, "Задача 3", "Описание задачи 3", NEW, 2),
                new SubTask(6, "Задача 4", "Описание задачи 4", DONE, 2),
                new SubTask(7, "Задача 5", "Описание задачи 5", IN_PROGRESS,
                        LocalDateTime.of(2022, 5, 10, 12, 15, 0),
                        Duration.ofMinutes(10), 2),
                new SubTask(8, "Задача 6", "Описание задачи 6", IN_PROGRESS,
                        LocalDateTime.of(2022, 5, 10, 12, 0, 0),
                        Duration.ofMinutes(10), 2),
                new SubTask(9, "Задача 7", "Описание задачи 7", IN_PROGRESS,
                        LocalDateTime.of(2022, 5, 10, 12, 30, 0),
                        Duration.ofMinutes(10), 2)};
        SubTask[] subTaskArray2 = manager.getSubTaskListOfEpic(2).toArray(new SubTask[0]);
        assertArrayEquals(subTaskArray1, subTaskArray2);
    }

    @Test
    void historyTest() {
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1"));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2"));
        manager.createSubTask(new SubTask(3, "Задача 1", "Описание задачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(4, "Задача 2", "Описание задачи 2", DONE, 1));
        manager.createSubTask(new SubTask(5, "Задача 3", "Описание задачи 3", NEW, 2));
        manager.createSubTask(new SubTask(6, "Задача 4", "Описание задачи 4", DONE, 2));
        manager.getSubTask(5);
        manager.getSubTask(6);
        assertEquals(new SubTask(5, "Задача 3", "Описание задачи 3", NEW, 2),
                manager.getHistory().get(0));
        assertEquals(new SubTask(6, "Задача 4", "Описание задачи 4", DONE, 2),
                manager.getHistory().get(1));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void getTaskMapTest() {
        assertEquals(new HashMap<Integer, Task>(), manager.getTaskMap());
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", NEW));
        manager.createTask(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        manager.createTask(new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS));
        manager.createTask(new Task(4, "Задача 4", "Описание задачи 4", NEW));
        HashMap<Integer, Task> taskHashMap = new HashMap<>();
        taskHashMap.put(1, new Task(1, "Задача 1", "Описание задачи 1", NEW));
        taskHashMap.put(2, new Task(2, "Задача 2", "Описание задачи 2", DONE));
        taskHashMap.put(3, new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS));
        taskHashMap.put(4, new Task(4, "Задача 4", "Описание задачи 4", NEW));
        assertEquals(taskHashMap, manager.getTaskMap());
    }

    @Test
    void getEpicMapTest() {
        assertEquals(new HashMap<Integer, Task>(), manager.getEpicMap());
        manager.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1", NEW));
        manager.createEpic(new Epic(2, "Эпик 2", "Описание эпика 2", DONE));
        manager.createEpic(new Epic(3, "Эпик 3", "Описание эпика 3", IN_PROGRESS));
        manager.createEpic(new Epic(4, "Эпик 4", "Описание эпика 4", NEW));
        HashMap<Integer, Epic> epicHashMap = new HashMap<>();
        epicHashMap.put(1, new Epic(1, "Эпик 1", "Описание эпика 1", NEW));
        epicHashMap.put(2, new Epic(2, "Эпик 2", "Описание эпика 2", DONE));
        epicHashMap.put(3, new Epic(3, "Эпик 3", "Описание эпика 3", IN_PROGRESS));
        epicHashMap.put(4, new Epic(4, "Эпик 4", "Описание эпика 4", NEW));
        assertEquals(epicHashMap, manager.getEpicMap());
    }

    @Test
    void getSubTaskMapTest() {
        assertEquals(new HashMap<Integer, SubTask>(), manager.getSubTaskMap());
        manager.createEpic(new Epic(1, "", ""));
        manager.createSubTask(new SubTask(1, "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(2, "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        manager.createSubTask(new SubTask(3, "Подзадача 3", "Описание подзадачи 3", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(4, "Подзадача 4", "Описание подзадачи 4", NEW, 1));
        HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();
        subTaskHashMap.put(1, new SubTask(1, "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        subTaskHashMap.put(2, new SubTask(2, "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        subTaskHashMap.put(3, new SubTask(3, "Подзадача 3", "Описание подзадачи 3", IN_PROGRESS, 1));
        subTaskHashMap.put(4, new SubTask(4, "Подзадача 4", "Описание подзадачи 4", NEW, 1));
        assertEquals(subTaskHashMap, manager.getSubTaskMap());
    }
}
