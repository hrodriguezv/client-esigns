/**
 * 
 */

package org.icepdf.ri.viewer;

import static org.icepdf.ri.util.PropertiesManager.PROPERTY_DEFAULT_VIEW_TYPE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingExtendedController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.SwingViewExtendedBuilder;
import org.icepdf.ri.common.views.Controller;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.util.PropertiesManager;

import com.consultec.esigns.core.io.FileSystemManager;

/**
 * The Class WindowExtendedManager.
 *
 * @author hrodriguez
 */
public class WindowExtendedManager extends WindowManager {

	protected static final Logger logger =
		Logger.getLogger(WindowExtendedManager.class.toString());

	/** The window manager. */
	private static WindowExtendedManager windowManager;

	/**
	 * Creates the instance.
	 *
	 * @param properties
	 *            the properties
	 * @param messageBundle
	 *            the message bundle
	 * @return the window manager
	 */
	public static WindowManager createInstance(
		PropertiesManager properties, ResourceBundle messageBundle) {

		return createInstance(properties, messageBundle, null);
	}

	/**
	 * Creates the instance.
	 *
	 * @param properties
	 *            the properties
	 * @param messageBundle
	 *            the message bundle
	 * @param id
	 *            the id
	 * @return the window manager
	 */
	public static WindowManager createInstance(
		PropertiesManager properties, ResourceBundle messageBundle, String id) {

		windowManager = new WindowExtendedManager();
		windowManager.properties = properties;
		windowManager.controllers = new ArrayList<>();
		try {
			FileSystemManager.getInstance().init(id);
		}
		catch (FileNotFoundException e) {
			logger.log(
				Level.FINE, "Error checking file system: " + e.getMessage(), e);
			org.icepdf.ri.util.Resources.showMessageDialog(
				null, JOptionPane.INFORMATION_MESSAGE, messageBundle,
				"viewer.dialog.error.exception.title",
				"viewer.dialog.error.exception.msg",
				"[Error de inconsistencia en el sistema de archivos. Por favor, contacte a su admininstrador]");
			e.printStackTrace();
			System.exit(1);
		}

		if (messageBundle != null) {
			windowManager.messageBundle = messageBundle;
		}
		else {
			windowManager.messageBundle = ResourceBundle.getBundle(
				PropertiesManager.DEFAULT_MESSAGE_BUNDLE);
		}
		return windowManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.icepdf.ri.viewer.WindowManager#commonWindowCreation()
	 */
	protected Controller commonWindowCreation() {

		Controller controller = new SwingExtendedController(messageBundle);
		controller.setWindowManagementCallback(this);

		// add interactive mouse link annotation support
		controller.getDocumentViewController().setAnnotationCallback(
			new MyAnnotationCallback(controller.getDocumentViewController()));

		controllers.add(controller);
		// guild a new swing viewer with remembered view settings.
		int viewType = DocumentViewControllerImpl.ONE_PAGE_VIEW;
		int pageFit = DocumentViewController.PAGE_FIT_WINDOW_WIDTH;
		float pageRotation = 0;
		Preferences viewerPreferences = getProperties().getPreferences();
		try {
			viewType = viewerPreferences.getInt(
				PROPERTY_DEFAULT_VIEW_TYPE,
				DocumentViewControllerImpl.ONE_PAGE_VIEW);
			pageFit = viewerPreferences.getInt(
				PropertiesManager.PROPERTY_DEFAULT_PAGEFIT,
				DocumentViewController.PAGE_FIT_WINDOW_WIDTH);
			pageRotation = viewerPreferences.getFloat(
				PropertiesManager.PROPERTY_DEFAULT_ROTATION, pageRotation);
		}
		catch (NumberFormatException e) {
			// eating error, as we can continue with out alarm
		}

		SwingViewBuilder factory = new SwingViewExtendedBuilder(
			(SwingController) controller, viewType, pageFit, pageRotation);

		JFrame frame = factory.buildViewerFrame();
		if (frame != null) {
			newWindowLocation(frame);
			frame.setVisible(true);
		}

		return controller;
	}

	@Override
	public void disposeWindow(
		Controller controller, JFrame viewer, Preferences preferences) {
		Boolean doIt = ((SwingExtendedController)controller).isDeleteOnExit();
		try {
			FileSystemManager.getInstance().deleteOnExit(doIt);
		}
		catch (IOException e) {
			logger.severe("Error deleting files in configured workspace");
		}
		super.disposeWindow(controller, viewer, preferences);
	}

}
