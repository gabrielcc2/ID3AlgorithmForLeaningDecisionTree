
package ml.view;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import ml.control.ID3Singleton;
import ml.model.DrawnNodeInfo;

// TODO: Auto-generated Javadoc
/**
 * ID3GUI
 * <p>
 * The GUI class of the project. 
 * It consists of a JFrame containing a JPanel, upon which a JGraph from the MXGraph library 
 * (https://www.jgraph.com/javascript-graph-visualization-library.html) is included.
 * <p>
 * @author Vishnu
 * @author Gabriel
 */
@SuppressWarnings("serial")
public class ID3GUI extends javax.swing.JFrame {
    
	/*Drawing library components*/
	/** The graph. */
	mxGraph graph = null;
	
	/** The parent. */
	Object parent = null;
	
	/** The graph component. */
	mxGraphComponent graphComponent;
	/*End of drawing library components*/
	
	/*Configuration for height and width of nodes*/
	/** The graph node height. */
	private int graphNodeHeight = 60;
	
	/** The graph node width. */
	private int graphNodeWidth = 220;;
	
	/*Variables required for tree*/
	/** The tree. */
	private Elements tree=null;				//Stores the whole tree elements
	
	/** The current parent in level. */
	private int currentParentInLevel=-1;	//Housekeeping variable used to get parent to extract children from on next click
	
	/** The current level. */
	private int currentLevel=0;				//Housekeeping variable used to get nodes at current level
	
	/** The max tree level. */
	private int maxTreeLevel=0;				//Housekeeping variable used to get max tree depth.
	
	/*Variables for keeping track of state during GUI use*/
	/** The redraw. */
	private boolean redraw=false;
	
	/** The pruned. */
	private boolean pruned=false;
	
	
	//Keeps track of nodes that have been drawn so far
	/** The processed nodes. */
	public ArrayList<DrawnNodeInfo> processedNodes=null;	
	
	/** The drawn nodes map. */
	public HashMap<Integer,Object> drawnNodesMap=null;
	
	/*End of variables required for tree*/
	
    /**
	 * Creates new form ID3GUI.
	 */
    public ID3GUI() {
    	super();
    	initComponents();
    }
    
