package ml.model;

// TODO: Auto-generated Javadoc
/** DrawNodeInfo class
 * <p>
 * A pojo that encapsulates information useful for drawing a node of a decision tree.
 * 
 * @author Gabriel
 * @author Vishnu
*/
public class DrawnNodeInfo {
	
	/** The parent id. */
	public int parentID;
	
	/** The id. */
	public int id;
	
	/** The x. */
	public int x;
	
	/** The y. */
	public int y;
	
	/** The width. */
	public int width;
	
	/** The label. */
	public String label;
	
	/** The attr label. */
	public String attrLabel;
	
	/**
	 * Instantiates a new drawn node info.
	 *
	 * @param parentID the parent id
	 * @param iD the i d
	 * @param x the x
	 * @param y the y
	 * @param classes the classes
	 * @param newAttrLabel the new attr label
	 * @param newWidth the new width
	 */
	public DrawnNodeInfo(int parentID, int iD, int x, int y, String classes, String newAttrLabel, int newWidth) {
		this.parentID = parentID;
		id = iD;
		this.x = x;
		this.y = y;
		this.label = classes;
		this.attrLabel=newAttrLabel;
		this.width=newWidth;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Parent:"+parentID+"\tNodeID:"+id+"\t(x,y):"+x+","+y+"\t+width:"+width+"\tlabel:"+label+"\tattribute:"+attrLabel;
	}
	
	
}
