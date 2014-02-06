/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.lib.simulink.model;

/**
 * This class contains constants used by the Simulink model builder. These
 * constants are section and parameter names that refer to the MDL file. Section
 * and parameters are distinguished by the prefix of the constants (SECTION vs
 * PARAM). The remainder of the constant is just like the name in the MDL file.
 * We use mixed case here to express the case differences found in the MDL file,
 * e.g. 'Name' vs 'name'.
 * 
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A8B4CF99C2F13B0143191B9550A1F4DE
 */
public class SimulinkConstants {
	/** Model section. */
	public static final String SECTION_Model = "Model";

	/** Stateflow section. */
	public static final String SECTION_Stateflow = "Stateflow";

	/** Library section. */
	public static final String SECTION_Library = "Library";

	/** Destination section in Stateflow transitions. */
	public static final String SECTION_dst = "dst";

	/** Source section in Stateflow transitions. */
	public static final String SECTION_src = "src";

	/** Transition section (Stateflow) */
	public static final String SECTION_transition = "transition";

	/** Junction section (Stateflow) */
	public static final String SECTION_junction = "junction";

	/** Event section (Stateflow) */
	public static final String SECTION_event = "event";

	/** Data section (Stateflow) */
	public static final String SECTION_data = "data";

	/** Target section (Stateflow) */
	public static final String SECTION_target = "target";

	/** State section (Stateflow) */
	public static final String SECTION_state = "state";

	/** Chart section (Stateflow) */
	public static final String SECTION_chart = "chart";

	/** Machine section (Stateflow) */
	public static final String SECTION_machine = "machine";

	/** Block parameter defaults section. */
	public static final String SECTION_BlockParameterDefaults = "BlockParameterDefaults";

	/** Block defaults section. */
	public static final String SECTION_BlockDefaults = "BlockDefaults";

	/** Annotation defaults section. */
	public static final String SECTION_AnnotationDefaults = "AnnotationDefaults";

	/** Line defaults section. */
	public static final String SECTION_LineDefaults = "LineDefaults";

	/** Block section. */
	public static final String SECTION_Block = "Block";

	/** System section. */
	public static final String SECTION_System = "System";

	/** Branch section. */
	public static final String SECTION_Branch = "Branch";

	/** Line section. */
	public static final String SECTION_Line = "Line";

	/** Annotation section. */
	public static final String SECTION_Annotation = "Annotation";

	/** Name parameter. */
	public static final String PARAM_Name = "Name";

	/** Tree node parameter for parent relationship (Stateflow) */
	public static final String PARAM_treeNode = "treeNode";

	/** Link node parameter for parent relationship (Stateflow) */
	public static final String PARAM_linkNode = "linkNode";

	/** State label (Stateflow) */
	public static final String PARAM_labelString = "labelString";

	/** Junction type (Stateflow) */
	public static final String PARAM_type = "type";

	/** Machine parameter (Stateflow) */
	public static final String PARAM_machine = "machine";

	/** Id (Stateflow) */
	public static final String PARAM_id = "id";

	/** Name (Stateflow) */
	public static final String PARAM_name = "name";

	/** Points (used for lines). */
	public static final String PARAM_Points = "Points";

	/** Intersection (used for stateflow transitions). */
	public static final String PARAM_intersection = "intersection";

	/** Position (of blocks). */
	public static final String PARAM_Position = "Position";

	/** Background color (of blocks) */
	public static final String PARAM_BackgroundColor = "BackgroundColor";

	/** Block type parameter. */
	public static final String PARAM_BlockType = "BlockType";

	/** Destination block parameter. */
	public static final String PARAM_DstBlock = "DstBlock";

	/** Source port parameter. */
	public static final String PARAM_SrcPort = "SrcPort";

	/** Destination port parameter. */
	public static final String PARAM_DstPort = "DstPort";

	/** Source block parameter. */
	public static final String PARAM_SrcBlock = "SrcBlock";

	/** Ports parameter. */
	public static final String PARAM_Ports = "Ports";

	/** Port parameter. */
	public static final String PARAM_Port = "Port";

	/** Targetlink data parameter. */
	public static final String PARAM_TARGETLINK_DATA = "data";

	/** The parameter that specifies the referenced type for a reference. */
	public static final String PARAM_SourceType = "SourceType";

	/** Simulink block type 'Abs'. */
	public static final String TYPE_Abs = "Abs";

	/** Simulink block type 'Assertion'. */
	public static final String TYPE_Assertion = "Assertion";

	/** Simulink block type 'Assignment'. */
	public static final String TYPE_Assignment = "Assignment";

	/** Simulink block type 'Backlash'. */
	public static final String TYPE_Backlash = "Backlash";

	/** Simulink block type 'Bias'. */
	public static final String TYPE_Bias = "Bias";

	/** Simulink block type 'BusAssignment'. */
	public static final String TYPE_BusAssignment = "BusAssignment";

