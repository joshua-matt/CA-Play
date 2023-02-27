/**
 * @author Joshua Turner
 * 
 * Where it all goes down. Main puts together all the UI elements.
 */

package hashlife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main extends JFrame implements ActionListener {

	static GridManager gm = new GridManager();
	static ToolPanel tp ;
	static StructureSelector ss;

	static {
		gm.setX_c(gm.getWidth() / 2);
		gm.setY_c(gm.getHeight() / 2);
		gm.repaint();
	}

	Main() {
		super();
	}

	Main(String title) {
		super(title);
	}

	public static void main(String[] args) {
		Main m = new Main("CA Play - untitled");
		m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel parent = new JPanel(); // parent holds all UI elements
		JPanel main = new JPanel(); // main holds the grid
		JPanel side = new JPanel(); // side holds the sidebar
		
		GridPlayer gp = new GridPlayer();
		tp = new ToolPanel(gm);
		RuleSetter rs = new RuleSetter(gm);
		ss = new StructureSelector(gm);

		/// MENU BAR ///
		
		JMenuBar mb = new JMenuBar(); 
		JMenu file = new JMenu("File");
		
		JMenuItem newg = new JMenuItem("New Grid");
		newg.setActionCommand("new");
		newg.addActionListener(m);
		
		JMenuItem save = new JMenuItem("Save Grid");
		save.setActionCommand("save");
		save.addActionListener(m);
		
		JMenuItem saveAs = new JMenuItem("Save Grid As");
		saveAs.setActionCommand("saveAs");
		saveAs.addActionListener(m);
		
		JMenuItem open = new JMenuItem("Open Grid");
		open.setActionCommand("load");
		open.addActionListener(m);
		
		
		JMenu view = new JMenu("View");
		

		file.add(newg); file.add(save); file.add(saveAs); file.add(open);
		mb.add(file);

		m.setJMenuBar(mb);
		
		/// SIZING AND LAYOUT ///
		parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
		rs.setLayout(new FlowLayout(FlowLayout.CENTER));
		ss.setLayout(new FlowLayout(FlowLayout.CENTER));

		tp.setMaximumSize(new Dimension(200, 235));
		rs.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		rs.setMaximumSize(new Dimension(200, 260));
		rs.setMinimumSize(new Dimension(200, 260));
		gp.setMaximumSize(new Dimension(1080, 100));
		
		side.setMaximumSize(new Dimension(250, 900));
		side.setPreferredSize(new Dimension(200, 900));
		
		ss.setPreferredSize(new Dimension(200, 400));
		ss.setMaximumSize(new Dimension(200, 400));
	
		m.setSize(1600, 900);
		m.setMinimumSize(new Dimension(750, 800));

		
		/// ADDING ///
		
		main.add(gm);
		main.add(gp);
		
		side.add(tp);
		side.add(rs);
		side.add(ss);
		
		parent.add(side);
		parent.add(main);

		m.add(parent);		
		m.setVisible(true);

		gm.repaint();
	}

	@Override
	/**
	 * Handles menubar events
	 */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "new":
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to create a new grid? Any unsaved changes will be lost.", "New grid", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
				gm.reset();
			break;
		case "save":
			gm.write(false);
			break;
		case "saveAs":
			gm.write(true);
			break;
		case "load":
			gm.read();
			setTitle("CA Play - " + gm.getCurrentFile());
			break;
		default:
			break;
		}
	}

}
