/**
 * @author Joshua Turner
 * The Selector tool allows the user to select regions of the grid and fill, delete, randomize, or invert them.
 */

package hashlife;

public class Selector extends Tool {

	/**
	 * Default constructor
	 * @param gm
	 */
	public Selector(GridManager gm) {
		super(gm, "Selector", "Select and edit regions of the grid.", "Select a region.", "Perform an action on the region.", true, false);
	}

	@Override
	/**
	 * Changes the corner of the selection box to the current mouse coordinates
	 */
	public void performPrimaryAction() {
		gm.selection = new int[][] {{initGridCoords[0], initGridCoords[1]}, {gridCoords[0], gridCoords[1]}};
	}

	@Override
	/**
	 * Performs the selected select action
	 */
	public void performSecondaryAction() {
		gm.performSelectAction();
	} 
}
