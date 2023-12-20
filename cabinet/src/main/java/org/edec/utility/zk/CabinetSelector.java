package org.edec.utility.zk;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.main.model.UserModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Label;

import static org.zkoss.zk.ui.Executions.getCurrent;

public abstract class CabinetSelector extends SelectorComposer<Component> {

    public TemplatePageCtrl template = new TemplatePageCtrl();

    protected ModuleModel currentModule;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        if (currentModule != null) {
            fill();
        } else {
            getSelf().getChildren().clear();
            getSelf().appendChild( new Label("Нет прав"));
        }
    }

    protected UserModel getCurrentUser() {
        return template.getCurrentUser();
    }

    abstract protected void fill() throws InterruptedException;
}
