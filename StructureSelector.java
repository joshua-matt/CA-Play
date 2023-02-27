/**
 * @author Joshua Turner
 * The StructureSelector is a UI element that lets users select which structure they want to add to the grid, edit structures, and delete them. Each saved structure is represented in a
 * box with a picture of it, its name, and buttons for the aforementioned actions. Mousing over the box shows the description as a tooltip.
 */

package hashlife;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class StructureSelector extends JPanel implements ActionListener {
	private GridManager gm;
	private JPanel structs;
	private JButton createStruct;
	
	/**
	 * Default constructor, initializes UI elements
	 * @param gm
	 */
	public StructureSelector(GridManager gm) {
		this.gm = gm;		
		
		structs = new JPanel();
		structs.setLayout(new BoxLayout(structs, BoxLayout.Y_AXIS));
		
		JScrollPane pane = new JScrollPane(structs, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setPreferredSize(new Dimension(200, 300));
		pane.setMaximumSize(new Dimension(200, 300));
		redraw(); // Add all structure boxes to interface
		
		add(pane);
		
		createStruct = new JButton("Create Structure");
		createStruct.setActionCommand("create");
		createStruct.addActionListener(this);
		
		add(createStruct);
	}
	
	/**
	 * Refreshes the list of structures
	 */
	public void redraw() {
		structs.removeAll();
		for (File f: new File(System.getProperty("user.dir") + "\\structs\\").listFiles()) {
			if (f.isFile()) {
				StructBox s = new StructBox(this, f.getPath());
				structs.add(s);
				s.revalidate();
				s.repaint();
			}
		}
		revalidate();
		repaint();
	}
	
	/**
	 * This private class acts as the container for each structure in the UI. The contents of the StructBox are mentioned above.
	 */
	private class StructBox extends JPanel implements MouseListener, ActionListener {
		private StructureSelector s;
		private Structure struct;
		private JButton select, edit, x;
		private GridManager disp;
		
		/**
		 * Default constructor
		 * @param s
		 * @param filePath
		 */
		private StructBox(StructureSelector s, String filePath) {
			this.s = s;
			struct = Structure.read(filePath);
			addMouseListener(this);
			
			disp = new GridManager(struct); // Image of structure
			disp.setPreferredSize(new Dimension(50,50));
			disp.setScale(disp.getScale() / (2 * Math.max(struct.getWidth(), struct.getHeight())));
			add(disp);
			
			JPanel text = new JPanel();
			text.setLayout(new BorderLayout());
			text.setPreferredSize(new Dimension(130, 50));
			
			JPanel top = new JPanel();
			top.setLayout(new BorderLayout());
			
			JPanel bottom = new JPanel();
			bottom.setLayout(new BorderLayout());
						
			top.add(new JLabel(struct.getName()), BorderLayout.LINE_START);			
			
			select = new JButton("Select");
			edit = new JButton("Edit");
			x = new JButton("X");
			x.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					x.setForeground(Color.red);
				}
				
				public void mouseExited(MouseEvent e) {
					x.setForeground(Color.black);
				}
			});
			x.setBorder(BorderFactory.createEmptyBorder());
			x.setBorderPainted(false); 
		    x.setContentAreaFilled(false); 
		    x.setFocusPainted(false); 
		    x.setOpaque(false);
			
		    select.setActionCommand("select");
			edit.setActionCommand("edit");
			x.setActionCommand("x");
			
			select.addActionListener(this);
			edit.addActionListener(this);
			x.addActionListener(this);
			
			
			top.add(x, BorderLayout.CENTER);
			text.add(top, BorderLayout.PAGE_START);
			
			bottom.add(select, BorderLayout.LINE_START);
			bottom.add(edit, BorderLayout.LINE_END);
			text.add(bottom, BorderLayout.PAGE_END);
			
			add(text);
			
			setLayout(new FlowLayout(FlowLayout.LEFT));
			setMaximumSize(new Dimension(200, 63));
			setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			
			createToolTip();
			setToolTipText(struct.getDesc().equals("") ? struct.getName() : struct.getDesc()); // Mouse over tooltip
		}

		@Override
		public void mouseEntered(MouseEvent e) { // Mouseover effect
			// TODO Auto-generated method stub
			setBackground(Color.LIGHT_GRAY);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			setBackground(new Color(238,238,238));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("edit")) {
				StructureCreator s = new StructureCreator(Structure.read(struct.getPath()));
				s.setVisible(true);
			} else if (e.getActionCommand().equals("x")) {
				if (JOptionPane.showConfirmDialog(this, "Warning: you are about to delete \'" + struct.getName() + "\', and this is irreversible. Are you sure you want to do this?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					new File(struct.getPath()).delete();
					s.redraw();
				}
			} else {
				gm.setStructure(struct);
				gm.setTool(new StructureAdder(gm));
				Main.tp.update();
			}
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("create")) {		
			StructureCreator s = new StructureCreator();
			s.setVisible(true);
		}
	}	
}
