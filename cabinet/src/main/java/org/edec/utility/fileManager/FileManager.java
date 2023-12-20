package org.edec.utility.fileManager;

import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.order.model.OrderModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by dmmax
 */
public class FileManager {
    private static final String PARAM_FILE_PATH = "file.path";
    private ServletContext servletContext;
    private Properties properties;
    private String pathFile;

    public FileManager() {
        setPathFile();
    }

    public FileManager(ServletContext servletContext) {
        this.servletContext = servletContext;
        setPathFile();
    }

    private void initProperties() {
        properties = new Properties();
        String propertyString = null;
        if (Executions.getCurrent() != null) {
            propertyString = Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/properties/filemanager.properties");
        } else if (servletContext != null) {
            propertyString = servletContext.getRealPath("WEB-INF/properties/filemanager.properties");
        }
        try {
            FileInputStream inputStream = new FileInputStream(propertyString);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPathFile() {
        if (properties == null) {
            initProperties();
        }
        pathFile = properties.getProperty(PARAM_FILE_PATH);
    }

    public File getFileByRelativePath(String relativePath) {
        setPathFile();
        pathFile += relativePath;
        return new File(pathFile);
    }

    public File getFileByRelativePathWithReplacing(String relativePath) {
        setPathFile();
        pathFile += relativePath;
        //Заменяем все \ на / (чтобы работало на Mac OS + Windows)
        pathFile = pathFile.replaceAll("\\\\", "/");
        return new File(pathFile);
    }

    public File[] getFilesByRelativePath(String relativePath) {
        pathFile += relativePath;
        return new File(pathFile).listFiles();
    }

    public File[] getFilesByFullPath(String fullPath) {
        return new File(fullPath).listFiles();
    }

    public String createFolder(FileModel fileModel, boolean signed, boolean attach, List<Media> medias) {
        pathFile += getRelativePath(fileModel);
        File file = new File(pathFile);
        file.mkdirs();
        if (attach) {
            new File(pathFile + File.separator + "attach").mkdir();
        }
        if (signed) {
            new File(pathFile + File.separator + "signed").mkdir();
        }

        if (medias != null && medias.size() > 0) createAttachFiles(pathFile + File.separator + "attach", medias);

        new File(pathFile + File.separator + "canceled").mkdir();
        return file.getAbsolutePath();
    }

    public void removeReferenceFromOrder(FileModel orderPath, String fio) {
        String pathApplication = pathFile + getRelativePath(orderPath) + File.separator + "attach" + File.separator + "Заявление " + fio + ".pdf";
        String pathReference = pathFile + getRelativePath(orderPath) + File.separator + "attach" + File.separator + "Справка " + fio + ".pdf";

        if (!new File(pathApplication).delete()) PopupUtil.showError("Не удалось удалить файл заявления");
        if (!new File(pathReference).delete()) PopupUtil.showError("Не удалось удалить файл справки");
    }

    public boolean transferFilesFromReferenceToAttached(FileModel fileModel, String fio, String urlRef) {
        setPathFile();
        try {
            String toApplicationFile = pathFile + getRelativePath(fileModel) + File.separator + "attach" + File.separator + "Заявление " + fio;
            String toReferenceFile = pathFile + getRelativePath(fileModel) + File.separator + "attach" + File.separator + "Справка " + fio;
            String application = "";
            String reference = "";

            File[] listRef = getFilesByRelativePath(urlRef);

            for (File aListRef : listRef) {
                if (aListRef.getName().startsWith("reference")) {
                    reference = aListRef.getAbsolutePath();
                    toApplicationFile += aListRef.getName().substring(aListRef.getName().lastIndexOf('.'));
                } else if (aListRef.getName().startsWith("application")) {
                    application = aListRef.getAbsolutePath();
                    toReferenceFile += aListRef.getName().substring(aListRef.getName().lastIndexOf('.'));
                } else {
                    System.out.println(fio + " " + " - найден файл, не соответствующий формату");
                }
            }

            InputStream isApp = null;
            InputStream isRef = null;
            OutputStream osApp = null;
            OutputStream osRef = null;
            try {

                if (!application.equals("")) {
                    isApp = new FileInputStream(new File(application));
                    osApp = new FileOutputStream(new File(toApplicationFile));

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = isApp.read(buffer)) > 0) {
                        osApp.write(buffer, 0, length);
                    }
                } else {
                    System.out.println(fio + " - не найдено заявление");
                }

                if (!reference.equals("")) {
                    isRef = new FileInputStream(new File(reference));
                    osRef = new FileOutputStream(new File(toReferenceFile));

                    byte[] buffer2 = new byte[1024];
                    int length2;
                    while ((length2 = isRef.read(buffer2)) > 0) {
                        osRef.write(buffer2, 0, length2);
                    }
                } else {
                    System.out.println(fio + " - не найдено справки");
                }
            } catch (IOException e) {
                System.out.println(fio + " - проблемы с переносом справок");
                e.printStackTrace();
                return false;
            } finally {
                try {
                    isApp.close();
                    isRef.close();
                    osApp.close();
                    osRef.close();
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            System.out.println(fio + " - проблемы с переносом справок");

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void removeAttachFileInOrder(OrderModel orderModel, String file) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")
                || file == null || file.equals("")) {
            return;
        }

        File f = new File(pathFile + orderModel.getUrl() + File.separator + "attach" + File.separator + file);

        if (!f.isDirectory()) {
            f.delete();
        }
    }

    public void removeAttachFileInOrder(OrderEditModel orderModel, String file) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")
                || file == null || file.equals("")) {
            return;
        }

        File f = new File(pathFile + orderModel.getUrl() + File.separator + "attach" + File.separator + file);

        if (!f.isDirectory()) {
            f.delete();
        }
    }

