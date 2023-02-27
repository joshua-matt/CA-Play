/**
 * @author Joshua Turner
 * The Structure class stores a collection of live cells for later simultaneous addition to the grid. Since many CA's have interesting patterns, Structures simplify matters
 * by only requiring these patterns to be drawn once, instead of every time. 
 */
package hashlife;

import java.util.ArrayList;
import java.util.HashSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

public class Structure {
	private HashSet<String> cells = new HashSet<String>(); // Strings are coordinates of the form "x y". They specify the coordinates of living cells
	private String name, desc;
	private int minX, minY = Integer.MAX_VALUE;
	private int maxX, maxY = Integer.MIN_VALUE;
	private int width, height;
	private ArrayList<String> intendedRules = new ArrayList<String>(); // UNIMPLEMENTED: Meant for the user to be able to specify which rules a Structure should be used with
	
	/**
	 * Default constructor
	 * @param name
	 * @param desc
	 */
	public Structure(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	/**
	 * Overloaded constructor, allows a set of cells to be read in
	 */
	public Structure(String name, String desc, HashSet<String> cells) {
		this(name, desc);
		for (String c: cells) {
			int[] coords = {Integer.parseInt(c.split(" ")[0]), Integer.parseInt(c.split(" ")[1])};
			updateMinMax(coords[0], coords[1]);
			this.cells.add(c);
		}
	}
	
	/**
	 * Overloaded constructor, copies another structure
	 */
	public Structure(Structure struct) {
		name = struct.getName();
		desc = struct.getDesc();
		intendedRules.addAll(struct.getRules());
		cells.addAll(struct.getCells());
	}
	
	/**
	 * Add a live cell to the Structure at a given coordinate
	 * @param gridCoords
	 */
	public void add(int[] gridCoords) {
		updateMinMax(gridCoords[0], gridCoords[1]); // Check whether the coordinates are outside the current bounds of the structure
		cells.add(gridCoords[0] + " " + gridCoords[1]);
	}
	
	/**
	 * Getter for name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setter for name
	 * @return name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter for description
	 * @return desc
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Setter for description
	 * @param desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	/**
	 * Getter for cells
	 * @return cells
	 */
	public HashSet<String> getCells() {
		return cells;
	}
	
	/**
	 * Getter for width
	 * @return width + 1
	 */
	public int getWidth() {
		return width + 1; // +1 because normal calculation of width is exclusive, when width is really inclusive
	}
	
	/**
	 * Getter for height
	 * @return height + 1
	 */
	public int getHeight() {
		return height + 1;
	}
	
	/**
	 * Converts a string of the form "x y" to a 2-array of ints
	 * @param s
	 * @return {x, y}
	 */
	public static int[] stringToCoords(String s) {
		String[] split = s.split(" ");
		return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
	}
	
	/**
	 * Read a .struct file into a Structure object
	 * @param filePath
	 * @return Structure
	 */
	public static Structure read(String filePath) {
		try {			
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			String[] parts = content.split("\\|");
			Structure newStruct = new Structure(parts[0], parts[1]);
			String[] cells = parts[2].split("\n");
			for (String c: cells)
				newStruct.add(Structure.stringToCoords(c));
			return newStruct;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Write this structure to a .struct file
	 */
	public void write() {
		try {
			File newFile = new File(getPath());
			if (newFile.exists())
				if (JOptionPane.showConfirmDialog(null, "A Structure called \'" + name + "\' already exists. Would you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return;
			FileWriter writer = new FileWriter(newFile);
			writer.write(name + "|" + desc + "|");
			for (String c: cells) {
				String[] split = c.split(" ");
				writer.write(split[0] + " " + split[1] + "\n");
			}
			writer.close();
			JOptionPane.showMessageDialog(null, "Save successful!", "Success", JOptionPane.INFORMATION_MESSAGE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the path of this structure
	 * @return
	 */
	public String getPath() {
		return System.getProperty("user.dir") + "\\structs\\" + name + ".struct";
	}
	
	/**
	 * Recalibrates the bounds of the structure
	 * @param x
	 * @param y
	 */
	private void updateMinMax(int x, int y) {
		if (x < minX) minX = x;
		else if (x > maxX) maxX = x;
		
		if (y < minY) minY = y;
		else if (y > maxY) maxY = y;
		
		width = maxX - minX;
		height = maxY - minY;
	}
	
	
	/**
	 * UNUSED: Getter for intendedRules
	 * @return intendedRules
	 */
	public ArrayList<String> getRules() {
		return intendedRules;
	}
	
	/**
	 * UNUSED: Add an intended rule
	 * @param code
	 * @throws Exception when an invalid code has been entered
	 */
	public void addRule(String code) throws Exception {
		if (code.matches("([0-8])*/([0-8])*M?") || code.matches("([0-4])*/([0-4])*V") || code.matches("([0-6])*/([0-6])*H"))
			intendedRules.add(code);
		else
			throw new Exception("Invalid code.");
	}
	
	/**
	 * UNUSED: Remove a rule
	 * @param code
	 */
	public void removeRule(String code) {
		intendedRules.remove(code);
	}
}
