# ID3AlgorithmForLeaningDecisionTree
A simple implementation of the ID3 algorithm for learning decision trees,. A JSwing and MXGraph-based GUI for visualizing the tree and performing reduced error pruning is also included. Submission for Machine Learning course.

As documentation, the generated Javadocs and the annotations of the code itself are supplied. We also included in the doc folder a ClassDiagram that helps to visualize the implementation.

Our code can be loaded into Eclipse, and should be easy to run and test, using (by default) the car dataset (http://archive.ics.uci.edu/ml/datasets/Car+Evaluation). There is a Main class with a main function, in the control package. First the user must click on draw to grow the tree with ID3, and after the tree is fully grown, the user can click on prune. After clicking prune, the current tree momentarily disappears from the screen, so the user should click on draw again to get the pruned tree to be shown.

An additional, dummy dataset was tested by us. It is available in the data folder.

The tooltip on the "select another data" button explains the characteristics of expected datasets. Any dataset is to be passed as a folder with one .data file and one .c45-names file.

By default we randomly divide the data in the following way: 70% for training, and 30% for validation. This can be changed in the percentage variable of the ID3Learning class.

The nodes can be moved for ease of viewing.
