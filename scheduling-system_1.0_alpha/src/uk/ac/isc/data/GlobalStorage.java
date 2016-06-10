package uk.ac.isc.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


/*
 * Used globally to register to change event and notify/fire changes.
 * Do not change the actual reference of these objects.
 */
public class GlobalStorage {

    private static final SeisEventList seisEventList = new SeisEventList();//save all assigned/unassigned events    
    private static final TaskBlockList taskBlockList = new TaskBlockList();
    private static final TaskBlock selectedTaskBlock = new TaskBlock(); // BlickTable: Selected block
    private static final SeisEventList selectedSeisEventList = new SeisEventList(); // Selcted events in the selected block

    
    public static void loadData() {
        
        VBASLogger.logDebug("Loading data...");
                
        HashMap<Integer, Integer> allocatedEvID = new HashMap<>();
        HashSet<TaskBlock> blockSet;

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
        
        
        VBASLogger.logDebug("#taskBlockList: " + taskBlockList.getTaskBlockList().size());
        
        VBASLogger.logDebug("@seisEventList :" + seisEventList);
        VBASLogger.logDebug("#seisEventList: " + seisEventList.getSeisEventList().size());
        VBASLogger.logDebug("@selectedSeisEventList:" + selectedSeisEventList);
        VBASLogger.logDebug("#selectedSeisEventList: " + selectedSeisEventList.getSeisEventList().size());
        seisEventList.setChangeFlag();
        seisEventList.notifyObservers();
        taskBlockList.setChangeFlag();
        taskBlockList.notifyObservers();
    }
    
    
    public static void setSelectedTaskBlock(TaskBlock taskBlock) {
        selectedTaskBlock.setTaskBlock(taskBlock);
        selectedTaskBlock.setChangeFlag();
        selectedTaskBlock.notifyObservers();
    }
    
    
    public static void setSelectedSeisEventList(int blockId) {
        selectedSeisEventList.getSeisEventList().clear();

        for (SeisEvent se : seisEventList.getSeisEventList()) {
            if (se.getBlockID() != null && se.getBlockID().equals(blockId)) {
                selectedSeisEventList.getSeisEventList().add(se);
            }
        }
        
        VBASLogger.logDebug("@selectedSeisEventList:" + selectedSeisEventList);
        VBASLogger.logDebug("#selectedSeisEventList: " + selectedSeisEventList.getSeisEventList().size());
        
        selectedSeisEventList.setChangeFlag();
        selectedSeisEventList.notifyObservers();
    }
    
    public static void setSelectedSeisEventList() {
        selectedSeisEventList.getSeisEventList().clear();
        selectedSeisEventList.setChangeFlag();
        selectedSeisEventList.notifyObservers();
    }
          
    
    // Getter
    
    public static SeisEventList getSeisEventList() {
        return seisEventList;
    }

    public static TaskBlockList getTaskBlockList() {
        return taskBlockList;
    }

    public static TaskBlock getSelectedTaskBlock() {
        return selectedTaskBlock;
    }
   
    public static SeisEventList getSelectedSeisEventList() {
        return selectedSeisEventList;
    }

}
