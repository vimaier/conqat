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
package org.conqat.engine.sourcecode.shallowparser.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.IToken;

/**
 * An entity resulting from shallow parsing. For classes outside of this
 * package, this class is immutable.
 * 
 * @author $Author: streitel $
 * @version $Rev: 46972 $
 * @ConQAT.Rating GREEN Hash: 93344ADF4B8B5267D52CBCCBA1D9582B
 */
public class ShallowEntity {

	/** The type of this entity. */
	private final EShallowEntityType type;

	/** The subtype of this entity. */
	private final String subtype;

	/** The name of the entity (may be null). */
	private final String name;

	/** The parent entity. */
	private ShallowEntity parent;

	/** The list of children. */
	private final List<ShallowEntity> children = new ArrayList<ShallowEntity>();

	/** Whether this node has been completed. */
	private boolean completed = false;

	/**
	 * The underlying list of tokens (as seen by the shallow parser, i.e.
	 * without comments)
	 */
	private final List<IToken> tokens;

	/**
	 * The index of the first token in the token list as seen by the shallow
	 * parser
	 */
	private final int startTokenIndex;

	/**
	 * The index of the end token (non-inclusive) in the token list as seen by
	 * the shallow parser
	 */
	private int endTokenIndex = -1;

	/**
	 * Marks this node as continued. See {@link #isContinued()} for an
	 * explanation.
	 */
	private boolean continuedNode = false;

	/** Constructor. */
	public ShallowEntity(EShallowEntityType type, String subtype, String name,
			List<IToken> tokens, int startTokenIndex) {
		this.type = type;
		this.subtype = subtype;
		this.name = name;
		this.tokens = tokens;
		this.startTokenIndex = startTokenIndex;
	}

	/**
	 * Returns the number of entity nodes including this entity, its children,
	 * grand children, etc.
	 */
	public int getEntityCount() {
		int result = 1;
		for (ShallowEntity child : children) {
			result += child.getEntityCount();
		}
		return result;
	}

	/**
	 * Returns the number of entity nodes that are complete, including this
	 * entity, its children, grand children, etc.
	 */
	public int getCompleteEntityCount() {
		int result = 0;
		if (isCompleted()) {
			result = 1;
		}
		for (ShallowEntity child : children) {
			result += child.getCompleteEntityCount();
		}
		return result;
	}

	/** Returns the type. */
	public EShallowEntityType getType() {
		return type;
	}

