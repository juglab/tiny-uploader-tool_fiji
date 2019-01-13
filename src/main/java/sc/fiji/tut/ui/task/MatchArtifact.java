package sc.fiji.tut.ui.task;

import net.imagej.updater.FileObject;
import net.imagej.updater.UpdateSite;
import sc.fiji.tut.status.UploaderStatus;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MatchArtifact extends Task implements ActionListener {

	private UploaderStatus status;

	private JList updateSiteFiles;

	public MatchArtifact(UploaderStatus status) {
		this.status = status;
		setTitle("Select Update Site");
		doneBtn.setText("Confirm");
		doneBtn.setEnabled(false);
	}

	@Override
	public Component ui() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("flowy"));
		panel.add(createSiteChoice(), "span");
		panel.add(new JScrollPane(createUpdateSiteList()), "grow, push, span");
		return panel;
	}

	private Component createUpdateSiteList() {
		updateSiteFiles = new JList<FileObject>();
		updateSiteFiles.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus)
			{
				FileObject file = (FileObject) value;
				setText(file.filename + " [" + file.getStatus() + "]");
				return this;
			}
		});
		return updateSiteFiles;
	}

	private Component createSiteChoice() {

		Collection<String> updateSites = status.getFiles().getUpdateSiteNames(false);
		updateSites.add("Please select an update site");
		JComboBox siteChoice = new JComboBox(updateSites.toArray());
		siteChoice.setSelectedIndex(siteChoice.getItemCount()-1);
		siteChoice.addActionListener(this);
		return siteChoice;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String siteName = (String)cb.getSelectedItem();
		new Thread(() -> {
			UpdateSite updateSite = status.getFiles().getUpdateSite(siteName, false);
			if(updateSite != null) {
				doneBtn.setEnabled(true);
				status.setUploadSite(updateSite);
				List<FileObject> files = getFiles(updateSite);
				updateSiteFiles.setModel(new DefaultListModel<FileObject>() {
					@Override
					public int getSize() {
						return files.size();
					}
					@Override
					public FileObject getElementAt(int index) {
						return files.get(index);
					}
				});
			}
		}).start();
	}

	private List getFiles(UpdateSite updateSite) {
		List<FileObject> list = new ArrayList<>();
		for(FileObject file : status.getFiles()) {
			if(file.updateSite != null && file.updateSite.equals(updateSite.getName())) {
				list.add(file);
			}
		}
		return list;
	}
}
