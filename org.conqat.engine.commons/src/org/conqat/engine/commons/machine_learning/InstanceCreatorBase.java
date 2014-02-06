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

import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * Abstract base class to create a weka instance for a classification object of
 * type T. An instance is a representation of the classification object based on
 * features and label which is used by a machine classifier. The scheme of an
 * instance consists of attribute declarations and a class. The attributes
 * correspond to the calculated features, the class wraps all possible labels of
 * the data.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44457 $
 * @ConQAT.Rating GREEN Hash: 0B117C9D74941F1C7BA2FB370E2F4C9F
 */
public abstract class InstanceCreatorBase<T, LABEL extends Enum<LABEL>> {

	/**
	 * Attribute vector needed for instance creation, contains all feature
	 * values and class label.
	 */
	private FastVector attributes;

	/** Set of features used to describe the classification object. */
	private final List<IFeature<T>> features;

	/** Constructor. */
	protected InstanceCreatorBase(Class<LABEL> labelClass) {
		features = getFeatures();
		createAttributeSchema(labelClass);
	}

	/** Creates a weka instance for the given classification object. */
	public weka.core.Instance createWekaInstance(T classificationObject,
			LABEL label) {

		// create empty instance. The size of the instance corresponds to the
		// number of features plus the class label.
		weka.core.Instance instance = new weka.core.Instance(
				features.size() + 1);

		// calculate features
		for (int i = 0; i < features.size(); i++) {
			instance.setValue((Attribute) attributes.elementAt(i), features
					.get(i).getValue(classificationObject));
		}

		// set instance label
		Attribute classAttribute = (Attribute) attributes.elementAt(attributes
				.size() - 1);
		instance.setValue(classAttribute, label.name());

		return instance;

	}

	/** Returns a list of features used for machine learning. */
	protected abstract List<IFeature<T>> getFeatures();

	/**
	 * Sets up the weka schema of attributes and class labels for this instance.
	 */
	private void createAttributeSchema(Class<LABEL> labelClass) {

		// attributes of the instance are the features and one class label
		attributes = new FastVector(features.size() + 1);

		for (IFeature<T> feature : features) {
			attributes.addElement(new Attribute(feature.getName()));
		}

		addClassAttribute(labelClass);

	}

	/**
	 * Adds the class of the classification object with all possible labels to
	 * the attribute vector.
	 */
	private void addClassAttribute(Class<LABEL> labelClass) {

		FastVector labelVector = new FastVector(
				labelClass.getEnumConstants().length);

		for (LABEL label : labelClass.getEnumConstants()) {
			labelVector.addElement(label.name());
		}

		Attribute classAttribute = new Attribute("Classification Category",
				labelVector);
		attributes.addElement(classAttribute);
	}

	/** Returns a default label for an instance. */
	public abstract LABEL getDefaultLabel();

	/** Returns the attribute vector. */
	public FastVector getAttributes() {
		return attributes;
	}

	/** Returns the number of features. */
	public int getNumberOfFeatures() {

		return features.size();
	}
}
