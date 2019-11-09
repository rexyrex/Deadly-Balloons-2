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
	
	public static void saveUserName(Path path, SecretKey sk, String userName) {
		byte[] res = EncryptUtils.encrypt(userName, sk);
		try (FileOutputStream fos = new FileOutputStream(path.toString())) {
		   fos.write(res);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		Path skPath = Paths.get("sk.rex");
		File skFile = new File(skPath.toString());
		Path path = Paths.get("uf.rex");
		File userFile = new File(path.toString());
		
		boolean resetNick = !skFile.exists() || !userFile.exists();
		

		if(!skFile.exists()) {
			EncryptUtils.generateSecretKeyFile(skPath);
		}
		SecretKey sk = EncryptUtils.readSecretKeyFile(skPath);
		
		String resultStr = "";
		if(resetNick) {
			while(resultStr.length() < 3 || resultStr.length() > 8) {
				resultStr = JOptionPane.showInputDialog("Enter Username (One time process)\n[3~8 Characters Long]");
				if(resultStr==null) {
					System.exit(1);
				}
			}
			saveUserName(path,sk,resultStr);
		}				
				
		byte[] usernameBytes = FileUtils.readBytes(path);
		String username = EncryptUtils.decrypt(usernameBytes, sk);		
		System.out.println("Username: " + username);
		
		final JFrame window = new JFrame("Rex Shooter");
		
		window.setLayout(new BorderLayout());

		ShopPanel sPanel = new ShopPanel();
		
		InfoPanel iPanel = new InfoPanel();
				
		GamePanel gp = new GamePanel(window, sPanel, iPanel, username);		
		
		window.add(gp, BorderLayout.CENTER);
		window.add(iPanel, BorderLayout.WEST);
		window.add(sPanel, BorderLayout.EAST);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

	}
	
}
