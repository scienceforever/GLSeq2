package org.glbrc.glseq2;

///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  GLSeq2_Main_Application.java
// File:             ScriptTask.java
//
//
// Author:           Michael Lampe | MrLampe@Wisc.edu
///////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.SwingWorker;

// SwingWorker allows for parallel execution
public final class ScriptTask extends SwingWorker<List<Integer>, Integer> {

	@Override
	protected List<Integer> doInBackground() throws IOException {
		startScript();
		return null;
	}

	private void startScript() {
		/*
		 * Saves both the attribute and run config file. This is just pulling
		 * data from the correct sources to get a good file path for the saved
		 * files.
		 */
		try {
			GLSeq2_Main_Application.updating("Saving current inputs");
			GLSeq2_Main_Application.att
					.saveConfigFile(GLSeq2_Main_Application.run
							.getDestinationDirectory(GLSeq2_Main_Application.att
									.getDestinationDirectory())
							+ "/AttributesConfigFile_"
							+ GLSeq2_Main_Application.att.runId + ".txt");
			GLSeq2_Main_Application.run
					.saveConfigFile(GLSeq2_Main_Application.run
							.getDestinationDirectory(GLSeq2_Main_Application.att
									.getDestinationDirectory())
							+ "/RunConfigFile_"
							+ GLSeq2_Main_Application.att.runId + ".txt");
		} catch (IOException e) {
			GLSeq2_Main_Application
					.updating("Problem saving config files.  Files unable to be saved or written to.");
		}
		// Indicate to user that the script has started
		GLSeq2_Main_Application.updating("Started GLSeq.top.R script. (Top Script which runs your input settings)");
		List<String> args = GLSeq2_Main_Application.run.returnArgs();
		// Script generated based on arguments from the RunOptions class.
		// It calls the R script with the correct user parameters
		ProcessBuilder script = new ProcessBuilder(args);
		script.directory(new File(GLSeq2_Main_Application.run
				.getScriptDirectory(GLSeq2_Main_Application.att
						.getScriptDirectory())));
		Process process = null;
		try {
			process = script.start();
			final InputStream is = process.getInputStream();
			final InputStream es = process.getErrorStream();
			new Thread(new Runnable() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is));
						String line;
						while ((line = reader.readLine()) != null) {
							GLSeq2_Main_Application.updating("Program: " + line);
						}
						reader = new BufferedReader(new InputStreamReader(es));
						while ((line = reader.readLine()) != null) {
							GLSeq2_Main_Application.updating("Program: " + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			// Error message if the script is incorrectly run. Did not select
			// directory or options are easy reasons for this error.
		} catch (IOException e) {
			GLSeq2_Main_Application
					.updating("Sorry, there was a problem running the script.  Please check that all the options have been correctly selected.");
			GLSeq2_Main_Application.updating(String.valueOf(e));
		}
		try {
			// Script is done when it says done
			process.waitFor();
			GLSeq2_Main_Application.updating("Process complete.");
		} catch (InterruptedException e) {
			GLSeq2_Main_Application.updating("Error running the script. Script interrupted.");
		}
	}
}