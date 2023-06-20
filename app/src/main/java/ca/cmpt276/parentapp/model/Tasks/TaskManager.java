package ca.cmpt276.parentapp.model.Tasks;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskManager implements Iterable<Task> {
    private static final TaskManager instance = new TaskManager();
    private ArrayList<Task> taskArrayList = new ArrayList<>();

    private TaskManager() {

    }

    public static TaskManager getInstance() {
        return instance;
    }

    public ArrayList<Task> getTaskArrayList() {
        if (taskArrayList == null) {
            taskArrayList = new ArrayList<>();
        }
        return this.taskArrayList;
    }

    public void setTaskArrayList(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    public void addTask(Task newTask) {
        taskArrayList.add(newTask);
    }

    public void removeTask(int index) {
        taskArrayList.remove(index);
    }

    @NonNull
    @Override
    public Iterator<Task> iterator() {
        return null;
    }
}
