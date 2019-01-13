package sc.fiji.tut.ui.task;

import javax.swing.*;
import java.awt.*;

public class Restart extends Task {

	public Restart() {
		setTitle("Restart");
		doneBtn.setText("Close");
	}

	@Override
	public Component ui() {
		return new JLabel("Please restart ImageJ to test and upload update site upgrade.");
	}

}
