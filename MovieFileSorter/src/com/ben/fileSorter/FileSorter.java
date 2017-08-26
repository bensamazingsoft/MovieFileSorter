package com.ben.fileSorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ben.fileSorter.ui.MainPanel;

/**
 * @author ben
 * @version 1.0
 */
public class FileSorter {

	/**
	 * add prefix to all files in directory
	 * 
	 * @param path2Sort
	 * @param text
	 * @param recusive
	 * @throws IOException
	 */
	public static void AddToName(Path path2Sort, String text, boolean recusive) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted AddToName with '" + "text" + "'");

		if (recusive) {
			Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.
				 * Object, java.nio.file.attribute.BasicFileAttributes)
				 */
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
					int ifExist = 1;

					String newPath = file.getParent().toFile().getAbsolutePath() + "/" + text + file.toFile().getName();

					if (!Files.exists(Paths.get(newPath), LinkOption.NOFOLLOW_LINKS)) {
						boolean success = file.toFile().renameTo(new File(
								file.getParent().toFile().getAbsolutePath() + "/" + text + file.toFile().getName()));

						if (success) {
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
						} else {
							MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
									+ file.toFile().getName() + ". Close all Explorer windows and retry.");
						}
					} else {
						boolean success = file.toFile().renameTo(new File(file.getParent().toFile().getAbsolutePath()
								+ "/" + text + ifExist + file.toFile().getName()));

						if (success) {
							ifExist++;
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
						} else {
							MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
									+ file.toFile().getName() + ". Close all Explorer windows and retry.");
						}

					}
					ifExist = 1;
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					MainPanel.setWarnings(MainPanel.getWarnings()
							+ "\nSomething went wrong. You may want to close all explorer windows and retry.");
					;
					return FileVisitResult.CONTINUE;
				}

			});
		}

		if (!recusive) {

			int ifExist = 1;
			DirectoryStream<Path> stream2Work = Files.newDirectoryStream(path2Sort);

			for (Path file : stream2Work) {

				if (file.toFile().isFile()) {
					String newPath = file.getParent().toFile().getAbsolutePath() + "/" + text + file.toFile().getName();
					if (!Files.exists(Paths.get(newPath), LinkOption.NOFOLLOW_LINKS)) {
						boolean success = file.toFile().renameTo(new File(
								file.getParent().toFile().getAbsolutePath() + "/" + text + file.toFile().getName()));

						if (success) {
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
						} else {
							MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
									+ file.toFile().getName() + ". Close all Explorer windows and retry.");
						}
					} else {
						boolean success = file.toFile().renameTo(new File(file.getParent().toFile().getAbsolutePath()
								+ "/" + text + ifExist + file.toFile().getName()));

						if (success) {
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
							ifExist++;
						} else {
							MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
									+ file.toFile().getName() + ". Close all Explorer windows and retry.");
						}

					}
					ifExist = 1;
				}
			}

		}
	}

	/**
	 * Count directories in a directory (including files in all subdirectories)
	 * 
	 * @param directory
	 *            the directory to start in
	 * @return the total number of directories
	 */
	public static int countDirInDirectory(File directory) {
		int count = 0;
		for (File file : directory.listFiles()) {

			if (file.isDirectory()) {
				count++;
				count += countDirInDirectory(file);
			}
		}
		return count;
	}

	/**
	 * Count files in a directory (including files in all subdirectories)
	 * 
	 * @param directory
	 *            the directory to start in
	 * @return the total number of files
	 */
	public static int countFilesInDirectory(File directory) {
		int count = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				count++;
			}
			if (file.isDirectory()) {
				count++;
				count += countFilesInDirectory(file);
			}
		}
		return count;
	}

	/**
	 * create (if necessary) a custom folder and move every file and folder
	 * which names contain the custom string
	 * 
	 * @param path2Sort
	 * @param text
	 * @param recursive
	 * @throws IOException
	 */
	public static void CustomFolderSort(Path path2Sort, String text, boolean recursive) throws IOException {

		String[] stringTab = text.split(",");

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted Custom Folder Sort. Custom folder : " + text);

		ArrayList<Path> file2Move = new ArrayList<Path>();

		// if the search is recusive, the array of file to move is filled with
		// all paths then folders and file without 'text' in filename are
		// removed
		if (recursive) {

			for (String subText : stringTab) {
				Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) {

						if (dir.toFile().getName().toLowerCase().contains(subText.toLowerCase())) {
							file2Move.add(dir);
						}
						return FileVisitResult.CONTINUE;
					}

					/**
					 * define what happens to files in the tree
					 * 
					 * @param file
					 * @param attr
					 * @return
					 */
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

						if (file.toFile().getName().toLowerCase().contains(subText.toLowerCase()) && !(file.getParent()
								.toFile().getAbsolutePath().toLowerCase().contains(subText.toLowerCase()))) {
							file2Move.add(file);
						}
						return FileVisitResult.CONTINUE;
					}

				});
			}

		}

		// if the search is not recursive, look for Path containing the Pattern
		// 'french' (case insensitive) in
		// the MainPanel-selected folder and fill an arrayList with the matches
		if (!(recursive)) {
			for (String subText : stringTab) {
				DirectoryStream<Path> stream2CustomSort = Files.newDirectoryStream(path2Sort);
				Pattern customPatt = Pattern.compile(subText, Pattern.CASE_INSENSITIVE);
				for (Path path : stream2CustomSort) {
					Matcher customMatcher = customPatt.matcher(path.toFile().getName());

					while (customMatcher.find()) {
						// System.out.println("Found : " +
						// path.toFile().getName());
						file2Move.add(path);
					}
				}
			}
		}

		// checks if "custom" folder exists, if not, creates it
		File customFolder;

		for (String subText : stringTab) {
			customFolder = new File(path2Sort.toFile().getAbsolutePath() + "/" + subText);
			if (!(customFolder.exists())) {
				customFolder.mkdirs();

			}
			// remove the 'custom' folder from the array file2Move and then
			// moves
			// all files in the array file2Move to the 'custom' folder
			file2Move.remove(Paths.get(customFolder.getAbsolutePath()));
			Path customFolderPath = Paths.get(customFolder.getAbsolutePath());
			for (Path path : file2Move) {
				Path thisPath = Paths.get(customFolderPath + "/" + path.toFile().getName());
				if (path.toFile().getName().toLowerCase().contains(subText.toLowerCase())) {
					Files.move(path, thisPath, StandardCopyOption.REPLACE_EXISTING);
					MainPanel.setLog(MainPanel.getLog() + "\n\t" + " moved : " + path.toFile().getName());
				}

			}
		}
	}

	/**
	 * deletes all empty folder of a Path
	 * 
	 * @param path2Sort
	 * @throws IOException
	 */
	public static void DelEmptyFolders(Path path2Sort) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted Delete Empty Folders");

		Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path file, IOException exc) throws IOException {

				try (DirectoryStream<Path> stream2Scan = Files.newDirectoryStream(file)) {
					if (!stream2Scan.iterator().hasNext()) {
						Files.delete(file);
						MainPanel.setLog(MainPanel.getLog() + "\n\tDeleted Empty Folder : " + file.toFile().getName());
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException e) {
				MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
						+ file.toFile().getAbsolutePath() + ". You may want to close all explorer windows and retry.");
				;
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * loops {@link FileSorter#DelEmptyFolders(Path)} until no empty folder
	 * remains
	 * 
	 * @see FileSorter#DelEmptyFolders
	 * @param path2Sort
	 * @throws IOException
	 */
	public static void DeleteAllEmptyFolders(Path path2Sort) throws IOException {
		int before = countDirInDirectory(path2Sort.toFile());
		int after = before - 1;

		while (before != after) {
			before = countDirInDirectory(path2Sort.toFile());
			FileSorter.DelEmptyFolders(path2Sort);
			after = countDirInDirectory(path2Sort.toFile());
		}

	}

	/**
	 * Deletes all 'Sample' folder and files
	 * 
	 * @param path2Sort
	 * @throws IOException
	 */
	public static void DelSamples(Path path2Sort) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted: Delete Samples");

		/**
		 * Inner class that implements SimpleFileVisitor to create the
		 * delSamples tree walker object
		 * 
		 * @author ben
		 *
		 */
		class DelSamplesFileVisitor extends SimpleFileVisitor<Path> {

			/**
			 * define what happens to folders in the tree
			 * 
			 * @param file
			 * @param attr
			 * @return
			 */
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

				if (dir.toFile().getName().toLowerCase().contains("sample")) {
					MainPanel.setLog(MainPanel.getLog() + "\n\tDeleted folder: " + dir.toFile().getName());
					dir.toFile().delete();
				}
				return FileVisitResult.CONTINUE;
			}

			/**
			 * define what happens to files in the tree
			 * 
			 * @param file
			 * @param attr
			 * @return
			 */
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

				if (file.toFile().getName().toLowerCase().contains("sample")) {
					MainPanel.setLog(MainPanel.getLog() + "\n\tDeleted file: " + file.toFile().getName());
					file.toFile().delete();
				}
				return FileVisitResult.CONTINUE;
			}

		}

		Files.walkFileTree(path2Sort, new DelSamplesFileVisitor());

	}

	/**
	 * replaces spaces (" ") with periods (".") in every files and folder
	 * 
	 * @param path2Sort
	 * @throws IOException
	 */
	public static void DelSpaces(Path path2Sort) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted: Replace spaces with periods");

		/**
		 * inner class that extends SimpleFileVisitor
		 * 
		 * @author ben
		 *
		 */
		class DelSpacesFileVisitor extends SimpleFileVisitor<Path> {

			/**
			 * Defines what happens to directories in the tree (post file visit)
			 * 
			 * @param dir
			 * @param exc
			 * @return
			 */
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

				if (dir.toFile().getAbsolutePath() != path2Sort.toFile().getAbsolutePath()) {
					System.out.println("Dossiers trouvé : " + dir.toFile().getName());
				}
				return FileVisitResult.CONTINUE;
			}

			/**
			 * define what happens to files in the tree
			 * 
			 * @param file
			 * @param attr
			 * @return
			 */
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

				String newName = file.getParent().toFile().getAbsolutePath() + "/"
						+ (file.toFile().getName()).replace(("\\s"), ".");
				File newFile = new File(newName);
				file.toFile().renameTo(new File(newName.trim()));
				MainPanel.setLog(MainPanel.getLog() + ("\n\tfichier renommé : " + newFile.getAbsolutePath()));
				System.out.println(file.toFile().getAbsolutePath());
				System.out.println(newName);
				System.out.println(path2Sort.toFile().getAbsolutePath());

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException e) {
				MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
						+ file.toFile().getAbsolutePath() + ". You may want to close all explorer windows and retry.");
				;
				return FileVisitResult.CONTINUE;
			}
		}
		// walk the file
		Files.walkFileTree(path2Sort, new DelSpacesFileVisitor());

	}

	/**
	 * create (if necessary) a 'french' folder and move all movies with "French"
	 * in the filename in this folder
	 * 
	 * @param path2Sort
	 * @param recursive
	 * @throws IOException
	 */
	public static void FrenchFolderSort(Path path2Sort, boolean recursive) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted French Folder Sort ");

		ArrayList<Path> file2Move = new ArrayList<Path>();

		// look for Path containing the Pattern 'french' (case insensitive) in
		// the MainPanel-selected folder and fill an arrayList with the matches

		Pattern frenchPatt = Pattern.compile("french", Pattern.CASE_INSENSITIVE);

		// if the search is recursive, it walks the entire folder and fill an
		// arraylist with files wich name contains 'french'
		if (recursive) {

			Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object,
				 * java.nio.file.attribute.BasicFileAttributes)
				 */
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

					if (file.toFile().getName().toLowerCase().contains("french")) {
						file2Move.add(file);
					}
					return FileVisitResult.CONTINUE;

				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					MainPanel.setWarnings(
							MainPanel.getWarnings() + "\nSomething went wrong with " + file.toFile().getAbsolutePath()
									+ ". You may want to close all explorer windows and retry.");
					;
					return FileVisitResult.CONTINUE;
				}

			});
		}

		// if the search is not recursive, a regex matcher is used on a
		// directorystream of the folder to search
		if (!(recursive)) {
			DirectoryStream<Path> stream2French = Files.newDirectoryStream(path2Sort);
			for (Path path : stream2French) {
				Matcher frenchMatcher = frenchPatt.matcher(path.toFile().getName());

				while (frenchMatcher.find()) {
					// System.out.println(path.toFile().getName());
					file2Move.add(path);
				}
			}
		}
		// checks if "french" folder exists, if not, creates it
		File frenchFolder = new File(path2Sort.toFile().getAbsolutePath() + "/french");

		if (!(frenchFolder.exists())) {
			frenchFolder.mkdirs();
		}

		// remove the 'french' folder from the array file2Move and then moves
		// all files in the array file2Move to the 'french' folder
		file2Move.remove(Paths.get(frenchFolder.getAbsolutePath()));
		Path frenchFolderPath = Paths.get(frenchFolder.getAbsolutePath());
		for (Path path : file2Move) {
			Path thisPath = Paths.get(frenchFolderPath + "/" + path.toFile().getName().trim());
			Files.move(path, thisPath, StandardCopyOption.REPLACE_EXISTING);
			MainPanel.setLog(MainPanel.getLog() + "\n\t" + " moved : " + path.toFile().getName());
		}

	}

	/**
	 * checks if path contains "s##e##'
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isTvShow(Path path) {

		String regex = "(s)(\\d)(\\d)?(e)(\\d)(\\d)?";
		Pattern tvShowPatt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher tvShowMatcher = tvShowPatt.matcher(path.toFile().getName());

		return tvShowMatcher.find();
	}

	/**
	 * moves all files containing the pattern s##e## in a 'TVShows' folder.
	 * ignores any files with 'TV' somewhere in its absolute path.
	 * 
	 * @param path2Sort
	 * @param recursive
	 * @throws IOException
	 */
	public static void MoveTVShows(Path path2Sort, boolean recursive) throws IOException {

		MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted MoveTVShows");

		ArrayList<Path> array2Move = new ArrayList<>();

		if (!recursive) {
			DirectoryStream<Path> stream2Sort = Files.newDirectoryStream(path2Sort);
			for (Path path : stream2Sort) {
				if (path.toFile().isFile() && isTvShow(path)) {
					array2Move.add(path);
				}
			}
		}

		if (recursive) {

			Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

					if (!file.toFile().getAbsolutePath().contains("TV-") && isTvShow(file)) {
						array2Move.add(file);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					MainPanel.setWarnings(MainPanel.getWarnings()
							+ "\nSomething went wrong. You may want to close all explorer windows and retry.");
					;
					return FileVisitResult.CONTINUE;
				}

			});

		}

		// creates (if necessary) the 'TVShows' folder
		File newFolder = new File(path2Sort.toFile().getAbsolutePath() + "/" + "TVShows");
		if (!newFolder.exists()) {
			newFolder.mkdirs();
		}

		// move files in the 'TVShows' folder
		for (Path file : array2Move) {
			Files.move(file, Paths.get(newFolder.getAbsolutePath() + "/" + file.toFile().getName()),
					StandardCopyOption.REPLACE_EXISTING);
			MainPanel.setLog(MainPanel.getLog() + "\n\tMoved file : " + file.toFile().getName());
		}
	}

	/**
	 * replaces 'this'with 'that' in filenames and folder names.
	 * 
	 * @param path2Sort
	 * @param that
	 * @param this
	 * @param recursive
	 * @param caseSensitive
	 * @throws IOException
	 */
	public static void ReplaceThisByThat(Path path2Sort, String thisTxt, String thatTxt, boolean recursive,
			boolean caseSensitive) throws IOException {

		int ifExist = 1;
		String[] thisTxtTab = thisTxt.split(",");

		for (String thisSubTxt : thisTxtTab) {

			int caseSens;
			caseSens = (caseSensitive) ? Pattern.LITERAL : Pattern.CASE_INSENSITIVE | Pattern.LITERAL;
			Pattern thisSubTxtPatt = Pattern.compile(thisSubTxt, caseSens);

			MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted Replace " + thisSubTxt + " by " + thatTxt);

			if (!recursive) {

				DirectoryStream<Path> stream2Sort = Files.newDirectoryStream(path2Sort);

				for (Path path : stream2Sort) {

					Matcher thisSubTxtMatcher = thisSubTxtPatt.matcher(path.toFile().getName());
					boolean found = thisSubTxtMatcher.find();

					String newPath = path.getParent().toFile().getAbsolutePath() + "/"
							+ thisSubTxtMatcher.replaceAll(thatTxt);

					if (found) {
						if (!Files.exists(Paths.get(newPath), LinkOption.NOFOLLOW_LINKS)) {
							path.toFile().renameTo(new File(path.getParent().toFile().getAbsolutePath() + "/"
									+ thisSubTxtMatcher.replaceAll(thatTxt).trim()));
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + path.toFile().getName());
						} else {
							path.toFile().renameTo(new File(path.getParent().toFile().getAbsolutePath() + "/" + "Twin "
									+ ifExist + thisSubTxtMatcher.replaceAll(thatTxt).trim()));
							MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + path.toFile().getName());
							ifExist++;
						}
					}
				}
			}

			ifExist = 1;
			if (recursive) {

				Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {
					int ifExist = 1;

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * java.nio.file.SimpleFileVisitor#postVisitDirectory(java.
					 * lang. Object, java.io.IOException)
					 */
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException e) {

						Matcher thisSubTxtMatcher = thisSubTxtPatt.matcher(dir.toFile().getName());
						boolean found = thisSubTxtMatcher.find();
						ifExist = 1;

						String newPath = dir.getParent().toFile().getAbsolutePath() + "/"
								+ thisSubTxtMatcher.replaceAll(thatTxt);

						if (found) {
							if (!Files.exists(Paths.get(newPath), LinkOption.NOFOLLOW_LINKS)) {
								boolean success = dir.toFile()
										.renameTo(new File(dir.getParent().toFile().getAbsolutePath() + "/"
												+ thisSubTxtMatcher.replaceAll(thatTxt).trim()));
								if (success) {
									MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + dir.toFile().getName());
								} else {
									MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
											+ dir.toFile().getName() + ". Close all Explorer windows and retry.");
								}
							} else {
								boolean success = dir.toFile()
										.renameTo(new File(dir.getParent().toFile().getAbsolutePath() + "/" + "Twin "
												+ ifExist + thisSubTxtMatcher.replaceAll(thatTxt).trim()));
								if (success) {
									MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + dir.toFile().getName());
									ifExist++;
								} else {
									MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
											+ dir.toFile().getName() + ". Close all Explorer windows and retry.");
								}
							}
						}
						return FileVisitResult.CONTINUE;
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.
					 * Object, java.nio.file.attribute.BasicFileAttributes)
					 */
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
						ifExist = 1;

						Matcher thisSubTxtMatcher = thisSubTxtPatt.matcher(file.toFile().getName());
						boolean found = thisSubTxtMatcher.find();
						String newPath = file.getParent().toFile().getAbsolutePath() + "/"
								+ thisSubTxtMatcher.replaceAll(thatTxt);

						if (found) {
							if (!Files.exists(Paths.get(newPath), LinkOption.NOFOLLOW_LINKS)) {
								boolean success = file.toFile()
										.renameTo(new File(file.getParent().toFile().getAbsolutePath() + "/"
												+ thisSubTxtMatcher.replaceAll(thatTxt).trim()));

								if (success) {
									MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
								} else {
									MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
											+ file.toFile().getName() + ". Close all Explorer windows and retry.");
								}
							} else {
								boolean success = file.toFile()
										.renameTo(new File(file.getParent().toFile().getAbsolutePath() + "/" + "Twin "
												+ ifExist + thisSubTxtMatcher.replaceAll(thatTxt).trim()));
								if (success) {
									MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
								} else {
									MainPanel.setWarnings(MainPanel.getWarnings() + "\nSomething went wrong with "
											+ file.toFile().getName() + ". Close all Explorer windows and retry.");
								}
								ifExist++;

							}
						}
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException e) {
						MainPanel.setWarnings(MainPanel.getWarnings()
								+ "\nSomething went wrong. You may want to close all explorer windows and retry.");
						;
						return FileVisitResult.CONTINUE;
					}

				});

			}
		}
	}

	/**
	 * rename files and/or folders to all-lowercase
	 * 
	 * @param path2Sort
	 * @param doFile
	 * @param doFolder
	 * @throws IOException
	 */
	public static void toLower(Path path2Sort, boolean doFile, boolean doFolder) throws IOException {

		if (doFile) {
			MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted ToLowerCase on Files");
		}

		if (doFolder) {
			MainPanel.setLog(MainPanel.getLog() + "\n\nExecuted ToLowerCase on Folders");
		}

		Files.walkFileTree(path2Sort, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException e) {

				boolean Success = true;

				if (doFolder) {
					Success = dir.toFile().renameTo(new File(
							dir.getParent().toFile().getAbsolutePath() + "/" + dir.toFile().getName().toLowerCase()));
				}
				if (!Success) {
					MainPanel.setWarnings(MainPanel.getWarnings()
							+ "\nSomething went wrong. You may want to close all explorer windows and retry.");
				} else {
					MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + dir.toFile().getName());
				}
				return FileVisitResult.CONTINUE;

			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

				boolean Success = true;

				if (doFile) {
					Success = file.toFile().renameTo(new File(
							file.getParent().toFile().getAbsolutePath() + "/" + file.toFile().getName().toLowerCase()));
				}
				if (!Success) {
					MainPanel.setWarnings(MainPanel.getWarnings()
							+ "\nSomething went wrong. You may want to close all explorer windows and retry.");
				} else {
					MainPanel.setLog(MainPanel.getLog() + "\n\tRenamed : " + file.toFile().getName());
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException e) {

				MainPanel.setWarnings(MainPanel.getWarnings()
						+ "\nSomething went wrong. You may want to close all explorer windows and retry.");

				return FileVisitResult.CONTINUE;
			}

		});

	}

	/**
	 * unused constructor since all methods are static
	 */
	public FileSorter() {
	}
}
