/**
 * @author Joshua Turner
 * StructureAdder is a Tool that allows users to add structures to the grid and select regions to be exported to a StructureCreator.
 */

package hashlife;

public class StructureAdder extends Tool {
	static boolean drawGhost = true;
	
	/**
	 * Default constructor
	 * @param gm
	 */
	public StructureAdder(GridManager gm) {
		super(gm, "Structure Adder", "Add structures to the grid.", "Place the selected structure.", "Select a region to turn into a structure.", false, true);
	}
	
	@Override
	/**
	 * Place the selected structure at the click coordinates
	 */
	public void performPrimaryAction() {
		gm.addStructure(gridCoords[0], gridCoords[1], gm.getSelectedStruct().getCells());		
	}

	@Override
	/**
	 * Selects an area to turn into a Structure
	 */
	public void performSecondaryAction() {
		drawGhost = false;
		gm.selection = new int[][] {{initGridCoords[0], initGridCoords[1]}, {gridCoords[0], gridCoords[1]}};
	}
}
