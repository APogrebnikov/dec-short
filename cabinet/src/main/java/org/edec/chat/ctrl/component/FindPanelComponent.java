package org.edec.chat.ctrl.component;

import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Ul;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

/**
 * <ul class="message-about_header">
 * <li class="message-about_header-title"><label id="labelGroupName" value="Название диалога"></label></li>
 * <li><button id="searchGroup" sclass="icons">search</button></li>
 * <li><button id="messageImportantGroup" sclass="icons">star</button></li>
 * <li><button id="filesGroup" sclass="icons">insert_drive_file</button></li>
 * </ul-->
 */
public class FindPanelComponent extends Ul {

    private Textbox txtKeyword = new Textbox();

    public FindPanelComponent () {
        this.setSclass("message-about_header");
    }

    public void initPanel () {

        Li liKeyWord = new Li();
        txtKeyword.setSclass("message-about_search-panel");
        txtKeyword.setId("txtKeyword");
        liKeyWord.appendChild(txtKeyword);

        Li liSearch = new Li();
        Button search = new Button();
        search.setSclass("icons");
        search.setLabel("search");
        search.setId("search");
        liSearch.appendChild(search);

        Li liClose = new Li();
        Button close = new Button();
        close.setId("close");
        close.setSclass("icons");
        close.setLabel("close");
        liClose.appendChild(close);

        this.appendChild(liKeyWord);
        this.appendChild(liSearch);
        this.appendChild(liClose);
    }

    private void clear () {
        this.getChildren().clear();
    }

    public Textbox getKeyword () {
        return txtKeyword;
    }
}
