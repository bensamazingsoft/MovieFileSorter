package com.ben.fileSorter.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ben.fileSorter.FileSorter;

/**
 * Create the panel.
 * 
 * @author ben
 * @version 1.0
 */

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	/**
	 * The path object the app will work with
	 */
	Path path2Sort = null;// Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());

	/**
	 * warnings diplayed in the post-launch() info dialog
	 */
	static private String warnings = new String("Job finished\n");

	public static String getWarnings() {
		return warnings;
	}

	public static void setWarnings(String warnings) {
		MainPanel.warnings = warnings;
	}

	/**
	 * log string to be written in the log file
	 */
	public static String log = new String("Ben's Amazing Sorting App v1.0\n");

	public static String getLog() {
		return log;
	}

	public static void setLog(String log) {
		MainPanel.log = log;
	}

	private JFileChooser jfc = new JFileChooser();
	private JPanel panel4jfc = new JPanel();
	private JLabel path2SortLabel = new JLabel();
	private JButton browseBut = new JButton("Browse");
	private JPanel panel4Options = new JPanel();
	private final JPanel panel4Launch = new JPanel();
	private final JButton btnNewButton = new JButton("Proceed");
	private final JCheckBox frenchSortCb = new JCheckBox("Move 'french' files in 'french' folder");
	private JOptionPane jobsDone;
	private final JCheckBox delSpacesCb = new JCheckBox("Replace spaces with periods");
	private final JPanel customFolderSortPnl = new JPanel();
	private final JCheckBox customFolderSortCb = new JCheckBox("Move files in custom folder (ex. BRRip) : ");
	private final JTextField customFolderSortTf = new JTextField();
	private final JCheckBox delSamplesCb = new JCheckBox("Delete Samples");
	private final JCheckBox cutomFolderRecursiveCb = new JCheckBox("Recursive ");
	private final JCheckBox frenchRecursiveCb = new JCheckBox("Recursive ");
	private final JPanel frenchPanel = new JPanel();
	private final JCheckBox delEmptyFoldersCb = new JCheckBox("Delete empty folders ");
	private final JPanel replaceThisByThatPnl = new JPanel();
	private final JCheckBox replaceThisByThatCb = new JCheckBox("Replace ");
	private final JTextField thisTf = new JTextField();
	private final JLabel lblBy = new JLabel(" by ");
	private final JTextField thatTf = new JTextField();
	private final JCheckBox replaceThisbyThatRecursiveCb = new JCheckBox("Recursive");
	private final JPanel Sort = new JPanel();
	private final JPanel Rename = new JPanel();
	private final JPanel Clean = new JPanel();
	private final JPanel addToNamePnl = new JPanel();
	private final JCheckBox addToNameCb = new JCheckBox("Add ");
	private final JTextField addToNameTf = new JTextField();
	private final JLabel lblNewLabel = new JLabel(" prefix  of filename (ex. '3D-')");
	private final JCheckBox addToNameRecursiveCb = new JCheckBox("Recursive");
	private final JCheckBox sortTvCb = new JCheckBox("Move TV shows in a 'TVShows' folder");
	private final JPanel sortTvPnl = new JPanel();
	private final JCheckBox sortTVShowsRecursive = new JCheckBox("Recursive");
	private final JCheckBox file2LowerCb = new JCheckBox("All files to lowercase");
	private final JCheckBox folder2LowerCb = new JCheckBox("All folders to lowercase");
	private final JCheckBox thisThatCaseCb = new JCheckBox("Case sensitive");

	public MainPanel() {
		addToNameTf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				addToNameCb.setSelected(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (addToNameTf.getText().length() != 0) {
					addToNameCb.setSelected(true);
				}
				if (addToNameTf.getText().length() == 0) {
					addToNameCb.setSelected(false);
				}
			}
		});
		addToNameTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '/' || c == '\\' || c == '$') {
					e.consume();
				}
			}
		});
		addToNameTf.setColumns(10);
		addToNameTf.setTransferHandler(null);
		setPreferredSize(new Dimension(500, 500));

		// Setup MainPanel
		setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel4jfc.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		// Setup file chooser panel
		panel4jfc.setBorder(BorderFactory.createTitledBorder("Set folder to sort"));
		add(panel4jfc, BorderLayout.NORTH);
		if (path2Sort != null) {
			path2SortLabel.setText(path2Sort.toFile().getAbsolutePath());
			jfc.setCurrentDirectory(path2Sort.toFile());
		} else {
			path2SortLabel.setText("Choose directory : ");
		}

		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		panel4jfc.add(path2SortLabel);
		browseBut.setToolTipText("browse computer to find the folder you want to sort");
		browseBut.setMnemonic('o');

		browseBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ChooseDir();
			}
		});
		panel4jfc.add(browseBut);
		FlowLayout flowLayout_2 = (FlowLayout) panel4Options.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);

		// Setup option panel
		panel4Options.setBorder(BorderFactory.createTitledBorder("Set Options"));
		add(panel4Options, BorderLayout.CENTER);
		FlowLayout flowLayout_7 = (FlowLayout) Rename.getLayout();
		flowLayout_7.setAlignment(FlowLayout.LEFT);
		Rename.setToolTipText(
				"<html>\r\n<p>Enable/Disable the AddToName option</p>\r\n<p>Will add the typed-in text as a prefix to filenames</p>\r\n</html>");
		Rename.setPreferredSize(new Dimension(440, 155));
		Rename.setBorder(new TitledBorder(null, "Rename", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panel4Options.add(Rename);
		thatTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '/' || c == '\\' || c == '$') {
					e.consume();
				}
			}
		});
		thatTf.setColumns(10);
		thatTf.setTransferHandler(null);
		thisTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '/' || c == '\\' || c == '$') {
					e.consume();
				}
			}
		});
		thisTf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (thisTf.getText().length() != 0) {
					replaceThisByThatCb.setSelected(true);
				}
				if (thisTf.getText().length() == 0) {
					replaceThisByThatCb.setSelected(false);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				replaceThisByThatCb.setSelected(true);
			}
		});
		file2LowerCb.setToolTipText("<html>\r\n<p>Enable/Disable the AllFilesToLowerCase option</p>\r\n<p>recusively changes filesnames to all-lowercase</p>\r\n</html>");
		
		Rename.add(file2LowerCb);
		folder2LowerCb.setToolTipText("<html>\r\n<p>Enable/Disable the AllFoldersToLowerCase option</p>\r\n<p>recursively changes folder names to all-lowercase</p>\r\n</html>");
		
		Rename.add(folder2LowerCb);
		Rename.add(delSpacesCb);
		delSpacesCb.setMnemonic('s');
		delSpacesCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable the option</p>\r\n<p>Replaces spaces with periods (\".\") in files and folders names</p>\r\n</html>");

		thisTf.setColumns(10);
		thisTf.setTransferHandler(null);
		FlowLayout flowLayout_4 = (FlowLayout) replaceThisByThatPnl.getLayout();
		flowLayout_4.setVgap(0);
		flowLayout_4.setHgap(0);
		Rename.add(replaceThisByThatPnl);
		replaceThisByThatPnl.setToolTipText(
				"<html>\r\n<p>Enable/Disable the 'Replace This by That' option</p>\r\n<p>Willl replace 'This' by 'That' in filename and folder names</p>\r\n<p>Leave 'that' blank to suppress 'this' from file/folder names</p>\r\n</html>");
		replaceThisByThatCb.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (!replaceThisByThatCb.isSelected()) {
					thisTf.setText("");
					thatTf.setText("");
					replaceThisbyThatRecursiveCb.setSelected(false);
				}
			}
		});
		replaceThisByThatCb.setMnemonic('l');

		replaceThisByThatPnl.add(replaceThisByThatCb);

		replaceThisByThatPnl.add(thisTf);

		replaceThisByThatPnl.add(lblBy);

		replaceThisByThatPnl.add(thatTf);
		replaceThisbyThatRecursiveCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable recursive search option</p>\r\n<p>if enabled, 'Replace This by That\" will look in every folders, not only the root of the folder to sort.</p>\r\n</html>");

		replaceThisByThatPnl.add(replaceThisbyThatRecursiveCb);
		thisThatCaseCb.setToolTipText("<html>\r\n<p>Enable/Disable the 'CaseSensitive' option</p>\r\n<p>it's plain what it does, if you don't get it, well, maybe don't use this software</p>\r\n</html>");
		
		replaceThisByThatPnl.add(thisThatCaseCb);
		FlowLayout flowLayout_10 = (FlowLayout) addToNamePnl.getLayout();
		flowLayout_10.setHgap(0);

		Rename.add(addToNamePnl);
		addToNameCb.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!addToNameCb.isSelected()) {
					addToNameTf.setText("");
					addToNameRecursiveCb.setSelected(false);
				}
			}
		});

		addToNamePnl.add(addToNameCb);

		addToNamePnl.add(addToNameTf);

		addToNamePnl.add(lblNewLabel);
		addToNameRecursiveCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable recursive search option</p>\r\n<p>if enabled, AddToName will look in every folders, not only the root of the folder to sort.</p>\r\n</html>");

		addToNamePnl.add(addToNameRecursiveCb);
		FlowLayout flowLayout_6 = (FlowLayout) Sort.getLayout();
		flowLayout_6.setAlignment(FlowLayout.LEFT);
		Sort.setBorder(new TitledBorder(null, "Sort", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		Sort.setPreferredSize(new Dimension(440, 120));

		panel4Options.add(Sort);
		FlowLayout flowLayout_5 = (FlowLayout) frenchPanel.getLayout();
		flowLayout_5.setVgap(0);
		flowLayout_5.setHgap(0);
		Sort.add(frenchPanel);
		frenchPanel.add(frenchSortCb);
		frenchSortCb.setMnemonic('f');
		frenchSortCb.setToolTipText(
				"<html><p>Enable/disable the French Sort option.</p>\r\n<p>This will create a <Strong>'french'</Strong> folder and then move there all movies with \"french\" in their name.</p>\r\n<p>If movies are in separate folders, the whole folder will be moved to the <Strong>'french'</Strong> folder as long as the folder name contains 'french'</p>\r\n<p>If the <Strong>'french'</Strong> folder already exist, the program will proceed without  creating a new one.</p></html>\r\n");
		frenchPanel.add(frenchRecursiveCb);
		frenchRecursiveCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable recursive search option</p>\r\n<p>if enabled, the French Sort  will look in every folders, not only the root of the folder to sort.</p>\r\n<p>Folders will be ignored</p>");
		frenchRecursiveCb.setMnemonic('h');
		customFolderSortTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '/' || c == '\\' || c == '$') {
					e.consume();
				}
			}
		});
		customFolderSortTf.setToolTipText(
				"<html>\r\n<p>Enable/Disable the custom folder sort option</p>\r\n<p>Type in a cutom category you want the program to use to create a custom folder and sort every folder, movie, subtitles etc... in that folder.</p>\r\n<p>It is possible to type multiple custom category, they must be separated with ',' (Ex. DvdRip,BRRip,Porn,3d). Multiple folders will be created.</p>\r\n<p>You can't paste text in this field or type in '/' and '\\', that's for your own good</p>\r\n</html>");
		customFolderSortTf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (customFolderSortTf.getText().length() != 0) {
					customFolderSortCb.setSelected(true);
				}
				if (customFolderSortTf.getText().length() == 0) {
					customFolderSortCb.setSelected(false);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				customFolderSortCb.setSelected(true);
			}
		});

		customFolderSortTf.setColumns(10);
		customFolderSortTf.setTransferHandler(null);
		FlowLayout flowLayout_3 = (FlowLayout) customFolderSortPnl.getLayout();
		flowLayout_3.setVgap(0);
		flowLayout_3.setHgap(0);
		Sort.add(customFolderSortPnl);
		customFolderSortPnl.setToolTipText(
				"<html>\r\n<p>Enable/Disable the custom folder sort option</p>\r\n<p>Type in a cutom category you want the program to use to create a custom folder and sort every folder, movie, subtitles etc... in that folder.</p>\r\n<p>It is possible to type multiple custom category, they must be separated with ',' (Ex. DvdRip,BRRip,Porn,3d). Multiple folders will be created.</p>");
		customFolderSortCb.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (!customFolderSortCb.isSelected()) {
					customFolderSortTf.setText("");
					cutomFolderRecursiveCb.setSelected(false);
				}
			}
		});
		customFolderSortCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable the custom folder sort option</p>\r\n<p>Moves file and folders matching a keyword in a 'keyword' folder</p>\r\n<p>multiple custom category  must be separated with ',' (Ex. DvdRip,BRRip,Porn,3d). Multiple folders will be created.</p>\r\n</p>Case sensitive! You may want to use the Replace option to normalize your filenames.</p>\r\n</html>");
		customFolderSortCb.setMnemonic('m');

		customFolderSortPnl.add(customFolderSortCb);

		customFolderSortPnl.add(customFolderSortTf);
		cutomFolderRecursiveCb.setMnemonic('i');
		cutomFolderRecursiveCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable recursive search option</p>\r\n<p>if enabled, the Sort movies in custom folder will look in every folders, not only the root of the folder to sort.</p>\r\n</html>");

		customFolderSortPnl.add(cutomFolderRecursiveCb);
		FlowLayout flowLayout_9 = (FlowLayout) sortTvPnl.getLayout();
		flowLayout_9.setHgap(0);

		Sort.add(sortTvPnl);
		sortTvCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable the Move TVShows option</p>\r\n<p>Moves TV Shows based on wether the filename contains the pattern (s##e##)</p>\r\n<p><Strong>To ignore existing TV shows folder</Strong>, you need to add 'TV-' in these folder names (ex. 'TV-Friends).</p>\r\n</html>");
		sortTvPnl.add(sortTvCb);
		sortTVShowsRecursive.setToolTipText(
				"<html>\r\n<p>Enable/Disable recursive search option</p>\r\n<p>if enabled, the MoveTVShows will look in every folders, not only the root of the folder to sort.</p>\r\n</html>");

		sortTvPnl.add(sortTVShowsRecursive);
		FlowLayout flowLayout_8 = (FlowLayout) Clean.getLayout();
		flowLayout_8.setAlignment(FlowLayout.LEFT);
		Clean.setPreferredSize(new Dimension(440, 60));
		Clean.setBorder(new TitledBorder(null, "Clean", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panel4Options.add(Clean);
		Clean.add(delEmptyFoldersCb);
		delEmptyFoldersCb.setMnemonic('y');
		delEmptyFoldersCb.setToolTipText(
				"<html>\r\n<p>Enable/Disable the Delete Empty Folders option</p>\r\n<p>if enabled it will deletes every empty folders after other processes</p>");
		Clean.add(delSamplesCb);
		delSamplesCb.setMnemonic('a');
		delSamplesCb.setToolTipText(
				"<html>\r\n<p>Enable/disable the delete samples option</p>\r\n<p>this will deletes all files and folders which names contains 'sample'</p>\r\n<p>this is <em>un-cancellable</em> so think long and hard before using this feature</p>\r\n</html> ");
		FlowLayout flowLayout_1 = (FlowLayout) panel4Launch.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);

		// Setup Launch panel
		panel4Launch.setBorder(BorderFactory.createTitledBorder("Launch that shit"));
		add(panel4Launch, BorderLayout.SOUTH);
		btnNewButton.setToolTipText("Launch the sorting with choosen options");
		btnNewButton.setMnemonic('c');
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Launch();
				} catch (IOException e) {

					MainPanel.setWarnings(MainPanel.getWarnings() + "Error(s) :" + e.getMessage());
				}
			}
		});
		btnNewButton.setHorizontalTextPosition(SwingConstants.RIGHT);

		panel4Launch.add(btnNewButton);

	}

	/**
	 * Open the Dialog box to choose the directory to work with
	 */
	public void ChooseDir() {
		int result = jfc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			this.path2Sort = Paths.get(jfc.getSelectedFile().getAbsolutePath());
		}
		if (path2Sort != null) {
			path2SortLabel.setText(path2Sort.toFile().getAbsolutePath());
			btnNewButton.setEnabled(true);
		}
	}

	/**
	 * launch the selected tasks
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public void Launch() throws IOException {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		MainPanel.setLog(log += "New Job Created " + dtf.format(now));

		// executing (or not) the FileSorter methods
		
		if (file2LowerCb.isSelected() || folder2LowerCb.isSelected()){
			FileSorter.toLower(path2Sort, file2LowerCb.isSelected(), folder2LowerCb.isSelected());
		}
		
		if (frenchSortCb.isSelected()) {
			FileSorter.FrenchFolderSort(path2Sort, frenchRecursiveCb.isSelected());
		}

		if (sortTvCb.isSelected()) {
			FileSorter.MoveTVShows(path2Sort, sortTVShowsRecursive.isSelected());
		}

		if (addToNameCb.isSelected()) {
			FileSorter.AddToName(path2Sort, addToNameTf.getText(), addToNameRecursiveCb.isSelected());
		}

		if (delSpacesCb.isSelected()) {
			FileSorter.DelSpaces(path2Sort);
		}

		if (customFolderSortCb.isSelected()) {
			if (customFolderSortTf.getText().length() != 0) {
				FileSorter.CustomFolderSort(path2Sort, customFolderSortTf.getText(),
						cutomFolderRecursiveCb.isSelected());
			} else {
				MainPanel.setWarnings(warnings + " Custom folder option ignored (no text)\n");
			}
		}

		if (delSamplesCb.isSelected()) {
			FileSorter.DelSamples(path2Sort);
		}

		if (replaceThisByThatCb.isSelected()) {
			FileSorter.ReplaceThisByThat(path2Sort, thisTf.getText(), thatTf.getText(),
					replaceThisbyThatRecursiveCb.isSelected(), thisThatCaseCb.isSelected());
		}

		if (delEmptyFoldersCb.isSelected()) {
			FileSorter.DeleteAllEmptyFolders(path2Sort);
		}

		// add warnings to log
		MainPanel.setLog(MainPanel.getLog() + "\n\n" + "Infos : " + "\n\t" + MainPanel.getWarnings());

		if (path2Sort != null) {
			// write log file
			File logFile = new File(path2Sort.toFile().getAbsolutePath() + "/log.txt");
			String separator = System.getProperty("line.separator"); // get the
			// system
			// line
			// separator
			String newLog = MainPanel.getLog().replace("\n", separator); // replaces
			// the
			// String
			// line
			// separator
			// with
			// the
			// system
			// line
			// separator
			// in a
			// new
			// string
			FileWriter fOut = new FileWriter(logFile, true);
			fOut.append(newLog);// writes the new string (with system line
			// separators) in the txt file
			fOut.flush();
			fOut.close();
		}

		// open dialog info box with warnings
		jobsDone = new JOptionPane();
		jobsDone.showMessageDialog(this, MainPanel.getWarnings(), "Info", JOptionPane.INFORMATION_MESSAGE);
		System.out.println(MainPanel.getLog());
		MainPanel.setWarnings("Jobs Done");

	}

}
