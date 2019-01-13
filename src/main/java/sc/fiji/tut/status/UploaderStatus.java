package sc.fiji.tut.status;

import net.imagej.updater.Checksummer;
import net.imagej.updater.FilesCollection;
import net.imagej.updater.UpdateService;
import net.imagej.updater.UpdateSite;
import net.imagej.updater.util.Progress;
import net.imagej.updater.util.UpdateCanceledException;
import org.scijava.log.LogService;
import org.scijava.util.AppUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UploaderStatus implements Progress {

	private boolean testPhase = false;
	private UpdateSite uploadSite = null;
	private String file;
	private FilesCollection files;
	private LogService log;

	public void load(String file, UpdateService updateService, LogService log) {
		this.file = file;
		this.log = log;
		byte[] encoded = new byte[0];
		setTestPhase(false);
		setUploadSite(null);
		try {
			encoded = Files.readAllBytes(Paths.get(file));

		} catch (IOException e) {
			//no status present
			return;
		}
		String content = new String(encoded);
		setUploadSite(updateService.getUpdateSite(content));
		setTestPhase(true);
	}

	public void write() {
		String text = "";
		if(testPhase && !(uploadSite == null)) {
			text = uploadSite.getURL();
		}
		if(text != "") {
			try (PrintWriter out = new PrintWriter(file)) {
				out.println(text);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			if(new File(file).exists()) {
				new File(file).delete();
			}
		}
	}

	public boolean isTestPhase() {
		return testPhase;
	}

	public void setTestPhase(boolean testPhase) {
		this.testPhase = testPhase;
	}

	public UpdateSite getUploadSite() {
		return uploadSite;
	}

	public void setUploadSite(UpdateSite uploadSite) {
		this.uploadSite = uploadSite;
	}

	public FilesCollection getFiles() {
		return files;
	}

	public void setFiles(FilesCollection files) {
		this.files = files;
	}

	public void loadFilesCollection() {
		String imagejDirProperty = System.getProperty("imagej.dir");
		final File imagejRoot = imagejDirProperty != null ? new File(imagejDirProperty) :
				AppUtils.getBaseDirectory("ij.dir", FilesCollection.class, "updater");
		System.out.println("ImageJ root: " + imagejRoot.getAbsolutePath());
		setFiles(new FilesCollection(log, imagejRoot));
		try {
			getFiles().read();
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		final Checksummer checksummer = new Checksummer(getFiles(), this);
		try {
			checksummer.updateFromLocal();
		} catch (UpdateCanceledException t) {
		}
	}

	@Override
	public void setTitle(String s) {

	}

	@Override
	public void setCount(int i, int i1) {

	}

	@Override
	public void addItem(Object o) {

	}

	@Override
	public void setItemCount(int i, int i1) {

	}

	@Override
	public void itemDone(Object o) {

	}

	@Override
	public void done() {

	}
}
