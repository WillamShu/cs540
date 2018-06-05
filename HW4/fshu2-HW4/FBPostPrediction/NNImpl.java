///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            NNImpl
// Files:            HW4.java Instance.java NNImpl.java Node.java
//					 NodeWeightPair.java
// Semester:         (CS 540) Spring 2017
//
// Author:           William Shu
// Email:            fshu2@wisc.edu
// CS Login:         wshu
// Lecturer's Name:  Erin Winter
// Lab Section:      None
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
//                   CHECK ASSIGNMENT PAGE TO see IF PAIR-PROGRAMMING IS ALLOWED
//                   If pair programming is allowed:
//                   1. Read PAIR-PROGRAMMING policy (in cs302 policy) 
//                   2. choose a partner wisely
//                   3. REGISTER THE TEAM BEFORE YOU WORK TOGETHER 
//                      a. one partner creates the team
//                      b. the other partner must join the team
//                   4. complete this section for each program file.
//
// Pair Partner:     Zhiqian Ma
// Email:            zma69@wisc.edu
// CS Login:         zhiqian
// Lecturer's Name:  Erin Winter
// Lab Section:      None
//
//////////////////////////// 80 columns wide //////////////////////////////////
/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public Node outputNode=null;// single output node that represents the result of the regression
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=1;
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
			


		Node node=new Node(4);
		//Connecting output node with hidden layer nodes
		for(int j=0;j<hiddenNodes.size();j++)
		{
			NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[j]);
			node.parents.add(nwp);
		}	
		outputNode = node;
			
	}
	
	/**
	 * Get the output from the neural network for a single instance. That is, set the values of the training instance to
	the appropriate input nodes, percolate them through the network, then return the activation value at the single output
	node. This is your estimate of y. 
	 */
	
	public double calculateOutputForInstance(Instance inst)
	{
		for(int i = 0; i < inst.attributes.size(); i++){
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}

		for(int i = 0; i < hiddenNodes.size(); i++){
			hiddenNodes.get(i).calculateOutput();
		}

		outputNode.calculateOutput();

		return outputNode.getOutput();
	}
	

	
	
	
	/**
	 * Trains a neural network with the parameters initialized in the constructor for the number of epochs specified in the instance variable maxEpoch.
	 * The parameters are stored as attributes of this class, namely learningRate (alpha) and trainingSet.
	 * Implement stochastic graident descent: update the network weights using the deltas computed after each the error of each training instance is computed.
	 * An single epoch looks at each instance training set once, so you should update weights n times per epoch if you have n instances in the training set.
	 */
	public double gprime(double x) {
		if (x > 0) {
			return 1.0;
		} else {
			return 0.0;
		}
	}
	public void train()
	{
		double output = 0;
		double teacher = 0;
		double error = 0;
		double value = 0;
		double adjustedWeight = 0;


		for(int i = 0; i < this.maxEpoch; i++){
			for(Instance a : trainingSet){
				output = calculateOutputForInstance(a);
				teacher = a.output;
				error = teacher - output;

				// hidden to output
				for(int j = 0; j < outputNode.parents.size(); j++){
					NodeWeightPair hidden = outputNode.parents.get(j);
					adjustedWeight = this.learningRate * hidden.node.getOutput() * error * gprime(output);
					value = hidden.weight * error * gprime(output);
					hidden.weight += adjustedWeight;

					// input to hidden
					if(hidden.node.parents != null){
						for(int k = 0; k < hidden.node.parents.size(); k++){
							NodeWeightPair inputNode = hidden.node.parents.get(k);
							inputNode.weight += this.learningRate * inputNode.node.getOutput() * gprime(hidden.node.getOutput()) * value;
						}
					}
				}
			}
		}

	

	}
	/**
	 * Returns the mean squared error of a dataset. That is, the sum of the squared error (T-O) for each instance
	in the dataset divided by the number of instances in the dataset.
	 */
	

	public double getMeanSquaredError(List<Instance> dataset){
		double err = 0;
		for (Instance inst: dataset) {
			double output = calculateOutputForInstance(inst);
			err += (output - inst.output) * (output - inst.output);
		}
		return err / dataset.size();
	}
}
