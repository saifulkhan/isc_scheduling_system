package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.TaskBlockList;
import uk.ac.isc.data.VBASLogger;

public class BlockTable extends JPanel implements Observer {

    private final TaskBlockList taskBlockList;
    private final TaskBlock selectedTaskBlock;
    private final SeisEventList seisEventList;
    private final SeisEventList selectedSeisEventList;

    private final HashMap<Integer, Integer> allocatedEvID = new HashMap<>();
    private HashSet<TaskBlock> blockSet;

    private JTable table = null;
    private BlockTableModel blockTableModel = null;
    private BlockTablePopupmenu blockTablePopupmenu = null;
    private ListSelectionListener listSelectionListener = null;

    private JScrollPane scrollPane = new JScrollPane();;

    public BlockTable(TaskBlockList taskBlockList,
            TaskBlock selectedTaskBlock,
            SeisEventList allSeisEventList,
            SeisEventList selectedSeisEventList) {

        this.taskBlockList = taskBlockList;
        this.selectedTaskBlock = selectedTaskBlock;
        this.seisEventList = allSeisEventList;
        this.selectedSeisEventList = selectedSeisEventList;

        updateData();
        updateTable();
    }

    private void updateData() {

        blockSet = new HashSet<>();
        taskBlockList.getTaskBlockList().clear();

        //load the block table from the database
        SeisEventsDAO.loadBlocks(blockSet);
        taskBlockList.getTaskBlockList().addAll(blockSet);
        Collections.sort(taskBlockList.getTaskBlockList());

        // fill in the events number
        Boolean ret = SeisEventsDAO.retrieveBlockEventNumber(taskBlockList.getTaskBlockList());
        ret = SeisEventsDAO.retrieveBlockReviewedEventNumber(taskBlockList.getTaskBlockList());

        // load the events
        ret = SeisEventsDAO.retrieveAllEvents(seisEventList.getSeisEventList());
        ret = SeisEventsDAO.retrieveAllocatedEvID(allocatedEvID);

        // initialize the phase number of blocks = 0
        for (TaskBlock tb : taskBlockList.getTaskBlockList()) {
            tb.setPhaseNumber(0);
        }

        for (SeisEvent se : seisEventList.getSeisEventList()) {

            if (allocatedEvID.containsKey(se.getEvid())) {
                se.setblAssigned(true);
                se.setBlockID(allocatedEvID.get(se.getEvid()));
                for (TaskBlock tb : taskBlockList.getTaskBlockList()) {
                    if (Objects.equals(tb.getBlockID(), allocatedEvID.get(se.getEvid()))) {
                        tb.setPhaseNumber(tb.getPhaseNumber() + se.getPhaseNumber());
                    }
                }
            }
        }

    }

    private void updateTable() {

        table = new JTable();
        table.setSelectionBackground(Color.gray);
        
        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {  // disable the double calls
                    onValueChanged(lse);
                }
            }
        };
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                onMouseClicked(evt);
            }
        });

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);

        blockTableModel = new BlockTableModel(taskBlockList.getTaskBlockList());
        table.setModel(blockTableModel);
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        
        blockTablePopupmenu = new BlockTablePopupmenu(table, blockTableModel, selectedSeisEventList);

        scrollPane.setViewportView(table);
        scrollPane.repaint();
        
        // select the first row
        if (taskBlockList.getTaskBlockList().size() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    
    public void onValueChanged(ListSelectionEvent e) {

        VBASLogger.logDebug("Here...");

        int blockId = (Integer) table.getValueAt(table.getSelectedRow(), 0);
              
        SeisEventList blockEvents = new SeisEventList();
        for (SeisEvent se : seisEventList.getSeisEventList()) {
            if (se.getBlockID() != null && se.getBlockID().equals(blockId)) {
                selectedSeisEventList.getSeisEventList().add(se);
            }
        }
        selectedSeisEventList.setChangeFlag();
        selectedSeisEventList.notifyObservers();
        
        selectedTaskBlock.setTaskBlock(blockTableModel.getTaskBlockArray().get(table.getSelectedRow()));
        selectedTaskBlock.setChangeFlag();
        selectedTaskBlock.notifyObservers();
       
    }

    private void onMouseClicked(MouseEvent e) {

        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();

        if (blockTablePopupmenu.getPopupMenu().isVisible()) {
            blockTablePopupmenu.getPopupMenu().setVisible(false);
        }

        // Specify the condition(s) you want for htPopupManager display.
        // For Example: show htPopupManager only if a row & column is selected.
        if (selectedRow >= 0 && selectedCol >= 0) {
            VBASLogger.logDebug("selectedRow=" + selectedRow
                    + ", selectedCol=" + selectedCol);

            Point p = e.getPoint();
            final int row = table.rowAtPoint(p);
            final int col = table.columnAtPoint(p);
            if (SwingUtilities.isRightMouseButton(e)) {
                Rectangle r = table.getCellRect(row, col, false);
                blockTablePopupmenu.getPopupMenu().show(table, r.x, r.y + r.height);
            } else {
                e.consume();
            }
        }
    }

    @Override
    public void update(Observable o, Object o1) {
        VBASLogger.logDebug(" Upadting the Block data...");
        VBASLogger.logDebug(" Upadting the BlockTable...");
        this.updateData();
        this.updateTable();
    }

    public JScrollPane getScrollBlockTable() {
        return scrollBlockTable;
    }
}
