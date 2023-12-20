package org.edec.ratingSystem.ctrl;

import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Wire;
import tools.dynamia.chartjs.*;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Chartjs chartStudent;

    @Override
    protected void fill () {
        chartStudent.setTitle("Статистика по студенту");
        chartStudent.setHflex("1");
        chartStudent.setVflex("1");
        ChartjsData chartData = new ChartjsData();

        XYDataset datasetMine = new XYDataset("Я");
        datasetMine.setBackgroundColor("red");
        for (int i = 0; i < 10; i++) {
            datasetMine.addData(i, i * 2 + 1);
        }

        XYDataset datasetGroup = new XYDataset("Группа");
        datasetGroup.setBackgroundColor("yellow");
        for (int i = 0; i < 10; i++) {
            datasetGroup.addData(i, i / 3 * 7);
        }

        chartData.addDataset(datasetMine);
        chartData.addDataset(datasetGroup);
        chartStudent.setData(chartData);
    }
}