	/** Simulink block type 'BusCreator'. */
	public static final String TYPE_BusCreator = "BusCreator";

	/** Simulink block type 'BusSelector'. */
	public static final String TYPE_BusSelector = "BusSelector";

	/** Simulink block type 'Clock'. */
	public static final String TYPE_Clock = "Clock";

	/** Simulink block type 'CombinatorialLogic'. */
	public static final String TYPE_CombinatorialLogic = "CombinatorialLogic";

	/** Simulink block type 'ComplexToMagnitudeAngle'. */
	public static final String TYPE_ComplexToMagnitudeAngle = "ComplexToMagnitudeAngle";

	/** Simulink block type 'ComplexToRealImag'. */
	public static final String TYPE_ComplexToRealImag = "ComplexToRealImag";

	/** Simulink block type 'Constant'. */
	public static final String TYPE_Constant = "Constant";

	/** Simulink block type 'DataStoreMemory'. */
	public static final String TYPE_DataStoreMemory = "DataStoreMemory";

	/** Simulink block type 'DataStoreRead'. */
	public static final String TYPE_DataStoreRead = "DataStoreRead";

	/** Simulink block type 'DataStoreWrite'. */
	public static final String TYPE_DataStoreWrite = "DataStoreWrite";

	/** Simulink block type 'DataTypeConversion'. */
	public static final String TYPE_DataTypeConversion = "DataTypeConversion";

	/** Simulink block type 'DeadZone'. */
	public static final String TYPE_DeadZone = "DeadZone";

	/** Simulink block type 'Demux'. */
	public static final String TYPE_Demux = "Demux";

	/** Simulink block type 'Derivative'. */
	public static final String TYPE_Derivative = "Derivative";

	/** Simulink block type 'DigitalClock'. */
	public static final String TYPE_DigitalClock = "DigitalClock";

	/** Simulink block type 'DiscreteFilter'. */
	public static final String TYPE_DiscreteFilter = "DiscreteFilter";

	/** Simulink block type 'DiscreteIntegrator'. */
	public static final String TYPE_DiscreteIntegrator = "DiscreteIntegrator";

	/** Simulink block type 'DiscretePulseGenerator'. */
	public static final String TYPE_DiscretePulseGenerator = "DiscretePulseGenerator";

	/** Simulink block type 'DiscreteStateSpace'. */
	public static final String TYPE_DiscreteStateSpace = "DiscreteStateSpace";

	/** Simulink block type 'DiscreteTransferFcn'. */
	public static final String TYPE_DiscreteTransferFcn = "DiscreteTransferFcn";

	/** Simulink block type 'DiscreteZeroPole'. */
	public static final String TYPE_DiscreteZeroPole = "DiscreteZeroPole";

	/** Simulink block type 'Display'. */
	public static final String TYPE_Display = "Display";

	/** Simulink block type 'Fcn'. */
	public static final String TYPE_Fcn = "Fcn";

	/** Simulink block type 'From'. */
	public static final String TYPE_From = "From";

	/** Simulink block type 'FromFile'. */
	public static final String TYPE_FromFile = "FromFile";

	/** Simulink block type 'FromWorkspace'. */
	public static final String TYPE_FromWorkspace = "FromWorkspace";

	/** Simulink block type 'Gain'. */
	public static final String TYPE_Gain = "Gain";

	/** Simulink block type 'Goto'. */
	public static final String TYPE_Goto = "Goto";

	/** Simulink block type 'GotoTagVisibility'. */
	public static final String TYPE_GotoTagVisibility = "GotoTagVisibility";

	/** Simulink block type 'Ground'. */
	public static final String TYPE_Ground = "Ground";

	/** Simulink block type 'HitCross'. */
	public static final String TYPE_HitCross = "HitCross";

	/** Simulink block type 'InitialCondition'. */
	public static final String TYPE_InitialCondition = "InitialCondition";

	/** Simulink block type 'Inport'. */
	public static final String TYPE_Inport = "Inport";

	/** Simulink block type 'Integrator'. */
	public static final String TYPE_Integrator = "Integrator";

	/** Simulink block type 'Logic'. */
	public static final String TYPE_Logic = "Logic";

	/** Simulink block type 'Lookup'. */
	public static final String TYPE_Lookup = "Lookup";

	/** Simulink block type 'Lookup2D'. */
	public static final String TYPE_Lookup2D = "Lookup2D";

	/** Simulink block type 'M-S-Function'. */
	public static final String TYPE_M_S_Function = "M-S-Function";

	/** Simulink block type 'MATLABFcn'. */
	public static final String TYPE_MATLABFcn = "MATLABFcn";

	/** Simulink block type 'MagnitudeAngleToComplex'. */
	public static final String TYPE_MagnitudeAngleToComplex = "MagnitudeAngleToComplex";

	/** Simulink block type 'Math'. */
	public static final String TYPE_Math = "Math";

	/** Simulink block type 'Memory'. */
	public static final String TYPE_Memory = "Memory";

