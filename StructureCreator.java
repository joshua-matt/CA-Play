/**
 * @author Joshua Turner
 * The StructureCreator is a JFrame that allows the user to draw on a grid, using all tools except the StructureAdder, to create a structure. This structure can then be named and saved for
 * later use.
 */

package hashlife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StructureCreator extends JFrame implements ActionListener, WindowListener {
	static GridManager gm;
	static Structure struct = new Structure("Untitled", "N/A");
	private static JButton save = new JButton("Save");
	private static JTextField name = new JTextField(11);
	private static JTextArea desc = new JTextArea(5, 11);
	
	/**
	 * Default constructor, initializes UI
	 */
	public StructureCreator() {
		super("Structure Creator");
		gm = new GridManager();
		gm.setScale(1.5);
		save.setActionCommand("save");
		save.addActionListener(this);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel parent = new JPanel();
		JPanel main = new JPanel();
		JPanel info = new JPanel();
		ToolPanel tp = new ToolPanel(gm, true);
		tp.setMaximumSize(new Dimension(400, 50));
		tp.setMinimumSize(new Dimension(400, 50));
		
		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem saveGrid = new JMenuItem("Save Grid");
		menu.add(saveGrid);
		mb.add(menu);
	
		parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		gm.setPreferredSize(new Dimension(475, 400));
		main.add(gm);
		main.add(tp);
		
		JLabel nameLabel = new JLabel("Name: ");
		
		JLabel descLabel = new JLabel("Description: ");
		desc.setLineWrap(true);
		
		JScrollPane scroll = new JScrollPane(desc, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		info.setMinimumSize(new Dimension(140, 400));
		info.setPreferredSize(new Dimension(140, 400));
		info.setMaximumSize(new Dimension(140, 400));
		info.setLayout(new FlowLayout(FlowLayout.LEFT));
		info.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		info.add(nameLabel);
		info.add(name);
		info.add(descLabel);
		info.add(scroll);
		info.add(save);
		
		parent.add(main);
		parent.add(info);
		
		add(parent);		
		setVisible(true);
		setResizable(true);
		setSize(650, 400);
		
		gm.repaint();
		addWindowListener(this); // Window listener allows for checking if the user has saved
	}
	
	/**
	 * Overloaded constructor, allows the window to be initialized with a pre-existing structure in the middle
	 * @param cells
	 */
	public StructureCreator(HashSet<String> cells) {
		this();
		gm.addStructure(0, 0, cells);
	}
	
	/**
	 * Overloaded constructor, allows the window to be initialized with a struct in the middle and the names already set
	 * @param struct
	 */
	public StructureCreator(Structure struct) {
		this(struct.getCells());
		name.setText(struct.getName());
		desc.setText(struct.getDesc());
	}
	
	/**
	 * Checks if the grid is equal to the last saved instance of the structure, and alerts the user if not
	 */
	private void checkSaved() {
		if (!gm.equals(new GridManager(struct))) {
			if (JOptionPane.showConfirmDialog(this, "There are unsaved changes. Are you sure you want to exit?", "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
				dispose();
		} else {		
			dispose();
		}
		Main.ss.redraw();
	}
	
	/**
	 * Adds all cells in the grid to a Structure, then writes that Structure to a file
	 */
	private void saveStructure() {
		struct.setName(name.getText());
		struct.setDesc(desc.getText());
		for (int x = gm.nm.minX; x < gm.nm.maxX + 1; x++)
			for (int y = gm.nm.minY; y < gm.nm.maxY + 1; y++)
				if (gm.nm.getCell(x, y))
					struct.add(new int[] {x, y});
		struct.write();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save"))
			saveStructure(); 		
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		checkSaved();		
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
