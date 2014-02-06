/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.commons.machine_learning;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

/**
 * Base class for a machine learning classifier using the WEKA library. T
 * represents the class of the objects to be classified, LABEL represents the
 * labels that can be assigned to a classification object. This class can save
 * the classification model and the training data to file.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46360 $
 * @ConQAT.Rating GREEN Hash: 5D836DB1D743847C0395AAABAB8887CC
 */
public class BaseWekaClassifier<T, LABEL extends Enum<LABEL>> {

	/** The creator of the training data set. */
	protected DataSetCreator<T, LABEL> wekaDataSetCreator;

	/** The classifier. */
	protected weka.classifiers.Classifier wekaClassifier;

	/** File containing the classification model. */
	protected File modelFile;

	/** File containing the training data. */
	protected File dataFile;

	/** Constructor with model and data file. */
	public BaseWekaClassifier(DataSetCreator<T, LABEL> dataSetCreator,
			File modelFile, File dataFile) {
		this.wekaDataSetCreator = dataSetCreator;
		this.modelFile = modelFile;
		this.dataFile = dataFile;
	}

	/**
	 * Adds a classification object with its label as data point to the data
	 * set.
	 */
	public void addData(T classificationObject, LABEL label) {
		wekaDataSetCreator.addData(classificationObject, label);
	}

	/**
	 * Returns the classification as a String for the given classification
	 * object. The method buildClassifier needs to be called before.
	 */
	public String getClassification(T classificationObject)
			throws ConQATException {

		weka.core.Instance instance = wekaDataSetCreator
				.createWekaUnlabeledInstance(classificationObject);

		try {
			CCSMAssert
					.isNotNull(wekaClassifier,
							"Weka Classifier called to classify instance although it was null.");
			double classification = wekaClassifier.classifyInstance(instance);
			return instance.classAttribute().value((int) classification);
		} catch (Exception e) {
			throw new ConQATException(e);
		}

	}

	/**
	 * Loads the classifier from the model file and the instance set from the
	 * data file.
	 */
	public void loadWekaClassifierAndData() throws ConQATException {
		wekaClassifier = loadClassifier();
		Instances data = loadData();

		data.setClassIndex(data.numAttributes() - 1);
		wekaDataSetCreator = new DataSetCreator<T, LABEL>(
				wekaDataSetCreator.getInstanceCreator(), data);
	}

	/** Loads the classifier from the model file. */
	private weka.classifiers.Classifier loadClassifier() throws ConQATException {
		try {
			return (weka.classifiers.Classifier) weka.core.SerializationHelper
					.read(modelFile.getAbsolutePath());
		} catch (Exception e) {
			throw new ConQATException(e);
		}
	}

	/** Loads the data set from file. */
	private Instances loadData() throws ConQATException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			return new Instances(reader);
		} catch (IOException io) {
			throw new ConQATException(io);
		} finally {
			FileSystemUtils.close(reader);
		}

	}

	/**
	 * Trains a classifier based on the training data set and saves the model to
	 * file.
	 */
	public Classifier buildAndSaveClassifier(EClassificationAlgorithm algorithm)
			throws ConQATException {
		Instances data = wekaDataSetCreator.getDataSet();
		wekaClassifier = getClassifier(algorithm);

		try {
			wekaClassifier.buildClassifier(data);
			saveClassifier(wekaClassifier);
			saveData(data);
			return wekaClassifier;
		} catch (Exception e) {
			throw new ConQATException(e);
		}

	}

	/**
	 * Returns a new classifier based on the given algorithm.
	 */
	protected weka.classifiers.Classifier getClassifier(
			EClassificationAlgorithm algorithm) {
		switch (algorithm) {
		case DECISION_TREE_REP:
			return new REPTree();
		case SUPPORT_VECTOR_MACHINE_SMO:
			return new SMO();
		case COST_SENSITIVE_CLASSIFIER:
			return new CostSensitiveClassifier();
		case DECISION_TREE_J48:
			return new J48();
		default:
			throw new AssertionError(
					"Cannot create a classifier without a specified algorithm.");
		}

	}

	/** Saves the given data set to file. */
	protected void saveData(Instances data) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(dataFile);
		saver.setDestination(dataFile);
		saver.writeBatch();
	}

	/** Saves the classifier model to file. */
	protected void saveClassifier(weka.classifiers.Classifier classifier)
			throws Exception {
		weka.core.SerializationHelper.write(modelFile.getAbsolutePath(),
				classifier);
	}

	/**
	 * Evaluates a classifier using 5-fold cross validation and returns the
	 * evaluation object. Use this method for debugging purpose to get
	 * information about precision, recall, etc.
	 */
	public Evaluation debugEvaluateClassifier() throws Exception, IOException {
		Instances data = wekaDataSetCreator.getDataSet();
		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(wekaClassifier, data, 5, new Random(1));
		return eval;
	}

	/**
	 * Evaluates a classifier using 5-fold cross validation and returns the
	 * evaluation object. Use this method for debugging purpose to get
	 * information about precision, recall, etc.
	 */
	public Evaluation debugEvaluateClassifierOnce() throws Exception,
			IOException {
		Instances data = wekaDataSetCreator.getDataSet();
		Evaluation eval = new Evaluation(data);
		eval.evaluateModel(wekaClassifier, data);
		return eval;
	}

	/**
	 * Visualizes a decision tree in a new pop-up window. This works only if the
	 * underlying classifier is a J48 tree.
	 */
	public void debugVisualizeDecisionTree() throws ConQATException {
		CCSMAssert.isTrue(wekaClassifier instanceof J48,
				"Only J48 trees can be visualized!");

		TreeVisualizer treeVisualizer;
		try {
			treeVisualizer = new TreeVisualizer(null,
					((J48) wekaClassifier).graph(), new PlaceNode2());
		} catch (Exception e) {
			throw new ConQATException(e);
		}
		JFrame frame = new JFrame("Weka Classifier Tree Visualizer: J48");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(800, 1200);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(treeVisualizer, BorderLayout.CENTER);
		frame.setVisible(true);
		treeVisualizer.fitToScreen();
	}
}
