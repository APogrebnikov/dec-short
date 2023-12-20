package org.edec.logs.service;

import org.edec.logs.model.LogModel;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

public interface LogService {
    List<LogModel> getLogs (String fileName) throws FileNotFoundException;

    List<String> getLogFiles ();

    List<LogModel> searchLogs (List<LogModel> logs, LocalDate searchingDate, String searchingText);

    List<LogModel> getAllLogs () throws FileNotFoundException;
}
