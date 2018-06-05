///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            HMW1 Q3
// Files:            AStarSearcher.java DepthFirstSearcher.java FindPath.java 
// 					 IO.java Maze.java Searcher.java Square.java State.java 
// 					 StateFValuePair.java
// Semester:         (CS540) Spring 2017
//
// Author:           William Shu
// Email:            fshu2@wisc.edu
// CS Login:         wshu
// Lecturer's Name:  Erin Winter
// Lab Section:      None
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
//		I have discussed this question with Zhiqian Ma.
//
//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		
		// explored list is a 2D Boolean array that indicates if a state
		// associated with a given position in the maze has already been
		// explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// ...

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		State firstState = new State(maze.getPlayerSquare(), null, maxDepthSearched, maxSizeOfFrontier);
		
		// add the first state
		stack.push(firstState);
		
		// set the first state explored
		explored[firstState.getX()][firstState.getY()] = true;

		// if the stack is not empty, poll out the last state of the stack,
		while (!stack.isEmpty()) {
			State currState = stack.pollLast();
			// increase the number of nodes expanded
			noOfNodesExpanded++;
			
			// If current state's depth is larger than the maxDepthSearched, update it
			if (currState.getDepth() > maxDepthSearched) {
				maxDepthSearched = currState.getDepth();
			}
			
			// if current square is the goal square, then we find the solution
			if (currState.getX() == maze.getGoalSquare().X && currState.getY() == maze.getGoalSquare().Y) {
				
				// keep retrieving back to the start square
				currState = currState.getParent();
				cost++;
				
				// if we have not retrieved back to the start square, we set all the path squares to "."
				while (currState.getX() != maze.getPlayerSquare().X || currState.getY() != maze.getPlayerSquare().Y) {
					maze.setOneSquare(currState.getSquare(), '.');
					currState = currState.getParent();
					cost++;
				}
				return true;
			}
			
			// add all the successors of current state to the frontier
			for (State itr : currState.getSuccessors(explored, maze)) {
				stack.addLast(itr);
				explored[itr.getX()][itr.getY()] = true;
			}
			
			// update maxSizeOfFrontier if the stack size if larger than it
			if (stack.size() > maxSizeOfFrontier) {
				maxSizeOfFrontier = stack.size();
			}
		}

		// return false if no solution found
		return false;
	}
}
