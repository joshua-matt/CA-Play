Welcome to CA Play, a software program for simulating and interacting with 2-state cellular automata!

WHAT IS A CELLULAR AUTOMATON?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A 2-state cellular automaton is a grid of cells, where each cell is either "alive" or "dead", equipped with a set 
of rules whereby, based on the number of its neighbors that are "on", each cell's state in the next generation 
is determined. For example, the most famous cellular automaton, called Conway's Game of Life, has the following 
rules:
	1. If a live cell has exactly 2 or 3 live neighbors, it survives to the next generation.
	2. If a dead cell has exactly 3 live neighbors, it becomes a live cell.
	3. Otherwise, the cell dies or stays dead.
From these three simple rules, incredibly complex patterns emerge - stable patterns that endure unless disturbed,
oscillators that cycle through a set of states, and gliders that precess across the grid. The beauty of Life,
and of cellular automata in general, is this complexity: that behavior both unpredictable yet ordered can result
from simple rules.

WHAT IS THIS PROGRAM, ANYWAYS?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
As stated above, CA Play provides you with an environment in which to experiment and play with simple cellular
automata. The primary element of the program is the grid, which you can manipulate with the several tools provided
to you. Though the program comes with preset rules and structures, you can create and save your own.

HOW DO I USE THIS PROGRAM?
~~~~~~~~~~~~~~~~~~~~~~~~~~
This program has several parts, each of which will be covered in detail: the tool panel, the rule setter,
the structure selector, the structure creator, the player, and the grid.


THE STRUCTURE CREATOR
~~~~~~~~~~~~~~~~~~~~~
Have a cool pattern you want to create, but don't want to draw it manually on the grid every time? The 
Structure Creator has got you covered. In this grid environment, you only have to draw the structure once,
name it, then save it. Once you've done this, you can place the pattern wherever you want on the grid using
a single click of the mouse. In the Structure Creator, not only can you draw cells, but you can also move around,
zoom, and select regions to fill, delete, randomize, or invert.

THE TOOL PANEL
~~~~~~~~~~~~~~
The tool panel allows you to select which tool you want to use and view information about it. The tools displayed, 
from left to right, are:
	- Mover: The mover allows you to click and drag to move around the grid. Right click to return to the origin.
	- Zoomer: For touchpad folk, the zoomer allows you to left click to zoom in and right click to zoom out.
		  For mouse folk, the same effect can be achieved by scrolling the mouse wheel.
	- Painter: The painter allows you to draw cells on the grid by clicking and dragging. Left click draws
		   live cells, right click draws dead cells.
	- Selector: The selector allows you to select regions of the grid and then choose an action to 
		    perform on that region, such as filling it with live cells.
	- Structure Adder: The Structure Adder allows you to add a structure to the grid by clicking or
			   select a region of the grid to export to the Structure Creator.
* Note that the Structure Adder is not present in the Structure Creator. If you wish to incorporate smaller structures
  into a larger structure, draw it on the grid, then select it using the structure adder.

THE STRUCTURE SELECTOR
~~~~~~~~~~~~~~~~~~~~~~
The structure selector panel shows you all of the structures you have saved, displaying their appearance and name. To view a
structure's description, mouse over it. Furthermore, the structure selector, unsurprisingly, allows you to select which
structure you want to add to the grid. You can also choose to edit a structure, which opens it in the Structure Creator,
or delete it, which permanently removes the structure.

THE RULE SETTER
~~~~~~~~~~~~~~~
The rule setter panel allows you to create, save, load, and delete rulesets. 
      - To create a new ruleset, simply type the number of neighbors a cell needs to survive and to be born into the 
	corresponding text fields. For the more adventurous, there is also the ability to change the neighborhood shape that 
	is used to calculate neighbors. Neighborhoods include the Moore neighborhood, which consists of all 8 adjacent cells, 
	the Von Neumann neighborhood, which consists of all 4 orthogonal cells (a cross), and the Hexagonal neighborhood, 
	which is a Moore neighborhood minus the upper-right and lower-left cells. 
      - To save a ruleset, click "Save" and then name it. You will then be able to load the ruleset in the future.
      - To load a ruleset, click the dropdown menu and select the ruleset you want.
      - To delete a ruleset, just click delete.
* Although rulesets can be changed while the grid is evolving (and this looks pretty cool), sometimes it doesn't work. If this
  happens, pause evolution and click 'Apply'.

THE PLAYER
~~~~~~~~~~
The player is located at the bottom of the main frame. Through this panel, you can control the playback of grid evolution
by playing/pausing, stepping forward and back, and changing evolution speed.

THE GRID
~~~~~~~~
The grid is where it all comes together. With a tool selected, you can click on the grid to edit it or maneuver around it.
By using the player, you can change the playback of the grid. You can also save and load grids. To do this, click "File"
at the top of the screen and select the file you want to save/load.

This about wraps it up! I hope you enjoy using CA Play as much as I have making it. 

~ Joshua Turner
	