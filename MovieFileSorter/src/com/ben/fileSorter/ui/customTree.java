package com.ben.fileSorter.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class customTree extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2096094195155925324L;
	String fileNames = "";
	int j;
	JScrollPane jsp;
	DefaultMutableTreeNode root;
	boolean stop = false;
	boolean subFound = false;
	JTree tree;
	Path selectedPath;
	JPopupMenu popupMenu;
	JMenuItem sortDirItem;

	public customTree(Path projectPath) {

		this.setLayout(new BorderLayout(0, 0));

		root = new DefaultMutableTreeNode(projectPath.toFile().getAbsolutePath() + "\\");

		for (File nom : projectPath.toFile().listFiles()) {

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(nom.getName() + "\\");
			DefaultMutableTreeNode populate = this.listFile(nom, node);

			if (populate != null) {
				root.add(populate);
			}
		}

		if (root.getChildCount() == 0) {
			root.add(new DefaultMutableTreeNode("No file to backup"));
		}
		tree = new JTree(root);
		
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				
				TreePath treePath = evt.getPath();
				
				String path = "";
				
				for (Object name : treePath.getPath()){
					if (name.toString() != null){
						path += name.toString();
					}
				}
				
				selectedPath = Paths.get(path);
				System.err.println(selectedPath);
				
			}
		});
		
		
		
		jsp = new JScrollPane(tree);
		
		popupMenu = new JPopupMenu();
		addPopup(tree, popupMenu);
		
		sortDirItem = new JMenuItem("sort folder");
		sortDirItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MainPanel newFrame = new MainPanel();
				JPanel contentPane = new JPanel();
				newFrame.setTitle("Ben's Amazing (Movie) File Sorter v1.0" + " --- " + selectedPath.toFile().getName());
				newFrame.setPath2Sort(selectedPath);
				newFrame.initPanel();
				newFrame.setLocationRelativeTo(null);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setVisible(true);
			
			}
		});
		popupMenu.add(sortDirItem);

		this.add(jsp, BorderLayout.CENTER);
	}

	private DefaultMutableTreeNode listFile(File file, DefaultMutableTreeNode node) {

		if (file.isFile()) {

			return new DefaultMutableTreeNode(file.getName());

		} else {
			File[] list = file.listFiles();
			if (list == null) {
				return null;
			}
			for (File nom : list) {

				DefaultMutableTreeNode subNode;
				if (nom.isDirectory()) {
					subNode = new DefaultMutableTreeNode(nom.getName() + "\\");
					DefaultMutableTreeNode subPopulate = this.listFile(nom, subNode);
					if (subPopulate != null) {
						node.add(subPopulate);
					}
				} else {

					node.add(new DefaultMutableTreeNode(nom.getName()));

				}
			}
			if (node.getChildCount() != 0) {
				return node;
			} else {
				return null;
			}
		}

	}

	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
			sortDirItem.setEnabled( (selectedPath.toFile().isFile()? false : true));
				popup.show(e.getComponent(), e.getX(), e.getY());

			}
		});
	}
}
