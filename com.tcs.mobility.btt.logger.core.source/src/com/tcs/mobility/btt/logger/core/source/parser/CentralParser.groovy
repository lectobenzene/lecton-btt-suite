package com.tcs.mobility.btt.logger.core.source.parser

import groovy.xml.XmlUtil
import org.omg.CORBA.StringHolder
import org.xml.sax.SAXParseException

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class CentralParser {
	private StringWriter buffer
	private Transformer transformer
	private TransformerFactory transFactory

	public static final String REQUEST_RESPONSE_END_STRING = '</jdf:root>'
	public static final String REQUEST_RESPONSE_lOG_LEVEL = '1'
    public static final String TYPE_REQUEST = 'REQUEST'
    public static final String TYPE_RESPONSE = 'RESPONSE'

	public String getRawSourceXML(def logRecordNode){
		return XmlUtil.serialize(logRecordNode)
	}

	public String getReqestResponseXML(String message){
		int index = message.indexOf(REQUEST_RESPONSE_END_STRING)
        if (index != -1){
            message = message.substring(0, index+REQUEST_RESPONSE_END_STRING.length())
            Node nMessage = new XmlParser().parseText(message)
            return XmlUtil.serialize(nMessage)
        }  else{
            int startIndex = message.indexOf('|')
            if (startIndex != -1){
                return message.substring(0, startIndex-1);
            }  else {
                return message
            }

        }

	}

	public String getMessageFromLogRecord(def logRecordNode){
		def logLevel = logRecordNode.logLevel.text()
		def message = logRecordNode.message.text()
		if(REQUEST_RESPONSE_lOG_LEVEL.equalsIgnoreCase(logLevel)){
			return getReqestResponseXML(message)
		}else{
			return message
		}
	}

    public String getRequestResponseFromMessage(def logRecordNode){
        Node messageRoot
        String content
        try{
            content = getMessageFromLogRecord(logRecordNode)
            messageRoot = (new XmlParser().parseText(content))
            String output
            if (messageRoot.'app:data'[0]) {
                output = XmlUtil.serialize(messageRoot.'app:data'[0].children()[0])
            } else {
                output = XmlUtil.serialize(messageRoot)
            }
            // Removes the XML Declaration part
            if(output.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
                output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            }
            return output
        }catch(SAXParseException e){
            return content
        }

    }

	public String getFormattedSourceXML(def logRecordNode){
		String output = getRawSourceXML(logRecordNode)
		// Removes the XML Declaration part
		if(output.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
			output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		}
		return output.replaceAll(/<message>.*<\/message>/, "<message>"+getMessageFromLogRecord(logRecordNode)+"</message>")
	}
}
