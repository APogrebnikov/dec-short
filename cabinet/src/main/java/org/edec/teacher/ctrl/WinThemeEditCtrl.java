package org.edec.teacher.ctrl;

import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.RegisterService;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Created by antonskripacev on 24.02.17.
 */
public class WinThemeEditCtrl extends SelectorComposer<Component> {
    public static final String RATING = "rating";
    public static final String FORM_CONTROL = "fc";
    public static final String READONLY = "isReadOnly";
    public static final String UPDATE_REGISTER_UI = "updateRegisterUi";

    @Wire
    private Window winTheme;
    @Wire
    private Button btnSave;
    @Wire
    private Textbox textBoxTheme;

    RegisterRowModel rating;
    FormOfControlConst formControl;

    RegisterService service = new RegisterServiceImpl();
    private Runnable updateRegisterUI;

    boolean cp;
    boolean isReadonly;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        try {
            rating = (RegisterRowModel) Executions.getCurrent().getArg().get(RATING);
            isReadonly = (Boolean) Executions.getCurrent().getArg().get(READONLY);
            formControl = (FormOfControlConst) Executions.getCurrent().getArg().get(FORM_CONTROL);
            updateRegisterUI = (Runnable) Executions.getCurrent().getArg().get(UPDATE_REGISTER_UI);

            cp = formControl == FormOfControlConst.CP;
            if (rating.getTheme() != null) {
                textBoxTheme.setValue(rating.getTheme());
            }

            if (isReadonly) {
                textBoxTheme.setReadonly(true);
                btnSave.setDisabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Ошибка инициализации окна редактирования темы. Обратитесь к администратору.", "Ошибка!", Messagebox.OK,
                            Messagebox.ERROR
            );
        }
    }

    /**
     * Метод сохранения темы.
     */
    @Listen("onClick = #btnSave")
    public void save() {
        try {
            if (null != textBoxTheme.getValue() && !textBoxTheme.getValue().equals("")) {
                if (cp) {
                    service.setCourseProjectTheme(textBoxTheme.getValue(), rating.getIdSR());
                } else {
                    service.setCourseWorkTheme(textBoxTheme.getValue(), rating.getIdSR());
                }

                Messagebox.show("Информация сохранена.", "Внимание!", Messagebox.OK, Messagebox.INFORMATION);
                updateRegisterUI.run();
            } else {
                Messagebox.show("Введите тему.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Ошибка сохранения темы. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
