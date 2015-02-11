package com.tcs.mobility.btt.logger.debuglogger.views;

import groovy.util.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.tcs.mobility.btt.logger.core.source.controller.Controller;
import com.tcs.mobility.btt.logger.core.source.models.LogRecords;
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser;
import com.tcs.mobility.btt.logger.debuglogger.Activator;
import com.tcs.mobility.btt.logger.debuglogger.composites.DoubleIndexComposite;
import com.tcs.mobility.btt.logger.debuglogger.composites.SourceComposite;
import com.tcs.mobility.btt.logger.debuglogger.composites.StepComposite;
import com.tcs.mobility.btt.logger.debuglogger.preferences.BTTLoggerPreferences;
import com.tcs.mobility.btt.logger.debuglogger.preferences.PreferenceConstants;

public class DebugLoggerView extends ViewPart {
	public DebugLoggerView() {
	}

	private static List<LogRecords> logRecords;
	private static Controller controller;
	private static CentralParser parser;
	private static Node root;

	private static int sequence = 0;
	private static int sequenceFirst;
	private static int sequenceLast;

	private Composite cmpFilepath;
	private Composite cmpDynamic;
	private Composite cmpTraversal;

	private SourceComposite cmpSource;
	private StepComposite cmpStep;
	private DoubleIndexComposite cmpDoubleIndex;

	private Label lblDebugFile;
	private Text txtFileLocation;
	private Button btnBrowse;
	private StackLayout layout;
	private static Slider sliderSequence;

	private Action nextItemAction;
	private Action previousItemAction;
	private Action separatorAction;
	private Action sourceViewAction;
	private Action lastItemAction;
	private Action firstItemAction;
	private Action stepViewAction;
	private Action listViewAction;
	private Action updateAction;
	private Action separatorAction1;
	private IPreferenceStore preferenceStore;

	@Override
	public void createPartControl(final Composite parent) {

		preferenceStore = Activator.getDefault().getPreferenceStore();

		createActions();
		createToolbar();
		parent.setLayout(new GridLayout(1, false));

		cmpFilepath = new Composite(parent, SWT.NONE);
		cmpFilepath.setLayout(new GridLayout(3, false));
		cmpFilepath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		lblDebugFile = new Label(cmpFilepath, SWT.NONE);
		lblDebugFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDebugFile.setText("Debug File :");

		txtFileLocation = new Text(cmpFilepath, SWT.BORDER);
		txtFileLocation.setEditable(false);
		txtFileLocation.setToolTipText("file to debug");
		txtFileLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnBrowse = new Button(cmpFilepath, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setText("Open log file");
				String[] filterExt = { "*.log", "*.txt", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				txtFileLocation.setText(selected);
				File file = new File(txtFileLocation.getText());
				initiateLogger(file);
			}
		});
		btnBrowse.setText("Browse...");

		cmpTraversal = new Composite(parent, SWT.NONE);
		cmpTraversal.setLayout(new GridLayout(1, false));
		cmpTraversal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		sliderSequence = new Slider(cmpTraversal, SWT.NONE);
		sliderSequence.setThumb(1);
		sliderSequence.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sliderSequence.setEnabled(false);

