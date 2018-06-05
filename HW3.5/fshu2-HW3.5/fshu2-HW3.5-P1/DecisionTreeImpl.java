import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	// ordered list of class labels
	private List<String> labels;
	// ordered list of attributes
	private List<String> attributes;
	// map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues;

	private DataSet trainingSet;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train:
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
		trainingSet = train;
		List<Instance> instances = train.instances;
		assert (instances.size() > 0);
		
		// Add all the attributes in the array first
		ArrayList<Integer> attributesLeft = new ArrayList<Integer>();
		for (int i = 0; i < train.attributes.size(); i++) {
			attributesLeft.add(i);
		}
		
		buildTree(null, instances, attributesLeft, "");
	}
	private int lookUpIndex(String a, List<String> b){
		return b.indexOf(a);
	}
	
	private int checkSameLabel(List<Instance> a){
		int b = -1;
		for (Instance instance: a){
			int label = lookUpIndex(instance.label, trainingSet.labels);
			if (b != -1 && b != label){
				return -1;
			}
			b = label;
		}
		return b;
	}
	private void buildTree(DecTreeNode parent, List<Instance> instances, List<Integer> attributesLeft, String attributeName){
		if(instances.size() == 0){
			parent.addChild(new DecTreeNode(trainingSet.labels.get(0),"", attributeName, true));
			return;
		}
		int label = checkSameLabel(instances);
		if(label != -1){
			parent.addChild(new DecTreeNode(trainingSet.labels.get(label),"", attributeName, true));
			return;
		}
		
		if (attributesLeft.size() == 0){
			parent.addChild(new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), "", attributeName, true));
			return;
		}
		double entropy = calculateEntropy(instances, trainingSet.labels.size());
		Integer highestEntropyId = -1;
		double highestMutualInfo = -1;
		for (int i = 0; i < attributesLeft.size(); i++) { //For each attribute
			double conditionalEntropy = calculateConditionalEntropy(attributesLeft.get(i), instances, trainingSet.attributeValues.get(trainingSet.attributes.get(attributesLeft.get(i))).size(), trainingSet.labels.size());
			double mutualInformation = entropy - conditionalEntropy;
			if (mutualInformation > highestMutualInfo){
				highestMutualInfo = mutualInformation;
				highestEntropyId = attributesLeft.get(i);
			}
		}
		DecTreeNode currentNode;
		if(parent == null){
			root = new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), trainingSet.attributes.get(highestEntropyId), "ROOT", isPure(instances) || attributesLeft.size() == 1);
			currentNode = root;
		} else {
			currentNode = new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), trainingSet.attributes.get(highestEntropyId), attributeName, isPure(instances) || attributesLeft.size() == 0);
			parent.addChild(currentNode);
		}
		List<String> values = trainingSet.attributeValues.get(trainingSet.attributes.get(highestEntropyId));
		int noOfChildren = values.size();
		ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
		for (int i = 0; i < noOfChildren; i++) {
			labelInstances.add(new ArrayList<Instance>());
		}
		for (Instance instance : instances) {
			labelInstances.get(lookUpIndex(instance.attributes.get(highestEntropyId), values)).add(instance);
		}
		for (int i = 0; i < noOfChildren; i++) {
			int attrIndex = attributesLeft.indexOf(highestEntropyId);
			attributesLeft.remove(attrIndex);
			buildTree(currentNode, labelInstances.get(i), attributesLeft, trainingSet.attributeValues.get(trainingSet.attributes.get(highestEntropyId)).get(i));
			attributesLeft.add(attrIndex, highestEntropyId);
		}
	}
	
	private boolean isPure(List<Instance> instances){
		String canidate = null;
		for (Instance instance : instances) {
			if (canidate == null){
				canidate = instance.label;
				continue;
			}
			if (canidate != instance.label){
				return false;
			}
		}	
		return true;
	}
	
	private int getMajority(List<Instance> instances, int noOfLabelTypes){
		assert(instances.size() > 0);
		int[] labelCounts = new int[noOfLabelTypes];
		for (Instance instance : instances) {
			labelCounts[lookUpIndex(instance.label, trainingSet.labels)]++;
		}	
		int majorityCanidate = -1;
		int canidateCount = -1;
		for (int i = 0; i < labelCounts.length; i++) {
			if(labelCounts[i] > canidateCount){
				majorityCanidate = i;
				canidateCount = labelCounts[i];
			}
		}
		return majorityCanidate;
	}
	
	private double calculateEntropy(List<Instance> instances, int noOfLabelTypes){
		int[] labelCounts = new int[noOfLabelTypes];
		int totalInstances = instances.size();
 
		for (Instance instance : instances) {
			labelCounts[lookUpIndex(instance.label, trainingSet.labels)]++;
		}
		double entropy = 0;
		for (int i = 0; i < labelCounts.length; i++) {
			if (totalInstances != 0 && labelCounts[i] != 0) {
				double probability = Double.valueOf(labelCounts[i])/Double.valueOf(totalInstances); 
				entropy += probability * Math.log10(probability) / Math.log10(2);
			}
		}
		return -entropy;
	}
	
	private double calculateConditionalEntropy(int attributeId, List<Instance> instances, int noOfAtrributeTypes, int noOfLabelTypes){

		String attrStr = trainingSet.attributes.get(attributeId);
		List<String> attrValues = trainingSet.attributeValues.get(attrStr);
		int[] labelCounts = new int[attrValues.size()];
		ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
		//Create empty lists
		for (int i = 0; i < attrValues.size(); i++) {
			labelInstances.add(new ArrayList<Instance>());
		}
		int totalInstances = instances.size();
		for (Instance instance : instances) {
			int attrValueIndex = lookUpIndex(instance.attributes.get(attributeId), attrValues);
			labelCounts[attrValueIndex]++;
			labelInstances.get(attrValueIndex).add(instance);
		}
		double conditionalEntropy = 0;
		for (int i = 0; i < labelCounts.length; i++) {
			if (totalInstances != 0 && labelCounts[i] != 0) {
				double probability = Double.valueOf(labelCounts[i])/Double.valueOf(totalInstances); 
				double subConditionalEntropy = calculateEntropy(labelInstances.get(i), noOfLabelTypes);
				conditionalEntropy += probability * subConditionalEntropy;
			}
		}
		
		return conditionalEntropy;
	}


	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train:
	 *            the training set
	 * @param tune:
	 *            the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
		trainingSet = train;
		List<Instance> instances = train.instances;
		assert (instances.size() > 0);
		
		// Add all the attributes in the array first
		ArrayList<Integer> attributesLeft = new ArrayList<Integer>();
		for (int i = 0; i < train.attributes.size(); i++) {
			attributesLeft.add(i);
		}
		
		buildTree(null, instances, attributesLeft, "");
		Prune(tune);
	}

	private DecTreeNode pruneParent = null;
	private DecTreeNode pruneNewChild = null;
	private int pruneChild;
	private double pruneAcc = 0;
	
	private void TraverseTreePrune(DecTreeNode cur, DataSet tune, List<Instance> instances) {
		if (cur.terminal) {
			return;
		}
		int attrIndex = lookUpIndex(cur.attribute, trainingSet.attributes);
		List<String> values = trainingSet.attributeValues.get(cur.attribute);
		int noOfChildren = values.size();
		ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
		for (int i = 0; i < noOfChildren; i++) {
			labelInstances.add(new ArrayList<Instance>());
		}
		for (Instance instance : instances) {
			labelInstances.get(lookUpIndex(instance.attributes.get(attrIndex), values)).add(instance);
		}

		for (int i = 0; i < cur.children.size(); ++i) {
			DecTreeNode child = cur.children.get(i);
			if (child.terminal) continue;
			DecTreeNode newNode = new DecTreeNode(trainingSet.labels.get(getMajority(labelInstances.get(i), trainingSet.labels.size())), "", values.get(i), true);
			cur.children.remove(i);
			cur.children.add(i, newNode);
			double acc = getAccuracy(tune);
			if (acc > pruneAcc) {
				pruneAcc = acc;
				pruneChild = i;
				pruneNewChild = newNode;
				pruneParent = cur;
			}
			cur.children.remove(i);
			cur.children.add(i, child);			
		}
		for (int i = 0; i < noOfChildren; i++) {
			TraverseTreePrune(cur.children.get(i), tune, labelInstances.get(i));
		}

	}
	
	private void Prune(DataSet b){
		while(true){
			double accuracy = getAccuracy(b);
			pruneAcc = 0;
			List<Instance> instances = trainingSet.instances;
			TraverseTreePrune(root, b, instances);
			if (pruneAcc >= accuracy) {
				pruneParent.children.remove(pruneChild);
				pruneParent.children.add(pruneChild, pruneNewChild);
			} else {
				break;
			}			
		}
	}
	@Override
	public String classify(Instance instance) {

		// TODO: add code here
		//Build reverse index
				Map<String, Integer > attributeMap = new HashMap<String, Integer>();
				for (int i = 0; i < trainingSet.attributes.size(); i++) {
					attributeMap.put(trainingSet.attributes.get(i), i);
				}
				DecTreeNode currNode = root;
				while(true){
					if (currNode.terminal){
						break;
					} else {
						int currAttr = attributeMap.get(currNode.attribute);
						String instanceValue = instance.attributes.get(currAttr);
						DecTreeNode foundNode = null;
						for (DecTreeNode node : currNode.children) {
							if (node.parentAttributeValue.equals(instanceValue)){
								foundNode = node;
								break;
							}
						}
						assert(foundNode != null);
						currNode = foundNode;
					}
				}
				return currNode.label;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.trainingSet = train;
		// TODO: add code here
		double entropy = calculateEntropy(train.instances, train.labels.size());
		double mutualInformation = 0.0;
		for (int i = 0; i < train.attributes.size(); i++) { //For each attribute
			double conditionalEntropy = calculateConditionalEntropy(i, train.instances, train.attributeValues.get(train.attributes.get(i)).size(), train.labels.size());
			mutualInformation = entropy - conditionalEntropy;
			System.out.printf("%s %.5f\n", train.attributes.get(i) + " ", mutualInformation);
		}
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		for (int i = 0; i < this.labels.size(); i++) {
			if (label.equals(this.labels.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		for (int i = 0; i < this.attributes.size(); i++) {
			if (attr.equals(this.attributes.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for
	 * the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * /* Returns the accuracy of the decision tree on a given DataSet.
	 */
	@Override
	public double getAccuracy(DataSet ds) {
		// TODO, compute accuracy
		int correctClassification = 0;
		for (Instance instance : ds.instances) {
			if(classify(instance).equals(instance.label)){
				correctClassification++;
			}
		}
		double accuracy = Double.valueOf(correctClassification)/Double.valueOf(ds.instances.size());
		return accuracy;
	}
}
