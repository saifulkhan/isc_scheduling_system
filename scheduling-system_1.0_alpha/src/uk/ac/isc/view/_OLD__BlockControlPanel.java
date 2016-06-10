
package uk.ac.isc.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;


/**
 * this panel has several buttons to control the information showing
 * in the figures in the blockinfopanel and activate the assignment panel.
 * 
 * @author hui
 */
public class _OLD__BlockControlPanel extends JPanel {
   
    /** Here are the data*/
    private final Date startDate;
    
    private final Date endDate;
    
    private final SeisEventList seisEventList;
    
    /*controllers*/
    private final JButton newButton = new JButton("New");
        
    private final JButton mergeButton = new JButton("Merge");
    
    private final JButton emailButton = new JButton("Email");
    
    private final JLabel breakLabel = new JLabel("            ");
    
    private final JButton splitButton = new JButton("Split");
    
    private final JButton reassignButton = new JButton("Reassign");
    
    //this is only for debug purpose
    private final JButton debugButton = new JButton("Detail Edit");
    
    private final JButton deleteButton = new JButton("Delete");
    
    private final JLabel break2Label = new JLabel("            ");
    
    private final JLabel viewLabel = new JLabel("View:");
    
    private final JRadioButton blockViewButton = new JRadioButton("Blocks");
    
    private final JRadioButton analystViewButton = new JRadioButton("Analysts");
    
    private final ButtonGroup bg = new ButtonGroup();
    
    /*referenct to blockinfo panel for setting the flags*/
    private final _OLD__BlockInfoPanel blockInfoPanel;
    
    public _OLD__BlockControlPanel(Date startDate, Date endDate, SeisEventList seisEventList, _OLD__BlockInfoPanel blockInfoPanel)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.seisEventList = seisEventList;
        
        this.blockInfoPanel = blockInfoPanel;
        
        this.setLayout(new FlowLayout());
        
        newButton.setPreferredSize(new Dimension(160,40));
        mergeButton.setPreferredSize(new Dimension(160,40));
        emailButton.setPreferredSize(new Dimension(160,40));
        splitButton.setPreferredSize(new Dimension(160,40));
        reassignButton.setPreferredSize(new Dimension(160,40));
        
        //debugButton
        debugButton.setPreferredSize(new Dimension(160,40));
        
        deleteButton.setPreferredSize(new Dimension(160,40));
        
        this.add(newButton);
        this.add(mergeButton);
        this.add(emailButton);
        
        this.add(breakLabel);
        
        this.add(splitButton);
        this.add(reassignButton);
        this.add(debugButton);
        this.add(deleteButton);
        
