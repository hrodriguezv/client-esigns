/**
 * 
 */

package org.icepdf.ri.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.icepdf.ri.common.views.BgpButton;
import org.icepdf.ri.images.Images;
import org.icepdf.ri.util.PropertiesManager;

/**
 * The Class SwingViewExtendedBuilder.
 *
 * @author hrodriguez
 */
public class SwingViewExtendedBuilder extends SwingViewBuilder {

  /** The Constant BLUE_BG. */
  public static final Color BLUE_BG = new Color(13, 63, 130);

  /** The Constant WHITE_BG. */
  public static final Color WHITE_BG = new Color(255, 255, 255);

  /** The Constant ORANGE_BG. */
  protected static final Color ORANGE_BG = new Color(255, 121, 0);

  /**
   * Instantiates a new swing view extended builder.
   *
   * @param c the c
   * @param documentViewType the document view type
   * @param documentPageFitMode the document page fit mode
   * @param rotation the rotation
   */
  public SwingViewExtendedBuilder(SwingController c, int documentViewType, int documentPageFitMode,
      float rotation) {

    // Use all the defaults
    super(c, null, null, false, SwingViewBuilder.TOOL_BAR_STYLE_FIXED, null, documentViewType,
        documentPageFitMode, rotation);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildViewerFrame()
   */
  @Override
  public JFrame buildViewerFrame() {

    JFrame viewer = new JFrame();

    viewer.setIconImage(new ImageIcon(Images.get("icepdf-app-icon-64x64.png")).getImage());
    viewer.setTitle(messageBundle.getString("viewer.window.title.default"));
    viewer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    Container contentPane = viewer.getContentPane();

    buildContents(contentPane, false);

    if (viewerController != null) {

      viewerController.setViewerFrame(viewer);

    }

    return viewer;
  }

  /**
   * Builds the swap button.
   *
   * @return the j button
   */
  private JPanel buildQuestionPanel() {

    JPanel questionPanel = new JPanel(new ToolbarLayout(ToolbarLayout.LEFT, 20, 10));

    questionPanel.setBackground(BLUE_BG);

    JLabel label = new JLabel();

    label.setText(messageBundle.getString("bgsignature.question.label.text"));
    label.setFont(new Font("Opens Sans", Font.PLAIN, 22));
    label.setForeground(WHITE_BG);

    questionPanel.add(label);

    return questionPanel;

  }

  private JPanel buildButtonsPanel() {

    JPanel buttonPanel = new JPanel(new ToolbarLayout(ToolbarLayout.LEFT, 20, 0));

    buttonPanel.setBackground(BLUE_BG);

    if ((propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_CANCEL))) {

      buttonPanel.add(buildCancelButton());

    }

    if ((propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_RESET))) {

      buttonPanel.add(buildSignAgainButton());

    }

