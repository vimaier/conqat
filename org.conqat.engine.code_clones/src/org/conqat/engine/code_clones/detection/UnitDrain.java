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
package org.conqat.engine.code_clones.detection;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.instance.ConQATStringPool;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Drains units from an {@link IUnitProvider} into a list.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 406A0C9631BEF43D02A98276112CC3AB
 */
/* package */class UnitDrain {

	/** Logger used to issue status messages */
	private final IConQATLogger logger;

	/**
	 * If this string is set to a non-empty value, a debug file (containing the
	 * normalized units) is written for each input file.
	 */
	private final String debugFileExtension;

	/**
	 * Key that contains flag that determines whether file gets ignored.
	 * Influences log message generation, but not unit draining.
	 */
	private final String ignoreKey;

	/**
	 * Flag that determines whether {@link ConQATStringPool} gets cleared after
	 * each element
	 */
	private final boolean clearStringPoolAfterElement;

	/** Keeps track of processed elements */
	private int elementCount = 0;

	/**
	 * Constructor
	 * 
	 * @param logger
	 *            logger of the processor that executes the {@link UnitDrain}.
	 */
	/* package */UnitDrain(IConQATLogger logger, String debugFileExtension,
			String ignoreKey, boolean clearStringPoolAfterElement) {
		this.logger = logger;
		this.debugFileExtension = debugFileExtension;
		this.ignoreKey = ignoreKey;
		this.clearStringPoolAfterElement = clearStringPoolAfterElement;

		if (clearStringPoolAfterElement) {
			logger.info("Clearing StringPool after each element");
		}
	}

	/**
	 * Determines all units used for clone detection.
	 * 
	 * @param units
	 *            List to add units to. If null, units are discarded.
	 * */
	public void drainUnits(ITextResource input,
			IUnitProvider<ITextResource, Unit> unitProvider, List<Unit> units)
			throws ConQATException {
		// include sentinelizer
		unitProvider = new Sentinelizer(unitProvider);

		// read units from unit provider
		logger.debug("Before unit drain...");
		int unitCount = drainUnitProvider(input, unitProvider, units);
		logger.debug("Units drained:" + unitCount);

		// write debug files
		if (!StringUtils.isEmpty(debugFileExtension)) {
			writeDebugFiles(units, input);
		}

		// mark files without units
		logEmptyFiles(input);
	}

	/**
	 * Writes a debug file for each input file. The debug file contains the
	 * units after normalization (in the same line numbers as in the original
	 * file). The name of the debug file is the name of the original file with
	 * the debugFileExtension.
	 */
	private void writeDebugFiles(List<Unit> units, ITextResource input)
			throws ConQATException {
		ListMap<String, Unit> pathUnits = new ListMap<String, Unit>();
		for (Unit unit : units) {
			pathUnits.add(unit.getElementUniformPath(), unit);
		}

		Map<String, ITextElement> map = ResourceTraversalUtils
				.createUniformPathToElementMap(input, ITextElement.class);

		for (String path : pathUnits.getKeys()) {
			ITextElement element = map.get(path);
			CCSMAssert.isNotNull(element,
					"May not be null due to same input root used!");

			List<Unit> unitsInFile = pathUnits.getCollection(path);
			CanonicalFile originalFile = ResourceUtils.getFile(element);
			if (originalFile == null) {
				throw new ConQATException(
						"Writing of debug files only works for elements in the file system!");
			}

			File debugFile = new File(originalFile.getCanonicalPath()
					+ debugFileExtension);
			writeDebugFile(debugFile, element, unitsInFile);
		}
	}

	/**
	 * Writes a single debug file.
	 * 
	 * @param debugFile
	 *            Debug file to write
	 * @param unitsInFile
	 *            List of units that gets written into the file
	 */
	private void writeDebugFile(File debugFile, ITextElement element,
			List<Unit> unitsInFile) throws ConQATException {
		int line = 0;
		StringBuilder content = new StringBuilder();
		for (Unit unit : unitsInFile) {
			int unitStartLine = TextElementUtils
					.convertFilteredOffsetToUnfilteredLine(element,
							unit.getFilteredStartOffset());
			while (line < unitStartLine) {
				content.append(StringUtils.CR);
				line++;
			}
			content.append(unit.getContent());
		}

		try {
			FileSystemUtils.writeFile(debugFile, content.toString());
		} catch (IOException e) {
			logger.warn("Could not write debug file: " + debugFile + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Searches for files with 0 units used for clone detection and creates log
	 * messages accordingly. The underlying assumption is that a file that is
	 * included in clone detection, yet does not contain a single unit that gets
	 * used for clone detection, can potentially indicate a configuration
	 * problem.
	 * <p>
	 * If a file is marked as ignore, no warning is created
	 */
	private void logEmptyFiles(ITextResource input) {
		List<ITextElement> elements = ResourceTraversalUtils
				.listTextElements(input);

		for (ITextElement element : elements) {
			if (element.getValue(UnitProcessorBase.UNITS_KEY) == null) {
				element.setValue(UnitProcessorBase.UNITS_KEY, 0);

				if (!NodeUtils.isIgnored(element, ignoreKey)) {
					logger.debug("File " + element.getLocation()
							+ " has 0 non-ignored units.");
				}
			}
		}
	}

	/**
	 * Retrieves all units from the unitProvider and stores them in a list
	 * <p>
	 * Additionally, the number of units encountered in a file is stored at the
	 * corresponding element. However, no unit count value is stored at file
	 * elements that don't contain a single unit.
	 * 
	 * @param units
	 *            List to add units to. If null, units are discarded.
	 * 
	 * @return Number of units drained
	 */
	private int drainUnitProvider(ITextResource input,
			IUnitProvider<ITextResource, Unit> unitProvider, List<Unit> units)
			throws ConQATException {

		List<ITextElement> elements = ResourceTraversalUtils
				.listNonIgnoredElements(input, ignoreKey, ITextElement.class);
		// sort to make processing order stable
		Collections.sort(elements, NodeIdComparator.INSTANCE);

		int unitsInElementsCount = 0;
		for (ITextElement fileElement : elements) {
			// initialize lazy pipeline with leaf
			unitProvider.init(fileElement, logger);

			// drain units into list
			unitsInElementsCount += drainUnitsFromElement(fileElement,
					unitProvider, units);
		}

		return unitsInElementsCount;
	}

	/** Drain units from element */
	private int drainUnitsFromElement(ITextElement input,
			IUnitProvider<ITextResource, Unit> unitProvider, List<Unit> units)
			throws CloneDetectionException, ConQATException {
		int unitsInFileCount = 0;
		Unit unit = unitProvider.getNext();
		while (unit != null) {
			if (units != null) {
				units.add(unit);
			}
			// ignore synthetic units, since they are not part of the file
			if (!unit.isSynthetic()) {
				unitsInFileCount++;
			}

			Unit nextUnit = unitProvider.getNext();
			if (lastUnitInFile(unit, nextUnit)) {
				input.setValue(UnitProcessorBase.UNITS_KEY, unitsInFileCount);
				unitsInFileCount = 0;
			}

			unit = nextUnit;
		}

		if (clearStringPoolAfterElement) {
			ConQATStringPool.clear();
			if (++elementCount % 100 == 0) {
				logger.info("Drained units from " + elementCount + " elements");
			}
		}

		return unitsInFileCount;
	}

	/** Checks whether a unit is the last unit from its file */
	private boolean lastUnitInFile(Unit unit, Unit nextUnit) {
		return nextUnit == null || !unit.inSameElement(nextUnit);
	}

}