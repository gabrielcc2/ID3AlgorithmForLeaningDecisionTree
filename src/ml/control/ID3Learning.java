package ml.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ml.model.CategoricalType;
// TODO: Auto-generated Javadoc
/**
 * CLASS ID3Learning
 * 	Includes the main function. 
 * 	In this class the functionality of learning a tree is implemented. 
 *  It takes as input 2 files, in c45 formatting. From this it learns through ID3 a Decision Tree
 *  and outputs the result as a file output.xml (by default).
 * <p> 
 *  CONSTANTS:
 *  static String NAME_FILE: Has to be manually changed for the program to read another name file.
 *  static String DATA_FILE: Has to be manually changed for the program to read another data file.
 *  static String OUTPUT_FILE: Has to be manually changed for the program to write to another file.
 *  <p>
 *  ATTRIBUTES:
 *  List<CategoricalType> attributeHeaderList: List of attributes.
 *  CategoricalType classHeader: Output variable or class
 *  List<int[]> data: Data as a list of instances, each one as an array of number representing attributes, the final position on each array is the class or value of output variable
 *  DecisionTree resultTree: Stores decision tree
 *  <p>
 *  FUNCTIONS:
 *  public static void main(String[] args): Main.
 *  <p>
 *  CORE FUNCTIONS:
 *  private int loadData(String , String ): Loads data from files. Its input are the names of a name file and data file, respectively. Returns 1 always.
 *  private int learnTreeWithID3(): Learns tree ith ID3. Returns 1 always.
 *  private int treeToXML(): Prints tree to XML file: output.xml. Returns 1 always. 
 *  <p>
 *  ADDITIONAL USEFUL FUNCTIONS: Functions that are left in the code, for they could aid in debugging.
 *  private void printHeaders(): Prints the headers or the names of each possible value of attributes and classes
 *  private void printData(): Prints the data
 *  <p>
 *  @author Gabriel
 *  */
public class ID3Learning {

	//CONSTANTS
	/** The name file. */
	private static String NAME_FILE="data/car/car.c45-names"; //Defines the name of the names file.
	
	/** The data file. */
	private static String DATA_FILE="data/car/car.data";//Defines the name of the data file.
	
	/** The output file. */
	private static String OUTPUT_FILE="tmp/output.xml";
	
	/////ATTRIBUTES
	/** The attribute header list. */
	private List<CategoricalType> attributeHeaderList;  //List of attributes.
	
	/** The class header. */
	private CategoricalType classHeader; //Output variable or class
	
	/** The data. */
	private List<int[]> data;// Data as a list of instances, each one as an array of number representing attributes, the final position on each array is the class or value of output variable
	
	/** The result tree. */
	private DecisionTree resultTree; //Stores the decision tree
	
	/** The validation data. */
	private List<int[]> validationData;
	
	/** The percentage. */
	private double percentage=0.7; //Percentage for dividing data between training and validation.
	/////FUNCTIONS
	
