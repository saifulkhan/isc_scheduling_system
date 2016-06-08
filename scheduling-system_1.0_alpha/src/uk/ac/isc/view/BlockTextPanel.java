package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.VBASLogger;

/**
 *
 * @author hui
 */
public class BlockTextPanel extends JPanel implements Observer {

    private TaskBlock selectedTaskBlock;

    private final TextArea blockText;
    private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public BlockTextPanel(TaskBlock tb) {

        this.selectedTaskBlock = tb;

        blockText = new TextArea();
        this.setLayout(new BorderLayout());
        this.add(blockText, BorderLayout.CENTER);
        
        this.updateText();
    }

    @Override
    public void update(Observable o, Object o1) {
        VBASLogger.logDebug("Update text...");
        updateText();
    }

    void updateText() {
        
        String text;
        if(selectedTaskBlock.getBlockID() <= 0) {
                    text = "No Taskblock Assigned Yet!";
        } else {

             text = "The Block " + selectedTaskBlock.getBlockID() + ":\n"
                + "The total event number is: " + selectedTaskBlock.getEventNumber() + "\n"
                + "The total phase number is: " + selectedTaskBlock.getPhaseNumber() + "\n"
                + "The current status is in " + selectedTaskBlock.getStatus() + " Stage.\n"
                + "The primary analyst is: " + selectedTaskBlock.getAnalyst1() + "\n"
                + "The planned review date of the P stage is from" + df.format(selectedTaskBlock.getPPlanStartDay())
                + " to " + df.format(selectedTaskBlock.getPPlanEndDay()) + "\n"
                + "The secondary analyst is: " + selectedTaskBlock.getAnalyst2() + "\n"
                + "The planned review date of the S stage is from" + df.format(selectedTaskBlock.getSPlanStartDay())
                + " to " + df.format(selectedTaskBlock.getSPlanEndDay()) + "\n"
                + "The final check analyst is: " + selectedTaskBlock.getAnalyst3() + "\n"
                + "The planned review date of the F stage is from" + df.format(selectedTaskBlock.getFPlanStartDay())
                + " to " + df.format(selectedTaskBlock.getFPlanEndDay());
        }
        //VBASLogger.logDebug("text:" + text);
        
        blockText.repaint();
        blockText.setText(text);
      }

}
