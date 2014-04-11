package replicaA;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger class handles the log file
 * @author Mehrdad Dehdashti
 */
class Log
{
	protected static Logger createLog(String pFileName)
	{
		Logger log = Logger.getLogger(pFileName);
		FileHandler aFileHandler = null;
		try 
		{
			aFileHandler = new FileHandler(pFileName+".log");
			log.addHandler(aFileHandler);
			SimpleFormatter aFormatter = new SimpleFormatter();
			aFileHandler.setFormatter(aFormatter);
			log.info("Log File " + pFileName + " Created");
		} catch (IOException e) {
			log.info("IOException:" + e.getMessage());
		} catch (SecurityException e) {
			log.info("SecurityException: " + e.getMessage());
		}
		return log;
	}
}