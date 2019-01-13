/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package sc.fiji.tut.command;

import net.imagej.ImageJ;
import net.imagej.updater.UpdateService;
import net.imagej.updater.UploaderService;
import sc.fiji.tut.status.UploaderStatus;
import sc.fiji.tut.ui.UploaderFrame;
import org.scijava.Initializable;
import org.scijava.app.AppService;
import org.scijava.command.Command;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import sc.fiji.tut.ui.task.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = Command.class, menuPath = "Help>Tiny Uploader Tool (TUT)")
public class TinyUploaderTool implements Command, Initializable {

    @Parameter
    private UploaderService uploaderService;

    @Parameter
    private UpdateService updateService;

    @Parameter
    private UIService uiService;

    @Parameter
    private ThreadService threadService;

    @Parameter
    private EventService eventService;

    @Parameter
    private AppService appService;

    @Parameter
    private LogService log;

    private List<Task> tasks;

    private UploaderStatus status;

    private UploaderFrame frame;

    private static String settingsFile = ".uploader";

    private LoadFileCollection loadFileCollection;
    private MatchArtifact matchArtifact;
    private UpgradeArtifact upgradeArtifact;
    private TestUpgrade testUpgrade;
    private HandleUpload handleUpload;
    private Restart restart;

    @Override
    public void initialize() {
        loadStatus();
        createTasks();
        tasks = new ArrayList<>();
        tasks.add(loadFileCollection);
        tasks.add(matchArtifact);
        tasks.add(upgradeArtifact);
        tasks.add(restart);
        tasks.add(testUpgrade);
        tasks.add(handleUpload);
        if(status.isTestPhase()) {
            matchArtifact.setDone();
            upgradeArtifact.setDone();
            restart.setDone();
            run();
        }
    }

    private void createGui() {
        frame = new UploaderFrame(tasks, status);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createTasks() {
        matchArtifact = new MatchArtifact(status);
        upgradeArtifact = new UpgradeArtifact(status);
        testUpgrade = new TestUpgrade();
        handleUpload = new HandleUpload();
        loadFileCollection = new LoadFileCollection();
        restart = new Restart();
    }

    private void loadStatus() {
        status = new UploaderStatus();
        status.load(
                appService.getApp().getBaseDirectory().getAbsolutePath() + "/" + settingsFile,
                updateService, log);
    }

    @Override
    public void run() {
        createGui();
        status.loadFilesCollection();
        loadFileCollection.setDone();
        frame.updateStatus();
    }

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        // invoke the plugin
        ij.command().run(TinyUploaderTool.class, true);
    }

}
