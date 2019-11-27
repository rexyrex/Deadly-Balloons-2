package Panels;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.crypto.SecretKey;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Utils.EncryptUtils;
import Utils.FileUtils;
import Utils.VersionUtils;
public class Game {
	
	private final static String version = "1.1.1";
	
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
		//test
		String latestVersion = VersionUtils.getLatestVersion();
		//rmv quotes
		latestVersion = latestVersion.substring(1, latestVersion.length()-2);
		
		String patchNotes = VersionUtils.getPatchNotes();
		patchNotes = patchNotes.substring(1, patchNotes.length()-2);
		
		if(!latestVersion.equals(version)){
			JOptionPane.showMessageDialog(null, 
					"Game version is outdated" + 
					"\n\nCurrent Version: "+version + 
					"\nLatest Version: "+ latestVersion + 
					"\n\nPatch notes: " + patchNotes);
			System.exit(1);
			
			try {
				URI uri = new URI("https://www.rexyrex.com/deadlyballoons");
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
			while(resultStr.length() < 3 || resultStr.length() > 8 || !isAllowedNickname(resultStr)) {
				resultStr = JOptionPane.showInputDialog("Enter Username (One time process)\n[3~8 Characters Long]\n[Only English + Numbers]");
				if(resultStr==null) {
					System.exit(1);
				}
			}
			saveUserName(path,sk,resultStr);
		}				
				
		byte[] usernameBytes = FileUtils.readBytes(path);
		String username = EncryptUtils.decrypt(usernameBytes, sk);		
		
		final JFrame window = new JFrame("Deadly Balloons 2");
		
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
	
	public static boolean isAllowedNickname(String name) {
	    return name.matches("[a-zA-Z0-9]+");
	}
	
}
