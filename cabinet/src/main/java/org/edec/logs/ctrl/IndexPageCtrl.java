package org.edec.logs.ctrl;

import org.edec.logs.ctrl.renderer.LogListRenderer;
import org.edec.logs.model.LogModel;
import org.edec.logs.service.LogService;
import org.edec.logs.service.impl.LogServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Radio rAllFiles, rOneFile;
    @Wire
    private Combobox cmbLogsFileNames;
    @Wire
    private Textbox tbLogsDate, tbLogsText;
    @Wire
    private Listbox lbLogs;
    @Wire
    private Button btnLogsSearch;

    private List<LogModel> logList = new ArrayList<>();

    private ListModelList<LogModel> logModels;

    private LogService logService = new LogServiceImpl();

    private List<String> logFiles;

    @Override
    protected void fill() {
        logFiles = logService.getLogFiles();
        for (String logFile : logFiles) {
            cmbLogsFileNames.appendItem(logFile);
        }
        cmbLogsFileNames.setSelectedIndex(0);
    }

    @Listen("onCheck = #rAllFiles, #rOneFile")
    public void displayFilesList () {
        if (rOneFile.isChecked()) {
            cmbLogsFileNames.setVisible(true);
        } else {
            cmbLogsFileNames.setVisible(false);
        }
    }

    private void fillLogsListbox(List<LogModel> logList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            logList = logService.searchLogs(logList, LocalDate.parse(tbLogsDate.getValue(), formatter),
                                            tbLogsText.getValue());
        } catch (Exception ex) {
            logList = logService.searchLogs(logList, null,
                                            tbLogsText.getValue());
        }

        logModels = new ListModelList<>(logList);
        lbLogs.setModel(logModels);
        lbLogs.setItemRenderer(new LogListRenderer());
        lbLogs.renderAll();
    }

    @Listen("onClick = #btnLogsSearch")
    public void searchLogs() throws FileNotFoundException {
        if (rOneFile.isChecked()) {
            logList = logService.getLogs(cmbLogsFileNames.getValue());
            fillLogsListbox(logList);
        } else {
            logList = logService.getAllLogs();
            fillLogsListbox(logList);
        }
    }
}
