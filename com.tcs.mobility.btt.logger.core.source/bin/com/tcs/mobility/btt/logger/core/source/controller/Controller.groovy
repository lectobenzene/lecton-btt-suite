package com.tcs.mobility.btt.logger.core.source.controller

import com.tcs.mobility.btt.logger.core.source.models.LogRecords
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser

import groovy.xml.XmlUtil
import org.xml.sax.SAXParseException

class Controller {

	public static void main(args){
		println 'Started...'
		Controller ct = new Controller()
		List logRecords = ct.getLogRecords('/Users/Saravana/Desktop/DEBUG-TFAL-20140806.log')

		//println logRecords.size()

		logRecords.each { LogRecords logRecord ->
			println logRecord.type
		}

		Node root = ct.parseFile('/Users/Saravana/Desktop/DEBUG-TFAL-20140806.log')
		def goal = ct.getLogRecord(root, 1)
		CentralParser parser = new CentralParser()


        println parser.getMessageFromLogRecord(goal)

        //println messageRoot.'jdf:header'.'jdf:command'.text();
        //println messageRoot.'app:data'[0].children()[0].name()

        //println XmlUtil.serialize(messageRoot.'app:data'[0].children()[0])
        //println parser.getRequestResponseFromMessage(goal)

        Node roopus = root.'log-record'[1]
        println parser.getFormattedSourceXML(roopus)
        int sequencer = 1;
        String sequencerString = sequencer+"";
        String sele = "$sequencer+''"

        roopus = root.'log-record'[2]
        println parser.getFormattedSourceXML(roopus)

        println sele
    }

	public List<LogRecords> getLogRecords(String logFilePath){
		println 'file path = '+logFilePath
		File logFile = new File(logFilePath)
		return getLogRecords(logFile)
	}

	public List<LogRecords> getLogRecords(File logFile){
		Node root = parseFile(logFile)
		return getLogRecords(root)
	}

	public List<LogRecords> getLogRecords(Node root) {
		List<LogRecords> logRecords = new ArrayList<LogRecords>()
        CentralParser parser = new CentralParser()
		LogRecords logRecord
		root.'log-record'.each { Node record ->
			logRecord = new LogRecords()
			logRecord.time = record.time.text()
			logRecord.userId = record.userId.text()
			logRecord.psp = record.psp.text()
			logRecord.flow = record.flow.text()
			logRecord.state = record.state.text()
			logRecord.wasReturnCode = record.wasReturnCode.text()
			logRecord.text = record.text.text()
			logRecord.message = record.message.text()
			logRecord.sequence = record.sequence.text()
            logRecord.logLevel = record.logLevel.text()
            println logRecord.sequence
            if(CentralParser.REQUEST_RESPONSE_lOG_LEVEL.equalsIgnoreCase(record.logLevel.text())){

                Node messageRoot
                String messageContent

                try{
                    messageContent = parser.getMessageFromLogRecord(record)
                    messageRoot = parseText(messageContent)
                    logRecord.command = messageRoot.'jdf:header'.'jdf:command'.text();

                    if((messageRoot.'app:data'[0]==null) || (messageRoot.'app:data'[0].children()[0] == null)||(messageRoot.'app:data'[0].children()[0].name().toString()).endsWith('Response')){
                        logRecord.type = CentralParser.TYPE_RESPONSE
                    }else{
                        logRecord.type = CentralParser.TYPE_REQUEST
                    }
                }catch(SAXParseException e){
                    logRecord.command = record.flow.text()
                    logRecord.type = CentralParser.TYPE_REQUEST
                }


            }
			logRecords.add(logRecord)
		}
		return logRecords
	}

	public Node parseFile(File file){
		Node root = new XmlParser().parseText("<ROOT>${file.text}</ROOT>")
		return root
	}

    public Node parseText(String text){
        Node root = new XmlParser().parseText(text)
        return root
    }

	public Node parseFile(String logFilePath){
		File logFile = new File(logFilePath)
		return parseFile(logFile)
	}

	public Node getLogRecord(Node root, int index){
		return root.'log-record'[index]
	}

    public Node getLogRecord(Node root, String sequence, String time){
        return root.'log-record'.find{ ((it.sequence.text() == sequence) && (it.time.text()==time) )}
    }
}
