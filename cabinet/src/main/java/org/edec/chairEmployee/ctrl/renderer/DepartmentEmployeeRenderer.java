package org.edec.chairEmployee.ctrl.renderer;

import org.edec.chairEmployee.model.PostModel;
import org.edec.chairEmployee.service.ChairEmployeeService;
import org.edec.chairEmployee.service.impl.ChairEmployeeImpl;
import org.edec.utility.component.model.EmployeeModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;


public class DepartmentEmployeeRenderer implements ListitemRenderer<EmployeeModel> {
    private ChairEmployeeService chairEmployeeService = new ChairEmployeeImpl();

    private List<PostModel> posts;

    public DepartmentEmployeeRenderer (List<PostModel> posts) {
        this.posts = posts;
    }

    @Override
    public void render (Listitem li, final EmployeeModel data, int index) throws Exception {
        li.setValue(data);
        Listcell lcFIO = new Listcell(data.getFIO());
        lcFIO.setParent(li);
        if (data.getIsHide()) {
            lcFIO.setStyle("background: #b7b7b7;");
        }

        Listcell lcPost = new Listcell();
        lcPost.setParent(li);
        final Combobox cmbPost = new Combobox();
        cmbPost.setParent(lcPost);
        cmbPost.setReadonly(true);
        cmbPost.setModel(new ListModelList<>(posts));
        cmbPost.setItemRenderer((ComboitemRenderer<PostModel>) (comboitem, postModel, i) -> {
            comboitem.setValue(postModel);
            comboitem.setLabel(postModel.getPost());
        });
        cmbPost.addEventListener(
                "onAfterRender",
                event -> cmbPost.setSelectedIndex(posts.indexOf(chairEmployeeService.getPostByName(data.getPost(), posts)))
        );
        cmbPost.addEventListener(
                "onChange",
                event -> {
                    if(chairEmployeeService.updateLED(data.getIdLED(),((PostModel)cmbPost.getSelectedItem().getValue()).getIdPost())) {
                        PopupUtil.showInfo("Сотрудник успешно обновлен");
                    }
                }
        );

        Listcell lcHide = new Listcell();
        lcHide.setParent(li);
        Checkbox chActive = new Checkbox();
        chActive.setChecked(!data.getIsHide());
        chActive.setParent(lcHide);

        // TODO REFACTOR убрать дублирование кода
        //Простите за дурацкое копирование кода
        lcHide.addEventListener("onClick", event -> {
            data.setIsHide(!data.getIsHide());
            chActive.setChecked(!data.getIsHide());
            if (data.getIsHide()) {
                lcFIO.setStyle("background: #b7b7b7;");
            } else {
                lcFIO.setStyle("background: none;");
            }
            chairEmployeeService.updateHideEmpDep(data);
        });

        chActive.addEventListener("onClick", event -> {
            data.setIsHide(!data.getIsHide());
            chActive.setChecked(!data.getIsHide());
            if (data.getIsHide()) {
                lcFIO.setStyle("background: #b7b7b7;");
            } else {
                lcFIO.setStyle("background: none;");
            }
            chairEmployeeService.updateHideEmpDep(data);
        });

        Listcell lcDel = new Listcell();
        lcDel.setParent(li);
        Button btnDel = new Button("", "/imgs/crossCLR.png");
        btnDel.setParent(lcDel);
        btnDel.addEventListener(Events.ON_CLICK, event -> {
            if (chairEmployeeService.deleteLED(data.getIdLED())) {
                PopupUtil.showInfo("Сотрудник успешно удален");
            } else {
                PopupUtil.showError("Удалить сотрудника не удалось");
            }
        });
    }
}
