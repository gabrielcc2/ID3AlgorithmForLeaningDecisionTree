package ml.model;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * CLASS CategoricalType
 * <p>
 * 	In this Pojo class the definition of a CategoricalType is implemented. This refers to types of data
 *  that are like Enums, having an attribute name, and a discrete set of possible values, which are defined as strings.
 *  This class stores the name of the attribute and such values, additionally it provides some functions to
 *  ease the use of these types.
 *  <p> 
 *  ATTRIBUTES
 *  String name: Name of the attribute or class
 *  List <String> categories: List with possible values of attributes, stored as strings.
 *  boolean used: Extra flag to indicate if this attribute has been used in the building of a tree, by default false.
 *  <p>
 *  FUNCTIONS
 *  public String getName(): Returns name of attribute
 *  public int setName(String ): Sets name of attribute, taking as input such a name. Returns 1 always.
 *  public int addCategory(String ): Adds a new category, or possible value of attribute (used on build time). The name of the category is passed as input. Returns 1 always.
 *  public int getNumCategories(): Returns number of categories or possible values of attribute. 
 *  public String getCategory(int ): Maps between a number (or index) and the value of attribute (a string). 
 *  public int getNumRepresentationOfCategory(String ): Maps between the value of attribute (a string) and the number (or index) it represents
 *  public void use(): Marks the attribute as used in learning a decision tree
 *  public boolean isUsed(): Checks if the attribute has been used in learning a decision tree
 *  
 *  public String toString (): Useful for debugging, Returns a string of the form: AttrName:PossibleValue1,PossibleValue2,...,PossibleValueN
 *  <p>
 *  @author Gabriel
 *  @author Vishnu
 *  */
public class CategoricalType {
	
	/////ATTRIBUTES
	/** The name. */
	private String name; //Name of the attribute or class
	
	/** The categories. */
	private List <String> categories = new ArrayList<String>(); //List with possible values
	
	/** The used. */
	private boolean used=false; //Extra flag to indicate if this attribute has been used in the building of a tree
	
	/////FUNCTIONS
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){//Returns name of attribute
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param newName the new name
	 * @return the int
	 */
	public int setName(String newName){//Sets name of attribute
		name=newName;
		return 1;
	}
	
	/**
	 * Adds the category.
	 *
	 * @param newCategory the new category
	 * @return the int
	 */
	public int addCategory(String newCategory){//Adds a new category, or possible value of attribute (used on build time)
		categories.add(newCategory);
		return 1;
	}
	
	/**
	 * Gets the num categories.
	 *
	 * @return the num categories
	 */
	public int getNumCategories(){//Returns number of categories or possible values of attribute
		return categories.size();
	}
	
	/**
	 * Gets the category.
	 *
	 * @param num the num
	 * @return the category
	 */
	public String getCategory(int num){ //Maps between numbers and the value of attribute
		return categories.get(num);
	}
	
	/**
	 * Gets the num representation of category.
	 *
	 * @param searchCategory the search category
	 * @return the num representation of category
	 */
	public int getNumRepresentationOfCategory(String searchCategory){ //Maps between the value of attribute and the number it represents
		return categories.indexOf(searchCategory); 
	}
	
	/**
	 * Use.
	 */
	public void use(){ //Marks the attribute as used in learning a decision tree
		used=true;
	}
	
	/**
	 * Checks if is used.
	 *
	 * @return true, if is used
	 */
	public boolean isUsed(){//Checks if the attribute has been used in learning a decision tree
		return used;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString (){ //Useful for debugging. Returns a string of the form: AttrName:PossibleValue1,PossibleValue2,...,PossibleValueN
		String outputString=name+":";
		for (int i=0; i<categories.size(); i++){
			outputString+=categories.get(i);
			if (i<categories.size()-1){
				outputString+=",";
			}
		}
		return outputString;
	}
	
	/**
	 * Gets the copy.
	 *
	 * @return the copy
	 */
	public CategoricalType getCopy(){
		CategoricalType result= new CategoricalType();
		result.setName(name);
		for (int i=0; i<categories.size(); i++){
			result.addCategory(categories.get(i));
		}
		if(this.isUsed()){
			result.use();
		}
		return result;
	}
}