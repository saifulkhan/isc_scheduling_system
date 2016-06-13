package uk.ac.isc.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.data.TaskBlock;

public class BlockTableModel extends AbstractTableModel {

    private final ArrayList<TaskBlock> taskBlockArray;
    private final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");

    private final String[] columnNames = {"Block ID.",
        "Start Date",
        "End Date",
        "Region",
        "Analyst 1",
        "Analyst 2",
        "Analyst 3",
        "Status",
        "#SeisEvents",
        "#Reviewed"};

    private final Class[] columns = new Class[]{Integer.class,
        String.class,
        String.class,
        Integer.class,
        String.class,
        String.class,
        String.class,
        String.class,
        Integer.class,
        Integer.class
    };

    public BlockTableModel(ArrayList<TaskBlock> taskBlockArray) {
        this.taskBlockArray = taskBlockArray;
    }

    public ArrayList<TaskBlock> getTaskBlockArray() {
        return this.taskBlockArray;
    }

    @Override
    public int getRowCount() {
        return this.taskBlockArray.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject = null;

        if (columnIndex == 0) {
            retObject = taskBlockArray.get(rowIndex).getBlockID();
        } else if (columnIndex == 1) {
            retObject = df.format(taskBlockArray.get(rowIndex).getStartDay());
            //retObject = taskBlockArray.get(rowIndex).getStartDay().toString();
        } else if (columnIndex == 2) {
            retObject = df.format(taskBlockArray.get(rowIndex).getEndDay());
        } else if (columnIndex == 3) {
            retObject = taskBlockArray.get(rowIndex).getRegionID();
        } else if (columnIndex == 4) {
            retObject = taskBlockArray.get(rowIndex).getAnalyst1();
        } else if (columnIndex == 5) {
            retObject = taskBlockArray.get(rowIndex).getAnalyst2();
        } else if (columnIndex == 6) {
            retObject = taskBlockArray.get(rowIndex).getAnalyst3();
        } else if (columnIndex == 7) {
            retObject = taskBlockArray.get(rowIndex).getStatus();
        } else if (columnIndex == 8) {
            retObject = taskBlockArray.get(rowIndex).getEventNumber();
        } else {
            retObject = taskBlockArray.get(rowIndex).getReviewedEventNumber();
        }

        return retObject;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
        return columns[c];
    }
}
