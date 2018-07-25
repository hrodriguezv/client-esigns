/**
 * 
 */
package org.icepdf.ri.common;

import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.icepdf.ri.images.Images;
import org.icepdf.ri.util.PropertiesManager;

/**
 * The Class SwingViewExtendedBuilder.
 *
 * @author hrodriguez
 */
public class SwingViewExtendedBuilder extends SwingViewBuilder {

	/**
	 * Instantiates a new swing view extended builder.
	 *
	 * @param c the c
	 * @param documentViewType the document view type
	 * @param documentPageFitMode the document page fit mode
	 * @param rotation the rotation
	 */
	public SwingViewExtendedBuilder(SwingController c, int documentViewType, int documentPageFitMode, float rotation) {
		// Use all the defaults
		super(c, null, null, false, SwingViewBuilder.TOOL_BAR_STYLE_FIXED, null, documentViewType, documentPageFitMode,
				rotation);
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildViewerFrame()
	 */
	public JFrame buildViewerFrame() {
		JFrame viewer = new JFrame();
		viewer.setIconImage(new ImageIcon(Images.get("icepdf-app-icon-64x64.png")).getImage());
		viewer.setTitle(messageBundle.getString("viewer.window.title.default"));
		viewer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		Container contentPane = viewer.getContentPane();
		buildContents(contentPane, false);
		if (viewerController != null)
			viewerController.setViewerFrame(viewer);
		return viewer;
	}

	/**
	 * Builds the swap button.
	 *
	 * @return the j button
	 */
	public JButton buildSwapButton() {
		JButton btn = makeToolbarButton("Mover Pantalla", "Mover Pantalla", "swapd", iconSize,
				buttonFont);
		if (viewerController != null && btn != null)
			((SwingExtendedController) viewerController).setSwapButton(btn);
		return btn;
	}
	
	/**
	 * Builds the ok button.
	 *
	 * @return the j button
	 */
	public JButton buildOkButton() {
		JButton btn = makeToolbarButton("Confirmar Cambios", "Confirmar Cambios", "ok", iconSize,
				buttonFont);
		if (viewerController != null && btn != null)
			((SwingExtendedController) viewerController).setOkButton(btn);
		return btn;
	}
	
	/**
	 * Builds the reset button.
	 *
	 * @return the j button
	 */
	public JButton buildResetButton() {
		JButton btn = makeToolbarButton("Limpiar Cambios", "Limpiar Cambios", "reset", iconSize,
				buttonFont);
		if (viewerController != null && btn != null)
			((SwingExtendedController) viewerController).setResetButton(btn);
		return btn;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildUtilityToolBar(boolean, org.icepdf.ri.util.PropertiesManager)
	 */
	public JToolBar buildUtilityToolBar(boolean embeddableComponent, PropertiesManager propertiesManager) {
		JToolBar toolbar = new JToolBar();
		commonToolBarSetup(toolbar, false);
		// if embeddable component, we don't want to create the open dialog, as we
		// have no window manager for this case.
		if ((!embeddableComponent)
				&& (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_SWAP)))
			addToToolBar(toolbar, buildSwapButton());
		if ((!embeddableComponent)
				&& (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_RESET)))
			addToToolBar(toolbar, buildResetButton());
		if ((!embeddableComponent)
				&& (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_OK)))
			addToToolBar(toolbar, buildOkButton());
		return toolbar;
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildCompleteToolBar(boolean)
	 */
	public JToolBar buildCompleteToolBar(boolean embeddableComponent) {
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new ToolbarLayout(ToolbarLayout.LEFT, 0, 0));
		commonToolBarSetup(toolbar, true);

		// Attempt to get the properties manager so we can configure which toolbars are
		// visible
		doubleCheckPropertiesManager();

		// Build the main set of toolbars based on the property file configuration
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY))
			addToToolBar(toolbar, buildUtilityToolBar(embeddableComponent, propertiesManager));
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV))
			addToToolBar(toolbar, buildPageNavigationToolBar());
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ZOOM))
			addToToolBar(toolbar, buildZoomToolBar());
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT))
			addToToolBar(toolbar, buildFitToolBar());
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE))
			addToToolBar(toolbar, buildRotateToolBar());
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL))
			addToToolBar(toolbar, buildToolToolBar());
		if (propertiesManager.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_SEARCH))
			addToToolBar(toolbar, buildSearchToolBar());

		// Set the toolbar back to null if no components were added
		// The result of this will properly disable the necessary menu items for
		// controlling the toolbar
		if (toolbar.getComponentCount() == 0) {
			toolbar = null;
		}

		if ((viewerController != null) && (toolbar != null))
			viewerController.setCompleteToolBar(toolbar);
		return toolbar;
	}
	
    /* (non-Javadoc)
     * @see org.icepdf.ri.common.SwingViewBuilder#buildPageNavigationToolBar()
     */
    public JToolBar buildPageNavigationToolBar() {
        return null;
    }

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildToolToolBar()
	 */
	public JToolBar buildToolToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildZoomToolBar()
	 */
	public JToolBar buildZoomToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildFullScreenToolBar()
	 */
	public JToolBar buildFullScreenToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildFitToolBar()
	 */
	public JToolBar buildFitToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildRotateToolBar()
	 */
	public JToolBar buildRotateToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildAnnotationlToolBar()
	 */
	public JToolBar buildAnnotationlToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildFormsToolBar()
	 */
	public JToolBar buildFormsToolBar() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildSearchToolBar()
	 */
	public JToolBar buildSearchToolBar() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingViewBuilder#buildStatusPanel()
	 */
	public JPanel buildStatusPanel() {
		return null;
	}
}
