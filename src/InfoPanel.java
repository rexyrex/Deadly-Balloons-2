import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
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

public class InfoPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private boolean showInstructions;
	String statsContents[][];
	JTable table2;
	
	public InfoPanel() {
		super();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(300, 700));
		showInstructions = false;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
        //add(scrollPane);
        
        
        
        instructionToggleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showInstructions = !showInstructions;
				if(showInstructions) {
					//InstructionsPanel.this.add(scrollPane);
					scrollPane.setPreferredSize(new Dimension(300,700));
					InfoPanel.this.setPreferredSize(new Dimension(300,GamePanel.HEIGHT));
					scrollPane.setVisible(true);
				} else {
					//InstructionsPanel.this.remove(scrollPane);
					scrollPane.setPreferredSize(new Dimension(10,700));
					InfoPanel.this.setPreferredSize(new Dimension(10,700));
					scrollPane.setVisible(false);
					
				}
			}
		});
	}
	
	public void updateStats() {
		//System.out.println("updating stats");
		statsContents[0][0] = "Wave";
		statsContents[0][1] = String.valueOf(GamePanel.waveNumber);
		statsContents[1][0] = "Bullet Damage";
		statsContents[1][1] = String.format("%.2f", GamePanel.player.getbulletDmg());
		statsContents[2][0] = "Missile Damage";
		statsContents[2][1] = String.format("%.2f", GamePanel.player.getSideMissileDmg());
		statsContents[3][0] = "Turret Damage";
		statsContents[3][1] = String.format("%.2f", GamePanel.player.getTurretDmg());
		statsContents[4][0] = "Bomb Damage";
		statsContents[4][1] = String.format("%.2f", GamePanel.player.getBombDmg());
		statsContents[5][0] = "Add-on Damage";
		statsContents[5][1] = String.format("%.2f", GamePanel.player.getAddonDmg());
		statsContents[6][0] = "Spaz Duration";
		statsContents[6][1] = String.valueOf(GamePanel.player.getSpazDuration())+ " ms";
		statsContents[7][0] = "Missile Duration";
		statsContents[7][1] = String.valueOf(GamePanel.player.getSideMissileDuration()) + " ms";		
		for(int i=0; i<8; i++) {
			for(int j=0; j<2; j++) {
				table2.setValueAt(statsContents[i][j], i, j);
			}
		}
	}
	
	public void updateStats2() {
		Runnable runner = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateStats();
			}			
		};
		
		EventQueue.invokeLater(runner);
	}
	
	public void init2() {
		Runnable runner = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				init();
			}			
		};
		
		EventQueue.invokeLater(runner);
	}
	
	public void init() {
		
		//panel.setPreferredSize(new Dimension ( 200,700));
        
		String statsTitle = 
				"<html>" +
				"<b>Stats</b>" + "<br/>" +
				"</html>";
		
		JLabel statsTitleLabel = new JLabel("tmp", SwingConstants.CENTER);
		statsTitleLabel.setForeground(Color.white);
		statsTitleLabel.setText(statsTitle);			
        add(statsTitleLabel);
        
        String statsHeaders[] = {"Key", "Value"};
        statsContents = new String[8][2];
        statsContents[0][0] = "Initializing";
        statsContents[0][1] = "Please wait...";
        
        
        table2 = new JTable(statsContents, statsHeaders);        
        table2.setBackground(Color.black);        
        Font f2 = new Font("Gulim", Font.BOLD, 15);
        table2.setFont(f2);
        table2.setForeground(Color.white);
        table2.getTableHeader().setOpaque(false);
        table2.getTableHeader().setBackground(Color.pink);
        table2.getTableHeader().setForeground(Color.black);
        final JScrollPane scrollPane2 = new JScrollPane(table2);
        scrollPane2.getViewport().setBackground(Color.black);
        add(scrollPane2);

	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.black);
		
	}
}