    if ((propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_OK))) {

      buttonPanel.add(buildSignatureMatchesButton());

    }

    return buttonPanel;

  }

  /**
   * Builds the swap button.
   *
   * @return the j button
   */
  private JButton buildFinishButton() {

    JButton btn = createBgpButton(messageBundle.getString("bgsignature.button.swap-l.label"),
      ORANGE_BG, WHITE_BG);

    if (viewerController != null) {

      ((SwingExtendedController) viewerController).setFinishButton(btn);

    }

    return btn;

  }

  /**
   * Builds the ok button.
   *
   * @return the j button
   */
  private JButton buildSignatureMatchesButton() {

    JButton btn = createBgpButton(messageBundle.getString("bgsignature.button.check.label"),
      ORANGE_BG, WHITE_BG);

    btn.setPreferredSize(new Dimension(180, 40));

    if (viewerController != null) {

      ((SwingExtendedController) viewerController).setOkButton(btn);

    }

    return btn;
  }

  /**
   * Builds the swap button.
   *
   * @return the j button
   */
  private JButton buildCancelButton() {

    JButton btn = createBgpButton(messageBundle.getString("bgsignature.button.swap-r.label"),
      WHITE_BG, BLUE_BG);

    btn.setPreferredSize(new Dimension(120, 40));

    if (null != viewerController) {

      ((SwingExtendedController) viewerController).setCancelButton(btn);

    }

    return btn;
  }

  /**
   * Builds the reset button.
   *
   * @return the j button
   */
  private JButton buildSignAgainButton() {

    JButton btn = createBgpButton(messageBundle.getString("bgsignature.button.back.label"),
      WHITE_BG, BLUE_BG);

    btn.setPreferredSize(new Dimension(220, 40));

    if (viewerController != null) {

      ((SwingExtendedController) viewerController).setResetButton(btn);

    }

    return btn;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildUtilityToolBar(boolean,
   * org.icepdf.ri.util.PropertiesManager)
   */
  @Override
  public JToolBar buildUtilityToolBar(boolean embeddableComponent,
      PropertiesManager propertiesManager) {

    JToolBar toolbar = new JToolBar();

    commonToolBarSetup(toolbar, false);

    /*
     * if embeddable component, we don't want to create the open dialog, as we have no window
     * manager for this case.
     */
    if (embeddableComponent) {

      return toolbar;

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_SWAP)) {

      addToToolBar(toolbar, buildFinishButton());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_RESET)) {

      addToToolBar(toolbar, buildSignAgainButton());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_OK)) {

      addToToolBar(toolbar, buildSignatureMatchesButton());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_CANCEL)) {

      addToToolBar(toolbar, buildCancelButton());

    }

    return toolbar;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildCompleteToolBar(boolean)
   */
  @Override
  public JToolBar buildCompleteToolBar(boolean embeddableComponent) {

    return null;

  }


  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildCompleteToolBar(boolean)
   */
  public JToolBar buildCompleteToolBarExt(boolean embeddableComponent) {

    JToolBar toolbar = new JToolBar();

    toolbar.setLayout(new ToolbarLayout(ToolbarLayout.LEFT, 0, 0));

    commonToolBarSetup(toolbar, true);

    /*
     * Attempt to get the properties manager so we can configure which toolbars are visible
     */
    doubleCheckPropertiesManager();

    /*
     * Build the main set of toolbars based on the property file configuration
     */
    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY)) {

      addToToolBar(toolbar, buildUtilityToolBar(embeddableComponent, propertiesManager));

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV)) {

      addToToolBar(toolbar, buildPageNavigationToolBar());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ZOOM)) {

      addToToolBar(toolbar, buildZoomToolBar());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT)) {

      addToToolBar(toolbar, buildFitToolBar());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE)) {

      addToToolBar(toolbar, buildRotateToolBar());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL)) {

      addToToolBar(toolbar, buildToolToolBar());

    }

    if (propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_SEARCH)) {

      addToToolBar(toolbar, buildSearchToolBar());

    }

    /*
     * Set the toolbar back to null if no components were added The result of this will properly
     * disable the necessary menu items for controlling the toolbar
     */
    if (toolbar.getComponentCount() == 0) {

      toolbar = null;

    }

    if ((viewerController != null) && (toolbar != null)) {

      viewerController.setCompleteToolBar(toolbar);

    }

    return toolbar;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildPageNavigationToolBar()
   */
  @Override
  public JToolBar buildPageNavigationToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildToolToolBar()
   */
  @Override
  public JToolBar buildToolToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildZoomToolBar()
   */
  @Override
  public JToolBar buildZoomToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildFullScreenToolBar()
   */
  @Override
  public JToolBar buildFullScreenToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildFitToolBar()
   */
  @Override
  public JToolBar buildFitToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildRotateToolBar()
   */
  @Override
  public JToolBar buildRotateToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildAnnotationlToolBar()
   */
  @Override
  public JToolBar buildAnnotationlToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildFormsToolBar()
   */
  @Override
  public JToolBar buildFormsToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildSearchToolBar()
   */
  @Override
  public JToolBar buildSearchToolBar() {

    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.icepdf.ri.common.SwingViewBuilder#buildStatusPanel()
   */
  @Override
  public JPanel buildStatusPanel() {

    if (!propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_STATUSBAR)) {

      return null;

    }

    JPanel statusPanel = new JPanel(new BorderLayout());

    statusPanel.setBackground(BLUE_BG);

    if (null != viewerController && propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_STATUSBAR_STATUSLABEL)) {

      viewerController.setStatusLabel(null);

    }

    JPanel viewPanel = new JPanel(new ToolbarLayout(ToolbarLayout.LEFT, 20, 0));

    viewPanel.setBackground(BLUE_BG);

    viewPanel.add(buildExtendedWindowPanel(), BorderLayout.CENTER);
    viewPanel.add(buildMainWindowPanel(), BorderLayout.CENTER);

    statusPanel.add(viewPanel);

    return statusPanel;

  }


  /**
   * Builds the status buttons panel.
   *
   * @return the j panel
   */
  private JPanel buildMainWindowPanel() {

    JPanel mainWindowPanel = new JPanel(new GridLayout(2, 4));

    mainWindowPanel.setBackground(BLUE_BG);

    mainWindowPanel.add(buildQuestionPanel());
    mainWindowPanel.add(buildButtonsPanel());

    mainWindowPanel.setVisible(false);

    if (viewerController != null) {

      ((SwingExtendedController) viewerController).setMainWindowPanel(mainWindowPanel);

    }

    return mainWindowPanel;

  }

  /**
   * Builds the status buttons panel.
   *
   * @return the j panel
   */
  private JPanel buildExtendedWindowPanel() {

    JPanel extWindowPanel = new JPanel(new ToolbarLayout(ToolbarLayout.LEFT, 0, 10));

    extWindowPanel.setBackground(BLUE_BG);

    extWindowPanel.setVisible(true);

    if ((propertiesManager
        .checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_SWAP))) {

      extWindowPanel.add(buildFinishButton());

    }

    if (viewerController != null) {

      ((SwingExtendedController) viewerController).setExtendedWindowPanel(extWindowPanel);

    }

    return extWindowPanel;

  }

  /**
   * Make BG button.
   *
   * @param title the title
   * @param bck the bck
   * @param frg the frg
   * @return the j button
   */
  private JButton createBgpButton(String title, Color bck, Color frg) {

    JButton btn = new BgpButton(title);

    btn.setBackground(bck);
    btn.setForeground(frg);
    btn.setPreferredSize(new Dimension(160, 40));
    btn.setFont(new Font("Opens Sans", Font.PLAIN, 18));
    btn.setContentAreaFilled(false);

    return btn;

  }

}
