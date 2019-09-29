import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShopPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GamePanel gp;
	HashMap<JButton, Integer> itemPurchaseMap;
	HashMap<JButton, Integer> itemBaseCostMap;
	HashMap<JButton, String> itemBaseName;
	
	long msgTimerDiff;
	long msgDelay;
	long msgStartTimer;

	public ShopPanel(GamePanel gPanel){
		super();
		gp = gPanel;
		
		msgTimerDiff = System.nanoTime();
		msgDelay = 2000;
		msgStartTimer = System.nanoTime();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//panel.setPreferredSize(new Dimension ( 200,700));
		
		JLabel shopTitleLabel = new JLabel();
		shopTitleLabel.setText("Shop");			
        add(shopTitleLabel);
        
		itemPurchaseMap = new HashMap();
		itemBaseCostMap = new HashMap();
		itemBaseName = new HashMap();
		
        final JButton buyLifeBtn = new JButton("Extra Life 1 ( Cost : " + 120 + " )");        
        final JButton buyPowerBtn = new JButton("Power Up 1 ( Cost : " + 40 + " )");
        final JButton spazDurationBtn = new JButton("Spaz Longer 1 ( Cost : " + 70 + " )");
        final JButton missileDurationBtn = new JButton("Missile Longer 1 ( Cost : " + 80 + " )");
        final JButton dropRateBtn = new JButton("Drop Rate 1 ( Cost : " + 100 + " )");
        
        initMaps(buyLifeBtn, "Extra Life", 120);
        initMaps(buyPowerBtn, "Power Up", 50);
        initMaps(spazDurationBtn, "Spaz Longer", 70);
        initMaps(missileDurationBtn, "Missile Longer", 70);
        initMaps(dropRateBtn, "Drop Rate", 100);
        
        dropRateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cost = calcCost(dropRateBtn);
				if(gp.player.attemptPurchase(cost)) {
					//0.015% increase
					gp.player.incDropRate(0.015);
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
				if(gp.player.attemptPurchase(cost)) {
					gp.player.upgradeSpazDuration();
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
				if(gp.player.attemptPurchase(cost)) {
					gp.player.upgradeMissileDuration();
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
				if(gp.player.attemptPurchase(cost)) {
					gp.player.increasePower(1);
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
				if(gp.player.attemptPurchase(cost)) {
					gp.player.gainLife();
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
		add(btn);
	}
	
	public void purchaseProcess(JButton btn) {
		msgOnGui(itemBaseName.get(btn) + " 구 매 완",true);
		itemPurchaseMap.put(btn, itemPurchaseMap.get(btn)+1);
		updateBtnName(btn);
	}
	
	public void updateBtnName(JButton btn) {
		btn.setText(itemBaseName.get(btn) + " "+ (itemPurchaseMap.get(btn)+1)+" ( Cost : " + calcCost(btn) + " )");
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
		gp.texts.add(new Text(gp.WIDTH/2 - s.length()/2, gp.HEIGHT/2,2000,s, true, c, Font.BOLD, 30));
	}
	
	
}
