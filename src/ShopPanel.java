import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class ShopPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<JButton, Integer> itemPurchaseMap;
	HashMap<JButton, Integer> itemBaseCostMap;
	HashMap<JButton, String> itemBaseName;
	
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
		itemPurchaseMap = new HashMap();
		itemBaseCostMap = new HashMap();
		itemBaseName = new HashMap();
		
        final JButton buyLifeBtn = new JButton("<html>Extra Life 1 <br />( Cost : " + 120 + " )</html>");        
        final JButton buyPowerBtn = new JButton("<html>Power Up 1 <br />( Cost : " + 40 + " )</html>");
        final JButton spazDurationBtn = new JButton("<html>Spaz Longer 1 <br />( Cost : " + 60 + " )</html>");
        final JButton missileDurationBtn = new JButton("<html>Missile Longer 1 <br />( Cost : " + 70 + " )</html>");
        final JButton dropRateBtn = new JButton("<html>Drop Rate 1 <br />( Cost : " + 100 + " )</html>");
        
        initMaps(buyLifeBtn, "Extra Life", 120);
        initMaps(buyPowerBtn, "Power Up", 40);
        initMaps(spazDurationBtn, "Spaz Longer", 60);
        initMaps(missileDurationBtn, "Missile Longer", 70);
        initMaps(dropRateBtn, "Drop Rate", 100);
        
        
        
        
        
        dropRateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(dropRateBtn);
				if(GamePanel.player.attemptPurchase(cost)) {
					//0.015% increase
					GamePanel.player.incDropRate(0.015);
					purchaseProcess(dropRateBtn);
				} else {
					msgOnGui("돈 부 족",false);	
				}
			}
		});
        
        spazDurationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(spazDurationBtn);
				if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.upgradeSpazDuration();
					purchaseProcess(spazDurationBtn);
				} else {
					msgOnGui("돈 부 족",false);	
				}
			}
		});

        missileDurationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(missileDurationBtn);
				if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.upgradeMissileDuration();
					purchaseProcess(missileDurationBtn);
				} else {
					msgOnGui("돈 부 족",false);	
				}
			}
		});
        
        
        buyPowerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(buyPowerBtn);
				if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.increasePower(1);
					purchaseProcess(buyPowerBtn);
				} else {
					msgOnGui("돈 부 족",false);	
				}
			}
		});
        
        buyLifeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(buyLifeBtn);
				if(GamePanel.player.attemptPurchase(cost)) {
					GamePanel.player.gainLife();
					purchaseProcess(buyLifeBtn);
				} else {
					msgOnGui("돈 부 족",false);	
				}						
			}			
		});
	}
	
	public void initMaps(JButton btn, String baseName, int cost) {
		itemBaseName.put(btn, baseName);
		itemBaseCostMap.put(btn, cost);
		itemPurchaseMap.put(btn,  0);
		btn.setBackground(Color.white);
		//btn.setPreferredSize(new Dimension(10,10));
		btn.setFont(new Font("Gulim", Font.BOLD, 15));
		add(btn);
	}
	
	public void purchaseProcess(JButton btn) {
		msgOnGui(itemBaseName.get(btn) + " 구 매 완",true);
		itemPurchaseMap.put(btn, itemPurchaseMap.get(btn)+1);
		updateBtnName(btn);
	}
	
	public void updateBtnName(JButton btn) {
		btn.setText("<html>"+itemBaseName.get(btn) + " "+ (itemPurchaseMap.get(btn)+1)+"<br />( Cost : " + calcCost(btn) + " )</html>");
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
