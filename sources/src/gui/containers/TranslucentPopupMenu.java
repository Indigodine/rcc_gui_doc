package gui.containers;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;

@SuppressWarnings("serial")
public class TranslucentPopupMenu extends JPopupMenu{
  private static final Color ALPHA_ZERO = new Color(0, true);
  private static final Color POPUP_BACK = new Color(250,250,250,155);
  private static final Color POPUP_LEFT = new Color(230,230,230,155);
  private static final int LEFT_WIDTH = 24;
  @Override public boolean isOpaque() {
    return false;
  }
  @Override public void updateUI() {
    super.updateUI();
    //Test:
    boolean isNimbus = UIManager.getBorder("PopupMenu.border")==null;
    if(isNimbus) {
      //XXX: NimbusLnF not work???
      setBorder(new BorderUIResource(BorderFactory.createLineBorder(Color.GRAY)));
    }
  }
  @Override public JMenuItem add(JMenuItem menuItem) {
    menuItem.setOpaque(false);
    return super.add(menuItem);
  }
  @Override public void show(Component c, int x, int y) {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        java.awt.Window p = SwingUtilities.getWindowAncestor(TranslucentPopupMenu.this);
        if(p!=null && p instanceof JWindow) {
          JWindow w = (JWindow)p;
          w.setBackground(ALPHA_ZERO);
        }
      }
    });
    super.show(c, x, y);
  }
  @Override protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g.create();
    g2.setPaint(POPUP_LEFT);
    g2.fillRect(0,0,LEFT_WIDTH,getHeight());
    g2.setPaint(POPUP_BACK);
    g2.fillRect(LEFT_WIDTH,0,getWidth(),getHeight());
    g2.dispose();
  }
}
