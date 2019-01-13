package sc.fiji.tut.maven;

import org.apache.maven.cli.MavenCli;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class MavenInstaller {

	public static void install(File ijRoot, File pom, PrintStream out) {
		System.out.println("Maven install " + pom.getAbsolutePath() + " to " + ijRoot.getAbsolutePath());
		MavenCli maven = new MavenCli();
		System.setProperty("maven.multiModuleProjectDirectory", pom.getAbsolutePath());
		maven.doMain(new String[]{
				"-Dimagej.app.directory="+ijRoot.getAbsolutePath(),
				"-Ddelete.other.versions=true",
				"-DskipTests", "install"},
				pom.getAbsolutePath(), out, out);
	}

	public static void main(final String... args) {
		MavenCli maven = new MavenCli();
		System.setProperty("maven.multiModuleProjectDirectory", "/home/random/Development/imagej/plugins/CSBDeep_fiji/");
		maven.doMain(new String[]{
						"-DskipTests", "install"},
				"/home/random/Development/imagej/plugins/CSBDeep_fiji/",
				System.out, System.out);
	}
}
