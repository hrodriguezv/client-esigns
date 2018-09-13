/**
 * 
 */

package org.icepdf.ri.common;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.icepdf.ri.common.utility.queue.ListenerMessageSender;
import org.icepdf.ri.common.utility.queue.ListenerQueueConfig;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerExtendedImpl;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.common.views.DocumentViewModelImpl;

import com.consultec.esigns.core.io.FileSystemManager;
import com.consultec.esigns.core.model.PayloadTO;
import com.consultec.esigns.core.model.PayloadTO.Stage;
import com.consultec.esigns.core.util.MQUtility;
import com.consultec.esigns.core.util.PropertiesManager;
import com.consultec.esigns.core.util.WMICUtil;
import com.consultec.esigns.strokes.SignaturePadVendor;
import com.consultec.esigns.strokes.api.IStrokeSignature;
import com.consultec.esigns.strokes.io.FeatureNotImplemented;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SwingExtendedController.
 *
 * @author hrodriguez
 */
public class SwingExtendedController extends SwingController {

	/** The vendor. */
	private IStrokeSignature vendor;

	/** The swap button. */
	private JButton switchButton;

	/** The swap button. */
	private JButton okButton;

	/** The reset button. */
	private JButton resetButton;

	/** The current screen. */
	private int currentScreen;

	/**
	 * The Enum Screen.
	 */
	public enum Screen {

			/** The main. */
			MAIN,
			/** The extended. */
			EXTENDED;

		/**
		 * Gets the screen.
		 *
		 * @param k
		 *            the k
		 * @return the screen
		 */
		public static Screen getScreen(int k) {

			if (k == 0)
				return MAIN;
			else
				return EXTENDED;
		}

		/**
		 * Gets the alternate screen.
		 *
		 * @param k
		 *            the k
		 * @return the alternate screen
		 */
		public static Screen getAlternateScreen(int k) {

			if (k == 0)
				return EXTENDED;
			else
				return MAIN;
		}
	}

	/**
	 * Instantiates a new swing extended controller.
	 *
	 * @param currentMessageBundle
	 *            the current message bundle
	 */
	public SwingExtendedController(ResourceBundle currentMessageBundle) {

		super(currentMessageBundle);
		setDefaultStrokeProvider();
	}

