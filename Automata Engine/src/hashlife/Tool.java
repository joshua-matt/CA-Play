/**
 * @author Joshua Turner
 * 
 * Tool is an abstract class that allows for any tool for manipulating the grid to be represented. Stored are the tool's name, description, primary and secondary actions, whether those actions
 * are click- or drag-based, and the coordinates, both grid and display, on which the user is using the tool. The abstract methods are the primary and secondary actions. The current subclasses 
 * of Tool are Mover, Painter, Selector, and StructureAdder.
 */
package hashlife;

public abstract class Tool {
	protected String name, desc, primary, secondary;
	protected static int[] initCoords, initGridCoords;
	protected static int[] coords, lastCoords;	
	protected static int[] gridCoords, lastGridCoords;
	protected boolean primaryIsDrag = true;
	protected boolean secondaryIsDrag = true;
	protected GridManager gm;
	
	/**
	 * Default constructor
	 * @param gm
	 * @param name
	 * @param desc
	 * @param primary
	 * @param secondary
	 */
	public Tool(GridManager gm, String name, String desc, String primary, String secondary) {
		coords = lastCoords = gridCoords = lastGridCoords = null;
		this.gm = gm;
		this.name = name;
		this.desc = desc;
		this.primary = primary;
		this.secondary = secondary;
	}
	
	/**
	 * Overloaded constructor that allows specification of mouse action
	 * @param gm
	 * @param name
	 * @param desc
	 * @param primary
	 * @param secondary
	 * @param primaryIsDrag
	 * @param secondaryIsDrag
	 */
	public Tool(GridManager gm, String name, String desc, String primary, String secondary, boolean primaryIsDrag, boolean secondaryIsDrag) {
		this(gm, name, desc, primary, secondary);
		this.primaryIsDrag = primaryIsDrag;
		this.secondaryIsDrag = secondaryIsDrag;
	}
	
	/**
	 * Left-click action
	 */
	public abstract void performPrimaryAction();
	
	/**
	 * Right-click action
	 */
	public abstract void performSecondaryAction();
	
	/**
	 * Getter for name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getter for description
	 * @return
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Getter for primary action description
	 * @return
	 */
	public String getPrimary() {
		return primary;
	}
	
	/**
	 * Getter for secondary action description
	 * @return
	 */
	public String getSecondary() {
		return secondary;
	}
}
