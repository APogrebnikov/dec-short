package org.edec.newOrder.report;

import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.newOrder.report.model.OrderLineModel;
import org.edec.newOrder.report.model.OrderPageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы со строками шаблона отображения приказов
 */
public class OrderLineService {

    private EditOrderManagerESO manager = new EditOrderManagerESO();

    /**
     * Получаем строки для конкретного приказа и если строки получены по академическому приказу группируем
     * @param idOrder id_order_head приказа
     * @return
     */
    public List<OrderLineModel> getOrderLines(Long idOrder){
        return manager.getOrderLines(idOrder);
    }

    /**
     * Сохраняем строки для конкретного приказа
     * @param idOrder id_order_head приказа
     * @param orderLineList список строк для шаблона приказа
     */
    public void saveOrderLines(Long idOrder, List<OrderLineModel> orderLineList){
        for(OrderLineModel orderLine : orderLineList){
            manager.saveOrderLines(idOrder ,orderLine);
        }
    }

    /**
     * Удаляем строки для приказа
     * @param idOrder
     * @return
     */
    public boolean deleteOrderLines(Long idOrder){
        return manager.deleteOrderLines(idOrder);
    }

    public void updateOrderLines(Long idOrder, List<OrderLineModel> orderLineList){
        manager.deleteOrderLines(idOrder);
        saveOrderLines(idOrder, orderLineList);
    }

    //методы для академ стипендии

    /**
     * Превращаем костыльную структуру для джаспера в виде страниц, в структуру линий для записи в БД
     * @param pages
     * @return
     */
    public List<OrderLineModel> getOrderLinesForAcademicScholarship(List<OrderPageModel> pages){
        List<OrderLineModel> lines = new ArrayList<>();

        int pageNumber = 1;

        for(OrderPageModel page : pages){
            int lineNumber = 1;

            for(OrderLineModel line : page.getOrderLines().get(0).getSubOrderLines()){
                line.setLineNumber(lineNumber);
                line.setLinePage(pageNumber);

                lines.add(line);

                lineNumber++;
            }

            pageNumber++;
        }

        return lines;
    }

    /**
     * Превращаем  структуру линий из БД в костыльную структуру для джаспера в виде страниц
     * @param lines
     * @param main
     * @return
     */
    public List<OrderPageModel> getOrderPagesForAcademicScholarship(List<OrderLineModel> lines, OrderReportMainModel main){
        List<OrderPageModel> pages = new ArrayList<>();

        OrderPageModel prevPage = new OrderPageModel();

        prevPage.setPredicatingfio(main.getPredicatingfio());
        prevPage.setPredicatingpost(main.getPredicatingpost());
        prevPage.setEmployees(main.getEmployees());

        int pageNumber = 1;

        for(OrderLineModel line : lines){

            if(pageNumber != line.getLinePage()){

                pages.add(prevPage);

                prevPage = new OrderPageModel();

                prevPage.setPredicatingfio(main.getPredicatingfio());
                prevPage.setPredicatingpost(main.getPredicatingpost());
                prevPage.setEmployees(main.getEmployees());
                
                pageNumber++;
            }

            prevPage.getOrderLines().get(0).getSubOrderLines().add(line);

        }

        pages.add(prevPage);

        return pages;
    }

}
