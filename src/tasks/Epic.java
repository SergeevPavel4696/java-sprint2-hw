package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Status> subTaskIdMap = new HashMap<>();

    public Epic(Integer id, String title, String description) {
        super(id, title, description, Status.NEW);
    }

    public Epic(Integer id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public Epic(Integer id, String title, String description, Status status,
                LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(id, title, description, status, startTime, endTime, duration);
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
    public Type getType() {
        return Type.EPIC;
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
