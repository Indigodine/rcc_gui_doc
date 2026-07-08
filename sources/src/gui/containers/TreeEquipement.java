package gui.containers;

import gui.components.EquipementUI;
import gui.components.Img;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import core.Common;
import core.equipement.Actor;
import core.equipement.Equipement;
import core.equipement.EquipementType;
import core.equipement.LienEqt;
import core.reseau.Communication;

/**
 * Arbre des équipements
 * @author malassigne
 *
 */
public class TreeEquipement extends JPanel implements MouseListener{

	@SuppressWarnings("serial")
	private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			/*
			System.out.println("-----------------\nvalue : " + value + "\nsel : " + sel + 
					"\nexpanded : " + expanded + "\nleaf : " + leaf + "\nrow : " + row +
					"\nhasFocus : " + hasFocus + "\n---------------\n");
			*/
			Border lineBorder = BorderFactory.createLineBorder(Color.BLUE, 1);
			JPanel panel = new JPanel();
			JLabel lab = new JLabel(value.toString());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();
			panel.add(lab);
			panel.setOpaque(hasFocus);

			if (obj == null) return this;

			if (obj instanceof EquipementUI) {

				EquipementUI eqt = (EquipementUI)obj;

				if (eqt.getEqt() == null) return this;

				if (eqt.getEqt().getType() != EquipementType.SUBSYSTEM_NARVAL) {
					lab.setIcon( eqt.getStateImg() );
						if (eqt.getStateImg() == null) System.err.println("No image found for state : " + eqt.getName());
						/*
						lineBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, eqt.getState().getColor());
					
						lab.setBorder(
							BorderFactory.createTitledBorder(
									lineBorder, " " + eqt.getState().name() + " ",
									TitledBorder.CENTER,
									TitledBorder.TOP, Font.decode("Courrier PLAIN 8")));
					*/
				} else {
					JLabel label = new JLabel("[ ] ");
					label.setForeground(Color.BLUE);
					lab.setForeground( hasFocus ? Color.WHITE : Color.BLACK);
					label.setFont(Font.decode("Courrier BOLD 12"));
					panel.add(label);
					panel.add(lab);
					panel.setOpaque(hasFocus);
					lineBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
					panel.setBorder(BorderFactory.createTitledBorder(lineBorder, " " + node.getChildCount() + " ",
							TitledBorder.RIGHT, TitledBorder.TOP,
							Font.decode("Courrier BOLD 12"), hasFocus ? Color.WHITE : Color.BLACK));
					panel.setBackground(Color.BLACK);
					return panel;
				}
			} else if (obj instanceof Lien) {
				lab.setFont(Font.decode("Courrier PLAIN 12"));
				lab.setForeground(hasFocus ? Color.WHITE : Color.DARK_GRAY);
				Lien tmp = (Lien)obj;
				String text = tmp.dest();
				if (text == null) text = tmp.src();
				lab.setText(text);
				panel.setBackground(new Color(0, 100, 160));
				lab.setIcon(Img.icon( tmp.src() == null ? "out" : "in"));
				return panel;
			} else {
				lab.setFont(Font.decode("Courrier BOLD 18"));
				lab.setForeground(Color.BLACK);
			}
			if (hasFocus) {
				panel.setBackground(Color.BLACK); //new Color(175, 215, 255));
				lab.setForeground(Color.WHITE); //.darker());
			}			
			return panel;
		}
	}

	class Lien {
		private String name;
		private String dest = null;
		private String src = null;
		public Lien(String name) {
			this.name = name;
		}
		public String src() {
			return src;
		}
		public String dest() {
			return dest;
		}
		public String name() {
			return name;
		}
		public void setSrc(String str) {
			this.src = str;
		}
		public void setDest(String str) {
			this.dest = str;
		}
	}

	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DefaultMutableTreeNode root;
	private TranslucentPopupMenu menuLien = new TranslucentPopupMenu();
	private JScrollPane scroll = new JScrollPane();
	private ArrayList<LienEqt> liensEqt = null;
	private JMenuItem modify = new JMenuItem(Common.getString("Modifier"));
	private JMenuItem delete = new JMenuItem(Common.getString("delete"));

	public TreeEquipement(String nameExp) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		root = new DefaultMutableTreeNode(nameExp);
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree = new JTree(model);
		scroll = new JScrollPane(tree);
		add(scroll);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.setShowsRootHandles(true);
		MyTreeCellRenderer renderer = new MyTreeCellRenderer();
		tree.setCellRenderer(renderer);
		tree.addMouseListener(this);
		//tree.setRootVisible(false);

		modify.addActionListener(new java.awt.event.ActionListener() {
			   public void actionPerformed(java.awt.event.ActionEvent e) {
				   String link = getSelectedLink();
				   if (link != null) {
					   for (LienEqt lien : liensEqt) {
						   if ( lien.getNom().equals(link) ) {
							   lien.openModificationPanel();
							   break;
						   }
					   }
				   }
			   }
		});

		delete.addActionListener(new java.awt.event.ActionListener() {
			   public void actionPerformed(java.awt.event.ActionEvent e) {
				   String link = getSelectedLink();
				   if (link != null) {
					   if (JOptionPane.showConfirmDialog(null, Common.getString("voulez_Vous_Supprimer")
							   + " " + Common.getString("Link") + " : " + link,
							   Common.getString("supression"), JOptionPane.YES_NO_OPTION)==0) {
						   Common.myClientSOAP.deleteLien(link);

						   Communication.needGridUpdate = true;
					   }
				   }
			   }
		});

		menuLien.add(modify);
		menuLien.add(delete);
	}

	public String getSelectedLink() {

		DefaultMutableTreeNode nod = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		Object obj = nod.getUserObject();

		if (obj != null) {
			if (obj instanceof Lien) {
				Lien tmp = (Lien)obj;
				return tmp.name();
			}
		}

		return null;
	}

	/**
	 * Change la liste des equipements
	 * @param eqts
	 */
	public void setEqtUI(Set<EquipementUI> eqts) {

		root.removeAllChildren();		

		HashMap<String, DefaultMutableTreeNode> narvalsContainers = new HashMap<String, DefaultMutableTreeNode>();

		/** Premier tour pour récupérer les containers Narval **/
		for (EquipementUI eqt : eqts) {

			Equipement equip = eqt.getEqt();

			if (equip != null) {
				if (equip.getType() == EquipementType.SUBSYSTEM_NARVAL) narvalsContainers.put(equip.getName(), add(eqt));				
				else continue;
			}
		}
		/** second tour pour générer l'arbre	**/
		for (EquipementUI eqt : eqts) {

			Equipement equip = eqt.getEqt();

			if (equip != null) {

				if ((equip.getType() == EquipementType.ACTOR) || (equip.getType() == EquipementType.SBUFPRODUCER)
						|| (equip.getType() == EquipementType.EVENTBUILDER) || (equip.getType() == EquipementType.STORAGE)
						|| (equip.getType() == EquipementType.RIKENTRANSMITTER)) {
					Actor actor = (Actor)equip;
					DefaultMutableTreeNode narval = narvalsContainers.get( actor.getContainerNARVAL() );
					if (narval == null) System.out.println("TreeEquipement.setEqtUI: narval=null");
					else narval.add(new DefaultMutableTreeNode(eqt));
				} else if (equip.getType() != EquipementType.SUBSYSTEM_NARVAL)

				add(eqt);
			}
		}

		maj();
	}

	public void setLiens(final ArrayList<LienEqt> liens) {

		liensEqt = liens;

		HashMap<String, DefaultMutableTreeNode> noeuds = new HashMap<String, DefaultMutableTreeNode>();

		for (int i = 0 ; i < root.getChildCount() ; i++) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt(i);

			Object obj = node.getUserObject();

			if (obj == null) continue;

			if (obj instanceof EquipementUI) {

				EquipementUI eqtUI = (EquipementUI)obj;

				Equipement equip = eqtUI.getEqt();

				if (equip != null) {
					if (equip.getType() != EquipementType.SUBSYSTEM_NARVAL) node.removeAllChildren();
					noeuds.put(eqtUI.getName(), node);
					/** Si l'eqt est un container Narval on récupère tous ses acteurs	**/
					if (equip.getType() == EquipementType.SUBSYSTEM_NARVAL) {
						for (int index = 0 ; index < node.getChildCount() ; index++) {
							DefaultMutableTreeNode noeudNarval = (DefaultMutableTreeNode)node.getChildAt(index);
							noeudNarval.removeAllChildren(); // on enleve les liens
							Object objNarv = noeudNarval.getUserObject();
							EquipementUI narvUI = (EquipementUI)objNarv;
							noeuds.put(narvUI.getName(), noeudNarval);
						}
					}
				}
			}
		}

		for (LienEqt lien : liensEqt) {
			String src = lien.getEqtSrc().getName();
			String dest = lien.getEqtDest().getName();
			DefaultMutableTreeNode source = noeuds.get(src);
			DefaultMutableTreeNode target = noeuds.get(dest);
			if (source == null) continue;
			Lien l1 = new Lien(lien.getNom());
			l1.setDest(dest);
			Lien l2 = new Lien(lien.getNom());
			l2.setSrc(src);
			source.add(new DefaultMutableTreeNode(l1));
			target.add(new DefaultMutableTreeNode(l2));
		}

		maj();
	}

	public void expand() {
		tree.expandRow(0);
	}

	/**
	 * Ajoute un eqt à l'arbre
	 * @param eqt
	 */
	public DefaultMutableTreeNode add(EquipementUI eqt) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(eqt);
		root.add(node);
		return node;
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			DefaultMutableTreeNode nod = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			Object obj = nod.getUserObject();
			int decY = scroll.getVerticalScrollBar().getValue();
			if (obj == null) return;
			if (obj instanceof EquipementUI) {
				EquipementUI eqt = (EquipementUI)obj;
				eqt.getPopupSurv().show(this, evt.getX(), evt.getY() - decY);
			} else if (obj instanceof Lien) {
				menuLien.show(this, evt.getX(), evt.getY() - decY);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent evt) {}
	@Override
	public void mouseExited(MouseEvent evt) {}
	@Override
	public void mousePressed(MouseEvent evt) {}
	@Override
	public void mouseReleased(MouseEvent evt) {}

	public void maj() {
		tree.updateUI();
		((DefaultTreeModel)tree.getModel()).reload();
		//expand();
	}

	public void offline() {
		modify.setEnabled(true);
		delete.setEnabled(true);
	}

	public void online() {
		modify.setEnabled(false);
		delete.setEnabled(false);
	}
}
