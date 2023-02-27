/**
 * @author Joshua Turner
 * The RuleSetter panel allows the user to edit, save, and load transition rules. It includes a combo box for selecting from saved rules, as well as text fields and buttons
 * for specifying rule parameters.
 */
package hashlife;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class RuleSetter extends JPanel implements ActionListener, ItemListener {
	private GridManager gm;
	
	private HashMap<String, String> nameRuleMap = new HashMap<String, String>(); // Relates rule name to rule itself
	private HashSet<Integer> invalid = new HashSet<Integer>(); // The numbers that are invalid for the current neighborhood
	private String neighborhood = "M"; // Default as Moore neighborhood
	
	private JTextField s, b;
	private Image m_i, v_i, h_i;
	private JRadioButton m, v, h;
	private ButtonGroup group;
	private JComboBox<String> rulesets;	
	private JButton apply, save, delete;
		
	/**
	 * Default constructor, initializes UI
	 * @param gm
	 */
	public RuleSetter(GridManager gm) {
		this.gm = gm;
		s = new JTextField(10);
		b = new JTextField(10);
		
		s.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar()) || invalid.contains(e.getKeyChar() - 48) || s.getText().contains(e.getKeyChar() + ""))
					e.consume(); // Limits characters the user can input: outlaws nondigits, invalid digits, and already-present digits
				else 
					makeUntitled(); // Changing the rule changes the combobox to blank
			}
		});
		
		b.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {			
				if (!Character.isDigit(e.getKeyChar()) || invalid.contains(e.getKeyChar() - 48) || b.getText().contains(e.getKeyChar() + ""))
					e.consume();
				else
					makeUntitled();
			}
		});
		
		m_i = gm.getToolkit().getImage("assets/mn.png").getScaledInstance(50, 50, 0); // Neighborhood images
		v_i = gm.getToolkit().getImage("assets/vn.png").getScaledInstance(50, 50, 0);
		h_i = gm.getToolkit().getImage("assets/hn.png").getScaledInstance(50, 50, 0);
		
		m = new JRadioButton();
		m.setMnemonic(KeyEvent.VK_M);
		m.setActionCommand("M");
		m.setSelected(true);
		
		v = new JRadioButton();
		v.setMnemonic(KeyEvent.VK_V);
		v.setActionCommand("V");
		
		h = new JRadioButton();
		h.setMnemonic(KeyEvent.VK_H);
		h.setActionCommand("H");
		
		group = new ButtonGroup();
		group.add(m); group.add(v); group.add(h);
		
		m.addActionListener(this); v.addActionListener(this); h.addActionListener(this);
		
		apply = new JButton("Apply");
		apply.setActionCommand("apply");
		apply.addActionListener(this);
		
		save = new JButton("Save");
		save.setActionCommand("save");
		save.addActionListener(this);
		
		delete = new JButton("Delete");
		delete.setActionCommand("delete");
		delete.addActionListener(this);
		
		rulesets = new JComboBox<String>();
		
		updateRules();
				
		rulesets.setSelectedItem("Conway's Life");
		rulesets.addItemListener(this);
		rulesets.setPreferredSize(new Dimension(150, 20));
		
		setRule((String) rulesets.getSelectedItem());
		gm.setRule(nameRuleMap.get(rulesets.getSelectedItem()));
		
		add(new JLabel("Current rule: "));
		add(rulesets);
		
		add(new JLabel("Survive: ")); add(s);
		add(new JLabel("Born: ")); add(b);
		
		add(radioImage(m, m_i)); add(radioImage(v, v_i)); add(radioImage(h, h_i));
		add(apply); add(save); add(delete);
		
		setInvalidChars(neighborhood);
	}
	
	/**
	 * Refreshes the combobox to include all saved rules
	 */
	public void updateRules() {
		rulesets.removeAllItems();
		nameRuleMap.clear();
		
		File[] rules = new File(System.getProperty("user.dir") + "\\rules\\").listFiles();
		rulesets.addItem("");
		
		for (int i = 1; i < rules.length; i++) {
			String name = rules[i].getName().substring(0, rules[i].getName().length() - 5);
			Transitions t = Transitions.read(name);
			nameRuleMap.put(name, t.getCode());
			rulesets.addItem(name);
		}
		
		rulesets.repaint();
		rulesets.revalidate();
	}

	/**
	 * Sets the current rule, changing the content of the textfields and the selected neighborhood to match
	 * @param name
	 */
	private void setRule(String name) {
		String[] parts = nameRuleMap.get(name).split("/");
		s.setText(parts[0]);
		b.setText(parts[1].substring(0, parts[1].length() - 1));
		neighborhood = parts[1].substring(parts[1].length() - 1, parts[1].length());
		
		if (neighborhood.equals("M"))
			m.setSelected(true);
		else if (neighborhood.equals("V"))
			v.setSelected(true);
		else
			h.setSelected(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("M") || command.equals("V") || command.equals("H")) {
			makeUntitled();
			setInvalidChars(command);
		} else if (command.equals("apply")) {
			gm.setRule(s.getText() + "/" + b.getText() + neighborhood); // Applies rule to GridManager
		} else if (command.equals("save")) {
			write();
		} else if (command.equals("delete")) {
			String name = (String) rulesets.getSelectedItem();
			if (!name.equals("") && !name.equals(" ") && JOptionPane.showConfirmDialog(this, "Are you sure you want to delete \'" + name + "\'?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
				deleteRule(name); // Prompts user before sending rule to its death			
		}
	}
	
	/**
	 * Saves the current rule to a file, prompting the user at several points
	 */
	private void write() {
		String code = s.getText() + "/" + b.getText() + neighborhood;
		String name = JOptionPane.showInputDialog(this, "What would you like to name this ruleset?", "Name ruleset", JOptionPane.QUESTION_MESSAGE);
		if (!name.equals("")) {
			try {
				Transitions.writeSafe(new Transitions(name, code));
			} catch (Exception ex) {
				switch (ex.getMessage()) {
				case "Duplicate code":
					if (JOptionPane.showConfirmDialog(this, "A ruleset with code \'" + code + "\' already exists. Would you like to save this ruleset anyway?", "Duplicate code", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
						Transitions.write(new Transitions(name, code));
					break;
				case "Duplicate name":
					if (JOptionPane.showConfirmDialog(this, "A ruleset named \'" + name + "\' already exists. Would you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
						Transitions.write(new Transitions(name, code));
					break;
				default:
					break;
				}
			}
			updateRules();
			rulesets.setSelectedItem(name);
			setRule(name);
		} else
			JOptionPane.showConfirmDialog(this, "Remember to enter a name for the ruleset!", "Enter name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Deletes the current rule
	 * @param name
	 */
	private void deleteRule(String name) {
		new File(System.getProperty("user.dir") + "\\rules\\" + name + ".rule").delete();
	}
	
	/**
	 * Sets the combobox to blank
	 */
	private void makeUntitled() {
		rulesets.setSelectedItem("");
	}
	
	/**
	 * Sets invalid digits based on the neighborhood type
	 * @param command
	 */
	private void setInvalidChars(String command) {
		invalid.clear();
		invalid.add(9);
		
		String sText = s.getText(),
			   bText = b.getText();
		
		if (command.equals("V")) {
			invalid.add(5); invalid.add(6);
			invalid.add(7); invalid.add(8);
		} else if (command.equals("H")) {
			invalid.add(7); invalid.add(8);
		}
		
		for (Integer i: invalid) {
			sText = sText.replace(i + "", "");
			bText = bText.replace(i + "", "");
		}
		
		s.setText(sText);
		b.setText(bText);
		
		neighborhood = command;
	}
	
	/**
	 * Creates a component consistent of a radio button below an image
	 * @param r
	 * @param i
	 * @return JPanel with image above radio button
	 */
	private JPanel radioImage(JRadioButton r, Image i) {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		p.setPreferredSize(new Dimension(50, 85));
		
		p.add(new JLabel(new ImageIcon(i))); p.add(r);
		return p;
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		if (nameRuleMap.containsKey(e.getItem())) {
			gm.setRule(nameRuleMap.get(e.getItem())); // Changing rule choice sets rule and changes display
			setRule((String) e.getItem());	
		}
	}
}