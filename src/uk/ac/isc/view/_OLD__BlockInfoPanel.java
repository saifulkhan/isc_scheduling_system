package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import uk.ac.isc.data.Analyst;
import uk.ac.isc.data.SeisEventsDAO;

/**
 * This is the panel to have the table of the blocks and a map to show the
 * events of the block
 *
 * @author hui
 */
public class _OLD__BlockInfoPanel extends JPanel implements TableModelListener {

    /**
     * data ref to keep the seisevents and blocks
     */
    //ArrayList<TaskBlock> blockArray;
    ArrayList<Analyst> analysts;

    /**
     * block control panel to control of building blocks, breaking blocks or
     * changing mode of showing different info
     */
    private final AnalystView analystView;
    private final BlockView blockView;

    /*use int type for future extension*/
    private int viewFlag;


    private final JTable blockTable;
    private final BlockTableModel blockTableModel;

    // table and table model to show the blocks info
    private final JScrollPane scrollPane;
    private final JScrollPane visPane;

    private final JSplitPane infoPane;

    public _OLD__BlockInfoPanel(BlockTableModel btm, JTable btable) {
        this.blockTableModel = btm;
        this.blockTable = btable;

        
        analysts = new ArrayList<>();
        SeisEventsDAO.loadAnslysts(analysts);

        analystView = new AnalystView(analysts, btm.getTaskBlockArray());

        blockView = new BlockView(btm.getTaskBlockArray());

        blockTableModel.addTableModelListener(this);

        //blockTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane = new JScrollPane(blockTable);

        visPane = new JScrollPane(blockView);
        visPane.add(analystView);

        if (viewFlag == 0) {
            visPane.setViewportView(blockView);
        } else {
            visPane.setViewportView(analystView);
        }

        infoPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, visPane);
        infoPane.setDividerLocation(0.4);

        this.setLayout(new BorderLayout());
        //this.add(bcp, BorderLayout.NORTH);
        this.add(infoPane, BorderLayout.CENTER);
    }

    public AnalystView getAnalystView() {
        return analystView;
    }

    public BlockView getBlockView() {
        return blockView;
    }

    public Integer getSBid() {
        return blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getBlockID();
    }

    public String getBlockStatus() {
        return blockTableModel.getTaskBlockArray().get(blockTable.getSelectedRow()).getStatus();
    }

    public BlockTableModel getBlockTableModel() {
        return this.blockTableModel;
    }

    public JTable getBlockTable() {
        return this.blockTable;
    }

    public void setViewFlag(int flag) {
        this.viewFlag = flag;

        if (viewFlag == 0) {
            visPane.setViewportView(blockView);
        } else {
            visPane.setViewportView(analystView);
        }
        repaint();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        //blockTableModel.reload();
        //if(blockTableModel.getTaskBlockArray().size()>0)
        //{
        //    blockTable.setRowSelectionInterval(0, 0);         
        //}
    }

}
