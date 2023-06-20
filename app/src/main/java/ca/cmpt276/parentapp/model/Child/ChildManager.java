package ca.cmpt276.parentapp.model.Child;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ChildManager implements Iterable<Child> {
    private static final ChildManager instance = new ChildManager();
    private ArrayList<Child> childArrayList = new ArrayList<>();
    private LinkedList<Child> Queue = new LinkedList<>();

    private ChildManager() {

    }

    public static ChildManager getInstance() {
        return instance;
    }

    public ArrayList<Child> getChildArrayList() {
        if (childArrayList == null) {
            childArrayList = new ArrayList<>();
        }
        return this.childArrayList;
    }

    public LinkedList<Child> getQueue() {
        return Queue;
    }

    public void setQueue(LinkedList<Child> queue) {
        Queue = queue;
    }

    public Child findChildByName(String name) {
        for (Child i : childArrayList) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    public Child findChildByIndex(Integer index) {
        Child i = childArrayList.get(index);
        return i;
    }

    public int findChildIndex(String name) {
        for (int i = 0; i < childArrayList.size(); i++) {
            if (childArrayList.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public void addToQueue(Child child) {
        Queue.addLast(child);
    }

    public int findTargetChild(String name) {
        for (int i = 0; i < Queue.size(); i++) {
            if (Queue.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void removeQueue(int index) {
        Queue.remove(index);
    }

    public void setChild(ArrayList<Child> kidsList) {
        this.childArrayList = kidsList;
    }

    public void addChild(Child child) {
        if (Queue == null) Queue = new LinkedList<>();
        childArrayList.add(child);
        Queue.add(child);
    }

    public void removeChild(int index) {
        childArrayList.remove(index);
    }

    @NonNull
    @Override
    public Iterator<Child> iterator() {
        return childArrayList.iterator();
    }
}