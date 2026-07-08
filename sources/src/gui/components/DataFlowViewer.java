package gui.components;

import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DataFlowViewer extends JPanel {

	private static int width = 800;
	private static int height = 600;
	private InfoViewer infos;

	public DataFlowViewer() {
		infos = new InfoViewer();
		setPreferredSize(new Dimension(width, height));
		add(infos);		
	}
}
