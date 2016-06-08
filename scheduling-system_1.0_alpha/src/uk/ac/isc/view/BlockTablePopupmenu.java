
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
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.VBASLogger;

public class BlockTablePopupmenu implements ActionListener {

    private final JTable table;
    private final BlockTableModel blockTableModel;
    private final SeisEventList seisEventList;
        
    private final JPopupMenu popupMenu;

    public BlockTablePopupmenu(JTable table, BlockTableModel blockTableModel, SeisEventList seisEventList) {

        this.seisEventList = seisEventList;
        this.table = table;
        this.blockTableModel = blockTableModel;

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

        JMenuItem menuItem_split = new JMenuItem("Split");
        menuItem_split.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_split);
        menuItem_split.addActionListener(this);

        JMenuItem menuItem_merge = new JMenuItem("Merge");
        menuItem_merge.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_merge);
        menuItem_merge.addActionListener(this);

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
                JOptionPane.showMessageDialog(null, "Done command will be added in next version.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                break;

            default:
                JOptionPane.showMessageDialog(null, "case: default \n Incorrect command, report to system admin!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;

        }
    }

    private void deleteBlock() {
        
        // selected block
        int blockID = blockTableModel.getTaskBlockArray().get(table.getSelectedRow()).getBlockID();
        String blockStatus = blockTableModel.getTaskBlockArray().get(table.getSelectedRow()).getStatus();
        
        UIManager.put("OptionPane.minimumSize", new Dimension(50, 50));
        VBASLogger.logDebug("blockStatus="  +blockStatus);

        if ("S".equals(blockStatus) || "F".equals(blockStatus)) {
            JOptionPane.showMessageDialog(null,
                    "The block is under review, hence cannot be deleted.\n"
                    + "Please contact with the system administrator for more information.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            
            int result = JOptionPane.showConfirmDialog(null, "Do you really want to delete the selected block?");

            if (result == JOptionPane.OK_OPTION) {
                SeisEventsDAO.deleteBlock(blockID);
                blockTableModel.reload();
                
                SeisEventsDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlockArray());
                SeisEventsDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlockArray());
                table.setModel(blockTableModel);

                if (blockTableModel.getTaskBlockArray().size() > 0) {
                
                    // reload the phase number
                    for (TaskBlock tb : blockTableModel.getTaskBlockArray()) {
                        tb.setPhaseNumber(0);
                    }

                    for (SeisEvent se : seisEventList.getSeisEventList()) {
                        if (se.getBlockID() != null) {
                            for (TaskBlock tb : blockTableModel.getTaskBlockArray()) {
                                if (tb.getBlockID().equals(se.getBlockID())) {
                                    tb.setPhaseNumber(tb.getPhaseNumber() + se.getPhaseNumber());
                                }
                            }

                        }
                    }
                } else {
                    return;
                }

                for (SeisEvent se : seisEventList.getSeisEventList()) {
                    if (se.getBlockID() != null && se.getBlockID() == blockID) {
                        se.setblAssigned(false);
                        se.setBlockID(null);
                    }
                }

                JOptionPane.showMessageDialog(null, "The selected block is deleted!");
            }
            
            table.updateUI();
            
            //SK
            /*
            blockInfoPanel.getAnalystView().updateBlocks();
            blockInfoPanel.getBlockView().repaint();*/
                    
            if (blockTableModel.getTaskBlockArray().size() > 0) {
                table.setRowSelectionInterval(0, 0);
                blockID = (int) table.getValueAt(0, 0);
            }

        }
    }

    private void splitBlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void mergeBlocks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reassignBlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doneBlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
    
    
}
