package tasks;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Status> subTaskIdMap = new HashMap<>();

    public Epic(Integer id, String title, String description) {
        super(id, title, description, Status.NEW);
    }

    public Epic(Integer id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public HashMap<Integer, Status> getSubTaskIdMap() {
        return subTaskIdMap;
    }

    public void setSubTaskId(SubTask subTask) {
        subTaskIdMap.put(subTask.getId(), subTask.getStatus());
    }

    public void removeSubTaskId(SubTask subTask) {
        subTaskIdMap.remove(subTask.getId());
    }

    public void removeAllSubTaskId() {
        subTaskIdMap.clear();
    }

    public void setSubTaskIdMap(HashMap<Integer, Status> subTaskIdMap) {
        this.subTaskIdMap = subTaskIdMap;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIdMap=" + subTaskIdMap +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
