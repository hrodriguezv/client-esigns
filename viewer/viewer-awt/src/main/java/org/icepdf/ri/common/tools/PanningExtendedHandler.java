/**
 * 
 */

package org.icepdf.ri.common.tools;

import java.awt.Adjustable;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;

import org.icepdf.ri.common.CurrentPageChanger;
import org.icepdf.ri.common.SwingExtendedController;
import org.icepdf.ri.common.views.AbstractDocumentView;
import org.icepdf.ri.common.views.AbstractPageViewComponent;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerExtendedImpl;
import org.icepdf.ri.common.views.DocumentViewModel;
import org.icepdf.ri.common.views.DocumentViewModelImpl;

import com.consultec.esigns.core.util.InetUtility;
import com.consultec.esigns.core.util.PropertiesManager;
import com.consultec.esigns.strokes.api.IStrokeSignature;

/**
 * The Class PanningExtendedHandler.
 *
 * @author hrodriguez
 */
public class PanningExtendedHandler extends CommonToolHandler
	implements ToolHandler {

	/** The Constant SIGNATURE_REASON. */
	final static String SIGNATURE_REASON =
		PropertiesManager.getInstance().getValue(
			PropertiesManager.PROPERTY_USER_STROKE_REASON);

	/** The sign provider. */
	IStrokeSignature signProvider;
	
	/** The experience. */
	SignStrokesUserExperience experience;

	/** The last mouse position. */
	// page mouse event manipulation
	protected Point lastMousePosition = new Point();

	/** The document view model. */
	protected DocumentViewModel documentViewModel;

	/** The current page changer. */
	private CurrentPageChanger currentPageChanger;

	/**
	 * Instantiates a new panning extended handler.
	 *
	 * @param documentViewController the document view controller
	 * @param pageViewComponent the page view component
	 */
	public PanningExtendedHandler(
		DocumentViewController documentViewController,
		AbstractPageViewComponent pageViewComponent) {

		super(documentViewController, pageViewComponent);
		this.documentViewModel =
			((DocumentViewControllerExtendedImpl) documentViewController).getDocumentViewModel();
		this.signProvider =
			((DocumentViewControllerExtendedImpl) documentViewController).getSignatureVendor();
		this.currentPageChanger = new CurrentPageChanger(
			documentViewModel.getDocumentViewScrollPane(),
			(AbstractDocumentView) documentViewController.getDocumentView(),
			documentViewModel.getPageComponents());
		String user = null;
		try {
			user = InetUtility.getLoggedUserNameExt();			
		}catch(Exception e) {
			e.printStackTrace();
		}
        MessageFormat formatter = new MessageFormat(SIGNATURE_REASON);
		String reason = formatter.format(new Object[]{user!=null?user:""});
		this.signProvider.setParameters(PropertiesManager.getInstance().getValue(
			PropertiesManager.PROPERTY_USER_STROKE_LOCATION), reason, null);
	}

	/** KeyEvents can queue up, if the user holds down a key, causing us to do several page changes, unless we use flagging to ignore the extraneous KeyEvents. */
	private boolean calculatingCurrentPage;

	/**
	 * Mouse dragged, initiates page panning if the tool is selected.
	 *
	 * @param e
	 *            awt mouse event
	 */
	public void mouseDragged(MouseEvent e) {

		if (documentViewController != null) {

			if (calculatingCurrentPage)
				return;

			calculatingCurrentPage = true;
			currentPageChanger.calculateCurrentPage();
			calculatingCurrentPage = false;
			
			// Get data about the current view port position
			Adjustable verticalScrollbar =
				documentViewModel.getDocumentViewScrollPane().getVerticalScrollBar();
			Adjustable horizontalScrollbar =
				documentViewModel.getDocumentViewScrollPane().getHorizontalScrollBar();

			if (verticalScrollbar != null && horizontalScrollbar != null) {
				// calculate how much the view port should be moved
				Point p = new Point(
					(int) e.getPoint().getX() - horizontalScrollbar.getValue(),
					(int) e.getPoint().getY() - verticalScrollbar.getValue());
				int x = (int) (horizontalScrollbar.getValue() -
					(p.getX() - lastMousePosition.getX()));
				int y = (int) (verticalScrollbar.getValue() -
					(p.getY() - lastMousePosition.getY()));

				// apply the pan
				horizontalScrollbar.setValue(x);
				verticalScrollbar.setValue(y);

				// update last position holder
				lastMousePosition.setLocation(p);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {

		if (documentViewController != null) {

			Adjustable verticalScrollbar =
				documentViewModel.getDocumentViewScrollPane().getVerticalScrollBar();
			Adjustable horizontalScrollbar =
				documentViewModel.getDocumentViewScrollPane().getHorizontalScrollBar();

			lastMousePosition.setLocation(
				e.getPoint().getX() - horizontalScrollbar.getValue(),
				e.getPoint().getY() - verticalScrollbar.getValue());
		}
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 *
	 * @param e the e
	 */
	public void mousePressed(MouseEvent e) {

		if (documentViewController != null &&
			documentViewController.getDocumentViewModel().isViewToolModeSelected(
				DocumentViewModel.DISPLAY_TOOL_PAN_EXTENDED)) {
			documentViewController.setViewCursor(
				DocumentViewController.CURSOR_HAND_CLOSE);
		}
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 *
	 * @param e the e
	 */
	public void mouseReleased(MouseEvent e) {

		if (documentViewController != null &&
			documentViewController.getDocumentViewModel().getViewToolMode() == DocumentViewModel.DISPLAY_TOOL_PAN_EXTENDED) {
			documentViewController.setViewCursor(
				DocumentViewController.CURSOR_HAND_OPEN);
		}
	}

	/**
	 * Invoked when the mouse enters a component.
	 *
	 * @param e the e
	 */
	public void mouseEntered(MouseEvent e) {

	}

	/**
	 * Invoked when the mouse exits a component.
	 *
	 * @param e the e
	 */
	public void mouseExited(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.tools.ToolHandler#paintTool(java.awt.Graphics)
	 */
	public void paintTool(Graphics g) {

		// nothing to paint for panning.
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.tools.ToolHandler#installTool()
	 */
	public void installTool() {

	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.tools.ToolHandler#uninstallTool()
	 */
	public void uninstallTool() {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {

		try {

			if (signProvider == null)
				return;

			int signal = signProvider.sign();

			int currentPage = this.documentViewModel.getViewCurrentPageIndex();
			AbstractPageViewComponent localComponent =
				documentViewModel.getPageComponents().get(currentPage);
			this.experience = new SignStrokesUserExperience(
				documentViewController, localComponent, signProvider);

			if (signal == 0) {

				Rectangle tBbox =
					experience.getInverseCoordinates(e.getX(), e.getY());

				File finalImgName = experience.createStrokeFiles();

				experience.addStrokesToPDF(
					finalImgName.getAbsolutePath(), tBbox);

				experience.displayChanges(tBbox);
			}
			else {
				((SwingExtendedController) documentViewController.getParentController()).setDisplayTool(
					DocumentViewModelImpl.DISPLAY_TOOL_PAN_EXTENDED);
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.tools.CommonToolHandler#checkAndApplyPreferences()
	 */
	@Override
	protected void checkAndApplyPreferences() {

	}

}
