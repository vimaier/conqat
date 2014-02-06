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

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to create a data set for the weka library. T represents the class of
 * the objects in the data set, LABEL represents the label that can be assigned
 * to the classification objects by the classifier.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44457 $
 * @ConQAT.Rating GREEN Hash: EF070F7A75FD1939978A405921CB45DC
 */
public class DataSetCreator<T, LABEL extends Enum<LABEL>> {

	/** The training data set. */
	private Instances trainingDataSet;

	/**
	 * Helper class to create a weka instance for a given classification object.
	 */
	private final InstanceCreatorBase<T, LABEL> instanceCreator;

	/** List of classification objects and their labels as training data points. */
	private final PairList<T, LABEL> trainingDataPoints = new PairList<T, LABEL>();

	/** Constructor. */
	public DataSetCreator(InstanceCreatorBase<T, LABEL> instanceCreator) {
		this.instanceCreator = instanceCreator;

	}

	/** Constructor with a given dataset (e.g. possibly loaded from file) */
	public DataSetCreator(InstanceCreatorBase<T, LABEL> instanceCreator,
			Instances dataset) {
		this.instanceCreator = instanceCreator;

		this.trainingDataSet = dataset;
		dataset.setClassIndex(instanceCreator.getNumberOfFeatures());
	}

	/**
	 * Creates a weka instance for the given classification object and adds it
	 * to the training data set.
	 * 
	 * @param label
	 *            The label of the classification object required for learning.
	 * 
	 */
	public void addData(T classificationObject, LABEL label) {
		CCSMAssert.isTrue(trainingDataSet == null,
				"TrainingDataSet not null when adding a data point");
		trainingDataPoints.add(classificationObject, label);
	}

	/**
	 * Creates a weka instance for the given object. This method is used for an
	 * object without label to be classified after learning. It creates an
	 * instance with a default label.
	 * 
	 * @return The weka instance with a default label.
	 */
	public Instance createWekaUnlabeledInstance(T object) {
		return createInstance(object, instanceCreator.getDefaultLabel(),
				createInstances(1));
	}

	/** Returns the data set. */
	public Instances getDataSet() {
		if (trainingDataSet == null) {
			trainingDataSet = createInstances(trainingDataPoints.size());

			for (int i = 0; i < trainingDataPoints.size(); i++) {
				createInstance(trainingDataPoints.getFirst(i),
						trainingDataPoints.getSecond(i), trainingDataSet);
			}
		}

		return trainingDataSet;
	}

	/**
	 * Creates a weka instance for the given classification object and the given
	 * label and adds it to the given data set.
	 */
	private Instance createInstance(T classificationObject, LABEL label,
			Instances dataSet) {
		Instance instance = instanceCreator.createWekaInstance(
				classificationObject, label);
		dataSet.add(instance);
		instance.setDataset(dataSet);
		return instance;
	}

	/** Creates an instance set of the given size. */
	private Instances createInstances(int size) {
		Instances instances = new Instances("Rel",
				instanceCreator.getAttributes(), size);
		instances.setClassIndex(instanceCreator.getNumberOfFeatures());
		return instances;
	}

	/** Returns the instance creator. */
	public InstanceCreatorBase<T, LABEL> getInstanceCreator() {
		return instanceCreator;
	}
}
