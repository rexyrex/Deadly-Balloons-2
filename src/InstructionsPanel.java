import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InstructionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	public InstructionsPanel() {
		super();
		setBackground(Color.BLACK);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//panel.setPreferredSize(new Dimension ( 200,700));
		
		String instructions = "Instructions \n"+
							"Z - Fire";
		
		JLabel shopTitleLabel = new JLabel();
		shopTitleLabel.setForeground(Color.white);
		shopTitleLabel.setText(instructions);			
        add(shopTitleLabel);
        

	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.black);
		
	}
}
