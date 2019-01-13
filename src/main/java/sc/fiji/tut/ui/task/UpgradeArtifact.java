package sc.fiji.tut.ui.task;

import net.imagej.updater.FileObject;
import sc.fiji.tut.maven.MavenInstaller;
import sc.fiji.tut.status.UploaderStatus;
import sc.fiji.tut.status.WatchDir;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class UpgradeArtifact extends Task {

	private UploaderStatus status;

	private JList unchangedFiles;
	private JList changedFiles;

	public UpgradeArtifact(UploaderStatus status) {
		this.status = status;
		setTitle("Upgrade");
		doneBtn.setText("Try this upgrade");
		unchangedFiles = new JList<FileObject>();
		changedFiles = new JList<FileObject>();
	}

	private void initWatchService() {
		new Thread(() -> {
			Path dir = Paths.get(status.getFiles().prefix("").getAbsolutePath());
			try {
				WatchDir watchDir = new WatchDir(dir, true);
				watchDir.processEvents();
				watchDir.addContentChangedListener(e -> {
					updateCollection();
					updateLists();
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void setInProgress() {
		super.setInProgress();
		updateLists();
	}

	private void updateLists() {
		if(status.getUploadSite() != null) {
			unchangedFiles.setModel(getModel(getUnchangedFiles()));
			changedFiles.setModel(getModel(getChangedFiles()));
			doneBtn.setEnabled(changedFiles.getModel().getSize() > 0);
		}
	}

	private ListModel getModel(List list) {
		return new DefaultListModel() {
			@Override
			public int getSize() {
				return list.size();
			}
			@Override
			public Object getElementAt(int index) {
				return list.get(index);
			}
		};
	}

	@Override
	public Component ui() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout(""));
		panel.add(createMavenPanel(), "grow, push, span");
		panel.add(createUnchangedList(), "grow, push");
		panel.add(createChangedList(), "grow, push");
		updateLists();
		initWatchService();
		return panel;
	}

	private Component createMavenPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JLabel("Install maven repository: "));
		JTextArea mavenLog = new JTextArea();
		panel.add(createMavenImportBtn(new PrintStream(new CustomOutputStream(mavenLog))), "wrap");
		panel.add(new JScrollPane(mavenLog), "grow, push, span");
		return panel;
	}

	private Component createMavenImportBtn(PrintStream printStream) {
		final JFileChooser fc = new JFileChooser();
		fc.setApproveButtonText("Install");
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().equals("pom.xml");
			}
			@Override
			public String getDescription() {
				return "pom.xml";
			}
		});
		fc.setFileHidingEnabled(true);
		fc.setDialogTitle("Chose maven repository for installation");
		JButton btn = new JButton("Chose local pom.xml");
		btn.addActionListener(e -> {
			new Thread(() -> {
				int returnVal = fc.showOpenDialog(btn);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					MavenInstaller.install(status.getFiles().prefix(""), file.getParentFile(), printStream);
				}
			}).start();
		});
		return btn;
	}

	private void updateCollection() {
		status.loadFilesCollection();
	}

	private Component createUnchangedList() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("flowy"));
		panel.add(new JLabel("Unchanged files"));
		unchangedFiles.setCellRenderer(fileRenderer());
		panel.add(new JScrollPane(unchangedFiles), "grow, push, span");
		return panel;
	}

	private Component createChangedList() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("flowy"));
		panel.add(new JLabel("Changed / removed / new files"));
		changedFiles.setCellRenderer(fileRenderer());
		panel.add(new JScrollPane(changedFiles), "grow, push, span");
		return panel;
	}

	private ListCellRenderer fileRenderer() {
		return new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus)
			{
				FileObject file = (FileObject) value;
				setText(file.filename + " [" + (file.getStatus().toString() + "]"));
				return this;
			}
		};
	}

	private List getUnchangedFiles() {
		List<FileObject> list = new ArrayList<>();
		for(FileObject file : status.getFiles()) {
			if(file.updateSite != null && file.updateSite.equals(status.getUploadSite().getName())
					&& file.getStatus().equals(FileObject.Status.INSTALLED)) {
				list.add(file);
			}
		}
		return list;
	}

	private List getChangedFiles() {
		List<FileObject> list = new ArrayList<>();
		for(FileObject file : status.getFiles()) {
			if(file.getStatus().equals(FileObject.Status.LOCAL_ONLY)
				|| file.getStatus().equals(FileObject.Status.MODIFIED)
				|| file.getStatus().equals(FileObject.Status.OBSOLETE)) {
				list.add(file);
			}
		}
		System.out.println("changed files: " + list.size());
		return list;
	}

}
