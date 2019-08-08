package org.icepdf.ri.common.views;

import javax.swing.JButton;

public class BgpButton extends JButton {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -2342849244422953588L;

  public BgpButton(String text) {
    super(text);
  }

  @Override
  public void updateUI() {
    setUI(new BgpButtonUI());
  }

}
