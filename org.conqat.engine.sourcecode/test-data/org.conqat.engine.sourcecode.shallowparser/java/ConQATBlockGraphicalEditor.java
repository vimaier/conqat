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
package org.conqat.ide.editor.editor;

import org.conqat.ide.commons.gef.editor.XmlModelEditorOutlinePage;
import org.conqat.ide.commons.gef.editor.XmlModelGefEditor;
import org.conqat.ide.core.model.ConQATModel;
import org.conqat.ide.core.model.ConQATModelManager;
import org.conqat.ide.core.model.IConQATModelSubscriber;
import org.conqat.ide.core.utils.MarkerUtils;
import org.conqat.ide.editor.content.CQLabelProvider;
import org.conqat.ide.editor.content.CqTreeContentProvider;
import org.conqat.ide.editor.editor.action.CopyAction;
import org.conqat.ide.editor.editor.action.CutAction;
import org.conqat.ide.editor.editor.action.HorizontalAlignAction;
import org.conqat.ide.editor.editor.action.InvertConditionAction;
import org.conqat.ide.editor.editor.action.OpenSpecificationAction;
import org.conqat.ide.editor.editor.action.ParameterRenameRefactoringAction;
import org.conqat.ide.editor.editor.action.PasteAction;
import org.conqat.ide.editor.editor.action.PowerDeleteAction;
import org.conqat.ide.editor.editor.action.SourceEdgeVisibilityAction;
import org.conqat.ide.editor.editor.action.VerticalAlignAction;
import org.conqat.ide.editor.editor.action.search.reference.ReferenceProjectSearchAction;
import org.conqat.ide.editor.editor.action.search.reference.ReferenceWorkspaceSearchAction;
import org.conqat.ide.editor.editor.editparts.ConQATEditPartFactory;
import org.conqat.ide.editor.editor.exposeinterface.ExposeInterfaceWizardAction;
import org.conqat.ide.editor.editor.layout.LayoutAction;
import org.conqat.ide.editor.model.CQXmlBlockSpecModel;
import org.conqat.ide.editor.refactoring.extractblock.ExtractBlockAction;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.w3c.dom.Document;

/**
 * Editor for CQB files.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: AC680401D141B1297D50933CD25A8D8B
 */
public class ConQATBlockGraphicalEditor extends
		XmlModelGefEditor<CQXmlBlockSpecModel> implements
		ITabbedPropertySheetPageContributor {

	/**
	 * The name of the property in the {@link GraphicalViewer}, under which the
	 * editor is registered.
	 */
	public static final String EDITOR_VIEWER_PROPRETY = "ConQATBlockEditor";

	/** Listener used to react to changes in the ConQAT specification model */
	private final IConQATModelSubscriber modelRefreshListener = new IConQATModelSubscriber() {
		@Override
		public void modelUpdate(ConQATModel model) {
			getModel().invalidateAssociationCache();
			hadModelChange();
		}
	};

	/** {@inheritDoc} */
	@Override
	protected CQXmlBlockSpecModel createRootModel(Document document) {
		return new CQXmlBlockSpecModel(document);
	}

	/** {@inheritDoc} */
	@Override
	protected void createActions() {
		super.createActions();

		selectionActions.add(registerAction(new OpenSpecificationAction(this)));
		selectionActions.add(registerAction(new ReferenceProjectSearchAction(
				this, ResourceUtil.getFile(getEditorInput()))));
		selectionActions.add(registerAction(new ReferenceWorkspaceSearchAction(
				this)));

		selectionActions.add(registerAction(new CopyAction(this)));
		selectionActions.add(registerAction(new CutAction(this)));
		selectionActions.add(registerAction(new PowerDeleteAction(this)));

		selectionActions.add(registerAction(new InvertConditionAction(this)));
		selectionActions
				.add(registerAction(new SourceEdgeVisibilityAction(this)));

		selectionActions.add(registerAction(new ExtractBlockAction(this)));
		selectionActions.add(registerAction(new LayoutAction(this)));
		selectionActions.add(registerAction(new VerticalAlignAction(this)));
		selectionActions.add(registerAction(new HorizontalAlignAction(this)));
		selectionActions.add(registerAction(new ExposeInterfaceWizardAction(
				this)));
		selectionActions
				.add(registerAction(new ParameterRenameRefactoringAction(this)));
		registerAction(new PasteAction(this));
	}

	/** {@inheritDoc} */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		if (errorHandler.hasError()) {
			return;
		}

		getGraphicalViewer().setEditPartFactory(new ConQATEditPartFactory());
		getGraphicalViewer().setContents(rootModel);

		// initialize editor as dnd target
		getGraphicalViewer().addDropTargetListener(
				new ConQATDropTargetListener(getGraphicalViewer(), rootModel));

		// register the editor with the viewer's property mechanism
		getGraphicalViewer().setProperty(EDITOR_VIEWER_PROPRETY, this);

		ConQATModelManager.getInstance().addModelSubscriber(
				modelRefreshListener);
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		ConQATModelManager.getInstance().removeModelSubscriber(
				modelRefreshListener);
		super.dispose();
	}

	/** {@inheritDoc} */
	@Override
	protected MenuManager createContextMenu(GraphicalViewer viewer) {
		return new GEFEditorContextMenu(viewer, getActionRegistry());
	}

	/** {@inheritDoc} */
	@Override
	protected String getMarkerType() {
		return MarkerUtils.CONQAT_MARKER;
	}

	/** {@inheritDoc} */
	@Override
	protected ContentOutlinePage createOutlinePage() {
		return new XmlModelEditorOutlinePage(
				(ScalableFreeformRootEditPart) getGraphicalViewer()
						.getRootEditPart(), rootModel) {
			@Override
			protected IContentProvider createTreeContentProvider() {
				return new CqTreeContentProvider();
			}

			@Override
			protected ILabelProvider createTreeLabelProvider() {
				return new CQLabelProvider();
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	protected void performPostSaveActions() {
		super.performPostSaveActions();
		ConQATModelManager.getInstance().compileBlockAndSetMarkers(
				getEditorFile());
	}

	/** {@inheritDoc} */
	@Override
	public String getContributorId() {
		return "org.conqat.ide.editor.property.contributor";
	}

}