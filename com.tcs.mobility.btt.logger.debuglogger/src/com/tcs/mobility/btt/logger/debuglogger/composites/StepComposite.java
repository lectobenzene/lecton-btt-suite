package com.tcs.mobility.btt.logger.debuglogger.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StepComposite extends Composite {
	private Composite cmpCommon;
	private Composite cmpInfo;

	private Label lblLbltime;
	private Label lblLblsequence;
	private Label lblLblflow;
	private Label lblLblstate;
	private Label lblLblmessage;
	private Label lblLblwascode;
	private Label lblLbltext;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StepComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		cmpCommon = new Composite(this, SWT.NONE);
		cmpCommon.setLayout(new GridLayout(2, false));
		cmpCommon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblLbltime = new Label(cmpCommon, SWT.NONE);
		GridData gd_lblLbltime = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblLbltime.widthHint = 103;
		lblLbltime.setLayoutData(gd_lblLbltime);
		lblLbltime.setText("lblTime");

		lblLblsequence = new Label(cmpCommon, SWT.NONE);
		lblLblsequence.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblLblsequence.setText("lblSequence");

		cmpInfo = new Composite(this, SWT.NONE);
		GridLayout gl_cmpInfo = new GridLayout(2, false);
		gl_cmpInfo.horizontalSpacing = 15;
		cmpInfo.setLayout(gl_cmpInfo);
		cmpInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblFlow = new Label(cmpInfo, SWT.NONE);
		lblFlow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblFlow.setText("Flow :");

		lblLblflow = new Label(cmpInfo, SWT.WRAP);
		lblLblflow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLblflow.setText("lblFlow");

		Label lblState = new Label(cmpInfo, SWT.NONE);
		lblState.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblState.setText("State :");

		lblLblstate = new Label(cmpInfo, SWT.WRAP);
		lblLblstate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLblstate.setText("lblState");
		new Label(cmpInfo, SWT.NONE);
		new Label(cmpInfo, SWT.NONE);

		Label lblMessage = new Label(cmpInfo, SWT.NONE);
		lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblMessage.setText("Message :");

		lblLblmessage = new Label(cmpInfo, SWT.WRAP);
		lblLblmessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLblmessage.setText("lblMessage");
		new Label(cmpInfo, SWT.NONE);
		new Label(cmpInfo, SWT.NONE);

		Label lblWasCode = new Label(cmpInfo, SWT.NONE);
		lblWasCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblWasCode.setText("WAS Code :");

		lblLblwascode = new Label(cmpInfo, SWT.WRAP);
		lblLblwascode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLblwascode.setText("lblWasCode");

		Label lblText = new Label(cmpInfo, SWT.NONE);
		lblText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblText.setText("Text :");

		lblLbltext = new Label(cmpInfo, SWT.WRAP);
		lblLbltext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLbltext.setText("lblText");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setLblLbltime(String lblLbltime) {
		this.lblLbltime.setText(lblLbltime);
	}

	public void setLblLblsequence(String lblLblsequence) {
		this.lblLblsequence.setText(lblLblsequence);
	}

	public void setLblLblflow(String lblLblflow) {
		this.lblLblflow.setText(lblLblflow);
	}

	public void setLblLblstate(String lblLblstate) {
		this.lblLblstate.setText(lblLblstate);
	}

	public void setLblLblmessage(String lblLblmessage) {
		this.lblLblmessage.setText(lblLblmessage);
	}

	public void setLblLblwascode(String lblLblwascode) {
		this.lblLblwascode.setText(lblLblwascode);
	}

	public void setLblLbltext(String lblLbltext) {
		this.lblLbltext.setText(lblLbltext);
	}
}