	/**
	 * Show on screen.
	 *
	 * @param screenEnum
	 *            the screen enum
	 * @param frame
	 *            the frame
	 */
	public void showOnScreen(Screen screenEnum, Frame frame) {

		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		int screen = screenEnum.ordinal();
		if (screen > -1 && screen < gd.length) {
			frame.setLocation(
				gd[screen].getDefaultConfiguration().getBounds().x,
				gd[screen].getDefaultConfiguration().getBounds().y +
					frame.getY());
		}
		else if (gd.length > 0) {
			frame.setLocation(
				gd[0].getDefaultConfiguration().getBounds().x,
				gd[0].getDefaultConfiguration().getBounds().y + frame.getY());
		}
		else {
			throw new RuntimeException("No Screens Found");
		}
		this.currentScreen = screen;
		applySettingsOnButtons(screenEnum);
		frame.setExtendedState(
			frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Sets the default stroke vendor.
	 */
	private void setDefaultStrokeProvider() {

		boolean forceLoadingProvider = Boolean.parseBoolean(
			PropertiesManager.getInstance().getValue(
				"forceload.stroke.provider.implementation"));

		try {
			Consumer<IStrokeSignature> consumer = null;
			ServiceLoader<IStrokeSignature> loader =
				ServiceLoader.load(IStrokeSignature.class);
			if (forceLoadingProvider) {
				consumer = arg0 -> {
					if (arg0.getVendor().getVendorID().equals(
						SignaturePadVendor.WACOM.getVendorID())) {
						vendor = arg0;
					}
				};
			}
			else {
				List<String> devices = WMICUtil.getRawDevicesConnected();
				consumer = arg0 -> {
					for (String device : devices) {
						if (arg0.getVendor().getVendorID().equals(device)) {
							vendor = arg0;
						}
					}
				};
			}

			loader.forEach(consumer);

			logger.info(
				"Signature strokes vendorID found [" +
					((vendor != null) ? vendor.getVendor() : "") + "]");

			documentViewController =
				new DocumentViewControllerExtendedImpl(this);

			if (vendor != null) {
				((DocumentViewControllerExtendedImpl) documentViewController).setSignatureVendor(
					vendor);
			}
			else
				throw new FeatureNotImplemented(
					"No signature strokes vendor-implementation was found");
		}
		catch (Throwable e) {
			logger.log(
				Level.FINE,
				"Error loading IStrokeSignature services: " + e.getMessage(),
				e);
			JOptionPane.showMessageDialog(
				viewer,
				"No se ha detectado ning\u00fAn dispositivo de firma digital conectado, por lo tanto no puede realizar ninguna operaci\u00f3n sobre el documento",
				"Informaci\u00f3n", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Sets the sign button.
	 *
	 * @param btn
	 *            the new sign button
	 */
	public void setSwapButton(JButton btn) {

		switchButton = btn;
		btn.addActionListener(this);
	}

	/**
	 * Sets the OK button.
	 *
	 * @param btn
	 *            the new sign button
	 */
	public void setOkButton(JButton btn) {

		okButton = btn;
		btn.addActionListener(this);
	}

	/**
	 * Sets the reset button.
	 *
	 * @param btn
	 *            the new reset button
	 */
	public void setResetButton(JButton btn) {

		resetButton = btn;
		btn.addActionListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingController#setDisplayTool(int)
	 */
	@Override
	public void setDisplayTool(int argToolName) {

		boolean actualToolMayHaveChanged = false;

		if (argToolName == DocumentViewModelImpl.DISPLAY_TOOL_SWAP_SELECTION) {
			actualToolMayHaveChanged = documentViewController.setToolMode(
				DocumentViewModelImpl.DISPLAY_TOOL_SWAP_SELECTION);
			documentViewController.setViewCursor(
				DocumentViewController.CURSOR_HAND_OPEN);
			setCursorOnComponents(DocumentViewController.CURSOR_DEFAULT);
		}
		else {
			super.setDisplayTool(argToolName);
		}

		if (actualToolMayHaveChanged) {
			reflectToolInToolButtons();
		}
		// repaint the page views.
		documentViewController.getViewContainer().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingController#openDocument(java.lang.String)
	 */
	@Override
	public void openDocument(String pathname) {

		super.openDocument(pathname);
		setPageViewMode(DocumentViewControllerImpl.ONE_COLUMN_VIEW, false);
		setPageFitMode(DocumentViewController.PAGE_FIT_WINDOW_WIDTH, false);
		setDocumentToolMode(DocumentViewModelImpl.DISPLAY_TOOL_PAN_EXTENDED);
		showOnScreen(Screen.EXTENDED, viewer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.icepdf.ri.common.SwingController#commonNewDocumentHandling(java.lang.
	 * String)
	 */
	@Override
	public void commonNewDocumentHandling(String fileDescription) {

		super.commonNewDocumentHandling(fileDescription);
		// remove the file name from title bar
		if (viewer != null) {
			viewer.setTitle(
				messageBundle.getString("viewer.window.title.default"));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingController#reflectToolInToolButtons()
	 */
	@Override
	protected void reflectToolInToolButtons() {

		super.reflectToolInToolButtons();
		reflectSelectionInButton(
			switchButton, documentViewController.isToolModeSelected(
				DocumentViewModelImpl.DISPLAY_TOOL_SWAP_SELECTION));
	}

	/*
	 * (non-Javadoc)
	 * @see org.icepdf.ri.common.SwingController#actionPerformed(java.awt.event.
	 * ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {

		super.actionPerformed(event);
		Object source = event.getSource();
		if (source == null)
			return;

		if (source == switchButton) {
			swapScreens();
		}
		else if (source == resetButton) {
			int dialogResult = JOptionPane.showConfirmDialog(
				viewer, messageBundle.getString("bgsignature.window.back.message"),
				messageBundle.getString("bgsignature.window.back.title"), JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				FileSystemManager.getInstance().getPdfStrokedDoc().delete();
				this.openDocument(
					FileSystemManager.getInstance().getPdfDocument().getAbsolutePath());
			}
		}
		if (source == okButton) {

			if (!FileSystemManager.getInstance().getPdfStrokedDoc().exists()) {
				JOptionPane.showMessageDialog(
					viewer,
					messageBundle.getString("bgsignature.window.errorstrokes.message"),
					messageBundle.getString("bgsignature.window.errorstrokes.title"), JOptionPane.ERROR_MESSAGE);
			}
			else {
					PayloadTO post = new PayloadTO();
					post.setSessionID(
						FileSystemManager.getInstance().getSessionId());
					post.setStage(Stage.MANUAL_SIGNED);
					ObjectMapper objectMapper = new ObjectMapper();
					String pckg = null;
					try {
						pckg = objectMapper.writeValueAsString(post);
					}
					catch (JsonProcessingException e) {
						logger.log(
							Level.WARNING,
							"There was an error trying to parse PayloadTO", e);
						e.printStackTrace();
					}
					try {
						MQUtility.sendMessageMQ(
							ListenerQueueConfig.class,
							ListenerMessageSender.class, pckg);
						windowManagementCallback.disposeWindow(
							this, viewer, propertiesManager.getPreferences());
					}
					catch (Exception e) {
						logger.log(
							Level.WARNING,
							"There was an error trying to connect with the local server to send package",
							e);
						e.printStackTrace();
						JOptionPane.showMessageDialog(
							viewer,
							messageBundle.getString("bgsignature.window.error.message"),
							messageBundle.getString("bgsignature.window.error.title"), JOptionPane.ERROR_MESSAGE);
					}
			}
		}
	}

	/**
	 * Swap screens and return a enum that represents the current screen where
	 * is the viewer is displayed .
	 *
	 * @return the screen
	 */
	private Screen swapScreens() {

		Frame viewer = this.getViewerFrame();
		int screen = this.getCurrentScreen();
		Screen screenEnum = Screen.getAlternateScreen(screen);
		if (screenEnum.equals(Screen.MAIN)) setDocumentToolMode(DocumentViewModelImpl.DISPLAY_TOOL_PAN);
		else setDocumentToolMode(DocumentViewModelImpl.DISPLAY_TOOL_PAN_EXTENDED);
		this.showOnScreen(screenEnum, viewer);
		return screenEnum;
	}

	/**
	 * Apply settings on action buttons.
	 *
	 * @param screenEnum
	 *            the screen enum
	 */
	private void applySettingsOnButtons(Screen screenEnum) {
		Color back = null;
		Color fore = null;
		String switchText = null;
		boolean isMain = !screenEnum.equals(Screen.EXTENDED);
		boolean isVendorFound = vendor != null;

		switch (screenEnum){
		case EXTENDED:
			back = SwingViewExtendedBuilder.ORANGE_BG;
			fore = SwingViewExtendedBuilder.WHITE_BG;
			switchText = messageBundle.getString("bgsignature.button.swap-l.label");
			break;
		case MAIN:
			back = SwingViewExtendedBuilder.WHITE_BG;
			fore = SwingViewExtendedBuilder.BLUE_BG;
			switchText = messageBundle.getString("bgsignature.button.swap-r.label");
			break;
		}

		if (switchButton!=null) {
			switchButton.setText(switchText);
			switchButton.setBackground(back);
			switchButton.setForeground(fore);
		}

		if (okButton != null) {
			okButton.setVisible(isMain);
			okButton.setEnabled(isVendorFound);
		}

		if (resetButton != null) {
			resetButton.setVisible(isMain);
			resetButton.setEnabled(isVendorFound);
		}
	}

	/**
	 * Gets the current screen.
	 *
	 * @return the current screen
	 */
	public int getCurrentScreen() {

		return currentScreen;
	}
}
