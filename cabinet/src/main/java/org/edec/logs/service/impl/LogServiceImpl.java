package org.edec.logs.service.impl;

import org.edec.logs.model.LogModel;
import org.edec.logs.service.LogService;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LogServiceImpl implements LogService {
    private static final String PATH = "D:\\studying\\2course\\проекты\\работа\\";

    @Override
    public List<LogModel> getLogs(String fileName) throws FileNotFoundException {
    List<LogModel> logs = new ArrayList<>();

    Scanner sc = new Scanner(new File(PATH + fileName));

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    LocalDate date = null;
    String time = "";
    String level = "";
    String text = "";

    while (sc.hasNextLine()) {
        String line = sc.nextLine();
        line = line.replace("  ", " ");

        String str = line.trim();

        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", str.split(" ", 2)[0])) {
            if (date != null) {
                LogModel log = new LogModel(date, time, level, text);
                logs.add(log);
            }
            line = line.trim();
            String[] logParts = line.split(" ", 5);
            date = LocalDate.parse(logParts[0], dateFormatter);

            time = logParts[1];
            level = logParts[2];
            text = logParts[4];
        } else {
            text += "\n" + line;
        }

        if (!sc.hasNextLine()) {
            LogModel log = new LogModel(date, time, level, text);
            logs.add(log);
        }
    }
    sc.close();

    return logs;
}

    @Override
    public List<String> getLogFiles() {
        List<String> logFiles = new ArrayList<>();

        File directory = new File(PATH);
        File[] filesArr = directory.listFiles();

        for (File file : filesArr) {
            if (!file.isDirectory()) {
                logFiles.add(file.getName());
            }
        }

        return logFiles;
    }

    @Override
    public List<LogModel> getAllLogs () throws FileNotFoundException {
        List<LogModel> allLogs = new ArrayList<>();

        for (String file : getLogFiles()) {
            allLogs.addAll(getLogs(PATH + file));
        }

        return allLogs;
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        final int needleLength = needle.length();
        if (needleLength == 0) {
            return true;
        }

        final char firstLo = Character.toLowerCase(needle.charAt(0));
        final char firstUp = Character.toUpperCase(needle.charAt(0));

        for (int charIndex = haystack.length() - needleLength; charIndex >= 0; charIndex--) {
            final char ch = haystack.charAt(charIndex);
            if (ch != firstLo && ch != firstUp) {
                continue;
            }

            if (haystack.regionMatches(true, charIndex, needle, 0, needleLength)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<LogModel> searchLogs(List<LogModel> logs, LocalDate searchingDate, String searchingText) {
        List<LogModel> filteredLogs = new ArrayList<>();

        if (searchingDate == null) {
            for (LogModel log : logs) {
                if (containsIgnoreCase(log.getText(), searchingText)) {
                    filteredLogs.add(log);
                }
            }
        } else {
            for (LogModel log : logs) {
                if (log.getDate().isEqual(searchingDate) && containsIgnoreCase(log.getText(), searchingText)) {
                    filteredLogs.add(log);
                }
            }
        }

        return filteredLogs;
    }
}