	/**
	 * Loads tree upon initialization or pruning.
	 */
	public void loadTree() {
		if(tree==null|| redraw){ 
			try {
				tree=ID3Singleton.getInstance().getTreeForDrawing(pruned);
				maxTreeLevel=getMaxTreeLevel();
				currentParentInLevel=-2;
				graph=null;
				if (ourCanvas!=null){
					ourCanvas.removeAll();
				}
				currentLevel=0;
				if (processedNodes!=null){
					processedNodes.clear();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}
		}
	}
	
	/**
	 * Function that returns the parent node as DrawnNodeInfo, given an id.
	 *
	 * @param parentNodeID the parent node id
	 * @return the parent node
	 */
	public DrawnNodeInfo getParentNode(int parentNodeID) {
		DrawnNodeInfo foundParent = null;
		for(int i=0;i<processedNodes.size();i++) {
			DrawnNodeInfo currentNode = processedNodes.get(i);
			if(currentNode.id==parentNodeID) {
				foundParent = currentNode;
			}
		}
		return foundParent;
	}
	
	/**
	 * Function that counts the number of siblings of a node, that are already drawn.
	 *
	 * @param node the node
	 * @return the drawn siblings count
	 */
	public int getDrawnSiblingsCount(DrawnNodeInfo node) {
		int siblingsCount=0;
		int referenceParentID = node.parentID;		
		for(int i=0;i<processedNodes.size();i++) {
			if(processedNodes.get(i).parentID==referenceParentID) {
				siblingsCount++;
			}
		}
		return siblingsCount;
	}
	
	/**
	 * Function that returns the DrawnNodeInfo of a node whose id is passed as input.
	 *
	 * @param nodeID the node id
	 * @return the node
	 */
	public DrawnNodeInfo getNode(int nodeID) {
		for(int i=0;i<processedNodes.size();i++) {
			DrawnNodeInfo currentNode = processedNodes.get(i);
			if(currentNode.id==nodeID) return currentNode;
		}
		return null;
	}
	
	
	/**
	 * Function that returns the maximum level of the tree in elements.
	 *
	 * @return the max tree level
	 */
	public int getMaxTreeLevel(){
		//If tree has not been loaded
		if (tree!=null){
			int foundLevel=0;
			for (int i=0; i<tree.size(); i++){
				if (Integer.parseInt(tree.get(i).attr("level"))>foundLevel){
					foundLevel=Integer.parseInt(tree.get(i).attr("level"));
				}
			}
			return foundLevel;
		}
		else{
			System.out.println("Warning: Read attempted at tree depth before initialization");
			return -1;
		}
	}

	/**
	 * Get all nodes at a particular level that havent been processed.
	 *
	 * @return the current level nodes
	 */
	public Elements getCurrentLevelNodes(){
		Elements retVal=new Elements();
		for (int i=0; i<tree.size(); i++){
			if (Integer.parseInt(tree.get(i).attr("level"))==currentLevel
					&& Integer.parseInt(tree.get(i).attr("parentId"))>currentParentInLevel ){  
				retVal.add(tree.get(i));
			}
		}
		return retVal;
	}

	/**
	 * Get all nodes at a particular level that havent been processed, and that belong
	 * to currentParentInLevel.
	 * 
	 * This supports the "step" functionality for displaying the growth of the tree.
	 *
	 * @return the step nodes
	 */
	public Elements getStepNodes(){
		Elements retVal=new Elements();
		for (int i=0; i<tree.size(); i++){
			if (Integer.parseInt(tree.get(i).attr("level"))==currentLevel
					&& Integer.parseInt(tree.get(i).attr("parentId"))==currentParentInLevel ){ //TODO parent used
				retVal.add(tree.get(i));
			}
		}
		return retVal;
	}
	
	/**
	 * Function that checks if a node overlaps with other already printed.
	 *
	 * @param currentDrawX the current draw x
	 * @param currentDrawY the current draw y
	 * @return true, if successful
	 */
	private boolean overlapsWithExistingCells(int currentDrawX, int currentDrawY) {
		DrawnNodeInfo currentNode;
		int drawnNodeX;
		int drawnNodeY;
		for(int i=0;i<processedNodes.size();i++) {
			currentNode = processedNodes.get(i);
			drawnNodeX = currentNode.x;
			drawnNodeY = currentNode.y;
			
			if(currentDrawX >= drawnNodeX-graphNodeWidth								//Overlap in X bounds 
					&& currentDrawX <=drawnNodeX+graphNodeWidth			//Overlap in X bounds 
					&& currentDrawY >= drawnNodeY-graphNodeHeight						//Overlap in Y bounds
					&& currentDrawY <= drawnNodeY+graphNodeHeight) {	//Overlap in Y bounds
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Function in charge of drawing the tree.
	 *
	 * @param elements the elements
	 */
	public void drawTree(Elements elements) {
		if(graph==null) {
			graph= new mxGraph();
			parent = graph.getDefaultParent();
		}

		if(processedNodes==null || processedNodes.isEmpty()) { //Printing the root
			
			int currentX=0;
			int currentY=0;
			//You are drawing the first node. Make necessary allocations
			processedNodes= new ArrayList<DrawnNodeInfo>();
			drawnNodesMap = new HashMap<Integer,Object>();

			//Node to draw now is inserted into processedNodes list for future tracking.
			DrawnNodeInfo currentNode = new DrawnNodeInfo((Integer.parseInt(elements.get(0).attr("parentid"))/*+1*/), //TODO parent used
					Integer.parseInt(elements.get(0).attr("id")),
					currentX,
					(Integer.parseInt(elements.get(0).attr("level")))*20, 
					"Instances distribution: \n"+elements.get(0).attr("classes")+"\nEntropy: "+elements.get(0).attr("entropy"),
					elements.get(0).attr("attr"),
					this.getWidth());
			processedNodes.add(currentNode);

			//Actually draw the node at the default location
			graph.getModel().beginUpdate();
			Object drawingObject = graph.insertVertex(
					parent, 
					null, //Node id 
					"Instances distribution: \n"+elements.get(0).attr("classes")+"\nEntropy: "+elements.get(0).attr("entropy"), 
					currentX, 
					currentY, 
					graphNodeWidth,
					graphNodeHeight,
					"whiteSpace=wrap;rounded=true;fillColor=#0066FF;fontColor=white",//root style
					false);
			//Put the id, object into the hashmap
			drawnNodesMap.put(Integer.parseInt(elements.get(0).attr("id")), drawingObject);

			graph.getModel().endUpdate();
			
			graphComponent = new mxGraphComponent(graph);
			graphComponent.setAutoScroll(true);
			graphComponent.setPreferredSize(new Dimension(this.getWidth()-100, this.getHeight()-350));	
			ourCanvas.add(graphComponent);
			this.revalidate();
			
		}
		else {
			int currentElemParentID = ((Integer.parseInt(elements.get(0).attr("parentid")))); 
			int currentElemLevel = Integer.parseInt(elements.get(0).attr("level"));
			int xOffset=0;
			int currentWidth= this.getWidth()/elements.size();
			
			DrawnNodeInfo currentNodeParent = getParentNode(currentElemParentID);

			if (processedNodes.size()>1){
				currentWidth=currentNodeParent.width/elements.size();
				xOffset=currentNodeParent.x;
			} 
			for(int i=0;i<elements.size();i++) {
				Element currentElement = elements.get(i);
				Object currentElementParent = drawnNodesMap.get((Integer.parseInt(currentElement.attr("parentid")))); 
				String label = "Instances distribution: \n"+currentElement.attr("classes")+"\nEntropy: "+currentElement.attr("entropy");
				int currentDrawX = xOffset+(i*currentWidth);
				int currentDrawY =  (2*currentElemLevel) * 80;//Determines the y distance between levels.
				while(overlapsWithExistingCells(currentDrawX,currentDrawY)) {
					currentDrawX+=10;
				}
				

				/* uneditable */
				graph.getModel().beginUpdate();
				/* uneditable */
				
				String color="";
				if (currentElement.attr("isLeaf").contains("1")){
					color="whiteSpace=wrap;rounded=true;fillColor=white;fontColor=black";
				}
				else{
					color="whiteSpace=wrap;rounded=true;fontColor=black";
				}
				//Create the insert vertex object and draw
				Object drawingObject = graph.insertVertex(
						parent, 
						null,
						label,
						currentDrawX,
						currentDrawY,
						graphNodeWidth,
						graphNodeHeight,
						color,//Style for leaf or node
						false);
				graph.insertEdge(parent, null, currentElement.attr("attr"), currentElementParent, drawingObject, "fontColor=black");
				
				graph.getModel().endUpdate();
				this.revalidate();
				

				//Add id,object to the hashmap to track position
				drawnNodesMap.put(Integer.parseInt(currentElement.attr("id")), drawingObject);
				
				//Add the object info to drawnNodesMap
				DrawnNodeInfo currentNode = new DrawnNodeInfo((Integer.parseInt(elements.get(i).attr("parentid"))), //TODO parent used
						Integer.parseInt(elements.get(i).attr("id")),
						currentDrawX, currentDrawY,
						elements.get(i).attr("classes"),
						elements.get(i).attr("attr"),
						currentWidth
						);
				processedNodes.add(currentNode);
			}
			this.revalidate();
		}
	}
	
	/**Initialization of the components of the interface.*/
    public void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        resultsDataUsedLabel = new javax.swing.JLabel();
        ourCanvas= new JPanel();
        drawTreeButton = new javax.swing.JButton();
        pruneTreeButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        selectAnotherDataButton = new javax.swing.JButton();
        selectFilesDataUsedLabel = new javax.swing.JLabel();
        useDefaultDataButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        
        this.setTitle("Visual ID3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        resultsDataUsedLabel.setText("Tree | Data used: Default | Pruned tree: No");

        drawTreeButton.setText("Draw tree");
        drawTreeButton.setEnabled(true);
        drawTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawTreeAction(evt);
            }
        });
        
        pruneTreeButton.setText("Prune tree");
        pruneTreeButton.setEnabled(false);
        pruned=false;
        pruneTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pruneTree(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(resultsDataUsedLabel)
                        .addContainerGap(654, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(drawTreeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pruneTreeButton)
                        .addGap(24, 24, 24))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(ourCanvas)
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsDataUsedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ourCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drawTreeButton)
                    .addComponent(pruneTreeButton))
                .addGap(14, 14, 14))
        );

        jTabbedPane1.addTab("View Tree", jPanel3);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("Developed by:\nGabriel Campero gabrielcampero@acm.org\nVishnu Unnikrishnan\n\nMaster's assignment: Machine Learning\nWinter Semester 2014/2015\n\nFakultät für Informatik, Otto-von-Güericke Universität\n\nThe Car Evaluation Database (the default data used in our program) was derived from a simple hierarchical decision model\noriginally developed for the demonstration of DEX, M. Bohanec, V. Rajkovic: Expert system for decision making.\nSistemica 1(1), pp. 145-157, 1990.). \n\n\nLichman, M. (2013). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml].\nIrvine, CA: University of California, School of Information and Computer Science.");
        jScrollPane3.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        
        selectAnotherDataButton.setToolTipText("Please select a folder with .data and .c45-names files, following the C4.5 format.<br>For more information on this format visit:<br>http://www.cs.washington.edu/dm/vfml/appendixes/c45.htm.");
        selectAnotherDataButton.setText("Select another data");
        selectAnotherDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAnotherDataAction(evt);
            }
        });

        selectFilesDataUsedLabel.setText("Data used: Default");

        useDefaultDataButton.setText("Use default data");
        useDefaultDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDefaultDataAction(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectFilesDataUsedLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(useDefaultDataButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 331, Short.MAX_VALUE)
                .addComponent(selectAnotherDataButton)
                .addGap(71, 71, 71))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectFilesDataUsedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useDefaultDataButton)
                    .addComponent(selectAnotherDataButton))
                .addContainerGap(441, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Select Data", jPanel2);
        
        jTabbedPane1.addTab("About", jPanel5);

        jPanel4.setBackground(java.awt.Color.white);

        jLabel1.setIcon(new javax.swing.ImageIcon("src/ml/view/images/ID3Logo_Small.png")); 

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        this.pack();
        
        this.setExtendedState(MAXIMIZED_BOTH);
    }                                                             

    /*Action handlers*/
    
    /*Function that implements the changes when the button "Use default data" is pressed*/
    /**
     * Use default data action.
     *
     * @param evt the evt
     */
    private void useDefaultDataAction(java.awt.event.ActionEvent evt) {                                         
    	if (!ID3Singleton.getInstance().usingDefaultDataFolder()){
    		ID3Singleton.getInstance().useDefaultDataFolder();
    		selectFilesDataUsedLabel.setText("Data used: Default");
        	resultsDataUsedLabel.setText("Tree | Data used: Default | Pruned tree: No");
        	pruned=false;
        	redraw=true;
			drawTreeButton.setEnabled(true);
			pruneTreeButton.setEnabled(false);
        }
    }                                        

    /*Function that implements the changes when the button "Select another data" is pressed*/
    /**
     * Select another data action.
     *
     * @param evt the evt
     */
    private void selectAnotherDataAction(java.awt.event.ActionEvent evt) {                                         
    	javax.swing.JFileChooser jFileChooser1 = new javax.swing.JFileChooser(){
    		public void approveSelection(){
        		super.approveSelection();
            	ID3Singleton.getInstance().setDataFolder(getSelectedFile().getPath());//Function to set the new data folder.            	
            	redraw=true;
            	drawTreeButton.setEnabled(true);
        		pruneTreeButton.setEnabled(false);
        		pruned=false;
            	
            	/**Updating the current labels*/
                if (!ID3Singleton.getInstance().usingDefaultDataFolder()){
                  	 selectFilesDataUsedLabel.setText("Data used: "+ID3Singleton.getInstance().getDataFolder());
                     resultsDataUsedLabel.setText("Tree | Data used: "+ID3Singleton.getInstance().getDataFolder()+" | Pruned tree: No");
                }
                else{
                	selectFilesDataUsedLabel.setText("Data used: Default");
                    resultsDataUsedLabel.setText("Tree | Data used: Default | Pruned tree: No");
                }
                
        	}
        };
        
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
    	jFileChooser1.showOpenDialog(this); //Now we show the file chooser
    }                                        


    /*Function that implements the changes when the button "Draw tree" is pressed*/
    /**
     * Draw tree action.
     *
     * @param evt the evt
     */
    private void drawTreeAction(java.awt.event.ActionEvent evt) {    			
		if(tree==null || redraw) {
			loadTree();
			redraw=false;
			String textForLabel="Tree | Data used: ";
			if (ID3Singleton.getInstance().usingDefaultDataFolder()){
				textForLabel+="Default ";
			}
			else{
				textForLabel+=ID3Singleton.getInstance().getDataFolder()+" ";
			}
			textForLabel+="| Pruned tree: ";
			if (pruned){
				textForLabel+="Yes ";
			}
			else{
				textForLabel+="No ";
			}
			textForLabel+="| Acurracy over validation data: "+ID3Singleton.getInstance().getAccuracyOverValidationData();
			resultsDataUsedLabel.setText(textForLabel);
		}
		boolean somethingPrinted = false;
		while (currentLevel<=maxTreeLevel && !somethingPrinted){
    		Elements currentLevelList= getCurrentLevelNodes();
    		if (currentLevelList.isEmpty()){
    	    	currentLevel++; //Have an empty list 
    	    	currentParentInLevel=-1; //New Level
    		}
    		else{
    			currentParentInLevel=Integer.parseInt(currentLevelList.get(0).attr("parentId")); //TODO parent used
    			currentLevelList=getStepNodes();
    			drawTree(currentLevelList); //<-Line added...
    			
    			somethingPrinted=true;
    		}
    	}
		if (currentLevel>maxTreeLevel){
			drawTreeButton.setEnabled(false);
			pruneTreeButton.setEnabled(true);
		}
    }
    
    /*Function that implements the changes when the "Prune tree" button is pressed*/
    /**
     * Prune tree.
     *
     * @param evt the evt
     */
    private void pruneTree(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    	ID3Singleton.getInstance().pruneTree();
    	drawTreeButton.setEnabled(true);
    	redraw=true;
		pruneTreeButton.setEnabled(false);
		pruned=true;
		if (ourCanvas!=null){
			ourCanvas.removeAll();
		}
    }                                        

    /**
     * Main function, launching the ID3GUI.
     * 
     * @param args the command line arguments
     * 
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ID3GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ID3GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ID3GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ID3GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ID3GUI().setVisible(true);
            }
        });
    }

    // Variables declaration      
    /** The draw tree button. */
    private javax.swing.JButton drawTreeButton;
    
    /** The prune tree button. */
    private javax.swing.JButton pruneTreeButton;
    
    /** The select another data button. */
    private javax.swing.JButton selectAnotherDataButton;
    
    /** The use default data button. */
    private javax.swing.JButton useDefaultDataButton;
    
    /** The j label1. */
    private javax.swing.JLabel jLabel1;
    
    /** The results data used label. */
    private javax.swing.JLabel resultsDataUsedLabel;
    
    /** The select files data used label. */
    private javax.swing.JLabel selectFilesDataUsedLabel;
    
    /** The j panel1. */
    private javax.swing.JPanel jPanel1;
    
    /** The j panel2. */
    private javax.swing.JPanel jPanel2;
    
    /** The j panel3. */
    private javax.swing.JPanel jPanel3;
    
    /** The j panel4. */
    private javax.swing.JPanel jPanel4;
    
    /** The j panel5. */
    private javax.swing.JPanel jPanel5;
    
    /** The our canvas. */
    private JPanel ourCanvas;
    
    /** The j scroll pane3. */
    private javax.swing.JScrollPane jScrollPane3;
    
    /** The j tabbed pane1. */
    private javax.swing.JTabbedPane jTabbedPane1;
    
    /** The j text area2. */
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration                   
}