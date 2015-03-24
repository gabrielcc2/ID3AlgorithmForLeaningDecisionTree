package ml.control;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

// TODO: Auto-generated Javadoc
/** ID3Singleton class
 *  <p>
 *  Class that adds state properties to the use of the ID3Learning class, so as to support a GUI.
 *  <p>
 *  @author Gabriel
 *  
 * */
public class ID3Singleton {

	/** Singleton instance of type ID3Singleton. */
	public static ID3Singleton instance=null;

	/**Default data folder. By default car folder*/
	private static String DEFAULT_DATA_FOLDER="data/car/";
	
	/** The output file. */
	private static String OUTPUT_FILE="tmp/output.xml";
	
	/** The data_folder. */
	private static String data_folder=DEFAULT_DATA_FOLDER;

	
	/** Tree. */
	private ID3Learning app;
	
	/** The tree_created. */
	private boolean tree_created=false;
	
	/**
	 * Functions.
	 */
	
	/**Protected constructor function, to defeat instantiation. */
	protected ID3Singleton(){
		 // Exists only to defeat instantiation.
	}
	
	/**
	 * getInstance function, for singleton use.
	 *
	 * @return single instance of ID3Singleton
	 */
	public static ID3Singleton getInstance() {
	      if(instance == null) {
	         instance = new ID3Singleton();
	      }
	      return instance;
	}
	
	/**
	 * Use default data folder.
	 */
	public void useDefaultDataFolder(){
		data_folder=DEFAULT_DATA_FOLDER;
	}
	
	/**
	 * Sets the data folder.
	 *
	 * @param folder the new data folder
	 */
	public void setDataFolder(String folder){
		data_folder=folder;
	}
	
	/**
	 * Gets the data folder.
	 *
	 * @return the data folder
	 */
	public String getDataFolder (){
			return data_folder;
	}
		
	/**
	 * Gets the tree created.
	 *
	 * @return the tree created
	 */
	public boolean getTreeCreated(){
		return tree_created;
	}
	
	/**
	 * Using default data folder.
	 *
	 * @return true, if successful
	 */
	public boolean usingDefaultDataFolder(){
		return data_folder==DEFAULT_DATA_FOLDER;
	}
	
	/**
	 * Function that returns an object of type Elements, from the jsoup library,
	 * encapsulating the information of a tree, according to its XML representation.
	 * This was designed so as to ease the drawing of the tree.
	 *
	 * @param pruned the pruned
	 * @return the tree for drawing
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Elements getTreeForDrawing(boolean pruned) throws IOException{
		if (!pruned){
			File dir = new File(data_folder);
			File[] hits_for_names = dir.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".c45-names");
				}
			});
			File[] hits_for_data = dir.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".data");
				}
			});
			app=new ID3Learning ();
			//We read the first files we find ending on .c45-names and .data, respectively.
			app.loadData(hits_for_names[0].toString(),hits_for_data[0].toString()); 
			app.learnTreeWithID3();
		}
		app.treeToXML(OUTPUT_FILE);
		File treeFile = new File (OUTPUT_FILE);
		org.jsoup.nodes.Document xmlDoc = Jsoup.parse(treeFile, "UTF-8");
		Elements nodes= xmlDoc.select("node");
		Elements root = xmlDoc.select("tree");
		root.addAll(nodes);
		return root;
	} 
	
	/**Function that commands the pruning of the tree.
	 * 
	 * */
	public void pruneTree(){
		app.reducedErrorPruneTree();
		app.treeToXML(OUTPUT_FILE);
	}
	
	/**Function that commands the pruning of the tree up until a step.
	 * 
	 * */
	public void pruneTree(int step){
		app.reducedErrorPruneTree(step);
		app.treeToXML(OUTPUT_FILE);
	}

	/*Function to get the maximum number of pruning steps*/
	public int getMaxPruningSteps(){
		app.initializeCopyTree();
		return app.maxStepsInReducedErrorPruneTree();
	}
	
	/**
	 * Function that returns the accuracy of the tree over
	 * the validation data.
	 *
	 * @return the accuracy over validation data
	 */
	public double getAccuracyOverValidationData(){
		return app.getAccuracyOverValidationData();
	}
}
