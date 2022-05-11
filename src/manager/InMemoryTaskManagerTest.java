package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    @Override
    public void createManager() {
        manager = new InMemoryTaskManager();
    }
}