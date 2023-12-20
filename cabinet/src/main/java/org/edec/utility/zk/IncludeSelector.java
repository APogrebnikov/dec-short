package org.edec.utility.zk;

import lombok.NonNull;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Include;

public abstract class IncludeSelector extends SelectorComposer<Component> {

    private static final String SELECTED_INSTITUTE = "selected_institute";
    private static final String SELECTED_FOS =  "selected_fos";

    private InstituteModel selectedInstitute;
    private FormOfStudy selectedFormOfStudy;

    public static void setPropertiesToInclude(@NonNull Include include, @NonNull InstituteModel selectedInstitute,
                                              @NonNull FormOfStudy selectedFormOfStudy) {

        include.setDynamicProperty(SELECTED_INSTITUTE, selectedInstitute);
        include.setDynamicProperty(SELECTED_FOS, selectedFormOfStudy);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        createLazyEventInRootComponent();
        checkParametersAndFill();
    }

    private void createLazyEventInRootComponent() {
        getSelf().addEventListener("onLater", event -> fill());
    }

    protected abstract void fill();

    private void checkParametersAndFill() {
        fillAttributeFromSession();
        if (!allPropertiesIsLoaded()) {
            return;
        }
        callLazyLoading();
        clearBusy();
    }

    protected void fillAttributeFromSession() {
        selectedFormOfStudy = (FormOfStudy) Executions.getCurrent().getAttribute(SELECTED_FOS);
        selectedInstitute = (InstituteModel) Executions.getCurrent().getAttribute(SELECTED_INSTITUTE);
    }

    protected boolean allPropertiesIsLoaded() {
        return selectedInstitute != null && selectedFormOfStudy != null;
    }

    private void callLazyLoading() {
        Clients.showBusy(getSelf(), "Загрузка данных");
        Events.echoEvent("onLater", getSelf(), null);
    }

    private void clearBusy() {
        Clients.clearBusy(getSelf());
    }

    public InstituteModel getSelectedInstitute() {
        return selectedInstitute;
    }

    public FormOfStudy getSelectedFormOfStudy() {
        return selectedFormOfStudy;
    }
}
