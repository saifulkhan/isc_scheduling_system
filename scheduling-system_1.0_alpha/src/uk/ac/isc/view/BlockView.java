package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import org.jfree.text.TextUtilities;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.data.VBASLogger;


public class BlockView extends JPanel implements Observer {

    private final ArrayList<TaskBlock> taskBlockList;

    // every 100 events, 5 pixel is showed
    private double pixelPerUnit = 0.1;
    private int blockHeight = 30;
    private int blockInterval = 60;
    private int maxEventNumber = 0;

    private final Color pColor = new Color(135, 206, 235);
    private final Color sColor = new Color(240, 128, 128);
    private final Color fColor = new Color(34, 139, 34);

    
    public BlockView(ArrayList<TaskBlock> taskBlockList) {
        this.taskBlockList = taskBlockList;
    }
    
    
    @Override
    public void update(Observable o, Object o1) {
        VBASLogger.logDebug("Upadting the BlockView chart...");
        repaint();
    }

    
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Dimension dim = getSize();  // dim of this component, dynamic! 
        Paint savedColor = g2.getPaint();
        Font savedFont = g2.getFont();

        
        for (TaskBlock tb : taskBlockList) {
            if (tb.getEventNumber() > maxEventNumber) {
                maxEventNumber = tb.getEventNumber();
            }
        }

        
        blockHeight = (int) (dim.getHeight() / (taskBlockList.size() * 3 + 2));
        blockInterval = blockHeight * 2;
        if (maxEventNumber > 0 && dim.getWidth() > 300) {
            pixelPerUnit = (double) (dim.getWidth() - 300) / (double) maxEventNumber;
        }


        String dateStartLabel = null;
        String dateEndLabel = null;
        //label start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");

        for (int i = 0; i < taskBlockList.size(); i++) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            double startX = 150;
            double idStartY = (i + 1) * (blockInterval) + (i - 1) * blockHeight;
            double startY = (i + 1) * (blockInterval) + i * blockHeight;

            double width1 = taskBlockList.get(i).getReviewedEventNumber() * pixelPerUnit;
            double width2 = (taskBlockList.get(i).getEventNumber() - taskBlockList.get(i).getReviewedEventNumber()) * pixelPerUnit;
            Rectangle2D blockBar = new Rectangle2D.Double(startX, startY, width1, blockHeight);
            Rectangle2D blockBar2 = new Rectangle2D.Double(startX + width1, startY, width2, blockHeight);

            //label the block ID first on top of the bar
            String label = "Block ID " + taskBlockList.get(i).getBlockID();
            TextUtilities.drawRotatedString(label, g2, (int) startX, (int) idStartY, org.jfree.ui.TextAnchor.CENTER_LEFT, 0, org.jfree.ui.TextAnchor.CENTER);

            if ("Done".equals(taskBlockList.get(i).getStatus())) {
                width1 = taskBlockList.get(i).getEventNumber() * pixelPerUnit;
                blockBar = new Rectangle2D.Double(startX, startY, width1, blockHeight);
                g2.setPaint(fColor);
                g2.fill(blockBar);
            } else {
                /*draw reviewed event bar*/

                if ("P".equals(taskBlockList.get(i).getStatus())) {
                    dateStartLabel = sdf.format(taskBlockList.get(i).getPPlanStartDay());
                    dateEndLabel = sdf.format(taskBlockList.get(i).getPPlanEndDay());
                    g2.setPaint(pColor);
                } else if ("S".equals(taskBlockList.get(i).getStatus())) {
                    g2.setPaint(sColor);
                    dateStartLabel = sdf.format(taskBlockList.get(i).getSPlanStartDay());
                    dateEndLabel = sdf.format(taskBlockList.get(i).getSPlanEndDay());
                } else if ("F".equals(taskBlockList.get(i).getStatus())) {
                    g2.setPaint(fColor);
                    dateStartLabel = sdf.format(taskBlockList.get(i).getFPlanStartDay());
                    dateEndLabel = sdf.format(taskBlockList.get(i).getFPlanEndDay());
                }
                g2.fill(blockBar);

                /*draw unreviewed event bar*/
                g2.setPaint(new Color(128, 128, 128));
                g2.fill(blockBar2);
            }

            g2.setPaint(savedColor);
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            TextUtilities.drawRotatedString(dateStartLabel, g2, (int) (startX - 50), 
                    (int) (startY + blockHeight / 2), org.jfree.ui.TextAnchor.CENTER, 
                    0, org.jfree.ui.TextAnchor.CENTER);
            
            TextUtilities.drawRotatedString(dateEndLabel, g2, (int) (startX + width1 + width2 + 50), 
                    (int) (startY + blockHeight / 2), org.jfree.ui.TextAnchor.CENTER, 
                    0, org.jfree.ui.TextAnchor.CENTER);

            g2.setFont(savedFont);
        }

    }

}
