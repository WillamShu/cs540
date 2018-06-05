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

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;

	// Maintain the gValue (the distance from start)
	// You may not need it for DFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            explored[i][j] is true if (i,j) is already explored
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] explored, Maze maze) {
		// find the up down left right square value of the current square
		char upSquareCh = maze.getSquareValue(getX() - 1, getY());
		char downSquareCh = maze.getSquareValue(getX() + 1, getY());
		char leftSquareCh = maze.getSquareValue(getX(), getY() - 1);
		char rightSquareCh = maze.getSquareValue(getX(), getY() + 1);
		ArrayList<State> validSuccessors = new ArrayList<State>();
		// check all four neighbors (up, right, down, left)
		// each successor's depth and gValue are
		// +1 of this object.
		if (leftSquareCh != 'S' && leftSquareCh != '%' && explored[getX()][getY() - 1] == false) {
			Square leftSquare = new Square(getX(), getY() - 1);
			State leftState = new State(leftSquare, this, gValue + 1, depth + 1);
			validSuccessors.add(leftState);
		}
		if (downSquareCh != 'S' && downSquareCh != '%' && explored[getX() + 1][getY()] == false) {
			Square downSquare = new Square(getX() + 1, getY());
			State downState = new State(downSquare, this, gValue + 1, depth + 1);
			validSuccessors.add(downState);
		}
		if (rightSquareCh != 'S' && rightSquareCh != '%' && explored[getX()][getY() + 1] == false) {
			Square rightSquare = new Square(getX(), getY() + 1);
			State rightState = new State(rightSquare, this, gValue + 1, depth + 1);
			validSuccessors.add(rightState);
		}
		if (upSquareCh != 'S' && upSquareCh != '%' && explored[getX() - 1][getY()] == false) {
			Square upSquare = new Square(getX() - 1, getY());
			State upState = new State(upSquare, this, gValue + 1, depth + 1);
			validSuccessors.add(upState);
		}


		return validSuccessors;
	}

	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze
	 *            initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X && square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the BFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
}