        /*start addding action listener for the four buttons*/
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AssignPanel amp = new AssignPanel(startDate, endDate);
                String[] options = {"OK"};
                UIManager.put("OptionPane.minimumSize", new Dimension(1400,850));
                int result = JOptionPane.showOptionDialog(null, amp, "Create Blocks by grouping unassigned events", 
                        JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
                
                //if the assign window is closed, change the selection states of the events
                //if (result == JOptionPane.OK_OPTION) {
                 
                    for(SeisEvent se:seisEventList.getSeisEventList())
                    {
                        se.setTSelection(true);
                        se.setGSelection(false);
                    }
                    
                    blockInfoPanel.getBlockTableModel().reload();
                    SeisEventsDAO.retrieveBlockEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                    SeisEventsDAO.retrieveBlockReviewedEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                    
                    //biPanel.getBlockTableModel().fireTableDataChanged();
                    blockInfoPanel.getBlockTable().setModel(blockInfoPanel.getBlockTableModel());
                    
                    if(blockInfoPanel.getBlockTableModel().getTaskBlockArray().size()>0)
                    {
                        blockInfoPanel.getBlockTable().setRowSelectionInterval(0, 0);
                        //int bid = (Integer) blockInfoPanel.getBlockTable().getValueAt(0, 0);
                            //reload the phase number
                        for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                        {
                            tb.setPhaseNumber(0);
                        }
        
                        for(SeisEvent se:seisEventList.getSeisEventList())
                        {
            
                            if(se.getBlockID()!=null)
                            {
                                for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                                {
                                    if(tb.getBlockID().equals(se.getBlockID()))
                                    {
                                        tb.setPhaseNumber(tb.getPhaseNumber()+se.getPhaseNumber());
                                    }
                                }
                
                            }
                        }
                    }
                    else
                    {
                        return;
                    }
                    
                    blockInfoPanel.getBlockTable().updateUI();
                    
                    blockInfoPanel.getAnalystView().updateBlocks();
                    blockInfoPanel.getBlockView().repaint();
                    //biPanel.getBlockTable().updateUI();
                    //seList.setChangeFlag();
                //}
            }
        });
        
        mergeButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(blockInfoPanel.getBlockTableModel().getTaskBlockArray().size()<2)
                {
                    return;
                }
                
                String mergeIDString = JOptionPane.showInputDialog(this, "Please input the Block ID with which the current block merges to:");
                
                Integer mergeID = null;
                boolean mergeSuccessFlag = false;
                
                String currBlockStatus = null;
                String mergeBlockStatus = null;
                
                if(mergeIDString != null)
                {
                    try {
                       mergeID = Integer.parseInt(mergeIDString);
                    } catch(NumberFormatException nfe)
                    {
                        JOptionPane.showMessageDialog(null,"Please input correct Block ID!");
                        return;
                    }
                    
                    if(mergeID.equals(blockInfoPanel.getSBid()))
                    {
                        JOptionPane.showMessageDialog(null,"The merged block ID is the same with current ID");
                        return;
                    }
                    
                    boolean existFlag = false;
                    for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                    {
                        if(tb.getBlockID().equals(blockInfoPanel.getSBid()))
                        {
                            currBlockStatus = tb.getStatus();
                        }
                        
                        if(tb.getBlockID().equals(mergeID))
                        {
                            existFlag = true;
                            mergeBlockStatus = tb.getStatus();
                        }
                    }
                    
                    if(existFlag == false)
                    {
                        JOptionPane.showMessageDialog(null,"The merged block ID cannot be found!");
                        return;
                    }
                    else
                    {
                        if(!currBlockStatus.equals(mergeBlockStatus))
                        {
                            JOptionPane.showMessageDialog(null,"The status of the merged block is not the same as the status of current block!");
                            return;
                        }
                        
                        /*will revisit the merge function again later*/
                        //mergeSuccessFlag = SeisEventsDAO.mergeBlock(blockInfoPanel.getSBid(),mergeID);
                    
                        if(mergeSuccessFlag == true)
                        {
                            JOptionPane.showMessageDialog(null, "Merge Succeeds!");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Merge function not been implemented yet!");
                        }
                    }
                    
                }
            }
        
        });
        
        emailButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                EmailPanel ePanel = new EmailPanel();
                 UIManager.put("OptionPane.minimumSize", new Dimension(250,600));               
                int result = JOptionPane.showConfirmDialog(null, ePanel, "Sending Message",JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    //System.out.println("Sending messages to "+ (String)analystList.getSelectedItem());
                    String host = "192.168.37.85";
                    //Map<String, String> env = System.getenv();
                    //String from = env.get("PGUSER");
                    String from = "Hui";
                    String to = ePanel.getRecipient();
                    
                    Properties properties = System.getProperties();
                    properties.setProperty("mail.smtp.host", host);  
                    Session session = Session.getDefaultInstance(properties);  
                    try{
                        // Create a default MimeMessage object.
                        MimeMessage message = new MimeMessage(session);

                        // Set From: header field of the header.
                        message.setFrom(new InternetAddress(from));

                        // Set To: header field of the header.
                         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));

                        // Set Subject: header field
                        message.setSubject(ePanel.getTopic());

                        // Now set the actual message
                        message.setText(ePanel.getMessage());

                        // Send message
                        Transport.send(message);
                        //System.out.println("Sent message successfully....");
                    }catch (MessagingException mex) {
                         mex.printStackTrace();
                    }
                }
            }
        });
        
        //for the split function now, we only work on to split unreviewed events into a new block        
        splitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Integer bid = blockInfoPanel.getSBid();
                
                if(bid == null)
                {
                    return;
                }
                
                TaskBlock old_tb = null;
                for(int i = 0; i<blockInfoPanel.getBlockTableModel().getTaskBlockArray().size();i++)
                {
                    if(Objects.equals(blockInfoPanel.getBlockTableModel().getTaskBlockArray().get(i).getBlockID(), bid))
                    {
                        old_tb = blockInfoPanel.getBlockTableModel().getTaskBlockArray().get(i);
                    }
                }    
                    
                if(old_tb.getStatus()=="P")
                {
                int result = JOptionPane.showConfirmDialog(null, "Would you like to split the current block based on the review status of the events?");
                    
                if(result == JOptionPane.OK_OPTION)
                {
                    /*get the block id from database first*/
                    int alloc_id = SeisEventsDAO.retrieveNewBlockID();   
                    
                    if(old_tb.getReviewedEventNumber()>0)
                    {
                        SeisEventsDAO.retrieveReviewedEvent(seisEventList.getSeisEventList(), bid);
                        
                        //get a new taskblock and set all the attributes to some value which are the same to the old taskblock
                        TaskBlock new_tb = new TaskBlock(alloc_id);
                        new_tb.setStartDay(old_tb.getStartDay());
                        new_tb.setEndDay(old_tb.getEndDay());
                        
                        new_tb.setAnalyst1(old_tb.getAnalyst1());
                        new_tb.setAnalyst1ID(old_tb.getAnalyst1ID());
                        new_tb.setAnalyst2(old_tb.getAnalyst2());
                        new_tb.setAnalyst2ID(old_tb.getAnalyst2ID());
                        new_tb.setAnalyst3(old_tb.getAnalyst3());
                        new_tb.setAnalyst3ID(old_tb.getAnalyst3ID());
                    
                        new_tb.setPPlanStartDay(old_tb.getPPlanStartDay());
                        new_tb.setPPlanEndDay(old_tb.getPPlanEndDay());
                        new_tb.setSPlanStartDay(old_tb.getSPlanStartDay());
                        new_tb.setSPlanEndDay(old_tb.getSPlanEndDay());
                        new_tb.setFPlanStartDay(old_tb.getFPlanStartDay());
                        new_tb.setFPlanEndDay(old_tb.getFPlanEndDay());
                    
                        //int count = 0;
                        for(SeisEvent ev:seisEventList.getSeisEventList())
                        {
                            //System.out.println(ev.getBlockID());
                            //events in the current block but not being reviewed
                            if(Objects.equals(ev.getBlockID(), bid) && ev.getbReviewed()==false)
                            {
                                ev.setBlockID(new_tb.getBlockID());
                                //count++;
                            }
                        }
                        
                        //System.out.println(count);
                        
                        //here bid is the old_bid
                        boolean sc = SeisEventsDAO.splitBlock(seisEventList.getSeisEventList(), new_tb,bid);
                        
                        if(sc == true)
                        {
                            JOptionPane.showMessageDialog(null, "Split Succeeds!");
                        }
                        
                                            
                        blockInfoPanel.getBlockTableModel().reload();
                        SeisEventsDAO.retrieveBlockEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                        SeisEventsDAO.retrieveBlockReviewedEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                    
                        //biPanel.getBlockTableModel().fireTableDataChanged();
                        blockInfoPanel.getBlockTable().setModel(blockInfoPanel.getBlockTableModel());
                    
                        blockInfoPanel.getBlockTable().setRowSelectionInterval(0, 0);
                        //int bid = (Integer) blockInfoPanel.getBlockTable().getValueAt(0, 0);
                            //reload the phase number
                        for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                        {
                            tb.setPhaseNumber(0);
                        }
        
                        for(SeisEvent se:seisEventList.getSeisEventList())
                        {
            
                            if(se.getBlockID()!=null)
                            {
                                for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                                {
                                    if(tb.getBlockID().equals(se.getBlockID()))
                                    {
                                        tb.setPhaseNumber(tb.getPhaseNumber()+se.getPhaseNumber());
                                    }
                                }
                
                            }
                        }
                    
                        blockInfoPanel.getBlockTable().updateUI();
                    
                        blockInfoPanel.getAnalystView().updateBlocks();
                        blockInfoPanel.getBlockView().repaint();
                    }
                    
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"The Status of the block need be Primary for splitting");
                }
            }
        
        });
        
        reassignButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
               Integer bid = blockInfoPanel.getSBid();
               
               String str = "Reassign the block " + bid + " to other anaylsts.";
               //the list is not empty
               if(bid != null)
               {
                   
                    AssignDialogPanel myPanel = new AssignDialogPanel(str);
                                                
                    int result = JOptionPane.showConfirmDialog(null, myPanel, "Reassign ",JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        TaskBlock tb = new TaskBlock(bid);
                        tb.setAnalyst1(myPanel.getAnalyst1Name());
                        tb.setAnalyst1ID(myPanel.getAnalyst1ID());
                        tb.setAnalyst2(myPanel.getAnalyst2Name());
                        tb.setAnalyst2ID(myPanel.getAnalyst2ID());
                        tb.setAnalyst3(myPanel.getAnalyst3Name());
                        tb.setAnalyst3ID(myPanel.getAnalyst3ID());
                    
                        tb.setPPlanStartDay(myPanel.getPPlanStartDate());
                        tb.setPPlanEndDay(myPanel.getPPlanEndDate());
                        tb.setSPlanStartDay(myPanel.getSPlanStartDate());
                        tb.setSPlanEndDay(myPanel.getSPlanEndDate());
                        tb.setFPlanStartDay(myPanel.getFPlanStartDate());
                        tb.setFPlanEndDay(myPanel.getFPlanEndDate());
                    
                        SeisEventsDAO.updateBlock(tb);
                        
                        blockInfoPanel.getBlockTableModel().reload();
                        
                        blockInfoPanel.getBlockTable().updateUI();
                        blockInfoPanel.getAnalystView().updateBlocks();
                        blockInfoPanel.getBlockView().repaint();
                    }
                }
            }
        
        });
       
        deleteButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

                int bid = blockInfoPanel.getSBid();
                
                UIManager.put("OptionPane.minimumSize", new Dimension(50,50));
                //System.out.println(blockInfoPanel.getBlockStatus());
                if("S".equals(blockInfoPanel.getBlockStatus())|| "F".equals(blockInfoPanel.getBlockStatus()))
                {
                    JOptionPane.showMessageDialog(null,
                        "The block has been reviewed which cannot be deleted.\n"
                                + "Please contact with the data administrator for more options",
                            "Inane error",
                    JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    int result = JOptionPane.showConfirmDialog(null, "Do you really mean to delete the selected block?");
                    
                    if(result == JOptionPane.OK_OPTION)
                    {
                        SeisEventsDAO.deleteBlock(bid);
                        
                        //remove the block from the list
                        //for(int i = 0; i<biPanel.getBlockTableModel().getTaskBlockArray().size(); i++)
                        //{
                        //    if(bid == blockInfoPanel.getBlockTableModel().getTaskBlockArray().get(i).getBlockID())
                        //    {
                        //        blockInfoPanel.getBlockTableModel().getTaskBlockArray().remove(i);
                        //    }
                        //}
                        //biPanel.getBlockTableModel().fireTableRowsDeleted(bid, bid);
                        //should update the views by reload the table model?
                        blockInfoPanel.getBlockTableModel().reload();
                        SeisEventsDAO.retrieveBlockEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                        SeisEventsDAO.retrieveBlockReviewedEventNumber(blockInfoPanel.getBlockTableModel().getTaskBlockArray());
                        blockInfoPanel.getBlockTable().setModel(blockInfoPanel.getBlockTableModel());
                        
                        if(blockInfoPanel.getBlockTableModel().getTaskBlockArray().size()>0)
                        {
                            //reload the phase number
                            for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                            {
                                tb.setPhaseNumber(0);
                            }
        
                            for(SeisEvent se:seisEventList.getSeisEventList())
                            {
            
                                if(se.getBlockID()!=null)
                                {
                                    for(TaskBlock tb:blockInfoPanel.getBlockTableModel().getTaskBlockArray())
                                    {
                                        if(tb.getBlockID().equals(se.getBlockID()))
                                        {
                                            tb.setPhaseNumber(tb.getPhaseNumber()+se.getPhaseNumber());
                                        }
                                    }
                
                                }
                            }
                        }
                        else
                        {
                            return;
                        }
                                                
                        for(SeisEvent se:seisEventList.getSeisEventList())
                        {
                            if(se.getBlockID()!=null && se.getBlockID()==bid)
                            {
                                se.setblAssigned(false);
                                se.setBlockID(null);
                            }
                        }
                        
                        JOptionPane.showMessageDialog(null, "The selected block is deleted!");
                    }
                    //seList.setChangeFlag();
                    blockInfoPanel.getBlockTable().updateUI();
                    
                    blockInfoPanel.getAnalystView().updateBlocks();
                    blockInfoPanel.getBlockView().repaint();
                    
                    if(blockInfoPanel.getBlockTableModel().getTaskBlockArray().size()>0)
                    {
                        blockInfoPanel.getBlockTable().setRowSelectionInterval(0, 0);
                        bid = (int)blockInfoPanel.getBlockTable().getValueAt(0, 0);
                    }
                    
                }
            }
        });
        
        blockViewButton.setSelected(true);
        bg.add(blockViewButton);
        bg.add(analystViewButton);
        
        blockViewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                blockInfoPanel.setViewFlag(0);
                //blockViewButton.setSelected(true);
            }
        });
        
        analystViewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                blockInfoPanel.setViewFlag(1);
                //analystViewButton.setSelected(true);
            }
        });
        
        this.add(break2Label);
        this.add(viewLabel);
        this.add(blockViewButton);
        this.add(analystViewButton);   
        
    }
    
}
