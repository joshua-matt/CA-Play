package hashlife;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorPicker extends JFrame implements ChangeListener, ActionListener {
	private JPanel alivePanel, deadPanel, chooserPanel, bottomPanel;
	private Main parent;
	private JButton OK;
	private JColorChooser aliveChooser, deadChooser;
	private GridManager disp;
	
	public ColorPicker() {
		super("Color Picker");
		
		this.parent = parent;
		
		aliveChooser = new JColorChooser();
		deadChooser = new JColorChooser();
		
		alivePanel = labeledPicker("Live Color", aliveChooser);
		deadPanel = labeledPicker("Dead Color", deadChooser);
        
        chooserPanel = new JPanel();
		chooserPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        chooserPanel.add(alivePanel);
        chooserPanel.add(deadPanel);
        
        OK = new JButton("OK");
        OK.addActionListener(this);
        OK.setActionCommand("OK");
        
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        
		disp = new GridManager(Structure.read(System.getProperty("user.dir") + "\\structs\\Glider.struct"));
		disp.setPreferredSize(new Dimension(90, 90));
		disp.setScale(disp.getScale() / 3);
        
        bottomPanel.add(disp, BorderLayout.CENTER);
        bottomPanel.add(OK, BorderLayout.LINE_END);
		
		setLayout(new FlowLayout());
		add(chooserPanel);
		add(bottomPanel);
	}	
	
	public static void main(String[] args) {
		ColorPicker c = new ColorPicker();
		c.setSize(400, 300);
		c.setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Color alive = aliveChooser.getColor(),
			  dead = deadChooser.getColor();
		GridManager.setLiveColor(alive);
		GridManager.setDeadColor(dead);
		disp.repaint();
		
		aliveChooser.setColor(new Color(alive.getRed(), alive.getGreen(), alive.getBlue(), 255));
		deadChooser.setColor(new Color(dead.getRed(), dead.getGreen(), dead.getBlue(), 255));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK"));
	}
	
	private JPanel labeledPicker(String name, JColorChooser jcc) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		stripChooser(jcc);
		jcc.getSelectionModel().addChangeListener(this);
		jcc.remove(0);
		
		p.add(new JLabel(name));
		p.add(jcc);
		
		return p;
	}
	
	private <T extends JComponent> void hideAll(Class<T> cl, Container c) {
		List<T> comps = SwingUtils.getDescendantsOfType(cl, c, true);
    	for (T jc: comps)
    		jc.setVisible(false);
	}
	
	private void stripChooser (JColorChooser jcc) {
		AbstractColorChooserPanel[] panels = jcc.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels)
            if(!accp.getDisplayName().equals("HSV"))
            	jcc.removeChooserPanel(accp);
            else {
            	hideAll(JSlider.class, accp);
            	hideAll(JSpinner.class, accp);
            	hideAll(JRadioButton.class, accp);
            	hideAll(JLabel.class, accp);
            }
	}
}