		cmpDynamic = new Composite(parent, SWT.NONE);
		layout = new StackLayout();
		cmpDynamic.setLayout(layout);
		cmpDynamic.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpSource = new SourceComposite(cmpDynamic, SWT.NONE);
		cmpSource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpStep = new StepComposite(cmpDynamic, SWT.NONE);
		cmpStep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpDoubleIndex = new DoubleIndexComposite(cmpDynamic, SWT.NONE);
		cmpDoubleIndex.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		layout.topControl = cmpSource;
		cmpDynamic.layout();
	}

	protected void initiateLogger(File file) {
		// Initialize the existing values

		controller = new Controller();
		parser = new CentralParser();

		RandomAccessFile randomFile;
		String fileContent = null;
		try {
			randomFile = new RandomAccessFile(file, "r");
			int sizePreference = preferenceStore.getInt(PreferenceConstants.P_SIZE);
			long seekLength;
			if (sizePreference != 0) {
				seekLength = sizePreference;
			} else {
				seekLength = 2097152;
			}
			if (randomFile.length() < seekLength) {
				seekLength = randomFile.length();
			}
			randomFile.seek(randomFile.length() - seekLength);
			byte[] b = new byte[(int) seekLength];
			randomFile.read(b);
			String contents = new String(b);
			fileContent = contents.substring(contents.indexOf("<log-record>"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fileContent != null) {
			fileContent = fileContent.replaceAll("<>", "<![CDATA[<>]]>");
			fileContent = "<ROOT>" + fileContent + "</ROOT>";
			root = controller.parseText(fileContent);
			logRecords = controller.getLogRecords(root);

			// Initialize the First and Last sequence
//			sequenceFirst = Integer.parseInt(logRecords.get(0).getSequence());
			sequenceFirst = 0;
//			sequenceLast = Integer.parseInt(logRecords.get(logRecords.size() - 1).getSequence());
			sequenceLast = logRecords.size()-1;

			setSequence(sequenceFirst);

			initializeViews();

			// Empty the search list
			cmpDoubleIndex.emptyList();

			sliderSequence.setEnabled(true);
			sliderSequence.setMaximum(sequenceLast);
			sliderSequence.setMinimum(sequenceFirst);

			nextItemAction.setEnabled(true);
			previousItemAction.setEnabled(true);
			firstItemAction.setEnabled(true);
			lastItemAction.setEnabled(true);

			sourceViewAction.setEnabled(true);
			stepViewAction.setEnabled(true);
			listViewAction.setEnabled(true);

			sourceViewAction.setChecked(true);
		}
	}

	private void initializeViews() {
		// Open the Source View by default
		openSourceView();
		setListeners();
	}

	private void setListeners() {
		sliderSequence.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				sequence = Integer.parseInt(logRecords.get(sliderSequence.getSelection() - sequenceFirst).getSequence());
				sequence = sliderSequence.getSelection();
				traverseInSourceView();
				traverseInStepView();
			}
		});
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(updateAction);
		mgr.add(separatorAction1);

		mgr.add(sourceViewAction);
		mgr.add(stepViewAction);
		mgr.add(listViewAction);
		mgr.add(separatorAction);

		mgr.add(firstItemAction);
		mgr.add(previousItemAction);
		mgr.add(nextItemAction);
		mgr.add(lastItemAction);
	}

	private void createActions() {
		firstItemAction = new Action("First") {
			public void run() {
				goToFirst();
			}
		};
		firstItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/checkout_action.gif"));
		firstItemAction.setToolTipText("First log record");

		lastItemAction = new Action("Last") {
			public void run() {
				goToLast();
			}
		};
		lastItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/checkin_action.gif"));
		lastItemAction.setToolTipText("Last log record");

		nextItemAction = new Action("Next") {
			public void run() {
				next();
			}
		};
		nextItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/nav_forward.gif"));
		nextItemAction.setToolTipText("Next Log");

		previousItemAction = new Action("Previous") {
			public void run() {
				previous();
			}
		};
		previousItemAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/nav_backward.gif"));
		previousItemAction.setToolTipText("Previous Log");

		separatorAction = new Action("") {
		};
		separatorAction.setEnabled(false);

		separatorAction1 = new Action("") {
		};
		separatorAction1.setEnabled(false);

		sourceViewAction = new Action("Source") {
			public void run() {
				openSourceView();
			}
		};
		sourceViewAction.setChecked(false);
		sourceViewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/source.gif"));
		sourceViewAction.setToolTipText("Source View");

		stepViewAction = new Action("Step") {
			public void run() {
				openStepView();
			}
		};
		stepViewAction.setChecked(false);
		stepViewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/showcat_co.gif"));
		stepViewAction.setToolTipText("Step View");

		listViewAction = new Action("List") {
			public void run() {
				openListView();
			}
		};
		listViewAction.setChecked(false);
		listViewAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/site_element.gif"));
		listViewAction.setToolTipText("List View");

		updateAction = new Action("Update") {
			public void run() {
				updateInput();
			}
		};
		updateAction.setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/update.gif"));
		updateAction.setToolTipText("Update Input");

		nextItemAction.setEnabled(false);
		previousItemAction.setEnabled(false);
		firstItemAction.setEnabled(false);
		lastItemAction.setEnabled(false);

		sourceViewAction.setEnabled(false);
		stepViewAction.setEnabled(false);
		listViewAction.setEnabled(false);
	}

	protected void updateInput() {
		String directoryPreference = preferenceStore.getString(PreferenceConstants.P_DIRECTORY);
		if ("".equalsIgnoreCase(directoryPreference)) {
			showPreferenceDialog();
		}
		// Check once again if the user has filled in the Directory
		directoryPreference = preferenceStore.getString(PreferenceConstants.P_DIRECTORY);
		// If a Directory path exists, then proceed.
		if (directoryPreference != null && !"".equalsIgnoreCase(directoryPreference)) {
			File directory = new File(directoryPreference);
			if(directory.exists()){
				File[] listOfFiles = directory.listFiles();
				List<File> logFiles = new ArrayList<File>();
				for (File file : listOfFiles) {
					if (file.getName().endsWith(".log")) {
						logFiles.add(file);
					}
				}
				sortFiles(logFiles);
				File logFile = getLastModifiedLogFile(logFiles);
				if(logFile != null){
					txtFileLocation.setText(logFile.getPath());
					initiateLogger(logFile);
				}
			}else{
				MessageBox alertDialog = new MessageBox(cmpDynamic.getShell(), SWT.ICON_ERROR | SWT.OK);
				alertDialog.setMessage("Directory doesn't exist");
				alertDialog.setText("Error");
				alertDialog.open();
			}
		}
	}

	private File getLastModifiedLogFile(List<File> logFiles) {
		RandomAccessFile randomFile = null;
		for (int index = logFiles.size() - 1; index >= 0; index--) {
			File file = logFiles.get(index);
			String fileContents = null;
			try {
				randomFile = new RandomAccessFile(file, "r");
				byte[] bytes = new byte[50];
				randomFile.read(bytes);
				fileContents = new String(bytes);
				if (fileContents.startsWith("<log-record>")) {
					return file;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					randomFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return null;
	}

	private void sortFiles(List<File> logFiles) {
		Collections.sort(logFiles, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
	}

	private void showPreferenceDialog() {
		MessageBox alertDialog = new MessageBox(cmpDynamic.getShell(), SWT.ICON_WARNING | SWT.OK);
		alertDialog.setMessage("Set Directory in Preferences Dialog");
		alertDialog.setText("Warning");
		int open = alertDialog.open();
		if (open == SWT.OK) {
			PreferencePage page = new BTTLoggerPreferences();
			page.setTitle("BTT Logger");
			PreferenceManager mgr = new PreferenceManager();
			IPreferenceNode node = new PreferenceNode("1", page);
			mgr.addToRoot(node);
			PreferenceDialog dialog = new PreferenceDialog(cmpDynamic.getShell(), mgr);
			dialog.create();
			dialog.setMessage(page.getTitle());
			dialog.open();
		}
	}

	protected void goToLast() {
		setSequence(sequenceLast);
		traverseInSourceView();
		traverseInStepView();
	}

	private void addItemsToListView() {
		cmpDoubleIndex.setInput(logRecords, root);
	}

	/**
	 * Actions to perform in Step View during traversal
	 */
	private void traverseInStepView() {
		LogRecords logRecord = logRecords.get(sequence - sequenceFirst);
		cmpStep.setLblLblsequence(logRecord.getSequence());
		cmpStep.setLblLbltime(logRecord.getTime());
		cmpStep.setLblLblflow(logRecord.getFlow());
		cmpStep.setLblLblstate(logRecord.getState());
		cmpStep.setLblLblmessage(logRecord.getMessage());
		cmpStep.setLblLblwascode(logRecord.getWasReturnCode());
		cmpStep.setLblLbltext(logRecord.getText());
	}

	/**
	 * Actions to perform in Source View during traversal
	 */
	private void traverseInSourceView() {
		Node logRecord = controller.getLogRecord(root, sequence);
		cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
	}

	protected void goToFirst() {
		setSequence(sequenceFirst);
		traverseInSourceView();
		traverseInStepView();
	}

	protected void openListView() {
		sourceViewAction.setChecked(false);
		stepViewAction.setChecked(false);
		listViewAction.setChecked(true);
		layout.topControl = cmpDoubleIndex;
		cmpDynamic.layout();
		if (cmpDoubleIndex.isListEmpty()) {
			addItemsToListView();
		}
	}

	protected void openStepView() {
		sourceViewAction.setChecked(false);
		listViewAction.setChecked(false);
		stepViewAction.setChecked(true);
		layout.topControl = cmpStep;
		cmpDynamic.layout();
		traverseInStepView();
	}

	protected void openSourceView() {
		stepViewAction.setChecked(false);
		listViewAction.setChecked(false);
		sourceViewAction.setChecked(true);
		layout.topControl = cmpSource;
		cmpDynamic.layout();
		traverseInSourceView();
	}

	protected void next() {
		setSequence(++sequence);
		previousItemAction.setEnabled(true);
		if (sequence > sequenceLast) {
			setSequence(--sequence);
			nextItemAction.setEnabled(false);
		} else {
			traverseInSourceView();
			traverseInStepView();
		}
	}

	protected void previous() {
		setSequence(--sequence);
		nextItemAction.setEnabled(true);
		if (sequence < sequenceFirst) {
			setSequence(++sequence);
			previousItemAction.setEnabled(false);
		} else {
			traverseInSourceView();
			traverseInStepView();
		}
	}

	@Override
	public void setFocus() {
		btnBrowse.setFocus();
	}

	public static void setSequence(int sequence) {
		DebugLoggerView.sequence = sequence;
		sliderSequence.setSelection(sequence);
	}

}
