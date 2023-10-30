package com.example.rfid_inventorysystem.Service;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerService {
    protected static final Logger logger=Logger.getLogger("MYLOG");
    public static void log(Exception ex, String level, String msg){
        FileHandler fh = null;
        try{
            fh = new FileHandler("log.xml",true);
            logger.addHandler(fh);
            StackTraceElement[] stackTrace = ex.getStackTrace();
            String methodName = stackTrace[0].getMethodName();
            String fullMsg = msg + " (Exception in method: " + methodName + ")";
            switch (level) {
                case "severe" -> logger.log(Level.SEVERE, fullMsg, ex);
                case "warning" -> logger.log(Level.WARNING, fullMsg, ex);
                case "info" -> logger.log(Level.INFO, fullMsg, ex);
                case "config" -> logger.log(Level.CONFIG, fullMsg, ex);
                case "fine" -> logger.log(Level.FINE, fullMsg, ex);
                case "finer" -> logger.log(Level.FINER, fullMsg, ex);
                case "finest" -> logger.log(Level.FINEST, fullMsg, ex);
                default -> logger.log(Level.ALL, fullMsg, ex);
            }
        } catch (IOException | SecurityException ex1){
            logger.log(Level.SEVERE, null, ex1);
        } finally {
            if(fh!=null)fh.close();
        }
    }
}
