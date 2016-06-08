package uk.ac.isc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import uk.ac.isc.data.Analyst;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.TaskBlockList;
import uk.ac.isc.data.VBASLogger;
import uk.ac.isc.view.AnalystView;
import uk.ac.isc.view.BlockControlPanel;
import uk.ac.isc.view.BlockTable;
import uk.ac.isc.view.BlockTextPanel;
import uk.ac.isc.view.BlockView;
import uk.ac.isc.view.MapPanel;

public class SchedularMainWindow extends JFrame {

    /*
     * data
     */
    private final TaskBlockList taskBlockList = new TaskBlockList();
    private final TaskBlock selectedTaskBlock = new TaskBlock();
    private final SeisEventList seisEventList = new SeisEventList(); // save all the events info assigned or unassigned    
    private final SeisEventList selectedSeisEventList = new SeisEventList(); // this for the selcted block

    // get the range of showing the data
    private final Date startDate;
    private final Date endDate;

    /**
     * Views
     */
    private final BlockTable blockTable;
    private final MapPanel mapPanel;
    private final BlockTextPanel blockTextPanel;
    private final BlockControlPanel blockControlPanel;
    private final AnalystView analystView;
    private final BlockView blockView;

    // SK
    //private final BlockTableModel blockTableModel;
    //private final JTable blockTable;
    //private int bid;
    //private final BlockInfoPanel blockInfoPanel;
    /**
     * structure the view
     */
    private final JSplitPane split1;
    private final JSplitPane split2;
    private final JSplitPane split3;
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public SchedularMainWindow() {

        // get the time range of all the data
        startDate = SeisEventsDAO.retrieveStartDate();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);

        endDate = SeisEventsDAO.retrieveEndDate();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        VBASLogger.logDebug("startDate=" + startDate + "endDate=" + endDate);

        // Monitor dimension - in multimonitor setup
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        this.setSize(width, height);
        this.setLayout(new BorderLayout());

        /*
         * Views
         */
        
        // block table
        blockTable = new BlockTable(taskBlockList, selectedTaskBlock, seisEventList, selectedSeisEventList);
        //taskBlockList.addObserver(blockTable);
        seisEventList.addObserver(blockTable);

        // small map
        mapPanel = new MapPanel(selectedSeisEventList, true);
        mapPanel.setMapSize(height / 3);
        mapPanel.removeMouseListener(mapPanel);
        selectedSeisEventList.addObserver(mapPanel);

        // block text
        blockTextPanel = new BlockTextPanel(selectedTaskBlock);
        selectedTaskBlock.addObserver(blockTextPanel);

        // control panel for, e.g., New, Email
        blockControlPanel = new BlockControlPanel(startDate, endDate, seisEventList);

        // Bar chart - Analyst view
        ArrayList<Analyst> analysts = new ArrayList<>();
        SeisEventsDAO.loadAnslysts(analysts);
        analystView = new AnalystView(analysts, taskBlockList.getTaskBlockList());
        //taskBlockList.addObserver(analystView);
        seisEventList.addObserver(analystView);

        // Bar chart - block view
        blockView = new BlockView(taskBlockList.getTaskBlockList());
        //taskBlockList.addObserver(blockView);
        seisEventList.addObserver(blockView);

        
        // Split1: Table + Map
        split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, blockTable.getScrollBlockTable(), mapPanel);
        //split1.setSize(new Dimension(width/3, height / 3));
        //split1.setDividerLocation(0.7);
        split1.setResizeWeight(0.65);

        // Split2: Split1 + text
        split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, split1, blockTextPanel);
        //split2.setSize(width/2, height / 3); split2.setDividerLocation(0.3);
        split2.setResizeWeight(0.9);

        // Tab: Block + Analyst
        tabbedPane.add("Block Statistics", blockView);
        tabbedPane.add("Analyst Statistics", analystView);

        // Split3: Split2 + Tab
        split3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split2, tabbedPane);

        this.add(blockControlPanel, BorderLayout.PAGE_START);
        this.add(split3, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            System.err.println(VBASLogger.debugAt() + " Here...");
            SchedularMainWindow schedularMainWindow  = new SchedularMainWindow();
            schedularMainWindow.setVisible(true);
            schedularMainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }

}