	/** Simulink block type 'MinMax'. */
	public static final String TYPE_MinMax = "MinMax";

	/** Simulink block type 'Model'. */
	public static final String TYPE_Model = "Model";

	/** Simulink block type 'MultiPortSwitch'. */
	public static final String TYPE_MultiPortSwitch = "MultiPortSwitch";

	/** Simulink block type 'Mux'. */
	public static final String TYPE_Mux = "Mux";

	/** Simulink block type 'Outport'. */
	public static final String TYPE_Outport = "Outport";

	/** Simulink block type 'Probe'. */
	public static final String TYPE_Probe = "Probe";

	/** Simulink block type 'Product'. */
	public static final String TYPE_Product = "Product";

	/** Simulink block type 'Quantizer'. */
	public static final String TYPE_Quantizer = "Quantizer";

	/** Simulink block type 'RandomNumber'. */
	public static final String TYPE_RandomNumber = "RandomNumber";

	/** Simulink block type 'RateLimiter'. */
	public static final String TYPE_RateLimiter = "RateLimiter";

	/** Simulink block type 'RateTransition'. */
	public static final String TYPE_RateTransition = "RateTransition";

	/** Simulink block type 'RealImagToComplex'. */
	public static final String TYPE_RealImagToComplex = "RealImagToComplex";

	/** Simulink block type 'Reference'. */
	public static final String TYPE_Reference = "Reference";

	/** Simulink block type 'RelationalOperator'. */
	public static final String TYPE_RelationalOperator = "RelationalOperator";

	/** Simulink block type 'Relay'. */
	public static final String TYPE_Relay = "Relay";

	/** Simulink block type 'Rounding'. */
	public static final String TYPE_Rounding = "Rounding";

	/** Simulink block type 'S-Function'. */
	public static final String TYPE_S_Function = "S-Function";

	/** Simulink block type 'Saturate'. */
	public static final String TYPE_Saturate = "Saturate";

	/** Simulink block type 'Scope'. */
	public static final String TYPE_Scope = "Scope";

	/** Simulink block type 'Selector'. */
	public static final String TYPE_Selector = "Selector";

	/** Simulink block type 'SignalConversion'. */
	public static final String TYPE_SignalConversion = "SignalConversion";

	/** Simulink block type 'SignalGenerator'. */
	public static final String TYPE_SignalGenerator = "SignalGenerator";

	/** Simulink block type 'SignalSpecification'. */
	public static final String TYPE_SignalSpecification = "SignalSpecification";

	/** Simulink block type 'Signum'. */
	public static final String TYPE_Signum = "Signum";

	/** Simulink block type 'Sin'. */
	public static final String TYPE_Sin = "Sin";

	/** Simulink block type 'StateSpace'. */
	public static final String TYPE_StateSpace = "StateSpace";

	/** Simulink block type 'Step'. */
	public static final String TYPE_Step = "Step";

	/** Simulink block type 'Stop'. */
	public static final String TYPE_Stop = "Stop";

	/** Simulink block type 'SubSystem'. */
	public static final String TYPE_SubSystem = "SubSystem";

	/** Simulink block type 'Sum'. */
	public static final String TYPE_Sum = "Sum";

	/** Simulink block type 'Switch'. */
	public static final String TYPE_Switch = "Switch";

	/** Simulink block type 'Terminator'. */
	public static final String TYPE_Terminator = "Terminator";

	/** Simulink block type 'ToFile'. */
	public static final String TYPE_ToFile = "ToFile";

	/** Simulink block type 'ToWorkspace'. */
	public static final String TYPE_ToWorkspace = "ToWorkspace";

	/** Simulink block type 'TransferFcn'. */
	public static final String TYPE_TransferFcn = "TransferFcn";

	/** Simulink block type 'TransportDelay'. */
	public static final String TYPE_TransportDelay = "TransportDelay";

	/** Simulink block type 'Trigonometry'. */
	public static final String TYPE_Trigonometry = "Trigonometry";

	/** Simulink block type 'UniformRandomNumber'. */
	public static final String TYPE_UniformRandomNumber = "UniformRandomNumber";

	/** Simulink block type 'UnitDelay'. */
	public static final String TYPE_UnitDelay = "UnitDelay";

	/** Simulink block type 'VariableTransportDelay'. */
	public static final String TYPE_VariableTransportDelay = "VariableTransportDelay";

	/** Simulink block type 'Width'. */
	public static final String TYPE_Width = "Width";

	/** Simulink block type 'ZeroOrderHold'. */
	public static final String TYPE_ZeroOrderHold = "ZeroOrderHold";

	/** Simulink block type 'ZeroPole'. */
	public static final String TYPE_ZeroPole = "ZeroPole";

	/**
	 * Simulink block name 'Subsystem' (Used by target link for structuring
	 * synthesized blocks)
	 **/
	public static final String NAME_Subsystem = "Subsystem";

}