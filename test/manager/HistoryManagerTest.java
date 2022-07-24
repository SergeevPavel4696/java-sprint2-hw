package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tasks.Status.DONE;
import static tasks.Status.IN_PROGRESS;

class HistoryManagerTest {

    private HistoryManager manager;

    @BeforeEach
    void init() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void linkLast() {
        manager.linkLast(null);
        assertNull(manager.getHistory());
        manager.linkLast(new Task(1, "Задача 1", "Описание задачи 1", Status.NEW));
        Task[] taskArray1 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW)};
        Task[] taskArray2 = manager.getHistory().toArray(new Task[0]);
        assertArrayEquals(taskArray1, taskArray2);
        manager.linkLast(new Task(2, "Задача 2", "Описание задачи 2", DONE));
        Task[] taskArray3 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW),
                new Task(2, "Задача 2", "Описание задачи 2", DONE)};
        Task[] taskArray4 = manager.getHistory().toArray(new Task[0]);
        assertArrayEquals(taskArray3, taskArray4);
        manager.linkLast(new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS));
        manager.remove(2);
        Task[] taskArray5 = {new Task(1, "Задача 1", "Описание задачи 1", Status.NEW),
                new Task(3, "Задача 3", "Описание задачи 3", IN_PROGRESS)};
        Task[] taskArray6 = manager.getHistory().toArray(new Task[0]);
        assertArrayEquals(taskArray5, taskArray6);
    }
}