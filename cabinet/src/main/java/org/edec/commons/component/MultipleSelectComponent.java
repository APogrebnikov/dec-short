package org.edec.commons.component;

import lombok.NonNull;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleSelectComponent<TYPE, SELECTED_TYPE> extends Bandbox {

    private Listbox lbForSelection;

    private FillLabelListener<TYPE> fillLabelListener = Object::toString;
    private TransformerTypeListener<TYPE, SELECTED_TYPE> transformerTypeListener;

    private List<TYPE> objects;

    public MultipleSelectComponent(List<TYPE> objects, @NonNull TransformerTypeListener<TYPE, SELECTED_TYPE> transformListener) {

        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException("Objects can't be empty!");
        }
        this.objects = objects;
        this.transformerTypeListener = transformListener;

        fill();
    }

    private void fill() {

        fillDefaultSettings();
        fillListbox();
        fillLabel();
    }

    private void fillDefaultSettings() {

        setReadonly(true);

        Bandpopup bandpopup = new Bandpopup();
        bandpopup.setParent(this);
        bandpopup.setWidth("300px");

        lbForSelection = new Listbox();
        lbForSelection.setParent(bandpopup);
        lbForSelection.setHflex("1");
        lbForSelection.setCheckmark(true);
        lbForSelection.setMultiple(true);
        lbForSelection.addEventListener(Events.ON_SELECT, event -> selectListBox());
    }

    private void selectListBox() {

        fillLabel();
        Events.postEvent(Events.ON_CHANGING, this, null);
    }

    private void fillLabel() {

        String selectedObjects = lbForSelection.getSelectedItems().stream()
                .map(listitem -> fillLabelListener.fillObjectNameInLabel(listitem.getValue()))
                .collect(Collectors.joining(", "));
        setValue(selectedObjects);
        setTooltiptext(selectedObjects);
    }

    private void fillListbox() {

        ListModelList<TYPE> lmModel = new ListModelList<>(objects);
        lmModel.setMultiple(true);
        lmModel.setSelection(objects);
        lbForSelection.setModel(lmModel);
        lbForSelection.renderAll();
    }

    public List<SELECTED_TYPE> getSelectedValues() {

        return lbForSelection.getSelectedItems().stream()
                .map(listitem -> transformerTypeListener.transform(listitem.getValue()))
                .collect(Collectors.toList());
    }

    public void selectAllItems() {

        lbForSelection.setSelectedItems(new HashSet<>(lbForSelection.getItems()));
    }

    public void unselectAllItems() {

        lbForSelection.setSelectedIndex(-1);
    }

    public void setFillLabelListener(@NonNull FillLabelListener<TYPE> fillLabelListener) {
        this.fillLabelListener = fillLabelListener;
        fillLabel();
    }

    public interface FillLabelListener<T> {

        @NonNull
        String fillObjectNameInLabel(T data);
    }

    public interface TransformerTypeListener<TYPE, RETURN_TYPE> {

        @NonNull
        RETURN_TYPE transform(TYPE selectedObject);
    }
}
