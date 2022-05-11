package tasks;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static tasks.Status.NEW;
import static tasks.Status.IN_PROGRESS;
import static tasks.Status.DONE;

class EpicTest {
    InMemoryTaskManager manager;
    Epic epic;

    @BeforeEach
    void createManager() {
        manager = new InMemoryTaskManager();
        epic = new Epic(manager.getId(), "Эпик", "Описание эпика");
        manager.createEpic(epic);
    }

    @Test
    public void checkingEpicStatusByEmptySubtasksList() {
        Assertions.assertEquals(manager.getEpic(1).getStatus(), NEW);

    }

    @Test
    public void checkingEpicStatusByNewSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", NEW, 1));
        Assertions.assertEquals(manager.getEpic(1).getStatus(), NEW);
    }

    @Test
    public void checkingEpicStatusByInProgressSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", IN_PROGRESS, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", IN_PROGRESS, 1));
        Assertions.assertEquals(manager.getEpic(1).getStatus(), IN_PROGRESS);
    }

    @Test
    public void checkingEpicStatusByDoneSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", DONE, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        Assertions.assertEquals(manager.getEpic(1).getStatus(), DONE);
    }

    @Test
    public void checkingEpicStatusByNewDoneSubtasksList() {
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW, 1));
        manager.createSubTask(new SubTask(manager.getId(), "Подзадача 2", "Описание подзадачи 2", DONE, 1));
        Assertions.assertEquals(manager.getEpic(1).getStatus(), IN_PROGRESS);
    }
}
