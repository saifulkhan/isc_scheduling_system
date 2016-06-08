
package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.VBASLogger;


public class AssignMainPanel extends JPanel {
    
     /*save all the events info assigned or unassigned*/
    private final SeisEventList allEvents;
    
    /*get the range of showing the data*/
    private final Date startDate;
    private final Date endDate;
    
    private final TimelinePanel timelinePanel;
    private final MapPanel mapPanel;
    private final AssignControlPanel assignControlPanel;
    
    /**structure panes*/
    private final JSplitPane split1;
    
    public AssignMainPanel(Date sDate, Date eDate, SeisEventList seList)
    {
        this.allEvents = seList;
        this.startDate = sDate;
        this.endDate = eDate;
        
        //VBASLogger.logDebug("startDate=" + startDate + "endDate=" + endDate);
        
        // create three panels 
        timelinePanel = new TimelinePanel(startDate,endDate,allEvents);
        mapPanel = new MapPanel(allEvents, false);
        assignControlPanel = new AssignControlPanel(timelinePanel,mapPanel,startDate,endDate,allEvents);
        
        split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, timelinePanel,mapPanel);
        split1.setSize(1400,850);
        split1.setDividerLocation(0.4d);
        
        this.setSize(1400,1000);
        this.setLayout(new BorderLayout());
        this.add(assignControlPanel,BorderLayout.NORTH);
        this.add(split1,BorderLayout.CENTER);
        
        allEvents.addObserver(timelinePanel);
        allEvents.addObserver(mapPanel);
    }
}
