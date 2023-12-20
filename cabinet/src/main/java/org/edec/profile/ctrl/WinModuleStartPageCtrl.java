package org.edec.profile.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.profile.ctrl.renderer.ModuleStartPageRenderer;
import org.edec.profile.service.ProfileService;
import org.edec.profile.service.impl.ProfileServiceEsoImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;


public class WinModuleStartPageCtrl extends SelectorComposer<Component>
{
    public static final String PROFILE_PAGE = "profile_page";

    @Wire
    private Label lEditStartPage;

    @Wire
    private Listbox lbModuleForStartPage;

    private IndexPageCtrl indexPageCtrl;
    private ProfileService profileService = new ProfileServiceEsoImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    @Override
    public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(PROFILE_PAGE);
        String startPage = profileService.getNameStartPage(template.getCurrentUser());
        lEditStartPage.setValue(startPage==null ?"Не выбрана":startPage);
        lbModuleForStartPage.setItemRenderer(new ModuleStartPageRenderer(this));
        lbModuleForStartPage.setModel(new ListModelList<>(
                profileService.getDistinctModulesByUser(template.getCurrentUser())));
        lbModuleForStartPage.renderAll();
    }

    public void choseStartPage(ModuleModel module) {
        lEditStartPage.setValue(module.getName());
        lEditStartPage.setAttribute("data", module);
    }

    @Listen("onClick = #btnSaveStartPage")
    public void saveStartPage() {
        if (lEditStartPage.getAttribute("data")==null) {
            PopupUtil.showWarning("Выберите модуль");
            return;
        }
        ModuleModel module = (ModuleModel) lEditStartPage.getAttribute("data");
        String path = module.getUrl();
        if (profileService.updateStartPage(path, template.getCurrentUser().getIdHum(), template.getCurrentUser().getIdParent())) {
            PopupUtil.showInfo("Стартовая страница успешно обновлена");
            template.getCurrentUser().setStartPage(path);
            indexPageCtrl.setStartPage(module);
        } else {
            PopupUtil.showError("Стартовую страницу обновить не удалось");
        }
    }
}