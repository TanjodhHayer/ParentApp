package ca.cmpt276.parentapp.model.Tasks;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskHistoryManager implements Iterable<TaskHistory> {
    private static final TaskHistoryManager instance = new TaskHistoryManager();
    private ArrayList<TaskHistory> TaskHistoryArrayList = new ArrayList<>();

    private TaskHistoryManager() {

    }

    public static TaskHistoryManager getInstance() {
        return instance;
    }

    public ArrayList<TaskHistory> getTaskHistoryArrayList() {
        if (TaskHistoryArrayList == null) {
            TaskHistoryArrayList = new ArrayList<>();
        }
        return this.TaskHistoryArrayList;
    }

    public void setTaskHistoryArrayList(ArrayList<TaskHistory> taskHistoryArrayList) {
        TaskHistoryArrayList = taskHistoryArrayList;
    }

    public void addTaskHistory(TaskHistory newTask) {
        TaskHistoryArrayList.add(0, newTask);
    }

    public void removeTaskHistory(int index) {
        TaskHistoryArrayList.remove(index);
    }

    @NonNull
    @Override
    public Iterator<TaskHistory> iterator() {
        return null;
    }
}
