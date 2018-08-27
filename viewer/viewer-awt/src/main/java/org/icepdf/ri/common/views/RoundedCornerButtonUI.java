/**
 * 
 */
package org.icepdf.ri.common.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * The Class RoundedCornerButtonUI.
 *
 * @author hrodriguez
 */
public class RoundedCornerButtonUI extends BasicButtonUI {
    
    /** The Constant ARC_WIDTH. */
    private static final double ARC_WIDTH = 8d;
    
    /** The Constant ARC_HEIGHT. */
    private static final double ARC_HEIGHT = 8d;
    
    /** The Constant FOCUS_STROKE. */
    private static final int FOCUS_STROKE = 2;
    
    /** The fc. */
    protected final Color fc = new Color(100, 150, 255);
    
    /** The ac. */
    protected final Color ac = new Color(220, 225, 230);
    
    /** The rc. */
    protected final Color rc = Color.ORANGE;
    
    /** The shape. */
    private Shape shape;
    
    /** The border. */
    private Shape border;
    
    /** The base. */
    private Shape base;

    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicButtonUI#installDefaults(javax.swing.AbstractButton)
     */
    @Override protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setBackground(new Color(245, 250, 255));
        b.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        initShape(b);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicButtonUI#installListeners(javax.swing.AbstractButton)
     */
    @Override protected void installListeners(AbstractButton button) {
        BasicButtonListener listener = new BasicButtonListener(button) {
            @Override public void mousePressed(MouseEvent e) {
                AbstractButton b = (AbstractButton) e.getComponent();
                initShape(b);
                if (isShapeContains(e.getPoint())) {
                    super.mousePressed(e);
                }
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (isShapeContains(e.getPoint())) {
                    super.mouseEntered(e);
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                if (isShapeContains(e.getPoint())) {
                    super.mouseEntered(e);
                } else {
                    super.mouseExited(e);
                }
            }
        };
        // if (listener != null)
        button.addMouseListener(listener);
        button.addMouseMotionListener(listener);
        button.addFocusListener(listener);
        button.addPropertyChangeListener(listener);
        button.addChangeListener(listener);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicButtonUI#paint(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override public void paint(Graphics g, JComponent c) {
        initShape(c);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ContentArea
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (model.isArmed()) {
                g2.setPaint(ac);
                g2.fill(shape);
            } else if (b.isRolloverEnabled() && model.isRollover()) {
                paintFocusAndRollover(g2, c, rc);
            } else if (b.hasFocus()) {
                paintFocusAndRollover(g2, c, fc);
            } else {
                g2.setPaint(c.getBackground());
                g2.fill(shape);
            }
        }

        // Border
        //g2.setPaint(c.getForeground());
        g2.draw(shape);
        g2.dispose();
        super.paint(g, c);
    }
    
    /**
     * Checks if is shape contains.
     *
     * @param pt the pt
     * @return true, if is shape contains
     */
    protected final boolean isShapeContains(Point pt) {
        return shape != null && shape.contains(pt.x, pt.y);
    }
    
    /**
     * Inits the shape.
     *
     * @param c the c
     */
    protected final void initShape(Component c) {
        if (!c.getBounds().equals(base)) {
            base = c.getBounds();
            shape = new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
            border = new RoundRectangle2D.Double(FOCUS_STROKE, FOCUS_STROKE,
                                                 c.getWidth() - 1 - FOCUS_STROKE * 2,
                                                 c.getHeight() - 1 - FOCUS_STROKE * 2,
                                                 ARC_WIDTH, ARC_HEIGHT);
        }
    }
    
    /**
     * Paint focus and rollover.
     *
     * @param g2 the g 2
     * @param c the c
     * @param color the color
     */
    protected final void paintFocusAndRollover(Graphics2D g2, Component c, Color color) {
        g2.setPaint(new GradientPaint(0, 0, color, c.getWidth() - 1, c.getHeight() - 1, color.brighter(), true));
        g2.fill(shape);
        g2.setPaint(c.getBackground());
        g2.fill(border);
    }
}