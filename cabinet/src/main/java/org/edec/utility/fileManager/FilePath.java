package org.edec.utility.fileManager;

import lombok.Cleanup;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class FilePath {
    private Properties properties;
    private ServletContext servletContext;

    public FilePath () {
        properties = new Properties();
    }

    public FilePath (ServletContext servletContext) {
        this();
        this.servletContext = servletContext;
    }

    public Properties getDataServiceProp () throws IOException {
        String propertyString;
        if (Executions.getCurrent() != null) {
            propertyString = Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/properties/dataservice.properties");
        } else {
            propertyString = servletContext.getRealPath("WEB-INF/properties/dataservice.properties");
        }
        @Cleanup FileInputStream inputStream = new FileInputStream(propertyString);
        properties.load(inputStream);
        return properties;
    }
}
