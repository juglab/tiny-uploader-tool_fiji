package sc.fiji.tut.ui.task;

import javax.swing.*;
import java.awt.*;

public class LoadFileCollection extends Task {

	public LoadFileCollection() {
		setTitle("Load file collection");
		doneBtn.setVisible(false);
	}

	@Override
	public Component ui() {
		return new JLabel("Wait until file collection is loaded.");
	}

}
