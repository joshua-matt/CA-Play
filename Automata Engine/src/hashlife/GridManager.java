/**
 * @author Joshua Turner
 * GridManager is the bridge between the user and the NodeManager. It represents the NodeManager's root graphically, allowing the user to edit and view it using different tools.
 * The user can draw cells, make selections, add structures, and move around the grid. 
 */
package hashlife;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GridManager extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

	/// FUNCTIONALITY VARIABLES ///
	NodeManager nm;
	private String currentFile = "untitled";
	static Random rng = new Random();

	/// DISPLAY VARIABLES ///	
	private boolean responsive = true;
	private boolean firstRender = true;	

	private boolean showLines = true;
	private boolean zoomable = true;

	private double scale = 1.0;
	private int sideLength = (int) (50 * scale);
	private int x_c, y_c;
	private static Color cAlive, cDead, cLine;

	/// TOOL VARIABLES ///
	int[][] selection;
	static enum selectActions {RANDOM, FILL, KILL, STRUCT, INVERT};
	selectActions selectAction = selectActions.FILL;
	private Structure selectedStruct = new Structure("Untitled", "None");

	private Tool tool = new Painter(this);
	private int mouseX, mouseY;
	private boolean leftClick; 

	/// GAME LOOP VARIABLES ///
	double GAME_HERTZ = 30.0;
	double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
	final int MAX_UPDATES_BEFORE_RENDER = 1;

	double TARGET_FPS = 30;
	double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

	private boolean running = false;


	/// CONSTRUCTORS ///

	/**
	 * Default constructor, initializes Conway's Life
	 */
	public GridManager() {
		nm = new NodeManager("23/3");

		cDead = new Color(108,108,108); // Didn't get around to color changing, but the framework is laid for the user to select the colors for live and dead states
		cAlive = Color.white;
		cLine = cDead.brighter(); // If lines are black, this doesn't really work

		addMouseListener(this); // Event listeners
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		repaint();
	}

	/**
	 * Overloaded constructor, initializes a static grid with a structure at the center. Used for StructureSelector
	 * @param struct
	 */
	public GridManager(Structure struct) {
		this();
		showLines = true; zoomable = responsive = false;
		addStructure(0, 0, struct.getCells());
	}


	/// GAME LOOP ///

	/**
	 * Evolves the grid
	 */
	void update() {
		nm.update();
		repaint();
	}

	/**
	 * Starts the game loop
	 */
	public void runGameLoop() {
		Thread loop = new Thread() {
			public void run() {
				gameLoop();
			}
		};
		loop.start();
	}

	/**
	 * Carries out the game loop
	 */
	private void gameLoop() { // Copied from http://www.java-gaming.org/index.php?topic=24220.0
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime = System.nanoTime();
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);
		while (running) {
			double now = System.nanoTime();
			int updateCount = 0;

			while(now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
				nm.update();
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				updateCount++;
			}
			if (now - lastUpdateTime > TIME_BETWEEN_UPDATES)
				lastUpdateTime = now - TIME_BETWEEN_UPDATES;

			repaint();
			lastRenderTime = now;

			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if (thisSecond > lastSecondTime)
				lastSecondTime = thisSecond;

			while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
				Thread.yield();
				try {Thread.sleep(1);} catch(Exception e) {}
				now = System.nanoTime();
			}
		}
	}


	/// DRAWING THE GRID ///

	/**
	 * Draws everything onto the canvas
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		setBackground(cDead);
		// Lower items are drawn on top of higher ones (e.g. The selection overlays the grid)
		drawGrid(g);
		if (tool instanceof StructureAdder && StructureAdder.drawGhost) // Faintly draw the selected structure where the user points if they are using the adder and not selecting
			drawGhost(g);
		if (showLines)
			drawGridLines(g);
		if (selection != null)
			drawSelection(g);

		if (firstRender) { // Sets the initial center 
			x_c = getWidth() / 2;
			y_c = getHeight() / 2;
			firstRender = false;
		}
	}

	/**
	 * Draws the grid with a call to drawNode
	 * @param g
	 */
	public void drawGrid(Graphics g) {
		g.setColor(cAlive);
		drawNode(g, nm.root, x_c, y_c);
	}

	/**
	 * Recursively draws a QuadNode centered at coordinates (x, y)
	 * @param g
	 * @param q
	 * @param x
	 * @param y
	 */
	private void drawNode(Graphics g, QuadNode q, int x, int y) {  // Similar to NodeManager.setCell(x, y)
		if (q.population == 0) // Saves time by skipping empty nodes
			return;
		if (q.level == 0) {
			g.fillRect(x, y, (int) sideLength, (int) sideLength);
		} else if (q.level == 1) {
			drawNode(g, q.nw, x - sideLength, y - sideLength);
			drawNode(g, q.ne, x, y - sideLength);
			drawNode(g, q.sw, x - sideLength, y);
			drawNode(g, q.se, x, y);
		} else {
			int shift = (int) (sideLength * 1 << (q.level - 2));
			drawNode(g, q.nw, x - shift, y - shift);
			drawNode(g, q.ne, x + shift, y - shift);
			drawNode(g, q.sw, x - shift, y + shift);
			drawNode(g, q.se, x + shift, y + shift);
		}
	}

	/**
	 * Draws the grid lines
	 * @param g
	 */
	public void drawGridLines(Graphics g) {
		g.setColor(cLine);
		int width = getWidth(),
				height = getHeight();

		for (int x = x_c; x <= width; x += sideLength)
			g.drawLine(x, 0, x, height);

		for (int x = x_c; x >= 0; x -= sideLength)
			g.drawLine(x, 0, x, height);

		for (int y = y_c; y <= height; y += sideLength)
			g.drawLine(0, y, width, y);

		for (int y = y_c; y >= 0; y -= sideLength)
			g.drawLine(0, y, width, y);

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(x_c, 0, x_c, height);
		g2.drawLine(0, y_c, width, y_c);
	}

	/**
	 * Draws the current selection
	 * @param g
	 */
	private void drawSelection(Graphics g) {
		int[] anchorDisplayCoords = gridToDisplay(selection[0][0], selection[0][1]); 
		int[] dynamicDisplayCoords = gridToDisplay(selection[1][0], selection[1][1]);

		int width = dynamicDisplayCoords[0] - anchorDisplayCoords[0],
			height = dynamicDisplayCoords[1] - anchorDisplayCoords[1];

		g.setColor(new Color(26, 182, 255, 100));
		g.fillRect(anchorDisplayCoords[0], anchorDisplayCoords[1], width, height); // Draw the selection overlay

		g.setColor(new Color(179, 231, 255, 200)); // Draw the border
		if (width < 0 && height >= 0) // Fixes bug with displaying rects of negative width or height
			g.drawRect(dynamicDisplayCoords[0], anchorDisplayCoords[1], -width, height);
		else if (height < 0 && width >= 0)
			g.drawRect(anchorDisplayCoords[0], dynamicDisplayCoords[1], width, -height);
		else if (width < 0 && height < 0)
			g.drawRect(dynamicDisplayCoords[0], dynamicDisplayCoords[1], Math.abs(width), Math.abs(height));
		else
			g.drawRect(anchorDisplayCoords[0], anchorDisplayCoords[1], width, height);
	}

	/**
	 * Draws the ghost of the selected structure at the location of the user's mouse
	 * @param g
	 */
	private void drawGhost(Graphics g) {
		g.setColor(new Color(cAlive.getRed(), cAlive.getGreen(), cAlive.getBlue(), 80)); // Ghost color
		for (String c: selectedStruct.getCells()) {
			int[] coords = Structure.stringToCoords(c);
			int[] displayCoords = gridToDisplay(coords);
			int[] newGridCoords = displayToGrid(displayCoords[0] + mouseX, displayCoords[1] + mouseY); // Discretizing the position for each cell + user mouse
			int[] newDisplayCoords = gridToDisplay(newGridCoords);
			g.fillRect(newDisplayCoords[0], newDisplayCoords[1] - sideLength, sideLength, sideLength);
		}
	}


	/// CONVERTING BETWEEN GRID AND DISPLAY COORDINATES ///

	/**
	 * Calculates where the upper left corner of cell (x, y) appears on the display
	 * @param x
	 * @param y
	 * @return
	 */
	private int[] gridToDisplay(int x, int y) {
		int x_coord = x * sideLength + x_c;
		int y_coord = y * -sideLength + y_c;

		return new int[] {x_coord, y_coord};
	}

	/**
	 * Overloaded, calculates where the upper left corner of cell (coords) appears on the display
	 * @param coords
	 * @return
	 */
	private int[] gridToDisplay(int[] coords) {
		return gridToDisplay(coords[0], coords[1]);
	}

	/**
	 * Calculates which cell the display point (x, y) is in
	 * @param x
	 * @param y
	 * @return
	 */
	private int[] displayToGrid(int x, int y) {
		double normalizedX = x - x_c;
		double normalizedY = y_c - y;

		int x_coord = (int) Math.floor(normalizedX / sideLength);
		int y_coord = (int) Math.floor(normalizedY / sideLength);

		return new int[] {x_coord, y_coord};
	}


	/// EDITING THE GRID ///

	public void createCellLine(int x0, int y0, int x1, int y1, boolean alive) {  // Based on pseudocode from https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
		if (responsive) {
			if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
				if (x0 > x1)
					drawLow(x1, y1, x0, y0, alive);
				else
					drawLow(x0, y0, x1, y1, alive);
			} else {
				if (y0 > y1)
					drawHigh(x1, y1, x0, y0, alive);
				else
					drawHigh(x0, y0, x1, y1, alive);
			}
		}
	}

	/**
	 * Sets a line of cells whose slope is less than 1
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param alive
	 */
	private void drawLow(int x0, int y0, int x1, int y1, boolean alive) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int y_i = 1;

		if (dy < 0) {
			y_i = -1;
			dy = -dy;
		}

		int D = 2*dy - dx;
		int y = y0;

		for (int x = x0; x <= x1; x++) {
			nm.setCell(x, y, alive);
			if (D > 0) {
				y += y_i;
				D -= 2 * dx;
			}
			D += 2 * dy;
		}
	}

	/**
	 * Sets a line of cells whose slope is greater than 1
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param alive
	 */
	private void drawHigh(int x0, int y0, int x1, int y1, boolean alive) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int x_i = 1;

		if (dx < 0) {
			x_i = -1;
			dx = -dx;
		}

		int D = 2 * dx - dy;
		int x = x0;

		for (int y = y0; y <= y1; y++) {
			nm.setCell(x, y, alive);
			if (D > 0) {
				x += x_i;
				D -= 2 * dy;
			}
			D += 2 * dx;
		}
	}


	/**
	 * Adds a structure to the grid at the coordinates (x, y)
	 * @param x
	 * @param y
	 * @param struct
	 */
	public void addStructure(int x, int y, HashSet<String> struct) {
		for (String coord: struct) {
			String[] split = coord.split(" ");
			int sX = Integer.parseInt(split[0]),
					sY = Integer.parseInt(split[1]);		
			nm.setCell(x + sX, y + sY, true);
		}
	}

	/**
	 * Performs the current select action on the current selected region and repaints
	 */
	void performSelectAction() {
		int minX = Math.min(selection[0][0], selection[1][0]),
				maxX = Math.max(selection[0][0], selection[1][0]) - 1;

		int minY = Math.min(selection[0][1], selection[1][1]),
				maxY = Math.max(selection[0][1], selection[1][1]) - 1;

		int width = maxX - minX + 1;
		int height = maxY - minY + 1;

		switch (selectAction) {
		case RANDOM:
			for (int x = minX; x <= maxX; x++)
				for (int y = minY; y <= maxY; y++)
					nm.setCell(x, y, rng.nextBoolean());
			break;
		case FILL:
			for (int x = minX; x <= maxX; x++)
				for (int y = minY; y <= maxY; y++)
					nm.setCell(x, y, true);
			break;
		case KILL:
			for (int x = minX; x <= maxX; x++)
				for (int y = minY; y <= maxY; y++)
					nm.setCell(x, y, false);
			break;
		case STRUCT:
			HashSet<String> structCells = new HashSet<String>();
			for (int x = 0; x <= width; x++)
				for (int y = 0; y <= height; y++)
					if (nm.getCell(minX + x, minY + y))
						structCells.add(x + " " + y);
			StructureCreator s = new StructureCreator(structCells);
			removeSelection();
			break;
		case INVERT:
			for (int x = minX; x <= maxX; x++)
				for (int y = minY; y <= maxY; y++)
					nm.flipCell(x, y);
		default:
			break;
		}		
		repaint();
	}

	/**
	 * Gets rid of the current selection and repaints
	 */
	public void removeSelection() {
		selection = null;
		repaint();
	}

	public void reset() {
		nm = new NodeManager();
		repaint();
	}


	/// MOUSE EVENTS ///

	@Override
	public void mousePressed(MouseEvent e) {
		Tool.initCoords = new int[] {e.getX(), e.getY()}; // Set initial coordinates for tool use
		Tool.initGridCoords = displayToGrid(e.getX(), e.getY() + 1);

		Tool.coords = Tool.initCoords;
		Tool.gridCoords = Tool.initGridCoords;

		leftClick = SwingUtilities.isLeftMouseButton(e);
		if (leftClick) { // Checks if the tool has any actions that activate with clicking rather than dragging
			if (!tool.primaryIsDrag) 
				tool.performPrimaryAction();
		} else {
			if (!tool.secondaryIsDrag)
				tool.performSecondaryAction();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
		repaint();
		Tool.coords = Tool.gridCoords = Tool.lastCoords = Tool.lastGridCoords = null; // Resets tool coordinates
		if (!StructureAdder.drawGhost && tool instanceof StructureAdder) { // If the right button is being released and the tool is a structure adder, export the selection to a StructureCreator
			selectAction = selectActions.STRUCT;
			performSelectAction();
			StructureAdder.drawGhost = true;
		}
	}

	public void mouseDragged(MouseEvent e) {
		Tool.coords = new int[] {e.getX(), e.getY()}; // Update tool coords
		Tool.gridCoords = displayToGrid(e.getX(), e.getY());

		if (Tool.lastCoords == null) {
			Tool.lastCoords = Tool.coords;
			Tool.lastGridCoords = Tool.gridCoords;
		}

		if (leftClick) { // Performs dragging actions
			if (tool.primaryIsDrag)
				tool.performPrimaryAction();
		} else {
			if (tool.secondaryIsDrag) 
				tool.performSecondaryAction();
		}

		Tool.lastCoords = Tool.coords;
		Tool.lastGridCoords = Tool.gridCoords;
		repaint();
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		changeZoom(Math.pow(1.1, -e.getWheelRotation()), e.getX(), e.getY());
	}

	/**
	 * 
	 */
	public void changeZoom(double factor, int mX, int mY) {
		if (zoomable) {
			scale *= factor;
			int x = mX - getWidth() / 2,
				y = mY - getHeight() / 2;
			double dx = x - (x * factor); 
			double dy = y - (y * factor);

			if (scale < 6 && scale > 0.04) {
				x_c += (dx * Math.signum(factor));
				y_c += (dy * Math.signum(factor));
			}

			rescale();
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX() - x_c;
		mouseY = e.getY() - y_c;
		repaint();
	}

	/**
	 * Rescales the grid
	 */
	private void rescale() {
		if (scale > 6) // Binds the scale
			scale = 6;
		else if (scale <= 0.04)
			scale = 0.04;

		sideLength = (int) (50 * scale);

		if (sideLength <= 3)
			showLines = false;
		else
			showLines = true;		

		repaint();
	}


	/// OPENING AND SAVING GRIDS ///

	/**
	 * Reads a grid from a file selected by the user
	 */
	public void read() {
		nm.root = QuadNode.newEmpty(7);
		try {			
			JFileChooser fd = new JFileChooser(System.getProperty("user.dir") + "\\grids\\");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Grid files", "grid");
			fd.setFileFilter(filter);
			if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String dest = fd.getSelectedFile().getPath();

				if (!dest.matches(".*\\.grid$")) { // Constrains opened files to be grid files
					JOptionPane.showConfirmDialog(this, "Invalid file type. You can only open '.grid' files, silly!", "Invalid file type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					return;
				}

				String content = new String(Files.readAllBytes(Paths.get(dest)));

				String[] cells = content.split("\n");
				for (String c: cells) // Fill cells
					nm.setCell(Structure.stringToCoords(c)[0], Structure.stringToCoords(c)[1], true);

			}
			currentFile = fd.getSelectedFile().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the grid to a destination selected by the user
	 * @param dialog
	 */
	public void write(boolean dialog) {
		try {
			String dest;
			if (dialog || currentFile.equals("untitled")) {
				JFileChooser fd = new JFileChooser(System.getProperty("user.dir") + "\\grids\\");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Grid files", "grid");
				fd.setFileFilter(filter);
				if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
					dest = fd.getSelectedFile().getPath();
				else
					return;
			} else {
				dest = currentFile;
			}

			File newFile = new File(dest + ".grid");
			FileWriter writer = new FileWriter(newFile);
			int minX = -(1 << (nm.root.level - 1)),
					minY = minX;
			int maxX = 1 << (nm.root.level - 1),
					maxY = maxX;

			for (int x = minX; x <= maxX + 1; x++)
				for (int y = minY; y <= maxY + 1; y++)
					if (nm.getCell(x, y))
						writer.write(x + " " + y + "\n"); // Writes all live cell coordinates
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/// GETTERS AND SETTERS ///

	/**
	 * Getter for currentFile
	 * @return
	 */
	public String getCurrentFile() {
		return currentFile;
	}

	/**
	 * Getter for x_c
	 * @return x_c
	 */
	public int getX_c() {
		return x_c;
	}

	/**
	 * Getter for y_c
	 * @return y_c
	 */
	public int getY_c() {
		return y_c;
	}

	/**
	 * Setter for x_c
	 * @param x_c
	 */
	public void setX_c(int x_c) {
		this.x_c = x_c;
	}

	/**
	 * Setter for y_c
	 * @param y_c
	 */
	public void setY_c(int y_c) {
		this.y_c = y_c;
	}

	/**
	 * Sets the number of grid evolutions per second
	 * @param speed
	 */
	public void setSpeed(int speed) {
		GAME_HERTZ = speed;
		TARGET_FPS = speed;
		TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
		TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;		
	}

	/**
	 * Sets the rule for transitions
	 * @param code
	 */
	public void setRule(String code) {
		nm.setRule(code);
	}


	/**
	 * Getter for scale
	 * @return
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Setter for scale
	 * @param scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
		rescale();
	}


	/**
	 * Getter for selectedStruct
	 * @return
	 */
	public Structure getSelectedStruct() {
		return selectedStruct;
	}

	/**
	 * Setter for selectedStruct
	 * @param struct
	 */
	public void setStructure(Structure struct) {
		selectedStruct = struct;		
	}

	/**
	 * Setter for running. If the game loop is running, setting to false pauses it.
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Getter for tool
	 * @return
	 */
	public Tool getTool() {
		return tool;
	}

	/**
	 * Setter for tool
	 * @param tool
	 */
	public void setTool(Tool tool) {
		if (tool instanceof Mover)
			setCursor(new Cursor(Cursor.MOVE_CURSOR)); // Cardinal directions cursor
		else if (tool instanceof Painter) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage("assets/pencil_c.png");
			Cursor c = toolkit.createCustomCursor(image, new Point(getX(), getY() + 31), "img");
			setCursor(c); // Custom pencil cursor
		} else if (tool instanceof Selector || tool instanceof Zoomer)
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); // Precision crosshair cursor
		else
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		removeSelection();
		this.tool = tool;
	}

	/**
	 * Change the right-click action for selections. Choices are: Fill, Delete, Randomize
	 * @param choice
	 */
	public void setSelectAction(String choice) {
		switch (choice) {
		case "Fill":
			selectAction = selectActions.FILL;
			break;
		case "Delete":
			selectAction = selectActions.KILL;
			break;
		case "Randomize":
			selectAction = selectActions.RANDOM;
			break;
		case "Invert":
			selectAction = selectActions.INVERT;
			break;
		default:
			break;
		}
	}

	/**
	 * Getter for zoomable
	 * @return
	 */
	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * Set the live cell color
	 * @param c
	 */
	public static void setLiveColor(Color c) {
		cAlive = c;
	}
	
	/**
	 * Set the dead cell color
	 * @param c
	 */
	public static void setDeadColor(Color c) {
		cDead = c;
		System.out.println(Math.max(Math.max(c.getRed(), c.getBlue()), c.getGreen()));
		if (Math.max(Math.max(c.getRed(), c.getBlue()), c.getGreen()) > 128)
			cLine = cDead.darker();
		else
			cLine = cDead.brighter();
	}


	/// MISC ///

	/**
	 * Compares the content of grids for equality
	 * @param gm
	 * @return
	 */
	public boolean equals(GridManager gm) {
		return nm.root.equals(gm.nm.root);
	}


	/**
	 * Toggles the visibility of grid lines
	 */
	public void flipLines() {
		showLines = !showLines;
	}


	/// STUBS ///

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}
}
