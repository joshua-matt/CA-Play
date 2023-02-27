/**
 * @author Joshua Turner
 * The Zoomer is a Tool that allows users without mice to zoom in and out on the grid.
 */

package hashlife;

public class Zoomer extends Tool {

	/**
	 * Default constructor
	 * @param gm
	 */
	public Zoomer(GridManager gm) {
		super(gm, "Zoomer", "Zoom in and out.", "Zoom in.", "Zoom out.", false, false);
	}
	
	@Override
	/**
	 * Zooms in by a factor of 1.5
	 */
	public void performPrimaryAction() {
		gm.changeZoom(1.5, coords[0], coords[1]);
	}

	/**
	 * Zooms out by a factor of 1.5
	 */
	@Override
	public void performSecondaryAction() {
		gm.changeZoom(1 / 1.5, coords[0], coords[1]);
	}

}
