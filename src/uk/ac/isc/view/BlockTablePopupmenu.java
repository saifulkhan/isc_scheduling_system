package uk.ac.isc.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import uk.ac.isc.data.GlobalStorage;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.VBASLogger;

public class BlockTablePopupmenu implements ActionListener {

    private final JTable blockTable;
    private final BlockTableModel blockTableModel;
    private final SeisEventList seisEventList;

    private final JPopupMenu popupMenu;
    private Integer bid = null;
    private String status = null;

    public BlockTablePopupmenu(JTable bt, BlockTableModel btModel, SeisEventList seList) {

        this.seisEventList = seList;
        this.blockTable = bt;
        this.blockTableModel = btModel;

        popupMenu = new JPopupMenu();
        setMenuAttributes();
    }

    private void setMenuAttributes() {

        JMenuItem menuItem_delete = new JMenuItem("Delete");
        menuItem_delete.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_delete);
        menuItem_delete.addActionListener(this);

        JMenuItem menuItem_done = new JMenuItem("Done");
        menuItem_done.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_done);
        menuItem_done.addActionListener(this);

        /*
         JMenuItem menuItem_split = new JMenuItem("Split");
         menuItem_split.setFont(new Font("Sans-serif", Font.PLAIN, 14));
         popupMenu.add(menuItem_split);
         menuItem_split.addActionListener(this);

        
         JMenuItem menuItem_reassign = new JMenuItem("Reassign");
         menuItem_reassign.setFont(new Font("Sans-serif", Font.PLAIN, 14));
         popupMenu.add(menuItem_reassign);
         menuItem_reassign.addActionListener(this);
        
         // get rid of Merge
         JMenuItem menuItem_merge = new JMenuItem("Merge");
         menuItem_merge.setFont(new Font("Sans-serif", Font.PLAIN, 14));
         popupMenu.add(menuItem_merge);
         menuItem_merge.addActionListener(this);
        
         */
    }

    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(VBASLogger.debugAt());

        bid = blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getBlockID();
        status = blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getStatus();

        switch (e.getActionCommand()) {
            case "Delete":
                deleteBlock();
                break;

            case "Split":
                splitBlock();
                break;

            case "Reassign":
                reassignBlock();
                break;

            case "Done":
                doneBlock();
                break;

            default:
                JOptionPane.showMessageDialog(null,
                        "switch -> case: default \nIncorrect command, report to system admin.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;

        }
    }

    private void deleteBlock() {


        if ("S".equals(status) || "F".equals(status)) {
            JOptionPane.showMessageDialog(blockTable,
                    "The block has been reviewed which cannot be deleted.\n"
                    + "Please contact with the system administrator for more options",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            int result = JOptionPane.showConfirmDialog(blockTable,
                    "Do you really want to delete the selected block?",
                    null, JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                SeisEventsDAO.deleteBlock(bid);
                JOptionPane.showMessageDialog(blockTable, "The selected block is deleted.", 
                        "success", JOptionPane.INFORMATION_MESSAGE);
                GlobalStorage.loadData();

            } 

            /*
            for (SeisEvent se : seisEventList.getSeisEventList()) {
                if (se.getBlockID() != null && se.getBlockID() == bid) {
                    se.setblAssigned(false);
                    se.setBlockID(null);
                }
            }*/
        }
    
        bid = null;
        status = null;
    }

    private void doneBlock() {
        
        UIManager.put("OptionPane.minimumSize", new Dimension(50, 50));

        if ("P".equals(status) || "S".equals(status)) {
            JOptionPane.showMessageDialog(null,
                    "The block has been reviewed which cannot be marked as done.\n"
                    + "Please contact the system administrator for more options",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        } else {
            int result = JOptionPane.showConfirmDialog(null,
                    "Is this block really done?",
                    null, JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                SeisEventsDAO.doneBlock(bid);
                JOptionPane.showMessageDialog(null, "The selected block is done!");
                GlobalStorage.loadData();
            }
        }
        
        bid = null;
        status = null;
    }

    private void splitBlock() {
        JOptionPane.showMessageDialog(null,
                "Split command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void reassignBlock() {
        JOptionPane.showMessageDialog(null,
                "Reassign command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

}
