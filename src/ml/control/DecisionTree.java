package ml.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.PrintWriter;

import ml.model.CategoricalType;

// TODO: Auto-generated Javadoc
/** Decision Tree Class
 *  <p>
 *  Represents a node of a decision tree over categorical data.
 *  <p>
 *  It includes the data used to build it (as a list of type array of ints).
 *  It also includes a list of CategoricalType for each attribute and output variable, which helps to map
 *  from the data representation to the actual named categories.
 *  <p>
 *  It also includes a list of it's descendants, identifying variables and additional information.
 *  
 *  @author Gabriel
 *  
 * */
public class DecisionTree {
	
	/** The attribute header list. */
	private List<CategoricalType> attributeHeaderList= new ArrayList<CategoricalType>();  //List of attributes.
	
	/** The class header. */
	private CategoricalType classHeader; //Output variable or class
	
	/** The data. */
	private List<int[]> data; //Data in the node as a list of instances, each represented as an array of numbers representing attributes + the final one representing the class.
	
	/** The descendants. */
	private List<DecisionTree> descendants = new ArrayList<DecisionTree>(); //Array with the immediate descendants of this specific tree.

	/*Identifying variables*/
	/** The id. */
	private int id=0;
	
	/** The parent id. */
	private int parentId=0;
	
	/** The id count. */
	private static int idCount=0; //To keep track of the number of nodes in the whole tree.

	//Extra information
	/** The attribute used. */
	private int attributeUsed=-1; //Attribute that is selected in this node for further branching.
	
	/** The incoming attribute index. */
	private int incomingAttributeIndex=-1;//Attribute that was selected in parent for branching.
	
	/** The incoming value index. */
	private int incomingValueIndex=-1; //Value of attribute used for generating this specific node.
	
	/** The output variable. */
	private int outputVariable=-1;//If it is a leaf, the outputVariable stores the learned outputValue.
	
	/** The level. */
	private int level=0;//Level of the node in the general tree. Values from 0 (root) to n (outermost leaf).

	/**
	 * Function to determine if the node is a leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf(){//Returns if the node is a Leaf (has no descendants)
		return descendants.size()==0;
	}

	/**
	 * Recursive function that returns a copy of the decision tree.
	 *
	 * @return copy of the tree
	 */	
	@SuppressWarnings("static-access")
	public DecisionTree getCopy(){//Returns a copy of the decision tree
		DecisionTree retTree = new DecisionTree();
		for (int i=0; i<attributeHeaderList.size(); i++){
			CategoricalType val=attributeHeaderList.get(i).getCopy();
			retTree.attributeHeaderList.add(val);
		}
		retTree.classHeader= classHeader.getCopy();
		retTree.data = new ArrayList <int[]>();
		for (int j=0; j<data.size(); j++){
			retTree.data.add(data.get(j));
		}
		retTree.attributeUsed=attributeUsed; 
		retTree.incomingAttributeIndex=incomingAttributeIndex;
		retTree.incomingValueIndex=incomingValueIndex;
		retTree.outputVariable=outputVariable;
		retTree.level=level;
		retTree.id=id;
		retTree.parentId=parentId;
		retTree.idCount=idCount;
		if (!isLeaf()){
			for (int k=0; k<descendants.size(); k++){
				DecisionTree aux=descendants.get(k).getCopy();
				retTree.descendants.add(aux);
			}
		}
		return retTree;
	}

	/**
	 * Recursive function that deletes a non-leaf node, based on it's id.
	 *
	 * @param nodeId the node id
	 */
	public void deleteNode(int nodeId){
		if (!this.isLeaf()){
			if (this.id==nodeId){
				descendants.clear();
				outputVariable=this.getMostCommonClass();
			}
			else{
				for (int i=0; i<descendants.size(); i++){
					descendants.get(i).deleteNode(nodeId);
				}
			}
		}
	}

