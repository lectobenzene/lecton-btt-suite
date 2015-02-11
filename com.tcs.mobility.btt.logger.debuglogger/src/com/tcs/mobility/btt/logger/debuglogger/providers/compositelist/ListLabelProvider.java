package com.tcs.mobility.btt.logger.debuglogger.providers.compositelist;

import org.eclipse.jface.viewers.LabelProvider;

import com.tcs.mobility.btt.logger.core.source.models.LogRecords;
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser;

public class ListLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof LogRecords) {
			if(CentralParser.REQUEST_RESPONSE_lOG_LEVEL.equalsIgnoreCase(((LogRecords) element).getLogLevel())){
				String command = ((LogRecords) element).getCommand();
				if(command.length() != 0){
					return ((LogRecords) element).getType() + " : " + ((LogRecords) element).getCommand();
				}else{
					return CentralParser.TYPE_REQUEST + " / " + CentralParser.TYPE_RESPONSE;
				}
			} else {
				return "Flow : " + ((LogRecords) element).getFlow();
			}
		}
		return "-----";
	}

}
