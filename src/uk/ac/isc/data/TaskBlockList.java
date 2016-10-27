package uk.ac.isc.data;

import java.util.ArrayList;
import java.util.Observable;

public class TaskBlockList extends Observable {

    private final ArrayList<TaskBlock> taskBlockList;

    public TaskBlockList() {
        taskBlockList = new ArrayList<>();
    }

    /*
     public void setTaskBlockList(ArrayList<TaskBlock> se) {
     this.taskBlockList = se;
     }*/
    public ArrayList<TaskBlock> getTaskBlockList() {
        return this.taskBlockList;
    }

    public void setChangeFlag() {
        setChanged();
    }

}
