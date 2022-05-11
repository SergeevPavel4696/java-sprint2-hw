package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final Integer idEpic;

    public SubTask(Integer id, String title, String description, Status status, Integer idEpic) {
        super(id, title, description, status);
        this.idEpic = idEpic;
    }

    public SubTask(Integer id, String title, String description, Status status,
                   LocalDateTime startTime, Duration duration, Integer idEpic) {
        super(id, title, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toStringForFile() {
        return super.toStringForFile() + "," + getIdEpic();
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "idEpic=" + idEpic +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(idEpic, subTask.idEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }
}