	/** Returns the subtype. */
	public String getSubtype() {
		return subtype;
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Returns the children. Returns an empty list, if there are no children. */
	public UnmodifiableList<ShallowEntity> getChildren() {
		return CollectionUtils.asUnmodifiable(children);
	}

	/** Adds a child entity. */
	/* package */void addChild(ShallowEntity child) {
		CCSMPre.isTrue(child.parent == null,
				"May not add entity to multiple parents!");
		children.add(child);
		child.parent = this;
	}

	/** Returns parent entity or null for the root. */
	public ShallowEntity getParent() {
		return parent;
	}

	/** Returns a new list containing all children of the given type in order. */
	public List<ShallowEntity> getChildrenOfType(EShallowEntityType type) {
		List<ShallowEntity> result = new ArrayList<ShallowEntity>();
		for (ShallowEntity child : children) {
			if (child.getType() == type) {
				result.add(child);
			}
		}
		return result;
	}

	/** Returns whether this node has been completed. */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * Marks this node as completed and stores whether we expect the node to be
	 * continued. An example for a continued node is an "if" followed by an
	 * "else". After parsing the "if" and its body, the entity for the "if" is
	 * complete, but we expect the if statement to logically continue with the
	 * else.
	 */
	/* package */void setComplete(boolean continuedNode) {
		completed = true;
		this.continuedNode = continuedNode;
	}

	/**
	 * Returns whether this node is continued, which means that the next sibling
	 * node logically is associated to this one. This feature is used, e.g., to
	 * connect the if-block and the corresponding else-block, which are parsed
	 * into separate nodes, but the first (if) node will be marked as continued.
	 * Note that even if this is true, a next sibling might not exist (typically
	 * the result of parsing errors).
	 */
	public boolean isContinued() {
		return continuedNode;
	}

	/** Sets the last (non-inclusive) token index. */
	/* package */void setEndTokenIndex(int endTokenIndex) {
		this.endTokenIndex = endTokenIndex;
	}

	/**
	 * Returns the start token index. This is the index in a token list without
	 * comments.
	 */
	public int getStartTokenIndex() {
		return startTokenIndex;
	}

	/**
	 * Returns the start token index relative to the parent (i.e. this is valid
	 * for the tokens from {@link #includedTokens()} called for the parent).
	 */
	public int getRelativeStartTokenIndex() {
		if (parent == null) {
			return startTokenIndex;
		}
		return startTokenIndex - parent.startTokenIndex;
	}

	/**
	 * Returns the end token index relative to the parent (i.e. this is valid
	 * for the tokens from {@link #includedTokens()} called for the parent).
	 */
	public int getRelativeEndTokenIndex() {
		if (parent == null) {
			return endTokenIndex;
		}
		return endTokenIndex - parent.startTokenIndex;
	}

	/** Returns the 1-based start line number. */
	public int getStartLine() {
		return getStartToken().getLineNumber() + 1;
	}

	/** Return start token */
	private IToken getStartToken() {
		return tokens.get(startTokenIndex);
	}

	/** Returns the (inclusive) offset of the start token */
	public int getStartOffset() {
		return getStartToken().getOffset();
	}

	/**
	 * Return (inclusive) offset of end token. This might be not the very last
	 * token, if parsing errors occurred.
	 */
	public int getEndOffset() {
		IToken endToken = getEndToken();
		if (endToken == null) {
			return getStartToken().getEndOffset();
		}
		return endToken.getEndOffset();
	}

	/**
	 * Returns the (exclusive) end token index. This is the index in a list of
	 * tokens without comments.
	 */
	public int getEndTokenIndex() {
		return endTokenIndex;
	}

	/**
	 * Returns the 1-based inclusive end line number. This might be not the very
	 * last line, if parsing errors occurred.
	 */
	public int getEndLine() {
		// we have to calculate +1 to convert to 1-basd lines
		IToken endToken = getEndToken();
		if (endToken == null) {
			return getStartToken().getLineNumber() + 1;
		}
		return endToken.getLineNumber() + 1;
	}

	/** Return end token (or null if invalid). */
	private IToken getEndToken() {
		if (endTokenIndex < 0) {
			return null;
		}
		return tokens.get(endTokenIndex - 1);
	}

	/** Returns a view of the included tokens without comments */
	public UnmodifiableList<IToken> includedTokens() {
		return readOnlyTokenView(startTokenIndex, endTokenIndex);
	}

	/**
	 * Returns a read-only view of the tokens from the (inclusive) start index
	 * to the (exclusive) end index.
	 */
	private UnmodifiableList<IToken> readOnlyTokenView(int startIndex,
			int endIndex) {
		if (endIndex <= startIndex) {
			return CollectionUtils.emptyList();
		}
		return CollectionUtils.asUnmodifiable(tokens.subList(startIndex,
				endIndex));
	}

	/**
	 * Returns a view of the tokens (without comments) from the beginning of the
	 * entity up to the first token included in the first child. For example for
	 * an if-block statement, this would include everything from the "if" to the
	 * first brace (inclusive). The first token of the first child statement
	 * would not be included.
	 */
	public UnmodifiableList<IToken> ownStartTokens() {
		if (children.isEmpty()) {
			return includedTokens();
		}
		return readOnlyTokenView(startTokenIndex,
				children.get(0).startTokenIndex);
	}

	/**
	 * Returns a view of the tokens (without comments) from the last token
	 * included in the last child up to the end of the entity. For example for a
	 * do-while statement, this would include everything from the last brace to
	 * the end of the "while". The last token of the last child statement would
	 * not be included.
	 */
	public UnmodifiableList<IToken> ownEndTokens() {
		if (children.isEmpty()) {
			return includedTokens();
		}
		return readOnlyTokenView(
				CollectionUtils.getLast(children).endTokenIndex + 1,
				endTokenIndex);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Before changing the output, note that we use this method also for
	 * regression testing.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, 0);
		return sb.toString();
	}

	/**
	 * Returns a string representation of this node without including its
	 * children.
	 */
	public String toLocalString() {
		return toLocalString(getStartLine(), getEndLine());
	}

	/**
	 * Returns a string representation of this node without including its
	 * children using the given line representation.
	 */
	private String toLocalString(int startLine, int endLine) {
		String incomplete = StringUtils.EMPTY_STRING;
		if (!isCompleted()) {
			incomplete = " [incomplete]";
		}
		return type + ": " + subtype + ": " + name + " (lines " + startLine
				+ "-" + endLine + ")" + incomplete;
	}

	/**
	 * Returns a string representation of this node without including its
	 * children but using unfiltered lines.
	 */
	public String toLocalStringUnfiltered(ITextElement element)
			throws ConQATException {
		return toLocalString(
				TextElementUtils.convertFilteredOffsetToUnfilteredLine(element,
						getStartToken().getOffset()),
				TextElementUtils.convertFilteredOffsetToUnfilteredLine(element,
						getEndToken().getEndOffset()));
	}

	/** Returns an indented string representation. */
	private void toString(StringBuilder sb, int indent) {
		sb.append(StringUtils.fillString(2 * indent, ' '))
				.append(toLocalString()).append(StringUtils.CR);
		for (ShallowEntity child : children) {
			child.toString(sb, indent + 1);
		}
	}

	/**
	 * Traverses this entity depth-first. For details of visiting the entities,
	 * see {@link IShallowEntityVisitor}.
	 */
	public void traverse(IShallowEntityVisitor visitor) {
		if (visitor.visit(this)) {
			traverse(children, visitor);
		}
		visitor.endVisit(this);
	}

	/** Utility method for traversing multiple entities. */
	public static void traverse(Collection<ShallowEntity> entities,
			IShallowEntityVisitor visitor) {
		for (ShallowEntity entity : entities) {
			entity.traverse(visitor);
		}
	}
}
