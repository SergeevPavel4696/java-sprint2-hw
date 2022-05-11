package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;
import static tasks.Status.IN_PROGRESS;

class HistoryManagerTest {

    HistoryManager manager;

    @BeforeEach
    public void createHistoryManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void linkLast() {
        manager.linkLast(null);
        Assertions.assertNull(manager.getHistory());
        manager.linkLast(new Task(1, "Задача 1", "Описание задачи 1", Status.NEW));
        Task[] taskArray1 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW)};
        Task[] taskArray2 = manager.getHistory().toArray(new Task[0]);
        Assertions.assertArrayEquals(taskArray1, taskArray2);
        manager.linkLast(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        Task[] taskArray3 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW),
                new Task(2, "Задача 2", "Описание задачи 2", DONE)};
        Task[] taskArray4 = manager.getHistory().toArray(new Task[0]);
        Assertions.assertArrayEquals(taskArray3, taskArray4);
        manager.linkLast(new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS));
        manager.remove(2);
        Task[] taskArray5 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW),
                new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS)};
        Task[] taskArray6 = manager.getHistory().toArray(new Task[0]);
        Assertions.assertArrayEquals(taskArray5, taskArray6);
    }
}