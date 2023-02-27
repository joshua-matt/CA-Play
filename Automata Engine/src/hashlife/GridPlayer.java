/**
 * @author Joshua Turner
 * GridPlayer allows the user to start and stop grid evolution, change the number of evolutions per second, and step forward and backward.
 */

package hashlife;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import hashlife.*;

public class GridPlayer extends JPanel implements ChangeListener, ActionListener {
	private JButton play = new JButton("Play");
	private JButton stepForward = new JButton("Step forward");
	private JButton stepBack = new JButton("Step back");
	private JSlider speed = new JSlider(JSlider.HORIZONTAL, SPEED_MIN, SPEED_MAX, SPEED_DEFAULT);
	
	private boolean playing = false;
	private GridManager gm = Main.gm;
	
	static final int SPEED_MIN = 2; // Minimum evolutions per second
	static final int SPEED_MAX = 80; // Maximum evolutions per second
	static final int SPEED_DEFAULT = 15;
	
	public GridPlayer() {
		add(stepBack);
		stepBack.setActionCommand("undo");
		stepBack.addActionListener(this);
		
		add(play);
		play.setActionCommand("play");
		play.addActionListener(this);
		
		add(stepForward);
		stepForward.setActionCommand("redo");
		stepForward.addActionListener(this);
		
		add(Box.createRigidArea(new Dimension(35,0))); // Spacer
		
		add(new JLabel("Speed: "));
		add(speed);
		speed.addChangeListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();

		if (e.getActionCommand().equals("play")) {
			playing = !playing;
			
			gm.setRunning(playing);
			gm.runGameLoop();
			
			if (playing)
				source.setText("Pause");
			else
				source.setText("Play");
		} else if (e.getActionCommand().equals("undo")) {
			gm.nm.undo();
			gm.repaint();
		} else if (e.getActionCommand().equals("redo")) {
			if (gm.nm.redoStack.isEmpty())
				gm.update();
			else
				gm.nm.redo();
			gm.repaint();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		gm.setSpeed(source.getValue());
	}	
}
