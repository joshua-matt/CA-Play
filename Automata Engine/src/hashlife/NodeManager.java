/**
 * @author Joshua Turner
 * 
 * The NodeManager class handles manipulations of the grid's data. It keeps the root node of the Quadtree and receives and executes commands to edit and evolve the root.
 * NodeManager also keeps track of the rules for cell transitions.
 */

package hashlife;

import java.util.ArrayList;
import hashlife.QuadNode;

public class NodeManager {
	QuadNode root; // The QuadNode corresponding to the entire grid
	ArrayList<QuadNode> undoStack = new ArrayList<QuadNode>(), // Experimental feature to let the user undo and redo actions; not implemented
						redoStack = new ArrayList<QuadNode>();
	static Transitions t = new Transitions(); // Transitions used to determine cell evolution
	int minX, minY = Integer.MAX_VALUE; // Store the minimum and maximum coordinates for the grid
	int maxX, maxY = Integer.MIN_VALUE;
		
	/**
	 * Default constructor
	 * Initializes Conway's Life
	 */
	public NodeManager() {
		t = new Transitions();		
		init();
	}
	
	/**
	 * Overloaded constructor
	 * Reads in a string of the form X/YC, where X and Y are any number of digits and C is a character denoting the type of neighborhood, and creates the appropriates rules
	 * The X digits specify rules for cell survival and the Y digits specify rules for cell birth
	 * @param code 
	 */
	public NodeManager(String code) {
		setRule(code);
		init();
	}
	
	/**
	 * Initializes the root node as empty
	 */
	private void init() {
		root = QuadNode.newEmpty(7);
		root = root.expand(); // Expand so that the area evolved is level 7 (with the root being level 8)
	}
	
	/**
	 * Sets the state of a cell in the grid
	 * @param x
	 * @param y
	 * @param alive
	 */
	public void setCell(int x, int y, boolean alive) {
		if (Math.abs(x) >= 1 << (root.level - 1) || Math.abs(y) >= 1 << (root.level - 1)) // If the cell being set is currently outside the root node, expand until the root encompasses
			expandRoot(Math.max(Math.abs(x), Math.abs(y)));
		root = root.setCell(x, y, alive);
		if (alive)
			setMinMaxCoords(x, y);	
	}
	
	public void flipCell(int x, int y) {
		if (Math.abs(x) >= 1 << (root.level - 1) || Math.abs(y) >= 1 << (root.level - 1)) // If the cell being set is currently outside the root node, expand until the root encompasses
			expandRoot(Math.max(Math.abs(x), Math.abs(y)));
		root = root.flipCell(x, y);
		setMinMaxCoords(x, y);	
	}
	
	/**
	 * Gets the state of a cell in the grid
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean getCell(int x, int y) {
		return root.getCell(x, y, root);
	}
	
	/**
	 * Sets the transition rule (S/BN format)
	 * @param code
	 */
	public void setRule(String code) {
		t = new Transitions(code);
	}
	
	/**
	 * Expands the root node until its dimension is greater than size
	 * @param size
	 */
	public void expandRoot(int size) {
		while (1 << (root.level - 1) <= size)
			root = root.expand();
	}
	
	/**
	 * Evolves the grid
	 */
	public void update() {
		root = root.expand().evolve();
		if (root.nw.population - root.nw.se.population > 0 || // If live cells are approaching the border, expand
			root.ne.population - root.ne.sw.population > 0 || 
			root.sw.population - root.sw.ne.population > 0 || 
			root.se.population - root.se.nw.population > 0)
			root = root.expand();
	}
	
	/**
	 * Given a coordinate, reassigns min and max coords as necessary
	 * @param x
	 * @param y
	 */
	public void setMinMaxCoords(int x, int y) {
		minX = x < minX ? x : minX;
		maxX = x > maxX ? x : maxX;
		minY = y < minY ? y : minY;
		maxY = y > maxY ? y : maxY;
	}
	
	/**
	 * EXPERIMENTAL FEATURE
	 * Undoes the last action, whether evolution or user modification
	 */
	public void undo() {
		if (!undoStack.isEmpty()) {
			redoStack.add(undoStack.remove(undoStack.size() - 1));
			root = redoStack.get(redoStack.size() - 1);
		}
	}
	
	/**
	 * EXPERIMENTAL FEATURE
	 * Redoes the last undone action
	 */
	public void redo() {
		if (!redoStack.isEmpty()) {
			undoStack.add(redoStack.remove(redoStack.size() - 1));
			root = undoStack.get(undoStack.size() - 1);
		}
	}
}
