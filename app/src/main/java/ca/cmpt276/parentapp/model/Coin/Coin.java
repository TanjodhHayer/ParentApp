package ca.cmpt276.parentapp.model.Coin;

import androidx.annotation.NonNull;

import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;

public class Coin {
    private final String FlipDate;
    private final int childIndex;
    private final int PlayerChoice; // 0 = head, 1 = tail
    private final boolean WinOrNot;

    public Coin(String flipDate, int playersName, int playerChoice, boolean winOrNot) {
        FlipDate = flipDate;
        childIndex = playersName;
        PlayerChoice = playerChoice;
        WinOrNot = winOrNot;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public boolean isWinOrNot() {
        return WinOrNot;
    }

    public String getChildName() {
        ChildManager childManager = ChildManager.getInstance();
        String childName = "";
        boolean inBounds = (childIndex >= 0) && (childIndex < childManager.getChildArrayList().size());
        if (inBounds && !childManager.getChildArrayList().isEmpty()) {
            childName = childManager.getChildArrayList().get(childIndex).getName();
        }
        return childName;
    }

    public Child getChild() {
        ChildManager childManager = ChildManager.getInstance();
        boolean inBounds = (childIndex >= 0) && (childIndex < childManager.getChildArrayList().size());
        if (inBounds && !childManager.getChildArrayList().isEmpty()) {
            return childManager.getChildArrayList().get(childIndex);
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        ChildManager childManager = ChildManager.getInstance();
        String childName = "";
        boolean inBounds = (childIndex >= 0) && (childIndex < childManager.getChildArrayList().size());
        if (inBounds && !childManager.getChildArrayList().isEmpty()) {
            childName = childManager.getChildArrayList().get(childIndex).getName();
        }

        String childChoice = PlayerChoice == 0 ? "head" : "tail";
        return "Flip Date: " + FlipDate + "\n" +
                "Child's Name: " + childName + "\n" +
                "Child's Choice: " + childChoice + "\n" +
                "Win: " + (WinOrNot ? "True" : "False") + "\n";
    }
}
