# ID3AlgorithmForLearningDecisionTree
A simple implementation of the ID3 algorithm for learning decision trees. A JSwing and MXGraph-based GUI for visualizing the growth of the tree and the execution of reduced error pruning is also included. Submission for assignment of Machine Learning course.

As documentation, the generated Javadocs and the annotations of the code itself are supplied. We also included in the doc folder a ClassDiagram that helps to visualize the implementation.

Our code should be easy to run and test, using (by default) the car dataset (http://archive.ics.uci.edu/ml/datasets/Car+Evaluation). There is a Main class with a main function, in the control package. 

First the user must click on draw to grow the tree with ID3. Each click of the button represents an expansion of ID3, or a step of the algorithm.

After the tree is fully grown, the user can click on prune. By clicking on the prune button the user sees the effects of reduced error pruning, happening by deleting one node at a time. This algorithm works by selecting from all nodes, the one whose deletion would improve the accuracy over the validation data. By clicking again, the user is applying reduced error pruning over the resulting tree. This can be carried out until no further improvements are possible.

An additional, dummy dataset was tested by us. It is available in the data folder.

The tooltip on the "select another data" button explains the characteristics of expected datasets. Any dataset is to be passed as a folder with one .data file and one .c45-names file. The program only draws the tree for correct data. If there is no visualization of the tree, the data should be checked first.

By default we randomly divide the data in the following way: 70% for training, and 30% for validation. This can be changed in the percentage variable of the ID3Learning class.

The nodes can be moved for ease of viewing.
