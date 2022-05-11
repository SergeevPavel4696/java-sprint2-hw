package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTasksManagerTest extends InMemoryTaskManagerTest {

    @BeforeEach
    @Override
    public void createManager() {
        File file = new File("Tasks1.csv");
        manager = new FileBackedTasksManager(file);
    }

    @Test
    void loadManagerFromFile() {
        File file4 = new File("Tasks4.csv");
        FileBackedTasksManager manager2 = new FileBackedTasksManager(file4);
        manager2.createTask(new Task(2, "Задача 1", "Описание задачи 1", Status.NEW));
        manager2.createEpic(new Epic(1, "Эпик 1", "Описание эпика 1", Status.IN_PROGRESS,
                LocalDateTime.of(2020, 10, 1, 9, 0, 0),
                LocalDateTime.of(2020, 10, 1, 10, 0, 0),
                Duration.ofMinutes(60)));
        manager2.createSubTask(new SubTask(3, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2020, 10, 1, 9, 0, 0),
                Duration.ofMinutes(15), 1));
        manager2.createSubTask(new SubTask(4, "Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS,
                LocalDateTime.of(2020, 10, 1, 9, 30, 0),
                Duration.ofMinutes(30), 1));
        manager2.createEpic(new Epic(5, "Эпик 2", "Описание эпика 2"));
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File("Tasks2.csv"));
        Assertions.assertEquals(manager2.getTask(2), manager.getTask(2));
        Assertions.assertEquals(manager2.getEpic(1), manager.getEpic(1));
        Assertions.assertEquals(manager2.getSubTask(3), manager.getSubTask(3));
        Assertions.assertEquals(manager2.getSubTask(4), manager.getSubTask(4));
        Assertions.assertEquals(manager2.getEpic(5), manager.getEpic(5));
    }
}