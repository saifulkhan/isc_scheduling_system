package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.VBASLogger;

/**
 *
 * @author hui
 */
public class BlockTextPanel extends JPanel implements Observer {

    private final TaskBlock selectedTaskBlock;

    private final JTextArea blockText;
    private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public BlockTextPanel(TaskBlock selectedTaskBlock) {

        this.selectedTaskBlock = selectedTaskBlock;

        blockText = new JTextArea();
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

        if (selectedTaskBlock.getBlockID() == null) {
            text = "No Taskblock Assigned Yet!";
        } else {
            text = "Block Number: " + selectedTaskBlock.getBlockID() + "\n"
                    + "Total number of events: " + selectedTaskBlock.getEventNumber() + "\n"
                    + "Total number of phases: " + selectedTaskBlock.getPhaseNumber() + "\n"
                    + "Status of the block: " + selectedTaskBlock.getStatus() + " stage, (P- Primary, S- Secondary, F- Final) \n"
                    + "Primary analyst:" + selectedTaskBlock.getAnalyst1() + "\n"
                    + "Primary review planned from: " + df.format(selectedTaskBlock.getPPlanStartDay())
                    + " to " + df.format(selectedTaskBlock.getPPlanEndDay()) + "\n"
                    + "Secondary analyst: " + selectedTaskBlock.getAnalyst2() + "\n"
                    + "Secondary review planned from: " + df.format(selectedTaskBlock.getSPlanStartDay())
                    + " to " + df.format(selectedTaskBlock.getSPlanEndDay()) + "\n"
                    + "Final analyst: " + selectedTaskBlock.getAnalyst3() + "\n"
                    + "Final review planned from: " + df.format(selectedTaskBlock.getFPlanStartDay());
        }
        //VBASLogger.logDebug("text:" + text);

        blockText.repaint();
        blockText.setText(text);
    }

}
