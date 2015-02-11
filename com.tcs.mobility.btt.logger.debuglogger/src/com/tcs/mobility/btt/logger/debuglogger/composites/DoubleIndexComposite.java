package com.tcs.mobility.btt.logger.debuglogger.composites;

import groovy.util.Node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.tcs.mobility.btt.logger.core.source.controller.Controller;
import com.tcs.mobility.btt.logger.core.source.models.LogRecords;
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser;
import com.tcs.mobility.btt.logger.debuglogger.views.DebugLoggerView;

public class DoubleIndexComposite extends Composite {
	private SashForm sashForm;
	private ListComposite cmpList;
	private SourceComposite cmpSource;
	private Controller controller;
	private CentralParser parser;
	private Node root;
	private Composite cmpSearch;
	private Text txtSearch;
	private List<LogRecords> logRecords;
	private int sequenceFirst;
	private Button btnSearch;
	private String primarySearchText;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DoubleIndexComposite(Composite parent, int style) {
		super(parent, style);

		setLayout(new GridLayout(1, false));

		controller = new Controller();
		parser = new CentralParser();

		cmpSearch = new Composite(this, SWT.NONE);
		cmpSearch.setLayout(new GridLayout(2, false));
		cmpSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		txtSearch = new Text(cmpSearch, SWT.BORDER);

		txtSearch.setToolTipText("search filter");
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		btnSearch = new Button(cmpSearch, SWT.NONE);

		// Used for search filtering
		final List<LogRecords> tempRecords = new ArrayList<LogRecords>();

		
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String secondarySearchText = txtSearch.getText();
				String textToSplit = txtSearch.getText();
				if(primarySearchText == null || (!secondarySearchText.startsWith(primarySearchText))){
					primarySearchText = txtSearch.getText();
					tempRecords.clear();
					tempRecords.addAll(logRecords);
				}else if(primarySearchText.equals(textToSplit)){
					return;
				}else{
					textToSplit = secondarySearchText.substring(primarySearchText.length()+1);
				}
				String[] searchTexts = textToSplit.split(";");
				
				for (String searchText : searchTexts) {
					int tempRecordsSize = tempRecords.size();
					for(int i=0, j=0; i< tempRecordsSize; i++, j++){
						LogRecords record = tempRecords.get(i);
						//int sequence = Integer.parseInt(((LogRecords) record).getSequence());
						Node logRecord = controller.getLogRecord(root,record.getSequence(),record.getTime());
						String xmlRecord = parser.getFormattedSourceXML(logRecord);
						if (!xmlRecord.contains(searchText)) {
							tempRecords.remove(record);
							tempRecordsSize--;
							i--;
						}
					}
				}
				primarySearchText = txtSearch.getText();
				cmpList.getListViewer().setInput(tempRecords);
				cmpList.getListViewer().refresh();
				cmpSource.setTxtLogRecordSource("");
			}
		});
		btnSearch.setText("Search");

		sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmpList = new ListComposite(sashForm, SWT.NONE);
		cmpSource = new SourceComposite(sashForm, SWT.NONE);

		sashForm.setWeights(new int[] { 1, 1 });

		cmpList.getListViewer().setInput(new ArrayList<LogRecords>());
		cmpList.getListViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object item = selection.getFirstElement();
				if (item instanceof LogRecords) {
					//int sequence = Integer.parseInt(((LogRecords) item).getSequence());
					int index = logRecords.indexOf(item);
					// Update the slider
					DebugLoggerView.setSequence(index);
					// Show message content in the source view
					Node logRecord = controller.getLogRecord(root, index);
					if (CentralParser.REQUEST_RESPONSE_lOG_LEVEL.equalsIgnoreCase(((LogRecords) item).getLogLevel())) {
						cmpSource.setTxtLogRecordSource(parser.getRequestResponseFromMessage(logRecord));
					} else {
						cmpSource.setTxtLogRecordSource(parser.getFormattedSourceXML(logRecord));
					}
				}
			}
		});

	}

	public void setInput(List<LogRecords> logRecords, Node root) {
		this.logRecords = logRecords;
		//this.sequenceFirst = Integer.parseInt(logRecords.get(0).getSequence());
		this.sequenceFirst = 0;
		cmpList.getListViewer().setInput(logRecords);
		cmpList.getListViewer().refresh();
		this.root = root;
	}

	public boolean isListEmpty() {
		return cmpList.isListEmpty();
	}

	public void emptyList() {
		cmpList.getList().removeAll();
	}
}
