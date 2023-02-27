/**
 * @author Joshua Turner
 * The Mover is a tool that allows the user to click and drag on the grid to move around. The secondary action centers the viewport on the origin. 
 */

package hashlife;

public class Mover extends Tool {

	/**
	 * Default constructor
	 * @param gm
	 */
	public Mover(GridManager gm) {
		super(gm, "Mover", "Move around the grid.", "Move around the grid.", "Return to the origin.", true, false);
	}

	@Override
	/**
	 * Moves the center based on where the user has dragged in the last tick
	 */
	public void performPrimaryAction() {
		gm.setX_c(gm.getX_c() + (coords[0] - lastCoords[0]));
		gm.setY_c(gm.getY_c() + (coords[1] - lastCoords[1]));
	}

	@Override
	/**
	 * Recenters the viewport at (0,0)
	 */
	public void performSecondaryAction() {	
		gm.setX_c(gm.getWidth() / 2);
		gm.setY_c(gm.getHeight() / 2);
	}
}
