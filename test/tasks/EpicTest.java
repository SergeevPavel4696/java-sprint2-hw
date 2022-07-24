package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.Status.DONE;
import static tasks.Status.IN_PROGRESS;
import static tasks.Status.NEW;

class EpicTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void createManager() {
        manager = new InMemoryTaskManager();
        Epic epic = new Epic(manager.getId(), "Эпик", "Описание эпика");
        manager.createEpic(epic);
    }

    @Test
    void checkingEpicStatusByEmptySubtasksList() {
        assertEquals(manager.getEpic(1).getStatus(), NEW);

    }

    @Test
    void checkingEpicStatusByNewSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", NEW, 1));
        assertEquals(manager.getEpic(1).getStatus(), NEW);
    }

    @Test
    void checkingEpicStatusByInProgressSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, 1));
        assertEquals(manager.getEpic(1).getStatus(), IN_PROGRESS);
    }

    @Test
    void checkingEpicStatusByDoneSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        assertEquals(manager.getEpic(1).getStatus(), DONE);
    }

    @Test
    void checkingEpicStatusByNewDoneSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        assertEquals(manager.getEpic(1).getStatus(), IN_PROGRESS);
    }
}
