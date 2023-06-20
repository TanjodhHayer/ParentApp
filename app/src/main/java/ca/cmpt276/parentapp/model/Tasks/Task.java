package ca.cmpt276.parentapp.model.Tasks;

import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;

public class Task {
    private String taskDesc;
    private int index;

    public Task(String taskDesc) {
        this.index = 0;
        this.taskDesc = taskDesc;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public int getIndex() {
        ChildManager childManager = ChildManager.getInstance();
        if (childManager.getChildArrayList().size() > 0) {
            return index % childManager.getChildArrayList().size();
        }
        return -1;
    }

    public void taskDone() {
        this.index += 1;
    }

    public Child currChild() {
        ChildManager childManager = ChildManager.getInstance();
        if (childManager.getChildArrayList().size() > 0) {
            return childManager.getChildArrayList().get(index % childManager.getChildArrayList().size());
        }
        return null;
    }

    @Override
    public String toString() {
        ChildManager childManager = ChildManager.getInstance();
        if (childManager.getChildArrayList().size() > 0) {
            Child childTurn = childManager.getChildArrayList().get(index % childManager.getChildArrayList().size());
            return "\nTask: " + taskDesc + "\n" +
                    "Whose Turn: " + childTurn.getName();
        } else {
            return "\nTask" + taskDesc + "\n" +
                    "Whose Turn:\n";
        }
    }
}
