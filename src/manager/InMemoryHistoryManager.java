package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head = null;
    private Node<Task> tail = null;
    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void linkLast(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node<Task> prev = tail;
        Node<Task> next = null;
        Node<Task> last = new Node<>(prev, next, task);
        tail = last;
        if (head == null) {
            head = last;
        }
        if (prev != null) {
            prev.setNext(last);
        }
        add(task);
    }

    private void add(Task task) {
        historyMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            Node<Task> prevNode = node.getPrev();
            Node<Task> nextNode = node.getNext();
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        if (head != null) {
            Node<Task> temp = head;
            while (temp != null) {
                tasksList.add(temp.getItem());
                temp = temp.getNext();
            }
        }
        return tasksList;
    }

    static class Node<Task> {
        private Node<Task> prev;
        private Node<Task> next;
        private Task item;

        Node(Node<Task> prev, Node<Task> next, Task item) {
            this.prev = prev;
            this.next = next;
            this.item = item;
        }

        public Node<Task> getPrev() {
            return this.prev;
        }

        public Node<Task> getNext() {
            return this.next;
        }

        public Task getItem() {
            return this.item;
        }

        public void setPrev(Node<Task> prev) {
            this.prev = prev;
        }

        public void setNext(Node<Task> next) {
            this.next = next;
        }

        public void setItem(final Task item) {
            this.item = item;
        }
    }
}
