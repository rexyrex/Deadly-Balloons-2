package Panels;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFrame;
public class Game {

	public static void main(String args[]){		
		final JFrame window = new JFrame("Rex Shooter");
		window.setLayout(new BorderLayout());
		//window.setPreferredSize(new Dimension(1500,700));

		//window.setBackground(new Color(0,0,0,0));
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
		

		File userFile = new File("./userFile.txt");
		System.out.println(userFile.exists());
		
		try (PrintWriter out = new PrintWriter("./userFile.txt")) {
		    out.println("herro");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("HS: " + HighScoreUtils.getHighscores("DefaultLevels", "MrYang"));
//		try {
//			//HighScoreUtils.addHighScore("DefaultLevels", "MrYang", "12m 5s","n0");
//			HighScoreUtils.getTopFive("DefaultLevels", "MrYang");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
