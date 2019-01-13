package sc.fiji.tut.ui.task;

import javax.swing.*;
import java.awt.*;

public class Task {

	private boolean taskDone = false;

	private boolean inProgress = false;

	private String title;

	protected JButton doneBtn = new JButton();

	public boolean isDone() {
		return taskDone;
	}

	public void setDone() {
		setDone(true);
	}

	public void setDone(boolean finished) {
		if(inProgress) inProgress = false;
		this.taskDone = finished;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress() {
		setInProgress(true);
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public Component ui() {
		return new JLabel("no ui for this task");
	}

	public JButton doneBtn() {
		return doneBtn;
	}
}
