package org.edec.schedule.service.xls;

import lombok.Cleanup;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.schedule.model.xls.XlsScheduleContainer;
import org.edec.schedule.model.xls.XlsHolderScheduleContainer;
import org.edec.schedule.service.ScheduleLoader;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.httpclient.manager.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HtmlXlsScheduleLoader implements ScheduleLoader<String, List<XlsHolderScheduleContainer>> {

    private static final String SEARCH_URL = "http://edu.sfu-kras.ru/timetable.xls";

    /**
     * Тройное значение специально нужно для парсинга и соотношения поля, курса и квалификации.
     * 1 значение – индекс в таблице;
     * 2 значение – курс;
     * 3 значение – квалификация
     */
    private List<Triple<Integer, Integer, QualificationConst>> triples = Arrays.asList(
            Triple.of(0, 1, QualificationConst.BACHELOR),
            Triple.of(1, 2, QualificationConst.BACHELOR),
            Triple.of(2, 3, QualificationConst.BACHELOR),
            Triple.of(3, 4, QualificationConst.BACHELOR),
            Triple.of(4, 5, QualificationConst.BACHELOR),
            Triple.of(5, 6, QualificationConst.BACHELOR),
            Triple.of(6, 1, QualificationConst.MASTER),
            Triple.of(7, 2, QualificationConst.MASTER),
            Triple.of(8, 3, QualificationConst.MASTER)
    );

    @Override
    public List<XlsHolderScheduleContainer> findScheduleByParam(@NonNull String param) {

        List<XlsHolderScheduleContainer> holders = new ArrayList<>();

        try {
            Document document = Jsoup.connect(SEARCH_URL).get();

            //Ищем таблицу и чтобы заголовок совпадал с названием института
            //Если нашли, то следующие строки должны совпадать с подразделениями (например очное и вечернее обучение)
            //Каждая строка содержит 9 колонок:
            //1 – название; со 2 по 6 – это с 1 по 6 курс бакалв/специалитет; с 7 по 9 – это с 1 по 3 курс магистров
            for (Element element : document.select("tr.heading.heading-section")) {
                String instName = element.getAllElements().first().text();
                if (param.toLowerCase().equals(instName.toLowerCase())) {
                    Element nextElem = element.nextElementSibling();

                    //Если следующая строка с заголовком, то значит перешли на другой институт, он нас неинтересует
                    while (nextElem == null || !nextElem.hasClass("heading")) {
                        if (nextElem != null && nextElem.is("tr")) {
                            Elements trElements = nextElem.select("td");

                            XlsHolderScheduleContainer holder = new XlsHolderScheduleContainer(trElements.get(0).text());

                            for (Triple<Integer, Integer, QualificationConst> triple : triples) {
                                String url = getUrlOrNull(trElements, triple.getLeft());

                                if (url != null) {
                                    Workbook workbook = getWorkbookByUrl(url);
                                    if (workbook != null) {
                                        holder.schedules().add(
                                                new XlsScheduleContainer(triple.getMiddle(), url, triple.getRight(), workbook));
                                    }
                                }
                            }

                            nextElem = nextElem.nextElementSibling();
                            holders.add(holder);
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return holders;
    }

    private String getUrlOrNull(Elements trElements, int index) {
        Elements aLink = trElements.get(index).nextElementSibling()
                .select("a[href*=xls]");
        return aLink.isEmpty() ? null : aLink.first().attr("href");
    }

    private Workbook getWorkbookByUrl(String url) {
        try {
            @Cleanup InputStream inputStream = HttpClient.getFileByUrl(url);
            return new HSSFWorkbook(inputStream);
        } catch (IOException ignored) {
            return null;
        }
    }
}
