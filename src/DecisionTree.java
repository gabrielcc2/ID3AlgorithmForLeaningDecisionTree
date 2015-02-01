import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
/*
 * CLASS DecisionTree
 * 	In this class the definition of a DecisionTree is implemented. 
 *  The possibility of branching or growing the tree with ID3 is offered, and so is the chance for printing the tree as an XML file.
 *   
 *  ATTRIBUTES
 *  List<CategoricalType> attributeHeaderList: List of attributes.
 *  CategoricalType classHeader: Output variable or class
 *  List<int[]> data: Data in the node as a list of instances, each represented as an array of numbers representing attributes + the final one representing the class.
 *  List<DecisionTree> descendants: Array with the immediate descendants of this specific tree.
 *  int attributeUsed: Attribute that is selected in this node for further branching, -1 by default.
 *  int incomingAttributeIndex: Attribute that was selected in parent for branching, -1 by default.
 *  int incomingValueIndex=-1: Value of attribute used for generating this specific node, -1 by default.
 *  int outputVariable=-1: If it is a leaf, the outputVariable stores the learned outputValue
 *  int level=0: Level of the node in the general tree. Values from 0 (root) to n (outermost leaf).
 *  
 *  FUNCTIONS
 *  public boolean isLeaf(): Returns if the node is a Leaf (has no descendants)
 *  private int getNumInstancesInClass(int ): Returns the number of instances that on this node belong to a specific class or value of output variable.
 *  private double getEntropy(): Returns the entropy of the node
 *  private double informationGain(int ): Here we calculate the information gain of the attribute whose index (in the attributeHeaderList) is passed as input
 *  
 *  public int branchWithID3(CategoricalType, List<CategoricalType>, List<int[]>, int, int, int):
 *  	This function branches the tree using ID3. It returns 1 always. It takes as input from its parent (or caller):
 *            1. the classHeader, 
 *            2. the list of attributeHeaders (indicating which attributes have been used, in the corresponding flags)
 *            3. the data over which it will branch
 *            4. the index of the attribute his parent used for branching (-1 on first call)
 *            5. the index of the value (of the attribute) his parent used for branching (-1 on first call)
 *            6. the level (0 for the root)
 * 	public int printToXML(PrintWriter ): Prints the tree to XML, recursively. Receives as input the file opened for writing. 
 *  
 *  AUTHORS: 
 *  Gabriel Campero, gabrielcampero@acm.org
 *  Vishnu Unnikrishnan, vishnu.unnikrishnan@gmail.com
 *  */
import java.io.UnsupportedEncodingException;

public class DecisionTree {
	private List<CategoricalType> attributeHeaderList= new ArrayList<CategoricalType>();  //List of attributes.
	private CategoricalType classHeader; //Output variable or class
	private List<int[]> data; //Data in the node as a list of instances, each represented as an array of numbers representing attributes + the final one representing the class.
	
	private List<DecisionTree> descendants = new ArrayList<DecisionTree>(); //Array with the immediate descendants of this specific tree.
	private int attributeUsed=-1; //Attribute that is selected in this node for further branching.
	private int incomingAttributeIndex=-1;//Attribute that was selected in parent for branching.
	private int incomingValueIndex=-1; //Value of attribute used for generating this specific node.
	private int outputVariable=-1;//If it is a leaf, the outputVariable stores the learned outputValue.
	private int level=0;//Level of the node in the general tree. Values from 0 (root) to n (outermost leaf).
	
	public boolean isLeaf(){//Returns if the node is a Leaf (has no descendants)
		return descendants.size()==0;
	}
	
	private int getNumInstancesInClass(int classNum){ //Returns the number of instances that on this node belong to a specific class or value of output variable.
		int count=0;
		for (int i=0; i<data.size();i++){
			if (data.get(i)[attributeHeaderList.size()]==classNum){
				count++;
			}
		}
		return count;
	}
	
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
	
	public int prune (){
		
		return 1;
	}
	
	public int branchWithID3(CategoricalType inheritedClassHeader, List<CategoricalType> inheritedAttributeHeaderList, List<int[]> inheritedData, int incomingAttribute, int incomingValue, int assignedLevel, List<Integer> usedAttr){
		classHeader=inheritedClassHeader.getCopy();
		attributeHeaderList.clear();
		attributeHeaderList.addAll(inheritedAttributeHeaderList);
		data=inheritedData;
		incomingValueIndex=incomingValue;
		incomingAttributeIndex=incomingAttribute;
		level=assignedLevel;
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
				
				//PrintWriter writer;
			//	try {
				//	writer = new PrintWriter("output.xml", "UTF-8");
					//this.printToXML(writer);
					//writer.close();
			//	} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
			//	}
				//try {
					//Thread.sleep(5000);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			//	}
				descendants.add(son);//We add the descendant to the descendants list...
				descendants.get(descendants.size()-1).branchWithID3(classHeader, newAttributeHeaderList, newData, attributeUsed, i, (level+1), usedAttr2); //Here we branch
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
	
	public int printToXML(PrintWriter writer){//Prints the tree to XML, recursively. Receives as input the file opened for writing. 
		if (this.isLeaf()){//&&incomingAttributeIndex!=-1 && outputVariable!=-1){//Is a leaf
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
			auxString+="\" entropy=\"";
			auxString+=String.format("%.3f", getEntropy());
			auxString+="\" ";
			auxString+=attributeHeaderList.get(incomingAttributeIndex).getName()+"=\"";
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
				auxString+="\" entropy=\"";
				auxString+=String.format("%.3f", getEntropy());
				auxString+="\" ";
				auxString+=attributeHeaderList.get(incomingAttributeIndex).getName()+"=\"";
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