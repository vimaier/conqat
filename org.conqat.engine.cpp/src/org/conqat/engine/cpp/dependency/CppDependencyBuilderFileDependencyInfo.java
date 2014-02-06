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
package org.conqat.engine.cpp.dependency;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Class for storing dependency information on each file in the
 * {@link CppDependencyBuilder}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41681 $
 * @ConQAT.Rating YELLOW Hash: 4DF0950825A471A301032E8ED077D76B
 */
/* package */class CppDependencyBuilderFileDependencyInfo {

	/** Pattern used to recognize/parse include statements. */
	private static final Pattern INCLUDE_PATTERN = Pattern.compile(
			"# *include +[\"<](.+)[\">]", Pattern.CASE_INSENSITIVE);

	/** Constant for declaration/implementation dependency. */
	private static final String IMPLEMENTATION_DEPENDENCY = "Implementation dependency";

	/** Constant for include dependencies. */
	private static final String INCLUDE_DEPENDENCY = "Include dependency";

	/** Reference to {@link CppDependencyBuilder}. */
	private final CppDependencyBuilder cppDependencyBuilder;

	/** The underlying element. */
	private ITokenElement element;

	/**
	 * The included files, mapping from the target header to the source
	 * locations.
	 */
	private final ListMap<String, TextRegionLocation> includes = new ListMap<String, TextRegionLocation>();

	/** Mapping of declared functions to declaration locations. */
	private final ListMap<String, TextRegionLocation> functionDeclarations = new ListMap<String, TextRegionLocation>();

	/** Flag used to avoid multiple processing. */
	private boolean processed = false;

	/**
	 * Transitive includes, mapping from the target header to the source
	 * locations.
	 */
	private final ListMap<String, TextRegionLocation> transitiveIncludes = new ListMap<String, TextRegionLocation>();

	/** Mapping of transitive declared functions to declaration locations. */
	private final ListMap<String, TextRegionLocation> transitiveDeclarations = new ListMap<String, TextRegionLocation>();

	/**
	 * The set of (normalized) methods called, as provided by the
	 * {@link CppDependencyBuilder#dependencyInjector}. Maps to location.
	 */
	private final ListMap<String, TextRegionLocation> methodSourceCalls = new ListMap<String, TextRegionLocation>();

	/** Constructor. */
	public CppDependencyBuilderFileDependencyInfo(
			CppDependencyBuilder cppDependencyBuilder, ITokenElement element)
			throws ConQATException {
		this.cppDependencyBuilder = cppDependencyBuilder;
		this.element = element;
		List<ShallowEntity> entities = ShallowParserFactory.parse(element,
				this.cppDependencyBuilder.getLogger());
		processInjectedDependencies(entities);
		processDirectDependencies(entities);
	}

	/**
	 * Processes direct and indirect dependencies created via the
	 * {@link CppDependencyBuilder#dependencyInjector}.
	 */
	private void processInjectedDependencies(List<ShallowEntity> entities)
			throws ConQATException {
		if (this.cppDependencyBuilder.dependencyInjector == null) {
			return;
		}

		for (ShallowEntity statement : ShallowEntityTraversalUtils
				.listEntitiesOfType(entities, EShallowEntityType.STATEMENT)) {
			// ignore statements with children (loops, etc.)
			if (!statement.getChildren().isEmpty()) {
				continue;
			}

			for (IToken token : statement.includedTokens()) {
				if (token.getType() != ETokenType.IDENTIFIER) {
					continue;
				}

				String text = token.getText();
				String sourceCall = this.cppDependencyBuilder.dependencyInjector
						.asNormalizedSourceCall(text, element);
				if (sourceCall != null) {
					methodSourceCalls.add(sourceCall, ResourceUtils
							.createTextRegionLocationForFilteredOffsets(
									element, token.getOffset(),
									token.getEndOffset()));
				}

				String targetCall = this.cppDependencyBuilder.dependencyInjector
						.asNormalizedTargetCall(text, element);
				if (targetCall != null) {
					this.cppDependencyBuilder.methodTargetCalls.add(targetCall,
							this);
				}

				PairList<String, String> directCalls = this.cppDependencyBuilder.dependencyInjector
						.getDirectDependencyTarget(text, element);
				if (directCalls != null) {
					String type = this.cppDependencyBuilder.dependencyInjector
							.getType();
					for (int i = 0; i < directCalls.size(); ++i) {
						String message = directCalls.getSecond(i);
						String uniformPath = directCalls.getFirst(i);
						this.cppDependencyBuilder
								.insertDependency(
										type,
										message,
										element,
										uniformPath,
										ResourceUtils
												.createTextRegionLocationForFilteredOffsets(
														element,
														token.getOffset(),
														token.getEndOffset()));
					}
				}
			}
		}
	}

	/**
	 * Processes direct dependencies, i.e. include and implements dependencies.
	 */
	private void processDirectDependencies(List<ShallowEntity> entities)
			throws ConQATException {
		for (ShallowEntity entity : ShallowEntityTraversalUtils
				.listAllEntities(entities)) {
			if (entity.getType() == EShallowEntityType.META) {
				Matcher matcher = INCLUDE_PATTERN.matcher(entity.getSubtype());
				if (matcher.find()) {
					TextRegionLocation location = ResourceUtils
							.createTextRegionLocationForFilteredLines(element,
									entity.getStartLine(),
									entity.getStartLine());
					includes.add(matcher.group(1), location);
				}
			} else if (entity.getType() == EShallowEntityType.METHOD) {
				insertMethod(entity);
			}
		}
	}

	/** Inserts the given method into the corresponding data structures. */
	private void insertMethod(ShallowEntity method) throws ConQATException {

		// ignore static methods, as these are local to a compilation unit
		if (isStatic(method)) {
			return;
		}

		String name = extractMethodName(method);
		if (name == null) {
			return;
		}

		if (method.getSubtype().contains("declaration")) {
			if (!isPureVirtual(method)) {
				TextRegionLocation location = ResourceUtils
						.createTextRegionLocationForFilteredLines(element,
								method.getStartLine(), method.getStartLine());
				functionDeclarations.add(name, location);
			}
			return;
		}

		if (this.cppDependencyBuilder.functionImplementations.containsKey(name)
				&& this.cppDependencyBuilder.functionImplementations.get(name) != element) {
			this.cppDependencyBuilder.logMessages.add(
					"Multiple implementations found.",
					name
							+ " (in "
							+ element.getUniformPath()
							+ " and "
							+ this.cppDependencyBuilder.functionImplementations
									.get(name) + ")");
		}

		this.cppDependencyBuilder.functionImplementations.put(name, element);
	}

	/** Returns whether the given method declaration is pure virtual. */
	private boolean isPureVirtual(ShallowEntity method) {
		List<IToken> tokens = method.includedTokens();
		int length = tokens.size();
		return length > 3 && tokens.get(length - 3).getType() == ETokenType.EQ
				&& tokens.get(length - 2).getText().equals("0");
	}

	/** Extracts the fully qualified name of a method. */
	private String extractMethodName(ShallowEntity method) {
		String name = extractFullName(method);

		ShallowEntity parent = method.getParent();
		while (parent != null
				&& (parent.getType() == EShallowEntityType.TYPE || parent
						.getType() == EShallowEntityType.MODULE)) {

			// ignore anonymous structs and namespaces
			if (StringUtils.isEmpty(parent.getName())) {
				return null;
			}
			name = extractFullName(parent) + "::" + name;
			parent = parent.getParent();
		}

		return name;
	}

	/** Extracts the full name. */
	private String extractFullName(ShallowEntity namedElement) {
		String name = namedElement.getName();

		List<IToken> tokens = namedElement.includedTokens();
		int i = 0;
		while (!tokens.get(i).getText().equals(name)) {
			i += 1;
			if (i >= tokens.size()) {
				this.cppDependencyBuilder.logMessages.add(
						"Failed to extract full name",
						name + " in " + element.getUniformPath() + " (tokens: "
								+ StringUtils.concat(tokens) + ")");
				return name;
			}
		}
		i -= 1;

		while (i > 0 && tokens.get(i).getType() == ETokenType.SCOPE) {
			name = tokens.get(i - 1).getText() + "::" + name;
			i -= 2;
		}

		return name;
	}

	/** Determines and stores the dependencies in the underlying element. */
	public void storeDependencies() {
		if (processed) {
			return;
		}
		processed = true;

		for (String include : includes.getKeys()) {
			resolveInclude(include);
		}
		// could be added due to transitivity and cycles
		transitiveIncludes.removeCollection(element.getUniformPath());
		transitiveDeclarations.removeCollection(element.getUniformPath());

		// use transitive declarations here (have been completed in
		// resolveInclude()
		for (String functionDeclaration : transitiveDeclarations.getKeys()) {
			resolveDeclaration(functionDeclaration);
		}
		for (String methodSourceCall : methodSourceCalls.getKeys()) {
			resolveInjectedDependencies(methodSourceCall);
		}

	}

	/**
	 * Resolves a single include and adds it to {@link #transitiveIncludes}.
	 */
	private void resolveInclude(String include) {
		List<TextRegionLocation> includeLocations = includes
				.getCollection(include);

		String lookup = include;
		if (this.cppDependencyBuilder.ignoreFilenameCasing) {
			lookup = lookup.toLowerCase();
		}
		ITokenElement headerElement = this.cppDependencyBuilder.headers
				.get(lookup);

		String target = include;
		if (headerElement == null) {
			if (!this.cppDependencyBuilder.preserveExternalHeaders) {
				return;
			}
		} else {
			target = headerElement.getUniformPath();
		}

		this.cppDependencyBuilder.insertDependencies(INCLUDE_DEPENDENCY,
				"Include of header " + include, element, target,
				includeLocations);
		transitiveIncludes.addAll(target, includeLocations);

		if (headerElement == null) {
			return;
		}

		// declarations are always transitive, but map to location of
		// include
		for (String functionDeclaration : functionDeclarations.getKeys()) {
			transitiveDeclarations
					.addAll(functionDeclaration, includeLocations);
		}

		if (this.cppDependencyBuilder.useTransitiveIncludes) {
			CppDependencyBuilderFileDependencyInfo headerInfo = this.cppDependencyBuilder.fileInfos
					.get(headerElement);
			headerInfo.storeDependencies();
			for (String transitiveTarget : headerInfo.transitiveIncludes
					.getKeys()) {
				transitiveIncludes.addAll(transitiveTarget, includeLocations);
			}
		}
	}

	/** Resolves a function declaration. */
	private void resolveDeclaration(String functionDeclaration) {
		ITokenElement implementer = this.cppDependencyBuilder.functionImplementations
				.get(functionDeclaration);
		if (implementer == null) {
			this.cppDependencyBuilder.logMessages
					.add("No implementation found for declared function. Dependecy will be missing!",
							functionDeclaration);
		} else {
			this.cppDependencyBuilder.insertDependencies(
					IMPLEMENTATION_DEPENDENCY, "Implementation of declaration "
							+ functionDeclaration, element,
					implementer.getUniformPath(),
					transitiveDeclarations.getCollection(functionDeclaration));
		}
	}

	/**
	 * Resolves dependencies injected via
	 * {@link CppDependencyBuilder#dependencyInjector}.
	 */
	private void resolveInjectedDependencies(String methodSourceCall) {
		Set<CppDependencyBuilderFileDependencyInfo> targets = this.cppDependencyBuilder.methodTargetCalls
				.getCollection(methodSourceCall);
		if (targets == null) {
			this.cppDependencyBuilder.logMessages
					.add("No matching target call found for normalized source call.",
							methodSourceCall + " in "
									+ element.getUniformPath());
			return;
		}

		for (CppDependencyBuilderFileDependencyInfo target : targets) {
			this.cppDependencyBuilder.insertDependencies(
					this.cppDependencyBuilder.dependencyInjector.getType(),
					this.cppDependencyBuilder.dependencyInjector.getType()
							+ " (" + methodSourceCall + ")", element,
					target.element.getUniformPath(),
					methodSourceCalls.getCollection(methodSourceCall));
		}
	}

	/** Returns whether the given method is static. */
	private static boolean isStatic(ShallowEntity method) {
		for (IToken token : method.includedTokens()) {
			if (token.getType() == ETokenType.STATIC) {
				return true;
			}
			// reached end of modifiers
			if (token.getType() == ETokenType.IDENTIFIER) {
				return false;
			}
		}
		return false;
	}
}