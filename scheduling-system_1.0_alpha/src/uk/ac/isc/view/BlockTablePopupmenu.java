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

        switch (e.getActionCommand()) {
            case "Delete":
                deleteBlock();
                break;

            case "Split":
                splitBlock();
                break;

            case "Merge":
                mergeBlocks();
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

        int bid = blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getBlockID();
        String status = blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getStatus();

        UIManager.put("OptionPane.minimumSize", new Dimension(50, 50));

        if ("S".equals(status) || "F".equals(status)) {
            JOptionPane.showMessageDialog(null,
                    "The block has been reviewed which cannot be deleted.\n"
                    + "Please contact with the system administrator for more options",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            int result = JOptionPane.showConfirmDialog(null, 
                    "Do you really want to delete the selected block?",
                    null, JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                SeisEventsDAO.deleteBlock(bid);

            } else {
                return;
            }

            for (SeisEvent se : seisEventList.getSeisEventList()) {
                if (se.getBlockID() != null && se.getBlockID() == bid) {
                    se.setblAssigned(false);
                    se.setBlockID(null);
                }
            }
            
            JOptionPane.showMessageDialog(null, "The selected block is deleted!");
            GlobalStorage.loadData();
        }
    }

    private void splitBlock() {
        JOptionPane.showMessageDialog(null,
                "Split command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void mergeBlocks() {
        JOptionPane.showMessageDialog(null,
                "Merge command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void reassignBlock() {
        JOptionPane.showMessageDialog(null,
                "Reassign command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void doneBlock() {
        JOptionPane.showMessageDialog(null,
                "Done command will be added in next version.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

}
