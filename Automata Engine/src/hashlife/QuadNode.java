/**
 * @author Joshua Turner
 * @author Tomas Rokicki
 * 
 * The Quadtree is at the core of the Hashlife algorithm. Every parent node in a Quadtree has four children. In our case, using the QuadNode structure, we subdivide the grid into 
 * four sections (NW, NE, SW, SE) and subdivide each of those sections in turn, etc., until we reach singular cells. By representing the grid as a Quadtree, as well as canonicalizing 
 * QuadNodes (which are immutable) and memoizing the centered subnode of the QuadNode one generation forward, Hashlife takes advantage of the immense regularity in 2D cellular automata.
 * 
 * Much of the code inspired by https://www.drdobbs.com/jvm/an-algorithm-for-compressing-space-and-t/184406478.
 * I'd looked at it before I knew I was going to be redirecting my project to use it, hence the similarity between our code.
 */

package hashlife;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class QuadNode {
	final QuadNode nw, ne, sw, se; // Children of QuadNode
	final int level, population; // Height of QuadNode in the Quadtree
	QuadNode result; // The QuadNode (one level down) that results from evolving this QuadNode
	boolean updatedToTransition = false; // Whether result has been computed for the current ruleset
	
	/// CREATING NODES ///

	/**
	 * Constructor that creates a single cell in either the dead or alive state
	 * @param alive
	 */
	public QuadNode(boolean alive) {
		nw = ne = sw = se = result = null;
		level = 0;
		population = alive ? 1 : 0;
	}

	/**
	 * Constructor that creates a new QuadNode from four smaller QuadNodes
	 * @param nw
	 * @param ne
	 * @param sw
	 * @param se
	 */
	public QuadNode(QuadNode nw, QuadNode ne, QuadNode sw, QuadNode se) {
		this.nw = nw;
		this.ne = ne;
		this.sw = sw;
		this.se = se;

		level = nw.level + 1;
		population = nw.population + ne.population + sw.population + se.population;
	}

	/**
	 * Creates a canonicalized QuadNode using QuadNode(alive) and intern()
	 * @param alive
	 * @return new QuadNode(alive).intern();
	 */
	public static QuadNode newNode(boolean alive) {
		return new QuadNode(alive).intern();
	}

	/**
	 * Creates a canonicalized QuadNode using QuadNode(nw, ne, sw, se) and intern()
	 * @param nw
	 * @param ne
	 * @param sw
	 * @param se
	 * @return new QuadNode(nw, ne, sw, se).intern();
	 */
	public static QuadNode newNode (QuadNode nw, QuadNode ne, QuadNode sw, QuadNode se) {
		return new QuadNode(nw, ne, sw, se).intern();
	}

	/**
	 * Creates an empty QuadNode of side length 2^level
	 * @param level
	 * @return (2^level) x (2^level) QuadNode with all dead squares
	 */
	public static QuadNode newEmpty(int level) {
		if (level == 0)
			return newNode(false);
		QuadNode subnode = newEmpty(level - 1);
		return newNode(subnode, subnode, subnode, subnode);
	}

	/**
	 * Gets the centered subnode
	 * @return newNode(nw.se, ne.sw, sw.ne, se.nw)
	 */
	QuadNode centeredSubNode() {
		return newNode(nw.se, ne.sw, sw.ne, se.nw);
	}

	/**
	 * Gets the horizontally centered subnode between two adjacent nodes
	 * @param w
	 * @param e
	 * @return newNode(w.ne.se, e.nw.sw, w.se.ne, e.sw.nw)
	 */
	QuadNode horizontalSubNode(QuadNode w, QuadNode e) {
		return newNode(w.ne.se, e.nw.sw, w.se.ne, e.sw.nw);
	}

	/**
	 * Gets the vertically centered subnode between two adjacent nodes
	 * @param n
	 * @param s
	 * @return newNode(n.sw.se, n.se.sw, s.nw.ne, s.ne.nw)
	 */
	QuadNode verticalSubNode(QuadNode n, QuadNode s) {
		return newNode(n.sw.se, n.se.sw, s.nw.ne, s.ne.nw);
	}

	/**
	 * Gets the centered subsubnode 
	 * @return newNode(nw.se.se, ne.sw.sw, sw.ne.ne, se.nw.nw)
	 */
	QuadNode centeredSubSubNode() {
		return newNode(nw.se.se, ne.sw.sw, sw.ne.ne, se.nw.nw);
	}

	/**
	 * Doubles the size of the QuadNode by bordering it with empty space
	 * @return
	 */
	QuadNode expand() {
		QuadNode empty = newEmpty(level - 1);
		return newNode(newNode(empty, empty, empty, nw),
				newNode(empty, empty, ne, empty),
				newNode(empty, sw, empty, empty),
				newNode(se, empty, empty, empty));
	}

	/// CANONICALIZING NODES ///

	static HashMap<QuadNode, QuadNode> hashMap = new HashMap<QuadNode, QuadNode>(); // Stores canonical nodes

	/**
	 * Creates a unique hash code for the QuadNode
	 * @return System.identityHashCode(nw) + 
			   11 * System.identityHashCode(ne) +
			  101 * System.identityHashCode(sw) +
			 1007 * System.identityHashCode(se)
	 */
	public int hashCode() {
		if (level == 0)
			return population;
		return System.identityHashCode(nw) + 
				11 * System.identityHashCode(ne) +
				101 * System.identityHashCode(sw) +
				1007 * System.identityHashCode(se);
	}

	/**
	 * Determines if two QuadNodes are equal
	 * @return this and o have the same subnodes
	 */
	public boolean equals(Object o) {
		QuadNode q = (QuadNode) o;
		if (level != q.level)
			return false;
		if (level == 0)
			return population == q.population;
		return nw == q.nw && ne == q.ne && sw == q.sw && se == q.se;
	}

	/**
	 * Canonicalizes the QuadNode
	 * @return hashMap.get(this) if hashMap.get(this) is null or this otherwise
	 */
	QuadNode intern() {
		QuadNode node = hashMap.get(this);
		if (node != null)
			return node;
		hashMap.put(this, this);
		return this;
	}

	/// EVOLVING NODES ///

	/**
	 * Evolves the interior of the QuadNode according to the rules in the parent NodeManager
	 * @return the new interior node
	 */

	public QuadNode evolve() {
		if (result != null && updatedToTransition) // If there is a cached result, then return that
			return result;
		if (level == 2) { // If the level is 2, then compute the transition directly
			QuadNode[] nines = new QuadNode[] {newNode(nw, 
											   		   newNode(nw.ne, ne.nw, nw.se, ne.sw), 
											   		   newNode(nw.sw, nw.se, sw.nw, sw.ne), 
											   		   newNode(nw.se, ne.sw, sw.ne, se.nw)), 
											   newNode(newNode(nw.ne, ne.nw, nw.se, ne.sw),
													   ne,
													   newNode(nw.se, ne.sw, sw.ne, se.nw),
													   newNode(ne.sw, ne.se, se.nw, se.ne)), 
											   newNode(newNode(nw.sw, nw.se, sw.nw, sw.ne),
													   newNode(nw.se, ne.sw, sw.ne, se.nw),
													   sw,
													   newNode(sw.ne, se.nw, sw.se, se.sw)), 
											   newNode(newNode(nw.se, ne.sw, sw.ne, se.nw),
													   newNode(ne.sw, ne.se, se.nw, se.ne),
													   newNode(sw.ne, se.nw, sw.se, se.sw),
													   se)}; // Creates the neighborhoods of the four center squares of the 4x4 supersquare
			QuadNode[] results = new QuadNode[4];
			for (int i = 0; i < 4; i++) {
				QuadNode q = nines[i];
				int n = countNeighbors(q);
				if ((q.nw.se.population == 0 && NodeManager.t.birth.contains(n)) || (q.nw.se.population == 1 && NodeManager.t.survive.contains(n))) // Determines how a single cell evolves
					results[i] = newNode(true);
				else
					results[i] = newNode(false);
			}
			result = newNode(results[0], results[1], results[2], results[3]);
			updatedToTransition = true;
			return result;
		}											  		//		XXXXXXXXXXXX
		QuadNode n1 = nw.centeredSubNode(), 		 	 	// 		X n1 n2 n3 X
				 n2 = horizontalSubNode(nw, ne),	  		// 		X n4 n5 n6 X	X = other cells
				 n3 = ne.centeredSubNode(),			  		// 		X n7 n8 n9 X
				 n4 = verticalSubNode(nw, sw),		 		//		XXXXXXXXXXXX
				 n5 = centeredSubSubNode(),
				 n6 = verticalSubNode(ne, se),
				 n7 = sw.centeredSubNode(),
				 n8 = horizontalSubNode(sw, se),
				 n9 = se.centeredSubNode();
		result = newNode(newNode(n1, n2, n4, n5).evolve(),
						 newNode(n2, n3, n5, n6).evolve(),
						 newNode(n4, n5, n7, n8).evolve(),
						 newNode(n5, n6, n8, n9).evolve()); // Otherwise, recursively evolve children
		updatedToTransition = true;
		return result;
	}		

	/**
	 * Counts the live neighbors of a center cell in a 3x3 node q
	 * @param q
	 * @return
	 */
	public static int countNeighbors(QuadNode q) { 
		if (NodeManager.t.neighborhood == 0) // Moore neighborhood, a 3x3 square (all adjacent squares)
			return (q.nw.population - q.nw.se.population) + // subtraction avoids counting the center square
					(q.ne.ne.population + q.ne.se.population) +
					(q.sw.sw.population + q.sw.se.population) +
					(q.se.se.population);
		else if (NodeManager.t.neighborhood == 1) // Von Neumann neighborhood, a cross (all squares with bordering edges)
			return q.nw.ne.population +
					q.nw.sw.population +
					q.se.sw.population +
					q.se.ne.population;
		else
			return (q.nw.population - q.nw.se.population) + // Hexagonal neighborhood, a Moore neighborhood without the upper right and lower left corners
					(q.se.population - q.nw.se.population);
	}

	/// MODIFYING NODES ///	
	
	/**
	 * Sets the state of the cell at (x, y), relative to the QuadNode's coordinate system (intersection of children is origin)
	 * @param x
	 * @param y
	 * @param alive
	 * @return the QuadNode except with (x, y) in the new state
	 */
	public QuadNode setCell(int x, int y, boolean alive) {
		if (level == 0) // Base case: At a 1x1 QuadNode, so we have reached to cell
			return newNode(alive);
		int offset = 1 << (level - 2); // Recursive case: Depending on where the point is relative to the current x and y, shift and recurse accordingly
		if (x >= 0)
			if (y >= 0)
				return newNode(nw, ne.setCell(x - offset, y - offset, alive), sw, se);
			else
				return newNode(nw, ne, sw, se.setCell(x - offset, y + offset, alive));
		else
			if (y >= 0)
				return newNode(nw.setCell(x + offset, y - offset, alive), ne, sw, se);
			else
				return newNode(nw, ne, sw.setCell(x + offset, y + offset, alive), se);
	}
	
	/**
	 * Inverts the cell at (x, y), relative to the QuadNodes coordinate system
	 * @param x
	 * @param y
	 * @return the QuadNode except (x, y) is inverted
	 */
	public QuadNode flipCell(int x, int y) {
		if (level == 0) // Base case: At a 1x1 QuadNode, so we have reached to cell
			return newNode(population == 0);
		int offset = 1 << (level - 2); // Recursive case: Depending on where the point is relative to the current x and y, shift and recurse accordingly
		if (x >= 0)
			if (y >= 0)
				return newNode(nw, ne.flipCell(x - offset, y - offset), sw, se);
			else
				return newNode(nw, ne, sw, se.flipCell(x - offset, y + offset));
		else
			if (y >= 0)
				return newNode(nw.flipCell(x + offset, y - offset), ne, sw, se);
			else
				return newNode(nw, ne, sw.flipCell(x + offset, y + offset), se);
	}
	
	
	/**
	 * Gets the state of the cell at (x, y), relative to the QuadNode's coordinate system
	 * @param x
	 * @param y
	 * @param cell
	 * @return whether the cell at (x, y) is alive or dead
	 */
	public boolean getCell(int x, int y, QuadNode cell) {
		if (level == 0)
			return cell.population == 1;
		int offset = 1 << (level - 2) ;
		if (x >= 0)
			if (y >= 0)
				return ne.getCell(x - offset, y - offset, ne);
			else
				return se.getCell(x - offset, y + offset, se);
		else
			if (y >= 0)
				return nw.getCell(x + offset, y - offset, nw);
			else
				return sw.getCell(x + offset, y + offset, sw);
	}
}