	//////////CORE FUNCTIONS
	/**
	 * This function loads the data from the files to the inner representation of the ID3Learning class.
	 *
	 * @param namesFile the names file
	 * @param dataFile the data file
	 * @return 1
	 */
	public int loadData(String namesFile, String dataFile){//Loads data from files into corresponding data structures.
		BufferedReader reader=null;  
		classHeader= new CategoricalType();
		attributeHeaderList=new ArrayList<CategoricalType>();
		data=new ArrayList<int[]>();
		validationData= new ArrayList<int[]>();
		List<int[]> data2=new ArrayList<int[]>();
		
		/*First we read the names file*/
		try {
			reader = new BufferedReader(new FileReader(namesFile));
			String line = null;
			boolean skip=false;
			while ((line = reader.readLine()) !=null & !skip)   {
				if (line.contains("class values")){ //Here we will read the categories for the output variable or class
					if ((line = reader.readLine())!=null){
						if (line.length()<3){
							line = reader.readLine();//Skip this line
						}
						String[] splited = line.split(",");
						for (String part : splited) {
							part=part.replaceAll("\\s","");
							classHeader.addCategory(part);
						}
						skip=true;
					}
				}
			}
			classHeader.setName("class");
			skip=false;
			while (!skip & (line = reader.readLine()) !=null)   {
				if (line.contains("| attributes")){ //Here we will read the attributes
					skip=true;
					while ((line = reader.readLine()) !=null){
						if (line.length()>1){
							attributeHeaderList.add(new CategoricalType());
							String auxName=line.substring(0, line.indexOf(':'));
							attributeHeaderList.get(attributeHeaderList.size()-1).setName(auxName);
							line=line.substring(line.indexOf(':')+1, line.length()-1);
							String[] splited = line.split(",");
							for (String part : splited) {
								part=part.replaceAll("\\s","");
								attributeHeaderList.get(attributeHeaderList.size()-1).addCategory(part);
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
		
		/*Now we read the data file*/
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			String line = null;
			while ((line = reader.readLine()) !=null){
				int auxArray[] = new int [attributeHeaderList.size()+1];
				String[] splited = line.split(",");
				int i=0;
				for (String part : splited) {
					if (i<attributeHeaderList.size()){
						part=part.replaceAll("\\s","");
						auxArray[i]=attributeHeaderList.get(i).getNumRepresentationOfCategory(part);
					}
					else {
						part=part.replaceAll("\\s","");
						auxArray[i]=classHeader.getNumRepresentationOfCategory(part);
					}
					i++;
				}
				data2.add(auxArray);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
		
		Collections.shuffle(data2);
		Collections.shuffle(data2);
		Collections.shuffle(data2);
		for (int i=0; i<(int)Math.floor(percentage*(data2.size())); i++){
			data.add(data2.get(i));
		}
		for (int i=(int)Math.floor(percentage*(data2.size())); i<data2.size(); i++){
			validationData.add(data2.get(i));
		}
		return 1;
	}
	
	/**
	 * Function commanding the Decision Tree to be built, using ID3.
	 *
	 * @return the int
	 */
	public int learnTreeWithID3(){ //Learns tree using ID3
		resultTree=new DecisionTree();
		List<Integer> usedAttr= new ArrayList<Integer>();
		return resultTree.branchWithID3(classHeader, attributeHeaderList, data, -1, -1, 0, usedAttr, 0, -1); 
	}
	
	/**
	 * Function in charge of commanding the reduced error pruning of the tree.
	 *
	 * @return the int
	 */
	public int reducedErrorPruneTree(){ //Learns tree using ID3
		DecisionTree prunedTree= resultTree.getCopy();
		DecisionTree atNodeCandidate= resultTree.getCopy();
		double currentAccuracy = this.getAccuracyOverValidationData();
		boolean accuracyImproved=true;
		int maxLevel=resultTree.getMaximumLevel();
		while (accuracyImproved){//While the accuracy improves...
			accuracyImproved=false;
			int currentLevel=maxLevel;
			while(currentLevel>=0){  
				/*Candidates are generated by deleting one non-leaf node at a time, by each level*/
				List<Integer> nodes= prunedTree.getIdsOfNonLeafNodesInLevel(currentLevel); //Here we get a list of all non-leaf nodes in a level.
				for (int i=0; i<nodes.size(); i++){//We iterate through that list.
					DecisionTree auxTree= prunedTree.getCopy(); //We make a copy of the current tree
					auxTree.deleteNode(nodes.get(i));//We delete the node
					double newAccuracy=getAccuracyOverValidationData(auxTree); //We get the new accuracy
					if (newAccuracy>currentAccuracy){
						atNodeCandidate=auxTree.getCopy(); //We store the selected tree
					}
				}
				currentLevel--;
			}
			currentAccuracy=getAccuracyOverValidationData(atNodeCandidate);
			if (currentAccuracy>getAccuracyOverValidationData(prunedTree)){
				accuracyImproved=true;
				prunedTree=atNodeCandidate.getCopy();
			}
		}
		if (getAccuracyOverValidationData(prunedTree)>this.getAccuracyOverValidationData()){
			resultTree=prunedTree.getCopy();			
		}
		return 1;
	}
	
	/**
	 * Function in charge of calculating the accuracy of the
	 * decision tree over the validation data.
	 *
	 * @return the accuracy over validation data
	 */
	public double getAccuracyOverValidationData(){ //Gets accuracy
		int numberOfExamples=validationData.size();
		int numberOfCorrectlyClassified=0;
		for (int i=0; i<validationData.size(); i++){
			if (resultTree.classify(validationData.get(i))==validationData.get(i)[validationData.get(i).length-1]){
				numberOfCorrectlyClassified++;
			}
		}
		if (numberOfExamples>0){
			return (double)numberOfCorrectlyClassified/numberOfExamples;
		}
		return 0.0;
	}
	
	/**
	 * Function in charge of calculating the accuracy of a
	 * decision tree passed as parameter over the validation data.
	 *
	 * @param tree the tree
	 * @return the accuracy over validation data
	 */
	public double getAccuracyOverValidationData(DecisionTree tree){ //Gets accuracy
		int numberOfExamples=validationData.size();
		int numberOfCorrectlyClassified=0;
		for (int i=0; i<validationData.size(); i++){
			if (tree.classify(validationData.get(i))==validationData.get(i)[validationData.get(i).length-1]){
				numberOfCorrectlyClassified++;
			}
		}
		if (numberOfExamples>0){
			return (double)numberOfCorrectlyClassified/numberOfExamples;
		}
		return 0.0;
	}
	
	/**
	 * Function in charge of commanding the printing of the tree to 
	 * an xml file.
	 *
	 * @param file the file
	 * @return the int
	 */
	public int treeToXML(String file){ //Prints tree to XML.
		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
			resultTree.printToXML(writer);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	
	//////////ADDITIONAL USEFUL FUNCTIONS
	/*Prints the headers or the names of each possible value of attributes and classes*/
	/**
	 * Prints the headers.
	 */
	public void printHeaders(){
		System.out.println("Class values");
		System.out.println(classHeader.toString());
		System.out.println("Attributes values");
		for (int i=0; i<attributeHeaderList.size(); i++){
			System.out.println(attributeHeaderList.get(i).toString());
		}
	}
	
	/*Prints the data*/
	/**
	 * Prints the data.
	 */
	public void printData(){ 
		for (int i=0; i<data.size(); i++){
			String outputString="";
			for (int j=0; j<attributeHeaderList.size(); j++){
				outputString+=attributeHeaderList.get(j).getCategory(data.get(i)[j]);
				outputString+=",";
			}
			outputString+=classHeader.getCategory(data.get(i)[attributeHeaderList.size()]);
			System.out.println(i+": "+outputString);
		}
	}

	/*Simple main function, allowing to test the library*/
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		ID3Learning app=new ID3Learning ();
		app.loadData(NAME_FILE,DATA_FILE);
		app.learnTreeWithID3(); //Internally stores the decision tree
	//	app.printHeaders();
    //	app.printData();
		app.treeToXML(OUTPUT_FILE);
		System.out.println("First accuracy: "+app.getAccuracyOverValidationData());
		app.reducedErrorPruneTree();
		app.treeToXML("tmp/output.xml");
		System.out.println("XML already printed");
		System.out.println("Improved accuracy: "+app.getAccuracyOverValidationData());
	}
}