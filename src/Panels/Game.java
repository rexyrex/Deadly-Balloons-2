package Panels;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Utils.EncryptUtils;
import Utils.FileUtils;
public class Game {
	
	public static void initFiles(Path path, SecretKey sk) {
//		try (PrintWriter out = new PrintWriter(path.toString())) {
//			SecretKey sk = EncryptUtils.generateNewSecretKey();
//			byte[] res = EncryptUtils.encrypt("init", sk);
//		    out.println(res);
//		    
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		//SecretKey sk = EncryptUtils.generateNewSecretKey();
		byte[] res = EncryptUtils.encrypt("init", sk);

		
		try (FileOutputStream fos = new FileOutputStream(path.toString())) {
			   fos.write(res);
			   //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void main(String args[]){
		Path skPath = Paths.get("sk.rex");
		EncryptUtils.generateSecretKeyFile(skPath);
		SecretKey sk = EncryptUtils.readSecretKeyFile(skPath);
		Path path = Paths.get("uf.rex");
		File userFile = new File(path.toString());
		if(!userFile.exists()) {
			System.out.println("Game Files Corrupted");
			JOptionPane.showMessageDialog(null, "Game Files Corrupted");
			return;
		}
		
		try (PrintWriter out = new PrintWriter(path.toString())) {
		    out.println("herro");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		initFiles(path,sk);
		
		byte[] test = FileUtils.readBytes(path);
		
		System.out.println(EncryptUtils.decrypt(test, sk));
		
		String resultStr = "";
		
		while(resultStr.length() < 3) {
			resultStr = JOptionPane.showInputDialog("Enter Username (One time process)\n[At Least 3 Characters Long]");
		}
		

		
		
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