	/**
	 * Function that returns the number of instances in the data of this node,
	 * which belong to a specific class, whose identifying number is passed
	 * as a parameter.
	 *
	 * @param classNum the class num
	 * @return the num instances in class
	 */
	private int getNumInstancesInClass(int classNum){ 
		int count=0;
		for (int i=0; i<data.size();i++){
			if (data.get(i)[attributeHeaderList.size()]==classNum){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Function that returns the identifying number of the most common
	 * class observed in the data of this node.
	 *
	 * @return the most common class
	 */
	private int getMostCommonClass(){ //Returns the most common class
		int mostCommonClass=0;
		int mostCommonValue=0;
		for (int j=0; j<classHeader.getNumCategories(); j++){
			int count=0;
			for (int i=0; i<data.size();i++){
				if (data.get(i)[attributeHeaderList.size()]==j){
					count++;
				}
			}
			if (count>mostCommonValue){
				mostCommonClass=j;
				mostCommonValue=count;
			}
		}
		return mostCommonClass;
	}

	/**
	 * Function that returns the entropy of the node.
	 *
	 * @return the entropy
	 */
	private double getEntropy(){//Returns the entropy of the node
		double tempEntropy=0;
		int casesByOutputValues[]=new int[classHeader.getNumCategories()];
		Arrays.fill(casesByOutputValues, 0);
		for (int i=0; i<data.size(); i++){
			casesByOutputValues[data.get(i)[attributeHeaderList.size()]]++; //Here we add-up all the instances observed in the node for each output value.
		}
		for (int i=0; i<classHeader.getNumCategories(); i++){
			double p=((double)casesByOutputValues[i]/(double)data.size());
			if (p!=0 & !Double.isNaN(p)){
			tempEntropy+=((double)(-p)*((double)(Math.log(p))/(double)Math.log(classHeader.getNumCategories())));//We add-up the entropy for each output value.
			}
		}
		return tempEntropy;
	}
	
	/**
	 * Function that returns the information gain from splitting on an attribute,
	 * whose index is passed as a parameter.
	 *
	 * @param attributeIndex the attribute index
	 * @return the double
	 */
	private double informationGain(int attributeIndex){//Here we calculate the information gain of the attribute whose index (in the attributeHeaderList) is passed as input
		int totalAttributeValues=attributeHeaderList.get(attributeIndex).getNumCategories();
		double gainOfAttribute=getEntropy(); //First term...
		int casesByAttributeValues[]=new int[totalAttributeValues];
		Arrays.fill(casesByAttributeValues, 0);
		double p[]=new double[totalAttributeValues];
		for (int i=0; i<data.size(); i++){
			casesByAttributeValues[data.get(i)[attributeIndex]]++; //Here we add-up all instances in the node that belong to a specific attribute value.
		}
		for (int i=0; i<totalAttributeValues; i++){
			p[i]=(double)casesByAttributeValues[i]/(double)data.size();//Intermediate proportions.
		}
	
		for (int i=0; i<totalAttributeValues; i++){
				double tempEntropy=0;
				int casesByOutputValues[]=new int[classHeader.getNumCategories()];
				Arrays.fill(casesByOutputValues, 0);
				for (int j=0; j<data.size(); j++){
					if (data.get(j)[attributeIndex]==i){
							casesByOutputValues[data.get(j)[attributeHeaderList.size()]]++;//Here we add-up all the instances for each output value from each attribute value.
					}
				}
				for (int j=0; j<classHeader.getNumCategories(); j++){
					double p1=(double)casesByOutputValues[j]/(double)casesByAttributeValues[i];//Innermost proportion
					if (p1!=0 && !Double.isNaN(p1)){
						tempEntropy+=((-p1)*((double)Math.log(p1)/(double)Math.log(classHeader.getNumCategories())));
					}
				}
				gainOfAttribute-=(p[i]*tempEntropy);//We multiply by intermediate proportion and add the term to the gain calculation.
		}
		return gainOfAttribute;
	}
	
	/**
	 * Recursive function that classifies according the decision tree the data from
	 * a given tuple.
	 * 
	 * @param tuple of attributes to be classified according to the decision tree.
	 * @return value from 0 to n, indicating the class assigned to the tuple by the tree.
	 * 
	 * */
	public int classify(int[] tuple){
		if (this.isLeaf()){
			return outputVariable;
		}
		for (int i=0; i<descendants.size(); i++){
			if (descendants.get(i).incomingValueIndex==tuple[attributeUsed]){
				return descendants.get(i).classify(tuple);
			}
		}
		return 0;//This is not ok, but it should never reach this...		
	}
	
	/**
	 * Recursive function that returns the ids of non-leaf nodes in a given level.
	 *
	 * @param testLevel the test level
	 * @return list with ids of all non-leaf nodes in a given level
	 */
	public List<Integer> getIdsOfNonLeafNodesInLevel(int testLevel){
		List<Integer> resultList = new ArrayList<Integer>();
		if (level==testLevel && !this.isLeaf()){
			resultList.add(id);
			return resultList;
		}
		else if (this.isLeaf()){
			return resultList;
		}
		else {
			List<Integer> partialList= new ArrayList<Integer>();
			for (int i=0; i<descendants.size(); i++){
				partialList=descendants.get(i).getIdsOfNonLeafNodesInLevel(testLevel);
				if (!partialList.isEmpty()){
					resultList.addAll(partialList);
				}
			}
			return resultList;
		}
	}
	
	/**
	 * Recursive function in charge of branching a given node, following the ID3 algorithm.
	 *
	 * @param inheritedClassHeader the inherited class header
	 * @param inheritedAttributeHeaderList the inherited attribute header list
	 * @param inheritedData the inherited data
	 * @param incomingAttribute the incoming attribute
	 * @param incomingValue the incoming value
	 * @param assignedLevel the assigned level
	 * @param usedAttr the used attr
	 * @param assignedId the assigned id
	 * @param assignedParentId the assigned parent id
	 * @return 1
	 */	
	public int branchWithID3(CategoricalType inheritedClassHeader, List<CategoricalType> inheritedAttributeHeaderList, List<int[]> inheritedData, int incomingAttribute, int incomingValue, int assignedLevel, List<Integer> usedAttr, int assignedId, int assignedParentId){
		classHeader=inheritedClassHeader.getCopy();
		attributeHeaderList.clear();
		attributeHeaderList.addAll(inheritedAttributeHeaderList);
		data=inheritedData;
		incomingValueIndex=incomingValue;
		incomingAttributeIndex=incomingAttribute;
		level=assignedLevel;
		parentId=assignedParentId;
		id=assignedId;
		descendants=new ArrayList<DecisionTree>();
		
		//First we check the two stopping conditions: if there are unused attributes or all perfectly classified
		boolean allAttributesAreUsed=true;
		boolean perfectlyClassified=true;
		if (attributeHeaderList.size()>usedAttr.size()){
			allAttributesAreUsed=false;
		}
		for (int i=0; i<data.size(); i++){
			if (data.get(i)[attributeHeaderList.size()]!=data.get(0)[attributeHeaderList.size()]){
				perfectlyClassified=false;
				i=data.size();
			}
		}
		if (!allAttributesAreUsed & !perfectlyClassified){ //Then it is not a leaf...
			double gains[] =new double [attributeHeaderList.size()];
			double tempMaxScore=-1;
			int maxIndex=-1;
			for (int i=0; i<attributeHeaderList.size(); i++){
				boolean canUse=true;
				for (int l=0; l<usedAttr.size(); l++){
					if (usedAttr.get(l)==i){
						canUse=false;
					}
				}
				if (canUse){
					gains[i]=informationGain(i);//Here we calculate the information gains for each unused attibute.
					if (gains[i]>=tempMaxScore){ //And we select for branching the attribute with the maximum gain
						maxIndex=i;
						tempMaxScore=gains[i];
					}
				}
			}
			attributeUsed=maxIndex; //We store which attribute will be used.
			
			//Now we create the values we will pass down.
			descendants.clear();
			List<Integer> usedAttr2= new ArrayList<Integer>();
			usedAttr2.addAll(usedAttr);
			usedAttr2.add(attributeUsed);
			for (int i=0; i<attributeHeaderList.get(attributeUsed).getNumCategories(); i++){ //We iterate over all new descendants...
				List<CategoricalType> newAttributeHeaderList= new ArrayList<CategoricalType>();
				newAttributeHeaderList.addAll(attributeHeaderList);  
				newAttributeHeaderList.get(attributeUsed).use(); //Here we create an attributeHeaderList and mark the attribute the node used

				List<int[]> newData=new ArrayList<int[]>();
				DecisionTree son=new DecisionTree();
				for (int j=0; j<data.size(); j++){
					if (data.get(j)[attributeUsed]==i){
						newData.add(data.get(j)); //Here we create the data that we will pass down (all instances where attribute used is of specific branching value)
					}
				}
				descendants.add(son);//We add the descendant to the descendants list...
				descendants.get(descendants.size()-1).branchWithID3(classHeader, newAttributeHeaderList, newData, attributeUsed, i, (level+1), usedAttr2, idCount++, id); //Here we branch
			}
		}
		else{//It is a leaf
			int casesByOutputValues[]=new int[classHeader.getNumCategories()];
			Arrays.fill(casesByOutputValues, 0);
			for (int i=0; i<data.size(); i++){
				casesByOutputValues[data.get(i)[attributeHeaderList.size()]]++;
			}
			int tempMaxIndex=0;
			int tempMaxCount=0;
			for (int i=0; i<classHeader.getNumCategories(); i++){
				if (casesByOutputValues[i]>=tempMaxCount){ //We store the output value observed to have more instances, this will be the output variable learned for the leaf.
					tempMaxCount=casesByOutputValues[i];
					tempMaxIndex=i;
				}
			}
			outputVariable=tempMaxIndex;
		}
		return 1;
	}
	
	/**
	 * Recursive function that returns the maximum level (or depth)
	 * of the tree.
	 *
	 * @return the maximum level
	 */
	public int getMaximumLevel(){
		if (this.isLeaf())
			return level;
		int maxLevel=level; //Counting self
		for (int i=0; i<this.descendants.size(); i++){
			int descendantsLevel=descendants.get(i).getMaximumLevel();
			if (maxLevel<descendantsLevel){
				maxLevel=descendantsLevel;
			}
		}
		return maxLevel;
	}
	
	/**
	 * Recursive function that prints the tree to an XML.
	 *
	 * @param writer the writer
	 * @return the int
	 */
	public int printToXML(PrintWriter writer){//Prints the tree to XML, recursively. Receives as input the file opened for writing. 
		if (this.isLeaf() && incomingAttributeIndex!=-1 ){//&& outputVariable!=-1){//Is a leaf
			String auxString="";
			for (int i=0; i<level; i++){
				auxString+="	";
			}
			
			String tabs=auxString;
			auxString=tabs+"<node classes=\"";
			for (int i=0; i<classHeader.getNumCategories();i++){
				int auxVal=getNumInstancesInClass(i);
				if (auxVal>0){
					auxString+=classHeader.getCategory(i);
					auxString+=":";
					auxString+=auxVal;
					if (i<classHeader.getNumCategories()-1)
						auxString+=",";
				}
			}
			if (parentId!=0){
				auxString+="\" id=\""+(int)(id+1)+"\" parentid=\""+(parentId+1);
			}
			else{
				auxString+="\" id=\""+(int)(id+1)+"\" parentid=\""+(parentId);
			}
			auxString+="\" level=\""+level;
			auxString+="\" entropy=\"";
			auxString+=String.format("%.3f", getEntropy());
			auxString+="\" ";
			auxString+="isLeaf=\"1\" ";
			auxString+="attr=\"";
			auxString+=attributeHeaderList.get(incomingAttributeIndex).getName()+"=";
			auxString+=attributeHeaderList.get(incomingAttributeIndex).getCategory(incomingValueIndex)+"\">";
			auxString+=classHeader.getCategory(outputVariable);
			auxString+="</node>";
			writer.println(auxString);
		}
		else{
			if (incomingValueIndex!=-1){//Is not the root node.
				String auxString="";
				for (int i=0; i<level; i++){
					auxString+="	";
				}
				String tabs=auxString;
				auxString=tabs+"<node classes=\"";
				for (int i=0; i<classHeader.getNumCategories();i++){
					int auxVal=getNumInstancesInClass(i);
					if (auxVal>0){
						auxString+=classHeader.getCategory(i);
						auxString+=":";
						auxString+=auxVal;
						if (i<classHeader.getNumCategories()-1)
							auxString+=",";
					}
				}
				if (parentId!=0){
					auxString+="\" id=\""+(int)(id+1)+"\" parentid=\""+(parentId+1);
				}
				else{
					auxString+="\" id=\""+(int)(id+1)+"\" parentid=\""+(parentId);
				}
				auxString+="\" level=\""+level;
				auxString+="\" entropy=\"";
				auxString+=String.format("%.3f", getEntropy());
				auxString+="\" ";
				auxString+="isLeaf=\"0\" ";
				auxString+="attr=\"";
				auxString+=attributeHeaderList.get(incomingAttributeIndex).getName()+"=";
				auxString+=attributeHeaderList.get(incomingAttributeIndex).getCategory(incomingValueIndex)+"\">";
				writer.println(auxString);
		}
			else{//Is the root node
				writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
				String auxString="<tree classes=\"";
				for (int i=0; i<classHeader.getNumCategories();i++){
					auxString+=classHeader.getCategory(i);
					auxString+=":";
					auxString+=getNumInstancesInClass(i);
					if (i<classHeader.getNumCategories()-1)
						auxString+=",";
				}
				auxString+="\" id=\""+id+"\" parentid=\""+parentId;
				auxString+="\" level=\""+level;
				auxString+="\" entropy=\"";
				auxString+=String.format("%.3f", getEntropy());
				auxString+="\">";
				writer.println(auxString);
			}
			for (int i=0; i<descendants.size(); i++){
				descendants.get(i).printToXML(writer); //Print descendants recursively
			}
			if (incomingValueIndex!=-1){//Close nodes that are not leaves.
				String auxString="";
				for (int i=0; i<level; i++){
					auxString+="	";
				}
				String tabs=auxString;
				auxString=tabs+"</node>";
				writer.println(auxString);

			}
			else{//Close root
				writer.println("</tree>");
			}
		}
		return 1;
	}
}