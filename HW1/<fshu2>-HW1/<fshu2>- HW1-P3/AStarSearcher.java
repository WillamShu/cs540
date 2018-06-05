
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
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// explored list is a Boolean array that indicates if a state associated
		// with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Frontier used to store the StateFValuePair
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();

		// Define the first state
		State firstState = new State(maze.getPlayerSquare(), null, maxDepthSearched, maxSizeOfFrontier);
		// Using the equation provided to calculate the distance between the
		// first state and the goal state
		double distance = Math
				.sqrt((firstState.getX() - maze.getGoalSquare().X) * (firstState.getX() - maze.getGoalSquare().X)
						+ (firstState.getY() - maze.getGoalSquare().Y) * (firstState.getY() - maze.getGoalSquare().Y));
		// Define a StateFValuePair for firstState
		StateFValuePair fValue = new StateFValuePair(firstState, firstState.getGValue() + distance);

		// Add the start square to the frontier
		frontier.add(fValue);

		// Make it explored
		explored[firstState.getX()][firstState.getY()] = true;

		// If the frontier is not empty, poll out the first item in the frontier
		// and find its sucessors
		while (!frontier.isEmpty()) {
			StateFValuePair fValue1 = frontier.poll();
			State currState = fValue1.getState();
			// Every time current state got poll out, the number of nodes
			// expanded increase one
			noOfNodesExpanded++;

			// If we got a bigger depth, replace the maxDepthSearch with the
			// bigger one
			if (currState.getDepth() > maxDepthSearched) {
				maxDepthSearched = currState.getDepth();
			}

			// if we got the goal square
			if (currState.getX() == maze.getGoalSquare().X && currState.getY() == maze.getGoalSquare().Y) {

				// We keep going back to the parents
				currState = currState.getParent();

				// Every time we find a parents, we increase the cost by one
				cost++;

				// While current state is not the start square, we set all the
				// square on the path to "."
				while (currState.getX() != maze.getPlayerSquare().X || currState.getY() != maze.getPlayerSquare().Y) {
					maze.setOneSquare(currState.getSquare(), '.');
					currState = currState.getParent();
					cost++;
				}
				// return true if a solution has been found
				return true;
			}
			// add successors of the current state to the frontier
			for (State itr : currState.getSuccessors(explored, maze)) {
				double distance1 = Math
						.sqrt((itr.getX() - maze.getGoalSquare().X) * (itr.getX() - maze.getGoalSquare().X)
								+ (itr.getY() - maze.getGoalSquare().Y) * (itr.getY() - maze.getGoalSquare().Y));
				StateFValuePair newPair = new StateFValuePair(itr, itr.getGValue() + distance1);
				frontier.add(newPair);
				explored[itr.getX()][itr.getY()] = true;
			}

			// update maxSizeOfFrontier size if the frontier size is larger than
			// maxSizeOfFrontier
			if (frontier.size() > maxSizeOfFrontier) {
				maxSizeOfFrontier = frontier.size();
			}
		}

		// return false if no solution
		return false;
	}

}
