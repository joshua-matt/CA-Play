/**
 * @author Joshua Turner
 * The ToolPanel allows users to select which tool they would like to use and presents
 * them with information about the selected tool.
 */

package hashlife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ToolPanel extends JPanel implements ActionListener, ItemListener {
	private GridManager gm;

	private JButton mover, zoomer,painter, selector, structures;
	private JPanel info;
	private JComboBox<String> actionList;
	private JLabel name, desc, prim, seco;
	private ImageIcon m_i, z_i, p_i, s_i, st_i;
	private Image lc, rc;

	private String selectOption; // The focused action for the Selector
	private boolean compact = false; // Whether to show the information panel

	public ToolPanel(GridManager gm) {
		this.gm = gm;

		Dimension maxButtonSize = new Dimension(25, 25);

		/// INITIALIZING BUTTONS ///

		m_i = new ImageIcon("assets/move_s.png");
		z_i = new ImageIcon("assets/zoom_s.png");
		p_i = new ImageIcon("assets/pencil_s.png");
		s_i = new ImageIcon("assets/select_s.png");
		st_i = new ImageIcon("assets/struct_s.png");

		mover = new JButton(m_i);		
		zoomer = new JButton(z_i);
		painter = new JButton(p_i);
		selector = new JButton(s_i);
		structures = new JButton(st_i);

		int padding = 0;

		add(mover);
		mover.setActionCommand("mover");
		mover.addActionListener(this);
		mover.setMaximumSize(maxButtonSize);
		mover.createToolTip();
		mover.setToolTipText("Mover");

		add(Box.createRigidArea(new Dimension(padding,0)));

		add(zoomer);
		zoomer.setActionCommand("zoomer");
		zoomer.addActionListener(this);
		zoomer.setMaximumSize(maxButtonSize);
		zoomer.createToolTip();
		zoomer.setToolTipText("Zoomer");

		add(Box.createRigidArea(new Dimension(padding,0)));

		add(painter);
		painter.setActionCommand("painter");
		painter.addActionListener(this);
		painter.setMaximumSize(maxButtonSize);
		painter.createToolTip();
		painter.setToolTipText("Cell Pen");

		add(Box.createRigidArea(new Dimension(padding,0)));

		add(selector);
		selector.setActionCommand("selector");
		selector.addActionListener(this);	
		selector.setMaximumSize(maxButtonSize);
		selector.createToolTip();
		selector.setToolTipText("Selector");

		add(Box.createRigidArea(new Dimension(padding,0)));

		add(structures);
		structures.setActionCommand("structures");
		structures.addActionListener(this);
		structures.setMaximumSize(maxButtonSize);
		structures.createToolTip();
		structures.setToolTipText("Structures");		

		mover.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 5));
		zoomer.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 5));
		painter.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 5));
		selector.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 5));
		structures.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 5));

		/// 

		info = new JPanel();
		info.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		info.setPreferredSize(new Dimension(200, 200));

		update();

		add(info);
	}

	public ToolPanel(GridManager gm, boolean compact) {
		this(gm);
		this.compact = compact;
		if (compact)
			structures.setVisible(false);
		update();
	}

	public void update() {
		if (compact)
			compactUpdate();
		else
			regularUpdate();

	}

	private void regularUpdate() {
		Tool t = gm.getTool();		
		Font bold = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		Font normal = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

		name = new JLabel(wideFormat(t.getName()));
		desc = new JLabel(wideFormat(t.getDesc()));

		JPanel acts = new JPanel();
		acts.setLayout(new GridLayout(2,2));

		prim = new JLabel(narrowFormat(t.getPrimary()));
		seco = new JLabel(narrowFormat(t.getSecondary()));

		lc = gm.getToolkit().getImage("assets/left-click.png").getScaledInstance(15, 25, 0);
		rc = gm.getToolkit().getImage("assets/right-click.png").getScaledInstance(15, 25, 0);

		acts.add(new JLabel(new ImageIcon(lc))); acts.add(new JLabel(new ImageIcon(rc)));

		acts.add(prim); acts.add(seco);

		name.setFont(bold);
		desc.setFont(normal);
		prim.setFont(normal); seco.setFont(normal);

		String[] selectActs = {"Fill", "Delete", "Randomize", "Invert"};
		actionList = new JComboBox<String>(selectActs);
		actionList.setSelectedIndex(0);
		actionList.addItemListener(this);

		info.removeAll();
		info.add(new JLabel("Current tool: "));
		info.add(name);
		info.add(desc);
		info.add(Box.createRigidArea(new Dimension(0, 15)));
		info.add(acts);
		if (t instanceof Selector)
			info.add(actionList);
		info.repaint();
		info.revalidate();
	}

	private void compactUpdate() {
		Tool t = gm.getTool();		

		String[] selectActs = {"Fill", "Delete", "Randomize", "Invert"};
		actionList = new JComboBox<String>(selectActs);
		actionList.setSelectedIndex(0);
		actionList.addItemListener(this);

		info.removeAll();

		info.setPreferredSize(new Dimension(90, 45));
		info.setBorder(BorderFactory.createEmptyBorder());
		info.add(actionList);

		actionList.setVisible(false);
		if (t instanceof Selector)
			actionList.setVisible(true);

		info.repaint();
		info.revalidate();
	}

	private String wideFormat(String s) {
		String html = "<html><body style='width: 130px; text-align:center'>%1s";
		return String.format(html, s);
	}

	private String narrowFormat(String s) {
		String html = "<html><body style='width: 50px; text-align:center'>%1s";
		return String.format(html, s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "mover":
			gm.setTool(new Mover(gm));
			break;
		case "zoomer":
			gm.setTool(new Zoomer(gm));
			break;
		case "painter":
			gm.setTool(new Painter(gm));
			break;
		case "selector":
			gm.setTool(new Selector(gm));
			break;
		case "structures":
			gm.setTool(new StructureAdder(gm));
			break;
		default:
			gm.setTool(new Mover(gm));
			break;
		}

		update();
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		selectOption = (String) actionList.getSelectedItem();
		gm.setSelectAction(selectOption);		
	}
}
