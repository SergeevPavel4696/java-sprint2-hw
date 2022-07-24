package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private final Integer id;
    private final String title;
    private final String description;
    private final Status status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Duration duration;

    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(Integer id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String title, String description, Status status,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public Task(Integer id, String title, String description, Status status,
                LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return Type.TASK;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public String toStringForFile() {
        String taskToFile = String.format("%d,%s,%s,%s,%s", id, getType(), title, status, description);
        if (startTime != null) {
            taskToFile = taskToFile + "," + startTime.format(dateFormat);
        }
        if (endTime != null) {
            taskToFile = taskToFile + "," + endTime.format(dateFormat);
        }
        if (duration != null) {
            taskToFile = taskToFile + "," + duration.toMinutes();
        }
        return taskToFile;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(endTime, task.endTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, startTime, endTime, duration);
    }
}
