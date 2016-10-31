package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import uk.ac.isc.data.GlobalStorage;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;

/**
 * this panel has several buttons to control the information showing in the
 * figures in the blockinfopanel and activate the assignment panel.
 *
 *  
 */
public class BlockControlPanel extends JPanel {

    //private final SeisEventList seisEventList = GlobalStorage.getSeisEventList();
 
    private final JButton button_refresh = new JButton("Refresh");
    private final JButton emailButton = new JButton("Email");
    private final JLabel gap1 = new JLabel("  ");

    private final Date startDate;
    private final Date endDate;
    private final SeisEventList seList;
    
    public BlockControlPanel(Date startDate, Date endDate, SeisEventList allEvents) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.seList = allEvents;
        
        
        button_refresh.setPreferredSize(new Dimension(150, 40));
        button_refresh.setFocusPainted(false);
        button_refresh.setBackground(new Color(59, 89, 182));
        button_refresh.setForeground(Color.WHITE);
        button_refresh.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        emailButton.setPreferredSize(new Dimension(150, 40));
        emailButton.setBackground(new Color(59, 89, 182));
        emailButton.setForeground(Color.WHITE);
        emailButton.setFont(new Font("Tahoma", Font.BOLD, 14));

        this.setLayout(new FlowLayout());
        this.add(button_refresh);
        this.add(gap1);
        this.add(emailButton);

        
        /* 
         * Refresh button action listener
         */
        button_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalStorage.loadData();
                
                /*
                AssignPanel amp = new AssignPanel(startDate, endDate);
                String[] options = {"OK"};
                UIManager.put("OptionPane.minimumSize", new Dimension(1400,850));
                int result = JOptionPane.showOptionDialog(null, amp, "Create Blocks by grouping unassigned events", 
                        JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
                
                //if the assign window is closed, change the selection states of the events
                //if (result == JOptionPane.OK_OPTION) {
                 
                    for(SeisEvent se : seList.getSeisEventList())
                    {
                        se.setTSelection(true);
                        se.setGSelection(false);
                    }
                    */
                
            }
        });

        emailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                EmailPanel ePanel = new EmailPanel();
                UIManager.put("OptionPane.minimumSize", new Dimension(250, 600));
                int result = JOptionPane.showConfirmDialog(null, ePanel, "Sending Message", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    //System.out.println("Sending messages to "+ (String)analystList.getSelectedItem());

                    String host = "192.168.37.85";
                    //Map<String, String> env = System.getenv();
                    //String from = env.get("PGUSER");
                    String from = "Saiful";
                    String to = ePanel.getRecipient();

                    Properties properties = System.getProperties();
                    properties.setProperty("mail.smtp.host", host);
                    Session session = Session.getDefaultInstance(properties);
                    try {
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
                    } catch (MessagingException mex) {
                        mex.printStackTrace();
                    }
                }
            }
        });

    }

}
