package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyList.size() == 10) historyList.remove(0);
            historyList.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
