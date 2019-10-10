package Panels;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import VFX.Text;

public class ShopPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<JButton, Integer> itemPurchaseMap;
	HashMap<JButton, Integer> itemBaseCostMap;
	HashMap<JButton, String> itemBaseName;
	HashMap<JButton, Integer> maxPurchaseMap;
	
	long msgTimerDiff;
	long msgDelay;
	long msgStartTimer;

	public ShopPanel(){
		super();
		setBackground(Color.BLACK);
		
		msgTimerDiff = System.nanoTime();
		msgDelay = 2000;
		msgStartTimer = System.nanoTime();
		
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		GridLayout gl = new GridLayout(0,1);
		gl.setVgap(5);
		gl.setHgap(5);
		setLayout(gl);
		setPreferredSize(new Dimension(300, 700));
		//panel.setPreferredSize(new Dimension ( 200,700));
		
		JLabel shopTitleLabel = new JLabel("tmp", SwingConstants.CENTER);
		shopTitleLabel.setForeground(Color.white);
		shopTitleLabel.setFont(new Font("Gulim", Font.BOLD, 20));
		shopTitleLabel.setText("Shop");			
        add(shopTitleLabel);
		
	}
	
	public void init() {
		System.out.println("Shop Panel Init");
		itemPurchaseMap = new HashMap();
		itemBaseCostMap = new HashMap();
		itemBaseName = new HashMap();
		maxPurchaseMap = new HashMap();
		
        final JButton buyLifeBtn = new JButton("<html>Extra Life 0 <br />( Cost : " + 120 + " )</html>");        
        final JButton buyPowerBtn = new JButton("<html>Power Up 0 <br />( Cost : " + 30 + " )</html>");
        final JButton buyAbilityBtn = new JButton("<html>Ability Up 0 <br />( Cost : " + 50 + " )</html>");
        final JButton dropRateBtn = new JButton("<html>Drop Rate 0 <br />( Cost : " + 200 + " )</html>");
        
        initMaps(buyLifeBtn, "Extra Life", 120, 1000);
        initMaps(buyPowerBtn, "Power Up", 30, 1000);
        initMaps(buyAbilityBtn, "Ability Up", 50, 1000);
        initMaps(dropRateBtn, "Drop Rate", 200, 10);
        
        
        dropRateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(dropRateBtn);
				if(checkMaxBought(dropRateBtn)) {
					msgOnGui("Max!",false);
				} else if(GamePanel.player.attemptPurchase(cost)) {
					//droprate 10% increase
					GamePanel.player.incDropRate(0.1);
					//spawn time 2% decrease
					GamePanel.player.incSpawnRate(0.02);
					purchaseProcess(dropRateBtn);
				} else {
					msgOnGui("Not enough Money!",false);	
				}
			}
		});
        
        buyAbilityBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(buyAbilityBtn);
				if(checkMaxBought(buyAbilityBtn)) {
					msgOnGui("Max!",false);
				} else if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.upgradeAbilities();
					purchaseProcess(buyAbilityBtn);
				} else {
					msgOnGui("Not enough Money!",false);	
				}
			}
		});

        buyPowerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(buyPowerBtn);
				if(checkMaxBought(buyPowerBtn)) {
					msgOnGui("Max!",false);
				} else if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.increasePower(1);
					purchaseProcess(buyPowerBtn);
				} else {
					msgOnGui("Not enough Money!",false);	
				}
			}
		});
        
        buyLifeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(buyLifeBtn);
				if(checkMaxBought(buyLifeBtn)) {
					msgOnGui("Max!",false);
				} else if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.gainLife();
					purchaseProcess(buyLifeBtn);
				} else {
					msgOnGui("Not enough Money!",false);	
				}						
			}			
		});
	}
	
	public void initMaps(JButton btn, String baseName, int cost, int maxPurchaseCount) {
		itemBaseName.put(btn, baseName);
		itemBaseCostMap.put(btn, cost);
		itemPurchaseMap.put(btn,  0);
		maxPurchaseMap.put(btn, maxPurchaseCount);
		btn.setBackground(Color.white);
		//btn.setPreferredSize(new Dimension(10,10));
		btn.setFont(new Font("Gulim", Font.BOLD, 15));
		add(btn);
	}
	
	public void resetPurchases() {    
	    for (Map.Entry<JButton, Integer> entry : itemPurchaseMap.entrySet()) {
	        entry.setValue(0);
	        updateBtnName((JButton) entry.getKey());
	    }	    
	}
	
	public boolean checkMaxBought(JButton btn) {
		if(itemPurchaseMap.get(btn) >= maxPurchaseMap.get(btn)) {
			btn.setText("<html>"+itemBaseName.get(btn) + "<br />( MAX )</html>");
			btn.setBackground(Color.red);
			return true;
		}
		return false;
	}
	
	public void purchaseProcess(JButton btn) {
		msgOnGui(itemBaseName.get(btn) + " Purchased!",true);
		itemPurchaseMap.put(btn, itemPurchaseMap.get(btn)+1);
		
		if(checkMaxBought(btn)) {
			return;
		}	
		
		updateBtnName(btn);
	}
	
	public void updateBtnName(JButton btn) {
		btn.setText("<html>"+itemBaseName.get(btn) + " "+ (itemPurchaseMap.get(btn))+"<br />( Cost : " + calcCost(btn) + " )</html>");
	}
	
	public int calcCost(JButton btnType) {
		double tmp = ((double)Math.sqrt(itemPurchaseMap.get(btnType)+1)) * (double)(itemBaseCostMap.get(btnType));
		return (int) tmp;
	}
	
	public void msgOnGui(String s, boolean bought) {
		Color c;
		if(bought) {
			c = Color.green;
		} else {
			c = Color.red;
		}
		GamePanel.texts.add(new Text(GamePanel.WIDTH/2 - s.length()/2, GamePanel.HEIGHT/2,2000,s, true, c, Font.BOLD, 30));
	}
	
	
}
