package uk.ac.isc.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
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
    private int barH = 30;
    private int blockInterval = 60;
    private int maxEventNumber = 0;

    private final Color pColor = new Color(135, 206, 235);
    private final Color sColor = new Color(240, 128, 128);
    private final Color fColor = new Color(34, 139, 34);
    private final Color dColor = Color.WHITE;

    private final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");

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
        this.setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g.create();
        Dimension dim = getSize();  // dynamically set the dimension of this component.
        Paint savedColor = g2.getPaint();
        Font savedFont = g2.getFont();

        int gapX = 100;
        int gapY = 50;
        int offset1 = 10;

        for (TaskBlock tb : taskBlockList) {
            if (tb.getEventNumber() > maxEventNumber) {
                maxEventNumber = tb.getEventNumber();
            }
        }

        VBASLogger.logDebug("Barchart: height=" + dim.getHeight() + ", width=" + dim.getWidth());
        //blockHeight = (int) (dim.getHeight() / (taskBlockList.size() * 3 + 2));
        //blockInterval = barH * 2;

        barH = ((int) dim.getHeight() - (gapY * taskBlockList.size() + 2 * gapY))
                / taskBlockList.size();

        // TODO: check this?
        if (maxEventNumber > 0 && dim.getWidth() > 300) {
            pixelPerUnit = (double) (dim.getWidth() - 300) / (double) maxEventNumber;
        }

        /*
         * draw the scale
         */
        int evNo = 100;
        double xS = gapX;
        double yS = gapY;

        g2.setPaint(Color.GRAY);

        // x-axis
        //g2.draw(new Line2D.Double(xS, yS,dim.getWidth(), yS));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        for (int i = 0; i <= (int) (maxEventNumber / evNo); ++i) {
            //VBASLogger.logDebug("maxEventNumber=" + maxEventNumber + ", i=" + i);
            float[] dashingPattern2 = {10f, 4f};
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 1.0f, dashingPattern2, 0.0f));
            g2.draw(new Line2D.Double(xS, yS, xS, dim.getHeight() - gapY));

            if (i == 0) {
                TextUtilities.drawRotatedString("Events", g2,
                        (int) xS - offset1, (int) dim.getHeight() - gapY / 2,
                        org.jfree.ui.TextAnchor.CENTER_RIGHT, 0, org.jfree.ui.TextAnchor.CENTER);
            }

            String label = String.valueOf((i * evNo));
            TextUtilities.drawRotatedString(label, g2,
                    (int) xS, (int) dim.getHeight() - gapY / 2,
                    org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);

            xS = evNo * pixelPerUnit + xS;
        }

        /*
         * draw the bars
         */
        String dateStartLabel = null;
        String dateEndLabel = null;

        double xB = gapX;
        double yB = gapY;

        g2.setFont(new Font("Monospaced", Font.BOLD, 12));

        for (int i = 0; i < taskBlockList.size(); i++) {

            //VBASLogger.logDebug("yB=" + yB + ", barH=" + barH + ", gapY=" + gapY); 
            double width1 = taskBlockList.get(i).getReviewedEventNumber() * pixelPerUnit;
            double width2 = (taskBlockList.get(i).getEventNumber()
                    - taskBlockList.get(i).getReviewedEventNumber()) * pixelPerUnit;

            Rectangle2D reviewedBar = new Rectangle2D.Double(xB, yB, width1, barH);
            Rectangle2D unreviewedBar = new Rectangle2D.Double(xB + width1, yB, width2, barH);

            // label: block ID
            g2.setPaint(Color.GRAY);
            String label = "Block:" + taskBlockList.get(i).getBlockID();
            TextUtilities.drawRotatedString(label, g2, (int) xB - offset1, (int) yB,
                    org.jfree.ui.TextAnchor.CENTER_RIGHT, 0, org.jfree.ui.TextAnchor.BOTTOM_CENTER);

            // if the block is done, draw it white
            if ("Done".equals(taskBlockList.get(i).getStatus())) {
                width1 = taskBlockList.get(i).getEventNumber() * pixelPerUnit; // the whole bar          
                reviewedBar = new Rectangle2D.Double(xB, yB, width1, barH);
                g2.setPaint(Color.GRAY);
                g2.fill(reviewedBar);

            } else {
                // draw reviewed event bar
                if ("P".equals(taskBlockList.get(i).getStatus())) {
                    g2.setPaint(pColor);
                } else if ("S".equals(taskBlockList.get(i).getStatus())) {
                    g2.setPaint(sColor);
                } else if ("F".equals(taskBlockList.get(i).getStatus())) {
                    g2.setPaint(fColor);
                }
                g2.fill(reviewedBar);

                // draw unreviewed event bar
                g2.setPaint(Color.BLACK);
                g2.fill(unreviewedBar);
            }

            g2.setPaint(Color.GRAY);
            dateStartLabel = df.format(taskBlockList.get(i).getPPlanStartDay());
            dateEndLabel = df.format(taskBlockList.get(i).getPPlanEndDay());

            TextUtilities.drawRotatedString(dateStartLabel, g2,
                    (int) (xB - offset1), (int) (yB + barH / 2),
                    org.jfree.ui.TextAnchor.CENTER_RIGHT, 0, org.jfree.ui.TextAnchor.CENTER);

            TextUtilities.drawRotatedString(dateEndLabel, g2,
                    (int) (xB + width1 + width2 + offset1), (int) (yB + barH / 2),
                    org.jfree.ui.TextAnchor.CENTER_LEFT, 0, org.jfree.ui.TextAnchor.CENTER);

            TextUtilities.drawRotatedString("Phases:" + String.valueOf(taskBlockList.get(i).getPhaseNumber()), g2,
                    (int) (xB + width1 + width2 + offset1), (int) (yB),
                    org.jfree.ui.TextAnchor.CENTER_LEFT, 0, org.jfree.ui.TextAnchor.CENTER);

            yB = yB + barH + gapY;
        }

    }

}
