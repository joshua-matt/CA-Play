/**
 * @author Joshua Turner
 * 
 * Transitions manages the specification of rules for transitions between grid states, including reading from and writing to files.
 * 
 * Codes for transitions are specified as follows:
 		* Numbers to the left of the slash indicate how many live neighbor cells a cell needs to survive
 		* Numbers to the right of the slash indicate how many live neighbor cells a dead cell needs to become live
 		* The last character indicates the type of neighborhood. M is Moore (all 8 adjacent squares), V is Von Neumann (all 4 orthogonal squares), H is Hexagonal (M without NE and SW corners).
 * Note that the accepted numbers in the first two parts depend on the last part.
 */

package hashlife;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Transitions {
	int neighborhood; // 0 indicates Moore, 1 indicates Von, 2 indicates Hex
	HashSet<Integer> survive = new HashSet<Integer>();
	HashSet<Integer> birth = new HashSet<Integer>();
	String code;
	private String name;

	/**
	 * Default constructor, initializes Conway's Life
	 */
	public Transitions() {
		neighborhood = 0;
		survive.add(2);
		survive.add(3);
		birth.add(3);
		code = "23/3M";
	}

	/**
	 * Overloaded constructor, parses a code
	 * @param code
	 */
	public Transitions(String code) {
		clearQuadCache(); // Resets the next state results for all QuadNodes
		String[] split = code.split("/");
		if (code.matches("([0-8])*/([0-8])*M"))
			neighborhood = 0;	
		else if (code.matches("([0-4])*/([0-4])*V"))
			neighborhood = 1;
		else if (code.matches("([0-6])*/([0-6])*H"))
			neighborhood = 2;

		for (int i = 0; i < split[0].length(); i++)
			survive.add(split[0].charAt(i) - 48);
		for (int i = 0; i < split[1].length(); i++)
			if (Character.isDigit(split[1].charAt(i)))	
				birth.add(split[1].charAt(i) - 48);
		this.code = code;
	}

	/**
	 * Overloaded constructor, parses a code and sets a name
	 * @param name
	 * @param code
	 */
	public Transitions(String name, String code) {
		this(code);
		this.name = name;
	}

	/**
	 * Gets the code for the transition
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Getter for name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Clears cached next generation results from all QuadNodes
	 */
	private void clearQuadCache() {
		try {
			for (QuadNode q: QuadNode.hashMap.keySet())
				q.updatedToTransition = false;
		} catch (Exception e) {} // ConcurrentModificationException occasionally happens
	}

	/**
	 * Writes a Transitions to a file with checks for rules with duplicate names and codes (experimental)
	 * @param t
	 * @throws Exception
	 */
	public static void writeSafe(Transitions t) throws Exception {
		String code = t.getCode();		
		File newFile = new File(System.getProperty("user.dir") + "\\rules\\" + t.getName() + ".rule");
		if (getSavedTransitions().contains(code))
			throw new Exception("Duplicate code");
		else if (newFile.exists())
			throw new Exception("Duplicate name");
		write(t);
	}

	/**
	 * Writes a Transitions to a file without checks
	 * @param t
	 */
	public static void write(Transitions t) {
		String code = t.getCode();		
		File newFile = new File(System.getProperty("user.dir") + "\\rules\\" + t.getName() + ".rule");

		try {
			FileWriter writer = new FileWriter(newFile);
			writer.write(t.getName() + "\n" + code);
			writer.close();

			writer = new FileWriter(System.getProperty("user.dir") + "\\rules\\allTransitions.txt", true);
			writer.write(code + "\n");
			writer.close();
		} catch (Exception e) {}
	}

	/**
	 * Read Transitions with a given name from a file 
	 * @param name
	 * @return
	 */
	public static Transitions read(String name) {
		try {
			String[] parts = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "\\rules\\" + name + ".rule"))).split("\n");
			return new Transitions(parts[0], parts[1]);
		} catch (Exception e) {}
		return null;
	}

	/**
	 * EXPERIMENTAL FEATURE
	 * Get the codes for all saved transitions
	 * @return codes of all saved transitions
	 */
	public static HashSet<String> getSavedTransitions() {
		HashSet<String> savedTransitions = new HashSet<String>();
		try {
			String content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "\\rules\\.allTransitions.txt")));
			for (String s: content.split("\n"))
				savedTransitions.add(s);		
		} catch (Exception e) {

		}
		return savedTransitions;
	}
}
