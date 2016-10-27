package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import uk.ac.isc.data.GlobalStorage;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.VBASLogger;

public class AssignPanel extends JPanel {

    private static final SeisEventList seisEventList = GlobalStorage.getSeisEventList();

    /*get the range of showing the data*/
    private final Date startDate;
    private final Date endDate;

    private final TimelinePanel timelinePanel;
    private final MapPanel mapPanel;
    private final AssignControlPanel assignControlPanel;

    /**
     * structure panes
     */
    private final JSplitPane split1;

    public AssignPanel(Date sDate, Date eDate) {

        VBASLogger.logDebug("@seisEventList: " + seisEventList);
        VBASLogger.logDebug("#seisEventList: " + seisEventList.getSeisEventList().size());

        this.startDate = sDate;
        this.endDate = eDate;

        // create three panels 
        // time panel
        timelinePanel = new TimelinePanel(seisEventList, startDate, endDate);
        seisEventList.addObserver(timelinePanel);
        // map panel
        mapPanel = new MapPanel(seisEventList, false);
        seisEventList.addObserver(mapPanel);
        // control panel
        assignControlPanel = new AssignControlPanel(timelinePanel, mapPanel, seisEventList, startDate, endDate);

        split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, timelinePanel, mapPanel);
        split1.setResizeWeight(0.4);
        this.setLayout(new BorderLayout());
        this.add(assignControlPanel, BorderLayout.NORTH);
        this.add(split1, BorderLayout.CENTER);
    }
}
