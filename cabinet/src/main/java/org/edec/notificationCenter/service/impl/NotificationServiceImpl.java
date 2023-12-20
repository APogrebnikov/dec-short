package org.edec.notificationCenter.service.impl;

import org.edec.notificationCenter.manager.NotificationManager;
import org.edec.notificationCenter.model.CommentaryModel;
import org.edec.notificationCenter.model.FeedbackModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.model.ReceiverModel;
import org.edec.notificationCenter.model.receiverTypes.CourseModel;
import org.edec.notificationCenter.model.receiverTypes.EmployeeModel;
import org.edec.notificationCenter.model.receiverTypes.GroupModel;
import org.edec.notificationCenter.model.receiverTypes.StudentModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.utility.constants.FormOfStudyConst;
import org.edec.utility.constants.ReceiverStatusConst;
import org.edec.utility.constants.ReceiverTypeConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.zk.PopupUtil;
import org.springframework.stereotype.Service;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final boolean IS_GROUP_LEADER_SEARCH = true;
    private static final boolean IS_STUDENT_SEARCH = false;

    private NotificationManager notificationManager = new NotificationManager();
    private FileManager fileManager = new FileManager();

    //Работа с уведомлениями в общем виде

    @Override
    public List<NotificationModel> getAllNotifications(Long idHumanface) {

        return notificationManager.getAllNotifications(idHumanface);
    }

    @Override
    public NotificationModel createNotification(Long idSender, ReceiverTypeConst receiverType) {
        NotificationModel notification = new NotificationModel();

        notification.setDatePosted(new Date());
        notification.setReceiverType(receiverType.getName());
        notification.setId(notificationManager.createNotification(idSender, receiverType.getValue()));

        return notification;
    }

    @Override
    public void createSystemNotification(String header, String text, List<Long> listIdReceivers) {
        Long idNotification = notificationManager.createSystemNotification(header, text);
        
        for(Long idReceiver : listIdReceivers){
            saveReceiver(idNotification, idReceiver, ReceiverStatusConst.NOTIFIED);
        }
    }

    @Override
    public boolean saveNotification(NotificationModel notification) {
        return notificationManager.saveNotificationInfo(notification) && saveReceivers(notification);
    }

    @Override
    public boolean sendNotification(Long idNotification) {
        return notificationManager.sendNotification(idNotification);
    }

    @Override
    public boolean removeNotification(Long idNotification) {
        return notificationManager.removeAllReceivers(idNotification)
               && notificationManager.removeNotification(idNotification)
               && fileManager.deleteAllAttachedFilesToNotification(idNotification);
    }

    //Прикрепление файлов

    @Override
    public List<String> getAttachedFiles(NotificationModel notification) {
        List<String> attachedFiles = new ArrayList<>();

        //Список прикрепленных файлов формируется путем поиска папки с названием соответствующим id уведомления в папке notifications
        //папки attached из которой берутся все хранимые там файлы
        File[] files = fileManager.getAttachedFilesForNotification(notification);

        if (files != null) {
            Arrays.asList(files).forEach(file -> attachedFiles.add(file.getName()));
        }

        return attachedFiles;
    }

    @Override
    public void attachFile(NotificationModel notification, List<Media> medias) {
        fileManager.createAttachForNotificationUrl(notification, medias);
    }

    @Override
    public void deleteFile(NotificationModel notification, String file) {
        fileManager.removeAttachFileInNotification(notification, file);
    }

    @Override
    public void downloadFile(Long idNotification, String filePath) {
        FileInputStream inputStream;

        try {
            File file = new File(fileManager.getSelectedAttachedFilePath(idNotification, filePath));
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                Filedownload.save(inputStream, new MimetypesFileTypeMap().getContentType(file), file.getName());
            } else {
                PopupUtil.showError("Запрашиваемый файл не найден! Обратитесь к администратору!");
            }
        } catch (FileNotFoundException e) {
            PopupUtil.showError("Запрашиваемый файл не найден! Обратитесь к администратору!");
            e.printStackTrace();
        }
    }

    //Работа с уведомлениями при редактировании уведомлений

    @Override
    public List<ReceiverModel> getReceivers(ReceiverTypeConst notificationType, Long idNotification) {
        List<ReceiverModel> receiversList = new ArrayList<>();

        switch (notificationType) {
            case STUDENTS:
                receiversList = new ArrayList<>(notificationManager.getStudents(idNotification, false));
                break;
            case GROUPS:
                receiversList = new ArrayList<>(notificationManager.getGroups(idNotification));
                break;
            case COURSES:
                receiversList = new ArrayList<>(notificationManager.getCourses(idNotification));
                break;
            case EMPLOYEES:
                receiversList = new ArrayList<>(notificationManager.getEmployees(idNotification));
                break;
            case LEADERS:
                receiversList = new ArrayList<>(notificationManager.getStudents(idNotification, true));
                break;
        }

        receiversList = receiversList.stream().map(receiver -> {
            receiver.setReceiverType(notificationType);

            return receiver;
        }).collect(Collectors.toList());

        return receiversList;
    }

    @Override
    public List<ReceiverModel> searchReceivers(int fos, long idInst, ReceiverTypeConst notificationType, List<ReceiverModel> addedReceivers,
                                               List<String> params) {
        List<ReceiverModel> receiversList = new ArrayList<>();

        switch (notificationType) {
            case STUDENTS:
            case LEADERS:
                //получаем список студентов удовлетворяющих введенным параметрам
                receiversList = notificationManager.searchStudents(params.get(0), params.get(1), fos, idInst,
                                                                   (notificationType.equals(ReceiverTypeConst.STUDENTS)
                                                                    ? IS_STUDENT_SEARCH
                                                                    : IS_GROUP_LEADER_SEARCH)
                ).stream().sorted(Comparator.comparing(StudentModel::getFio)).collect(Collectors.toList());

                //проверяем чтобы не было студентов уже добавленных в список рассылки
                receiversList = receiversList.stream().filter(receiverModel -> {
                    for (ReceiverModel receiver : addedReceivers) {
                        if (((StudentModel) receiverModel).getIdHumanface().equals(((StudentModel) receiver).getIdHumanface())) {
                            return false;
                        }
                    }

                    return true;
                }).collect(Collectors.toList());

                break;
            case GROUPS:
                receiversList = notificationManager.searchGroups(params.get(0), fos, idInst).stream()
                                                   .sorted(Comparator.comparing(GroupModel::getGroupName)).collect(Collectors.toList());

                receiversList = receiversList.stream().filter(receiverModel -> {
                    for (ReceiverModel receiver : addedReceivers) {
                        if (((GroupModel) receiverModel).getIdGroup().equals(((GroupModel) receiver).getIdGroup())) {
                            return false;
                        }
                    }

                    return true;
                }).collect(Collectors.toList());

                break;
            case COURSES:

                receiversList.add(new CourseModel(1, fos));
                receiversList.add(new CourseModel(2, fos));
                receiversList.add(new CourseModel(3, fos));
                receiversList.add(new CourseModel(4, fos));
                receiversList.add(new CourseModel(5, fos));
                receiversList.add(new CourseModel(6, fos));

                receiversList = receiversList.stream().filter(receiverModel -> {
                    for (ReceiverModel receiver : addedReceivers) {
                        if (((CourseModel) receiverModel).getCourseNumber().equals(((CourseModel) receiver).getCourseNumber()) &&
                            ((CourseModel) receiverModel).getFos().equals(((CourseModel) receiver).getFos())) {
                            return false;
                        }
                    }

                    return true;
                }).collect(Collectors.toList());

                break;
            case EMPLOYEES:
                receiversList = notificationManager.searchEmployees(params.get(0)).stream()
                                                   .sorted(Comparator.comparing(EmployeeModel::getFio)).collect(Collectors.toList());

                receiversList = receiversList.stream().filter(receiverModel -> {
                    for (ReceiverModel receiver : addedReceivers) {
                        if (((EmployeeModel) receiverModel).getIdHumanface().equals(((EmployeeModel) receiver).getIdHumanface())) {
                            return false;
                        }
                    }

                    return true;
                }).collect(Collectors.toList());

                break;
        }

        receiversList = receiversList.stream().map(receiver -> {
            receiver.setReceiverType(notificationType);
            return receiver;
        }).collect(Collectors.toList());

        return receiversList;
    }

    private boolean saveReceivers(NotificationModel notification) {

        List<ReceiverModel> receiverList = notification.getReceivers();

        ReceiverTypeConst receiverType = ReceiverTypeConst.getByName(notification.getReceiverType());

        receiverList = receiverList.stream().filter(receiverModel -> receiverModel.getStatus().equals(ReceiverStatusConst.ADDED))
                                   .collect(Collectors.toList());

        switch (receiverType) {
            case STUDENTS:
            case LEADERS:

                for (ReceiverModel receiver : receiverList) {
                    notificationManager.saveReceiver(((StudentModel) receiver).getIdHumanface(), notification.getId(), ReceiverStatusConst.SAVED);
                }

                break;
            case GROUPS:

                for (ReceiverModel receiver : receiverList) {
                    List<Long> students = notificationManager.getStudentsByGroup(((GroupModel) receiver).getIdGroup());

                    for (Long idHumanface : students) {
                        notificationManager.saveReceiver(idHumanface, notification.getId(), ReceiverStatusConst.SAVED);
                    }
                }

                break;
            case COURSES:

                List<Long> students = notificationManager.getStudentsByCourses(1l, getCourseSubquery(receiverList));

                for (Long idHumanface : students) {
                    notificationManager.saveReceiver(idHumanface, notification.getId(), ReceiverStatusConst.SAVED);
                }

                break;
            case EMPLOYEES:

                for (ReceiverModel receiver : receiverList) {
                    notificationManager.saveReceiver(((EmployeeModel) receiver).getIdHumanface(), notification.getId(), ReceiverStatusConst.SAVED);
                }

                break;
        }

        notification.setReceivers(notification.getReceivers().stream().map(receiver -> {
            if (receiver.getStatus().equals(ReceiverStatusConst.ADDED)) {
                receiver.setStatus(ReceiverStatusConst.SAVED);
            }

            return receiver;
        }).collect(Collectors.toList()));

        return true;
    }

    @Override
    public boolean deleteReceiver(ReceiverTypeConst receiverType, Long idNotification, ReceiverModel receiver) {

        switch (receiverType) {
            case STUDENTS:
            case LEADERS:

                StudentModel student = (StudentModel) receiver;

                notificationManager
                        .deleteReceiver(notificationManager.getLinkNotificationHumanface(idNotification, student.getIdHumanface()));
                break;
            case GROUPS:

                GroupModel group = (GroupModel) receiver;

                List<Long> students = notificationManager.getStudentsByGroup(group.getIdGroup());

                for (Long studentFromGroup : students) {
                    notificationManager.deleteReceiver(notificationManager.getLinkNotificationHumanface(idNotification, studentFromGroup));
                }

                break;
            case COURSES:

                List<ReceiverModel> courses = new ArrayList<>();

                courses.add(receiver);

                List<Long> studentsFromCourse = notificationManager.getStudentsByCourses(1L, getCourseSubquery(courses));

                for (Long studentFromCourse : studentsFromCourse) {
                    notificationManager.deleteReceiver(notificationManager.getLinkNotificationHumanface(idNotification, studentFromCourse));
                }

                break;
            case EMPLOYEES:

                EmployeeModel employee = (EmployeeModel) receiver;

                notificationManager
                        .deleteReceiver(notificationManager.getLinkNotificationHumanface(idNotification, employee.getIdHumanface()));

                break;
        }

        return false;
    }

    private String getCourseSubquery(List<ReceiverModel> courses) {
        String subquery = "";

        for (ReceiverModel course : courses) {
            subquery += "or (course = " + ((CourseModel) course).getCourseNumber() + " and semester.formofstudy = " +
                        ((CourseModel) course).getFos() + ") ";
        }

        return subquery.replaceFirst("or", "");
    }

    //Обратная связь

    @Override
    public List<FeedbackModel> getFeedback(Long idNotification) {
        return notificationManager.getFeedback(idNotification);
    }

    @Override
    public List<CommentaryModel> getComments(Long idSender, Long idLinkNotificationHumanface) {
        return notificationManager.getCommentaries(idSender, idLinkNotificationHumanface);
    }

    @Override
    public void sendComment(Long idLinkNotificationHumanface, CommentaryModel comment) {
        comment.setDatePosted(notificationManager.sendCommentary(comment.getIdSender(), idLinkNotificationHumanface, comment.getMessage()));
    }

    @Override
    public void updateCommentStatus(List<CommentaryModel> unreadComments) {
        unreadComments.stream().forEach(unreadComment -> notificationManager.updateCommentStatus(unreadComment.getIdCommentary()));
    }

    @Override
    public List<NotificationModel> getUsersNotifications(Long idHumanface) {
        List<NotificationModel> usersNotifications = notificationManager.getUserNotifications(idHumanface);

        usersNotifications = usersNotifications.stream().map(notificationModel -> {
            notificationModel.setAttachedFiles(getAttachedFiles(notificationModel));
            return notificationModel;
        }).collect(Collectors.toList());

        return usersNotifications;
    }

    @Override
    public void fillDivChat(Div divChat, List<CommentaryModel> commentaries, Long idCurrentUser) {
        divChat.getChildren().clear();

        if (commentaries.size() == 0) {

            Div divEmpty = new Div();
            divEmpty.setParent(divChat);
            divEmpty.setSclass("data");

            new Label("Список сообщений пуст!").setParent(divEmpty);

            return;
        }

        Date previousCommentaryDate = null;
        for (CommentaryModel commentaryModel : commentaries) {
            constructDivMessage(divChat, commentaryModel, previousCommentaryDate, idCurrentUser);
            previousCommentaryDate = commentaryModel.getDatePosted();
        }
    }

    @Override
    public void constructDivMessage(Div divChat, CommentaryModel commentaryModel, Date previousCommentaryDate, Long idCurrentUser) {
        String dateValue = DateConverter.getDateForMessageGroup(previousCommentaryDate, commentaryModel.getDatePosted());

        //если даты предыдущего и текущего сообщения отличаются, вставляем блок с датой
        if (!dateValue.isEmpty()) {
            Div divDate = new Div();
            divDate.setParent(divChat);
            divDate.setSclass("data");
            new Label(dateValue).setParent(divDate);
        }

        Div divMessage = new Div();
        divMessage.setParent(divChat);

        Label username = new Label(commentaryModel.getNameSender());
        username.setParent(divMessage);
        username.setSclass("username");

        new Html("<p></p>").setParent(divMessage);

        Label text = new Label(commentaryModel.getMessage());
        text.setParent(divMessage);
        text.setSclass("text");
        text.setMultiline(true);

        new Html("<br>").setParent(divMessage);

        Label time = new Label(DateConverter.convertTimeToString(commentaryModel.getDatePosted()));
        time.setParent(divMessage);

        if (!commentaryModel.getIsRead() && !(commentaryModel.getIdSender().equals(idCurrentUser))) {
            divMessage.setSclass("container unread");
            time.setSclass("time-unread");
        } else if (commentaryModel.getIdSender().equals(idCurrentUser)) {
            divMessage.setSclass("container darker");
            time.setSclass("time");
        } else {
            divMessage.setSclass("container");
            time.setSclass("time");
        }

        if (commentaryModel.getIdSender().equals(idCurrentUser)) {
            username.setStyle("float: right");
            text.setStyle("float: right; margin-left: 8px; margin-right: 0px;");
            time.setStyle("float: right");
        }

        Clients.scrollIntoView(divMessage);
    }

    @Override
    public void updatePersonalNotificationStatus(Long idLinkNotificationHumanface) {
        notificationManager.updatePersonalNotificationStatus(idLinkNotificationHumanface);
    }

    @Override
    public void updateNotificationStatus(Long idNotification) {
        notificationManager.updateNotificationStatus(idNotification);
    }
    
    private void saveReceiver(Long idNotification, Long idReceiver, int status){
        notificationManager.saveReceiver(idReceiver, idNotification, status);
    }
}
