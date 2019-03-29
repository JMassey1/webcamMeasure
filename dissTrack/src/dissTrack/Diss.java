package dissTrack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Diss {
	public static void main(String[] args) {
		JFrame diss = new JFrame();
		JPanel diss1 = new JPanel();
		diss1.setLayout(new BoxLayout(diss1, BoxLayout.Y_AXIS));
		JPanel temp = new JPanel();
		temp.add(new JLabel("<html><h1><strong><i>You, Lindsey Burns, are a Loser</i></strong></h1></html>", SwingConstants.CENTER));
		diss1.add(temp);
		JPanel temp2 = new JPanel();
		JButton trueB = new JButton("This is true");
		trueB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newFrame();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton trueA = new JButton("This is very correct");
		trueA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newFrame();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		temp2.add(trueB);
		temp2.add(trueA);
		diss1.add(temp2);
		diss.add(diss1);
		diss.setVisible(true);
		diss.pack();
	}
	
	public static void newFrame() throws InterruptedException {
		JFrame temp2 = new JFrame();
		temp2.add(new JLabel("<html><h1><strong><i>THIS IS TRUE</i></strong></h1><html>", SwingConstants.CENTER));
		temp2.setVisible(true);
		temp2.pack();
	}
}
