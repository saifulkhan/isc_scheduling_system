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
import uk.ac.isc.data.GlobalStorage;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.TaskBlockList;
import uk.ac.isc.data.VBASLogger;
import uk.ac.isc.view.AnalystView;
import uk.ac.isc.view.AssignPanel;
import uk.ac.isc.view.BlockControlPanel;
import uk.ac.isc.view.BlockTable;
import uk.ac.isc.view.BlockTextPanel;
import uk.ac.isc.view.BlockView;
import uk.ac.isc.view.MapPanel;


public class SchedularMainWindow extends JFrame {

    private final SeisEventList seisEventList = GlobalStorage.getSeisEventList();
    private final SeisEventList selectedSeisEventList = GlobalStorage.getSelectedSeisEventList();
    private final TaskBlockList taskBlockList = GlobalStorage.getTaskBlockList();
    private final TaskBlock selectedTaskBlock = GlobalStorage.getSelectedTaskBlock();
    
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
    private final AssignPanel assignPanel;

    public SchedularMainWindow() {

        this.setTitle("Scheduling System");
        // get the time range of all the data
        startDate = SeisEventsDAO.retrieveStartDate();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);

        endDate = SeisEventsDAO.retrieveEndDate();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);
        
        GlobalStorage.loadData();
            
        // Monitor dimension - in multimonitor setup
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        this.setSize(width, height);
        this.setLayout(new BorderLayout());

        /*
         * Views
         */
        
        VBASLogger.logDebug("@seisEventList :" + seisEventList);
        VBASLogger.logDebug("#seisEventList: " + seisEventList.getSeisEventList().size());
        VBASLogger.logDebug("@selectedSeisEventList:" + selectedSeisEventList);
        VBASLogger.logDebug("#selectedSeisEventList: " + selectedSeisEventList.getSeisEventList().size());

        
        // Assign panel
        assignPanel = new AssignPanel(startDate, endDate);
        
        // block table
        blockTable = new BlockTable(seisEventList, selectedSeisEventList, taskBlockList, selectedTaskBlock);
        seisEventList.addObserver(blockTable);
        
        // small map
        mapPanel = new MapPanel(selectedSeisEventList, true);
        mapPanel.setMapSize((int) (height * 0.3));
        mapPanel.removeMouseListener(mapPanel); // we need mouse interaction only for the assign map
        selectedSeisEventList.addObserver(mapPanel);
        
        // block text
        blockTextPanel = new BlockTextPanel(selectedTaskBlock);
        selectedTaskBlock.addObserver(blockTextPanel);
        
        // control panel for, e.g., Refresh, Email
        blockControlPanel = new BlockControlPanel(startDate, endDate, seisEventList);

        // Bar chart - Analyst view
        ArrayList<Analyst> analysts = new ArrayList<>();
        SeisEventsDAO.loadAnslysts(analysts);
        analystView = new AnalystView(analysts, taskBlockList.getTaskBlockList());
        taskBlockList.addObserver(analystView);

        // Bar chart - block view
        blockView = new BlockView(taskBlockList.getTaskBlockList());
        taskBlockList.addObserver(blockView);

        
        JSplitPane split0 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, blockTextPanel, mapPanel);
        split0.setResizeWeight(0.5);

        
        // Split1:  
        JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, assignPanel, split0);
        split1.setResizeWeight(0.8);

        
        // Tab: Block + Analyst
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Block Statistics", blockView);
        tabbedPane.add("Analyst Statistics", analystView);
        
        // Split2: 
        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,  blockTable.getScrollBlockTable(), tabbedPane);
        split2.setResizeWeight(0.1);

        // Split3:
        JSplitPane split3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, split1, split2);
        split3.setResizeWeight(0.4);
        
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
