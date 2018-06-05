/****************************************************************
 * studPlayer.java Implements MiniMax search with A-B pruning and iterative
 * deepening search (IDS). The static board evaluator (SBE) function is simple:
 * the # of stones in studPlayer's mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for
 * educational purposes provided that (1) you do not distribute or publish
 * solutions, (2) you retain the notice, and (3) you provide clear attribution
 * to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his
 * TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu). Some
 * GUI componets are from Mancala Project in Google code.
 */

// ################################################################
// studPlayer class
// ################################################################

public class studPlayer extends Player {

	/*
	 * Use IDS search to find the best move. The step starts from 1 and keeps
	 * incrementing by step 1 until interrupted by the time limit. The best move
	 * found in each step should be stored in the protected variable move of
	 * class Player.
	 */
	public void move(GameState state) {
		int maxStep = 0;
		while (true) {
			maxStep++;
			move = maxAction(new GameState(state), maxStep);

		}
	}

	// Return best move for max player. Note that this is a wrapper function
	// created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the
	// best move by comparing the
	// sbe values returned by maxSBE. This function should call minAction with 5
	// parameters.
	public int maxAction(GameState state, int maxDepth) {
		int alpha = -100;
		int beta = +100;
		int v = -100;
		int nextMoveValue;
		int bestMove = 0;

		for (int i = 0; i < 6; i++) {
			if (state.stoneCount(i) > 0) {
				GameState newState = new GameState(state);
				if (newState.applyMove(i)) {
					nextMoveValue = maxAction(newState, 1, maxDepth, alpha, beta);
				} else {
					nextMoveValue = minAction(newState, 1, maxDepth, alpha, beta);
				}
				if (nextMoveValue > v) {
					v = nextMoveValue;
					bestMove = i;
				}
				if (v >= beta) {
					return bestMove;
				}
				if (alpha < v) {
					alpha = v;
				}

			}
		}
		return bestMove;
	}

	// return sbe value related to the best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta) {
		int v = -100;
		int nextMoveValue;
		
		if (state.gameOver() || currentDepth == maxDepth){
			return sbe(state);
		}

		for (int i = 0; i < 6; i++) {
			if (state.stoneCount(i) > 0) {
				GameState newState = new GameState(state);
				if (newState.applyMove(i)) {
					nextMoveValue = maxAction(newState, currentDepth+1, maxDepth, alpha, beta);
				} else {
					nextMoveValue = minAction(newState, currentDepth+1, maxDepth, alpha, beta);
				}
				if (nextMoveValue > v) {
					v = nextMoveValue;
				}
				if (v >= beta) {
					return v;
				}
				if (alpha < v) {
					alpha = v;
				}

			}
		}
		return v;
	}

	// return sbe value related to the bset move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta) {
		int v = +100;
		int nextMoveValue;
		
		if (state.gameOver() || currentDepth == maxDepth){
			return sbe(state);
		}

		for (int i = 0; i < 6; i++) {
			if (state.stoneCount(i) > 0) {
				GameState newState = new GameState(state);
				if (newState.applyMove(i)) {
					nextMoveValue = minAction(newState, currentDepth+1, maxDepth, alpha, beta);
				} else {
					nextMoveValue = maxAction(newState, currentDepth+1, maxDepth, alpha, beta);
				}
				if (nextMoveValue < v) {
					v = nextMoveValue;
				}
				if (v <= alpha) {
					return v;
				}
				if (beta > v) {
					beta = v;
				}

			}
		}
		return v;
	}

	// the sbe function for game state. Note that in the game state, the bins
	// for current player are always in the bottom row.
	private int sbe(GameState state) {
		return state.stoneCount(6)-state.stoneCount(13);
	}
}
