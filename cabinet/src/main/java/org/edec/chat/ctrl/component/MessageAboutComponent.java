package org.edec.chat.ctrl.component;

import org.zkoss.zhtml.Ul;
import org.zkoss.zhtml.Li;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * <ul class="message-about_header">
 * <li class="message-about_header-title"><label id="labelGroupName" value="Название диалога"></label></li>
 * <li><button id="searchGroup" sclass="icons">search</button></li>
 * <li><button id="messageImportantGroup" sclass="icons">star</button></li>
 * <li><button id="filesGroup" sclass="icons">insert_drive_file</button></li>
 * </ul-->
 */
public class MessageAboutComponent extends Ul {

    private String title;

    private Textbox txtKeyword = new Textbox();

    private Button search = new Button();
    private Button important = new Button();
    private Button files = new Button();

    private Button star = new Button();
    private Button reply = new Button();
    private Button close = new Button();

    public MessageAboutComponent (String title) {
        this.setSclass("message-about_header");
        this.title = title;
        initPanel();
    }

    public void initPanel () {
        clear();

        Li liTitle = new Li();
        liTitle.setSclass("message-about_header-title");
        Label lbTitle = new Label();
        lbTitle.setValue(this.title);
        liTitle.appendChild(lbTitle);

        Li liKeyWord = new Li();
        txtKeyword.setSclass("message-about_search-panel");
        txtKeyword.setId("txtKeyword");
        liKeyWord.appendChild(txtKeyword);

        Li liSearch = new Li();
        search.setSclass("icons");
        search.setLabel("search");
        search.setId("search");
        liSearch.appendChild(search);

        Li liStar = new Li();
        important.setSclass("icons");
        important.setLabel("star");
        important.setId("importantMessages");
        liStar.appendChild(important);

        Li liFile = new Li();
        files.setSclass("icons");
        files.setLabel("insert_drive_file");
        files.setId("files");
        liFile.appendChild(files);

        this.appendChild(liTitle);
        this.appendChild(liKeyWord);
        this.appendChild(liSearch);
        this.appendChild(liStar);
        this.appendChild(liFile);
    }

    public void update () {
        //Li liFill = (Li) this.getFirstChild();
        clear();

        Li liStar = new Li();
        star.setId("star");
        star.setSclass("icons");
        star.setLabel("star");
        liStar.appendChild(star);

        Li liReply = new Li();
        reply.setId("reply");
        reply.setSclass("icons");
        reply.setLabel("reply");
        liReply.appendChild(reply);

        Li liClose = new Li();
        close.setId("close");
        close.setSclass("icons");
        close.setLabel("close");
        liClose.appendChild(close);

        //this.appendChild(liFill); // The field for title
        this.appendChild(liStar);
        this.appendChild(liReply);
        this.appendChild(liClose);
    }

    public void liteInit () {
        clear();

        Li liTitle = new Li();
        liTitle.setSclass("message-about_header-title");
        Label lbTitle = new Label();
        lbTitle.setValue(this.title);
        liTitle.appendChild(lbTitle);

        Li liFile = new Li();
        files.setSclass("icons");
        files.setLabel("insert_drive_file");
        files.setId("files");
        liFile.appendChild(files);

        this.appendChild(liTitle);
        this.appendChild(liFile);
    }

    public void enabled () {

    }

    public void disabled () {

    }

    public Textbox getKeyword () { return this.txtKeyword; }

    public Button getSearch () { return this.search; }

    public Button getImportant () { return this.important; }

    public Button getFiles () { return this.files; }

    public Button getStar () { return this.star; }

    public Button getReply () { return this.reply; }

    public Button getClose () { return this.close; }

    private void clear () { this.getChildren().clear(); }
}
