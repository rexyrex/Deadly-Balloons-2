import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class InstructionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private boolean showInstructions;
	public InstructionsPanel() {
		super();
		setBackground(Color.BLACK);
		showInstructions = false;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//panel.setPreferredSize(new Dimension ( 200,700));
		
		String instructions = 
				"<html>" +
				"Instructions" + "<br/>" +
				//"Z - Fire" + "<br/>" +
				//"Z - Fire" + "<br/>" +
				"</html>";
		
		JLabel shopTitleLabel = new JLabel("tmp", SwingConstants.CENTER);
		shopTitleLabel.setForeground(Color.white);
		shopTitleLabel.setText(instructions);			
        add(shopTitleLabel);
        
        String headers[] = {"Key", "Function", "Stamina Cost"};
        String contents[][] = {
        		{"Z", "Shoot", "0"},
        		{"C", "Push Enemy", "600"},
        		{"X", "Place Bomb", "0"},
        		{"A", "Toggle Addon", "0"},
        		{"B", "Place BlackHole", "700"},
        		{"E", "Place Wall", "100"},
        		{"T", "Place Turret", "420"},
        		{"I", "Invincibility", "Constant"},
        		{"1,2,3,4,5", "Teleport to turret", "50"},
        };
        
        
        JTable table = new JTable(contents, headers);        
        table.setBackground(Color.black);
        //table.setOpaque(false);
        
        //scrollPane.getViewport().getView().setBackground(Color.black);
        
        Font f = new Font("Gulim", Font.BOLD, 15);
        table.setFont(f);
        table.setForeground(Color.white);
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(Color.pink);
        table.getTableHeader().setForeground(Color.black);
        //scrollPane.getViewport().getView().setForeground(Color.black);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.black);
        //scrollPane.setBackground(Color.white);
        //scrollPane.setPreferredSize(new Dimension(300,GamePanel.HEIGHT));
        //scrollPane.setOpaque(false);
        
        final JButton instructionToggleBtn = new JButton("Show Controls");
        add(instructionToggleBtn);
        add(scrollPane);
        
        instructionToggleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showInstructions = !showInstructions;
				if(showInstructions) {
					//InstructionsPanel.this.add(scrollPane);
					scrollPane.setPreferredSize(new Dimension(300,GamePanel.HEIGHT));
					scrollPane.setVisible(true);
				} else {
					//InstructionsPanel.this.remove(scrollPane);
					scrollPane.setPreferredSize(new Dimension(10,GamePanel.HEIGHT));
					scrollPane.setVisible(false);
					
				}
			}
		});
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.black);
		
	}
}
