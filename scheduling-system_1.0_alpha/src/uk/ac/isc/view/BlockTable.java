package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.isc.data.GlobalStorage;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.TaskBlockList;
import uk.ac.isc.data.VBASLogger;

public class BlockTable extends JPanel implements Observer {

    private final SeisEventList seisEventList;
    private final SeisEventList selectedSeisEventList;
    private final TaskBlockList taskBlockList;
    private final TaskBlock selectedTaskBlock;

    private JTable table = null;
    private BlockTableModel blockTableModel = null;
    private BlockTablePopupmenu blockTablePopupmenu = null;
    private ListSelectionListener listSelectionListener = null;
    private JScrollPane scrollPane = new JScrollPane();

   
    public BlockTable(SeisEventList seisEventList,
            SeisEventList selectedSeisEventList,
            TaskBlockList taskBlockList,
            TaskBlock selectedTaskBlock) {

        this.seisEventList = seisEventList;
        this.selectedSeisEventList = selectedSeisEventList;
        this.taskBlockList = taskBlockList;
        this.selectedTaskBlock = selectedTaskBlock;

        this.updateTable();
    }

    @Override
    public void update(Observable o, Object o1) {
        VBASLogger.logDebug("Upadte the BlockTable...");
        this.updateTable();
    }

    private void updateTable() {

        VBASLogger.logDebug("@seisEventList: " + seisEventList);
        VBASLogger.logDebug("#seisEventList: " + seisEventList.getSeisEventList().size());

        table = new JTable();
        table.setSelectionBackground(Color.GRAY);
        table.setSelectionForeground(Color.WHITE);

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

        blockTablePopupmenu = new BlockTablePopupmenu(table, blockTableModel, seisEventList);

        scrollPane.setViewportView(table);
        scrollPane.repaint();

        // Select first row
        if (taskBlockList.getTaskBlockList().size() > 0) {
            table.setRowSelectionInterval(0, 0);

        } else { // BloackTable is empty
            GlobalStorage.setSelectedTaskBlock(new TaskBlock()); // No or empty selected TaskBlock
            GlobalStorage.setSelectedSeisEventList(); // No or empty Selected SeisEventList
        }
    }

    /*
     * New row is selected: Chenage selectedTaskBlock and selectedSeisEventList data.
     */
    public void onValueChanged(ListSelectionEvent e) {
        VBASLogger.logDebug("Chenage selected block.");

        int blockId = (Integer) table.getValueAt(table.getSelectedRow(), 0);
        GlobalStorage.setSelectedSeisEventList(blockId);

        TaskBlock selectedTaskBlock = blockTableModel.getTaskBlockArray().get(table.getSelectedRow());
        GlobalStorage.setSelectedTaskBlock(selectedTaskBlock);
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
            VBASLogger.logDebug("selectedRow=" + selectedRow);

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

    public JScrollPane getScrollBlockTable() {
        return scrollPane;
    }
}
