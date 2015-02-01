import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
/*
 * CLASS ID3Learning
 * 	Includes the main function. 
 * 	In this class the functionality of learning a tree is implemented. 
 *  It takes as input 2 files, in c45 formatting. From this it learns through ID3 a Decision Tree
 *  and outputs the result as a file output.xml (by default).
 *  
 *  CONSTANTS:
 *  static String NAME_FILE: Has to be manually changed for the program to read another name file.
 *  static String DATA_FILE: Has to be manually changed for the program to read another data file.
 *  static String OUTPUT_FILE: Has to be manually changed for the program to write to another file.
 *  
 *  ATTRIBUTES:
 *  List<CategoricalType> attributeHeaderList: List of attributes.
 *  CategoricalType classHeader: Output variable or class
 *  List<int[]> data: Data as a list of instances, each one as an array of number representing attributes, the final position on each array is the class or value of output variable
 *  DecisionTree resultTree: Stores decision tree
 *  
 *  FUNCTIONS:
 *  public static void main(String[] args): Main.
 *  
 *  CORE FUNCTIONS:
 *  private int loadData(String , String ): Loads data from files. Its input are the names of a name file and data file, respectively. Returns 1 always.
 *  private int learnTreeWithID3(): Learns tree ith ID3. Returns 1 always.
 *  private int treeToXML(): Prints tree to XML file: output.xml. Returns 1 always. 
 *  
 *  ADDITIONAL USEFUL FUNCTIONS: Functions that are left in the code, for they could aid in debugging.
 *  private void printHeaders(): Prints the headers or the names of each possible value of attributes and classes
 *  private void printData(): Prints the data
 *  
 *  AUTHORS: 
 *  Gabriel Campero, gabrielcampero@acm.org
 *  Vishnu Unnikrishnan, vishnu.unnikrishnan@gmail.com
 *  */
public class ID3Learning {

	//CONSTANTS
	private static String NAME_FILE="car.c45-names"; //Defines the name of the names file.
	private static String DATA_FILE="car.data";//Defines the name of the data file.
	private static String OUTPUT_FILE="output.xml";
	private static String OUTPUT_FILE_PRUNED="output_pruned.xml";
	
	/////ATTRIBUTES
	private List<CategoricalType> attributeHeaderList;  //List of attributes.
	private CategoricalType classHeader; //Output variable or class
	private List<int[]> data;// Data as a list of instances, each one as an array of number representing attributes, the final position on each array is the class or value of output variable
	private DecisionTree resultTree; //Stores the decision tree
	/////FUNCTIONS
	
	//////////CORE FUNCTIONS
	/*This function loads the data from the files to the inner representation of the ID3Learning class*/
	private int loadData(String namesFile, String dataFile){//Loads data from files into corresponding data structures.
		BufferedReader reader=null;  
		classHeader= new CategoricalType();
		attributeHeaderList=new ArrayList<CategoricalType>();
		data=new ArrayList<int[]>();
		
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
				data.add(auxArray);
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
		return 1;
	}
	
	/*Function responsible for learning the Decision Tree, using ID3*/
	private int learnTreeWithID3(){ //Learns tree using ID3
		resultTree=new DecisionTree();
		List<Integer> usedAttr= new ArrayList<Integer>();
		return resultTree.branchWithID3(classHeader, attributeHeaderList, data, -1, -1, 0, usedAttr); 
	}
	
	/*Function responsible for learning the Decision Tree, using ID3*/
	private int pruneTree(){ //Learns tree using ID3
		return resultTree.prune();
	}
	
	
	private int treeToXML(String file){ //Prints tree to XML.
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
	private void printHeaders(){
		System.out.println("Class values");
		System.out.println(classHeader.toString());
		System.out.println("Attributes values");
		for (int i=0; i<attributeHeaderList.size(); i++){
			System.out.println(attributeHeaderList.get(i).toString());
		}
	}
	
	/*Prints the data*/
	private void printData(){ 
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

	public static void main(String[] args) {
		ID3Learning app=new ID3Learning ();
		app.loadData(NAME_FILE,DATA_FILE);
		app.learnTreeWithID3(); //Internally stores the decision tree
		app.treeToXML(OUTPUT_FILE);
	//	app.pruneTree();
		//app.treeToXML(OUTPUT_FILE_PRUNED);
		/**TODO
		 * 1) Print partial tree.
		 * 2) Get rules.
		 * 3) Prune rules.
		 * 4) Prunt rules, printing partially.
		 * 5) GUI!
		 * */
	}
}