    public void createAttachForOrderUrl(OrderModel orderModel, List<Media> medias) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) {
            return;
        }
        createAttachFiles(pathFile + orderModel.getUrl() + File.separator + "attach", medias);
    }

    public void createAttachForOrderUrl(OrderEditModel orderModel, List<Media> medias) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) {
            return;
        }
        createAttachFiles(pathFile + orderModel.getUrl() + File.separator + "attach", medias);
    }

    public void createAttachForOrderUrl(OrderModel orderModel, byte[] file, String name) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) return;
        createFileByRelativePath(orderModel.getUrl() + File.separator + "attach", name, file);
    }

    public void createAttachForOrderUrl(OrderEditModel orderModel, byte[] file, String name) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) {
            return;
        }

        createFileByRelativePath(orderModel.getUrl() + File.separator + "attach", name, file);
    }

    public File[] getAttachedFilesForOrder(OrderModel orderModel) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) {
            return null;
        }

        File folderAttach = new File(pathFile + orderModel.getUrl() + File.separator + "attach");
        return folderAttach.listFiles();
    }

    public File[] getAttachedFilesForOrder(OrderEditModel orderModel) {
        setPathFile();

        if (orderModel == null || orderModel.getUrl() == null || orderModel.getUrl().equals("")) {
            return null;
        }

        File folderAttach = new File(pathFile + orderModel.getUrl() + File.separator + "attach");
        return folderAttach.listFiles();
    }

    private String createAttachFiles(String url, List<Media> medias) {
        File folderAttach = new File(url);
        if (!folderAttach.exists()) {
            folderAttach.mkdirs();
        }
        FileOutputStream fos;
        for (Media media : medias) {
            File attachFile = new File(folderAttach.getAbsoluteFile() + File.separator + media.getName());

            try {
                fos = new FileOutputStream(attachFile);
                fos.write(media.getByteData());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return folderAttach.getAbsolutePath();
    }

    public String getFullPath(String url) {
        setPathFile();

        return pathFile + url;
    }

    public void deleteFolderWithFiles(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFolderWithFiles(f);
            }
        }
        file.delete();
    }

    /**
     * @param fileModel
     * @param buffer
     * @return
     */
    public String createFile(FileModel fileModel, byte[] buffer) {
        setPathFile();
        pathFile += getRelativePath(fileModel);
        File directory = new File(pathFile);
        directory.mkdirs();
        pathFile += File.separator + fileModel.getName() + "." + fileModel.getFormat();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(pathFile));
            fos.write(buffer);
            fos.close();
            File file = new File(pathFile);
            if (file.exists())
                return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createFile(FileModel fileModel, byte[] buffer, String fileName) {
        setPathFile();
        pathFile += getRelativePath(fileModel);
        File directory = new File(pathFile);
        directory.mkdirs();
        pathFile += File.separator + fileName + "." + fileModel.getFormat();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(pathFile));
            fos.write(buffer);
            fos.close();
            File file = new File(pathFile);
            if (file.exists())
                return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createFileByRelativePath(String relativePath, String fileName, byte[] buffer) {
        setPathFile();
        pathFile += relativePath + File.separator + fileName;
        File newFile = new File(pathFile);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(newFile);
            fos.write(buffer);
            fos.close();
            File file = new File(pathFile);
            if (file.exists()) {
                return file.getParent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRelativePath(FileModel fileModel) {
        return fileModel.getInst().getValue() + File.separator + //инст
                fileModel.getTypeDocument().getValue() + File.separator + //тип документа
                fileModel.getSem() + File.separator + //семестр
                fileModel.getSubTypeDocument().getValue() + File.separator + //подтип документа
                fileModel.getName();
    }

    public boolean cancelFile(String relativePath) {
        return moveFilesFromParentToChildDir(relativePath, "canceled", true);
    }

    public boolean signedFile(String relativePath) {
        return moveFilesFromParentToChildDir(relativePath, "signed", false);
    }

    /**
     * @param relativeParenDir - родительская директика
     * @param childDir         - путь до дочерней папки (canceled, signed)
     * @return
     */
    private boolean moveFilesFromParentToChildDir(String relativeParenDir, String childDir, boolean withDate) {
        setPathFile();
        pathFile += relativeParenDir;
        String dirPath = pathFile + File.separator + childDir + File.separator;
        File parentDir = new File(pathFile);
        for (File file : parentDir.listFiles()) {
            if (file.isFile()) {
                File movedFile = new File(dirPath + (withDate ? new Date().getTime() : "") + file.getName());
                //Если не удалось перенести
                if (!file.renameTo(movedFile)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String replaceFileByRelativePathForFile(String relativePathForFile, byte[] bytes) {
        setPathFile();
        pathFile += relativePathForFile;
        File file = new File(pathFile);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            if (file.exists())
                return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createFileAndDirectories(String pathToDirectory, String fileName, byte[] content) {
        setPathFile();
        pathFile += pathToDirectory;
        pathFile = pathFile.replaceAll("\\\\", "/");
        File directory = new File(pathFile);
        directory.mkdirs();
        File file = new File(pathFile + File.separator + fileName);
        try {
            file.createNewFile();
            Files.write(file.toPath(), content);
            return file.exists();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkOnExistsFile(String relativePath) {
        setPathFile();
        pathFile += relativePath;
        pathFile = pathFile.replaceAll("\\\\", "/");
        File file = new File(pathFile);
        return file.exists();
    }


    public void createAttachForNotificationUrl(NotificationModel notificationModel, List<Media> attachedFiles){
        setPathFile();

        createAttachFiles(pathFile + "isit/notification/" + notificationModel.getId() + "/attached" + File.separator, attachedFiles);
    }

    public void removeAttachFileInNotification(NotificationModel notificationModel, String file){
        setPathFile();

        File f = new File(pathFile + "isit/notification/" + notificationModel.getId() + "/attached" + File.separator + file);

        if (!f.isDirectory()) {
            f.delete();
        }
    }

    public void removeAttachedFolderInNotification(NotificationModel notificationModel){
        setPathFile();
        File f = new File(pathFile + "isit/notification/" + notificationModel.getId());
        deleteFolderWithFiles(f);
    }

    public File[] getAttachedFilesForNotification(NotificationModel notificationModel) {
        setPathFile();

        File folderAttach = new File(pathFile + "isit/notification/" + notificationModel.getId() + "/attached" + File.separator);
        return folderAttach.listFiles();
    }

    public String getSelectedAttachedFilePath(Long idNotification, String filePath){
        setPathFile();

        return pathFile + "isit/notification/" + idNotification + "/attached" + File.separator + filePath + File.separator;
    }

    public boolean deleteAllAttachedFilesToNotification(Long idNotification){
        setPathFile();

        File folderAttach = new File(pathFile + "isit/notification/" + idNotification);

        deleteFolderWithFiles(folderAttach);

        return true;
    }

}
