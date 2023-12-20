package org.edec.teacher.ctrl.listener;

import org.edec.teacher.ctrl.WinRegisterCtrl;
import org.edec.teacher.ctrl.WinThemeEditCtrl;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by antonskripacev on 24.02.17.
 */
public class ThemeEditListener implements EventListener<Event> {
    private RegisterRowModel rating;
    private FormOfControlConst formOfCtrl;
    private Runnable updateRegisterUI;
    private Boolean readonly;

    public ThemeEditListener(RegisterRowModel rating, FormOfControlConst formOfCtrl, Runnable updateRegisterUI, Boolean readonly) {
        this.rating = rating;
        this.formOfCtrl = formOfCtrl;
        this.updateRegisterUI = updateRegisterUI;
        this.readonly = readonly;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        Map arg = new HashMap();
        arg.put(WinThemeEditCtrl.RATING, rating);
        arg.put(WinThemeEditCtrl.READONLY, readonly);
        arg.put(WinThemeEditCtrl.FORM_CONTROL, formOfCtrl);
        arg.put(WinThemeEditCtrl.UPDATE_REGISTER_UI, updateRegisterUI);

        ComponentHelper.createWindow("winTheme.zul", "winTheme", arg).doModal();
    }
}
