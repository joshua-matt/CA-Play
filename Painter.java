/**
 * @author Joshua Turner
 * The Painter is a Tool that lets the user add and remove cells from the grid by clicking and dragging.
 */
package hashlife;

public class Painter extends Tool {

	/**
	 * Default constructor
	 * @param gm
	 */
	public Painter(GridManager gm) {
		super(gm, "Painter", "Draw cells on the grid.", "Draw live cells.", "Draw dead cells.");
	}

	@Override
	/**
	 * Draws a line of live cells
	 */
	public void performPrimaryAction() {
		gm.createCellLine(lastGridCoords[0], lastGridCoords[1], gridCoords[0], gridCoords[1], true);
	}

	@Override
	/**
	 * Draws a line of dead cells
	 */
	public void performSecondaryAction() {
		gm.createCellLine(lastGridCoords[0], lastGridCoords[1], gridCoords[0], gridCoords[1], false);
	}
}
