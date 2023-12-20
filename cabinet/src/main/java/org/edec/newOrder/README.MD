#Процесс добавления нового вида приказа
1. Создание класса наследника OrderService (Пакет org.edec.newOrder.service.orderCreator)
2. Добавления case в метод getServiceByRule в OrderServiceFactory
3. Добавления case в метод getGroupingByRule в OrderServiceFactory
4. Добавление case в метод getJasperForOrder в ReportService
5. В проекте sync добавить проведение приказа в org.edec.scheduled.mine.service.MineOrderService
6. Добавить условие в метод для прикрепления документов getTypeDocumentByRule
7. Создание правила и секций через модуль редактора шаблонов приказов
