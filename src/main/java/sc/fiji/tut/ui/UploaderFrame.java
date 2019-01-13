package sc.fiji.tut.ui;

import sc.fiji.tut.ui.task.Restart;
import sc.fiji.tut.ui.task.Task;
import net.imagej.updater.util.Progress;
import sc.fiji.tut.status.UploaderStatus;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.List;

public class UploaderFrame extends JFrame implements Progress {

	private List<Task> tasks;
	private UploaderStatus status;
	private JPanel taskContent;
	private JList taskList;

	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

	public UploaderFrame(List<Task> tasks, UploaderStatus status) {
		super("Tiny Uploader Tool (TUT)");
		this.tasks = tasks;
		this.status = status;
		setContentPane(createContent());
		updateStatus();
	}

	public void updateStatus() {
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			if(task.isDone()) continue;
			else {
				task.setInProgress();
				SwingUtilities.invokeLater(() -> setTaskContent(task));
				return;
			}
		}
		status.write();
	}

	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void setTaskContent(Task task) {
		taskContent.removeAll();
		taskContent.add(task.ui(), "grow, push, span");
		taskContent.add(createTaskDoneBtn(task), "grow");
		taskContent.revalidate();
		taskList.updateUI();
	}

	private JButton createTaskDoneBtn(Task task) {
		JButton btn = task.doneBtn();
		btn.addActionListener(e -> new Thread(() -> {
			if(task.getClass().equals(Restart.class)) {
				close();
				status.write();
				return;
			}
			task.setDone();
			updateStatus();
		}).start());
		return btn;
	}

	private Container createContent() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		panel.setLayout(new MigLayout("gap 15px"));
		panel.add(createTaskList(), "aligny top");
		panel.add(createTaskContent(), "grow, push, span");
		return panel;
	}

	private Component createTaskContent() {
		taskContent = new JPanel();
		int width = 800;
		int height = 600;
		taskContent.setSize(width, height);
		taskContent.setPreferredSize(new Dimension(width, height));
		taskContent.setMinimumSize(new Dimension(width, height));
		taskContent.setMaximumSize(new Dimension(width, height));
		taskContent.setBackground(new Color(228,228,228));
		taskContent.setLayout(new MigLayout("flowy"));
		return taskContent;
	}

	private Component createTaskList() {
		taskList = new JList<>();
		taskList.setModel(new DefaultListModel<Task>() {
			@Override
			public int getSize() {
				return tasks.size();
			}
			@Override
			public Task getElementAt(int index) {
				return tasks.get(index);
			}
		});
		taskList.setCellRenderer(new DefaultListCellRenderer(){

			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus)
			{
				Task task = (Task) value;
				setText((task.isInProgress() ? "--> " :
						("[" + (task.isDone()? "x" : " ") +  "] "))
						+ task.getTitle());
				setFont(font);
				return this;
			}
		});

		return taskList;
	}

	@Override
	public void setCount(int count, int total) {

	}

	@Override
	public void addItem(Object item) {

	}

	@Override
	public void setItemCount(int count, int total) {

	}

	@Override
	public void itemDone(Object item) {

	}

	@Override
	public void done() {

	}

}
