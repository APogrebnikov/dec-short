package org.edec.chat.ctrl.component;

import org.zkoss.zhtml.Ul;
import org.zkoss.zhtml.Li;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

/**
 * <ul class="send-panel">
 * <li class="send-panel_clip"><button id="chooseFile" upload="true" sclass="icons">attach_file</button></li>
 * <li class="send-panel_textarea"><textbox id="textMessage" placeholder="Сообщение..." rows="3"/></li>
 * <li class="send-panel_send"><button id="sendMessage" sclass="icons">send</button></li>
 * </ul>
 */
public class SendPanelComponent extends Ul {

    private Button btnAttachment = new Button();
    private Button btnClip = new Button();
    private Textbox txtMessage = new Textbox();
    private Button btnSend = new Button();

    public SendPanelComponent () {
        this.setSclass("send-panel");
        initPanel();
    }

    public void initPanel () {
        Li liAttach = new Li();
        liAttach.setSclass("send-panel_attach");
        btnAttachment.setId("attachment");
        btnAttachment.setSclass("icons");
        btnAttachment.setLabel("attach_file");
        invisible(btnAttachment);
        liAttach.appendChild(btnAttachment);

        Li liClip = new Li();
        liClip.setSclass("send-panel_clip");
        btnClip.setId("chooseFile");
        btnClip.setSclass("icons");
        btnClip.setUpload("true,maxsize=5120");
        btnClip.setLabel("attach_file");
        visible(btnClip);
        liClip.appendChild(btnClip);

        Li liText = new Li();
        liText.setSclass("send-panel_textarea");
        txtMessage.setId("textMessage");
        txtMessage.setPlaceholder("Сообщение...");
        txtMessage.setRows(3);
        liText.appendChild(txtMessage);

        Li liSend = new Li();
        liSend.setSclass("send-panel_send");
        btnSend.setId("sendMessage");
        btnSend.setSclass("icons");
        btnSend.setLabel("send");
        liSend.appendChild(btnSend);

        this.appendChild(liAttach);
        this.appendChild(liClip);
        this.appendChild(liText);
        this.appendChild(liSend);
    }

    public void swap (Button left, Button right) {
        this.invisible(left);
        this.visible(right);
    }

    public void visible (Button btn) {
        btn.setStyle("display: block; opacity: 1;");
    }

    public void invisible (Button btn) {
        btn.setStyle("display: none; opacity: 0; width: 0;");
    }

    public void enabled () {

    }

    public void disabled () {

    }

    public Button getClip () { return this.btnClip; }

    public void setAttachment (String icons) { btnAttachment.setLabel(icons); }

    public Button getAttachment () { return this.btnAttachment; }

    public Textbox getMessage () { return this.txtMessage; }

    public Button getSend () { return this.btnSend; }
}
