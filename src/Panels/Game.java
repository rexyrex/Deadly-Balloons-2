package Panels;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class Game {

	public static void main(String args[]){		
		final JFrame window = new JFrame("Rex Shooter");
		window.setLayout(new BorderLayout());
		//window.setPreferredSize(new Dimension(1500,700));

		
		ShopPanel sPanel = new ShopPanel();
		
		
		InfoPanel iPanel = new InfoPanel();
		
		
		GamePanel gp = new GamePanel(window, sPanel, iPanel);
		
		
		window.add(gp, BorderLayout.CENTER);
		window.add(iPanel, BorderLayout.WEST);
		window.add(sPanel, BorderLayout.EAST);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
	}
	
}
