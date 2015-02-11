package com.tcs.mobility.btt.logger.debuglogger.composites;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import com.tcs.mobility.btt.logger.debuglogger.providers.compositelist.ListContentProvider;
import com.tcs.mobility.btt.logger.debuglogger.providers.compositelist.ListLabelProvider;

public class ListComposite extends Composite {
	private List list;
	private ListViewer listViewer;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ListComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		list = listViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listViewer.setContentProvider(new ListContentProvider());
		listViewer.setLabelProvider(new ListLabelProvider());
	}

	public List getList() {
		return list;
	}

	public ListViewer getListViewer() {
		return listViewer;
	}

	public void addItem(String item) {
		list.add(item);
	}

	public boolean isListEmpty() {
		return list.getItemCount() == 0 ? true : false;
	}
}
