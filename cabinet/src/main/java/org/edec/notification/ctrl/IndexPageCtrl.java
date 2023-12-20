package org.edec.notification.ctrl;

import org.edec.model.HumanfaceModel;
import org.edec.notification.manager.NotificationEsoManager;
import org.edec.notification.model.Department;
import org.edec.notification.model.Incoming;
import org.edec.notification.service.NotificationDecService;
import org.edec.notification.service.NotificationEsoService;
import org.edec.notification.service.impl.NotificationDecServiceImpl;
import org.edec.notification.service.impl.NotificationEsoImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;


public class IndexPageCtrl extends CabinetSelector {

	@Wire
	private Button btnRefresh;
	@Wire
	private Combobox cmbInst, cmbFormOfStudy;
	@Wire
	private Listbox box;
	@Wire
	private Radio radioStudent, radioEmployee;
	@Wire
	private Tab tabIncoming;
	@Wire
	private Vbox vboxEmployee, vboxStudent;

	private ComponentService componentService = new ComponentServiceESOimpl();
	private NotificationEsoService notificationEsoService = new NotificationEsoImpl();
	private NotificationEsoManager notificationEsoManager = new NotificationEsoManager();
	private NotificationDecService notificationDecService = new NotificationDecServiceImpl();

	private List<Department> departments = new ArrayList<>();
	private List<Incoming> incomingList = new ArrayList<>();

	@Override
	protected void fill () {
		componentService.fillCmbInst(cmbInst, cmbInst.getParent(), currentModule.getDepartments());
		componentService.fillCmbFormOfStudy(cmbFormOfStudy, cmbFormOfStudy.getParent(), currentModule.getFormofstudy());
	}

	@Listen("onCheck = #radioStudent")
	public void checkRadioStudent () {
		vboxEmployee.setVisible(false);
		vboxStudent.setVisible(true);
	}

	@Listen("onCheck = #radioEmployee")
	public void checkRadioEmployee () {
		vboxStudent.setVisible(false);
		vboxEmployee.setVisible(true);
	}

	@Listen("onSelect = #tabIncoming")
	public void selectTabIncoming () {
		//Запрос всех диалогов
		incomingList = notificationDecService.getAllIncoming();
		displayIncoming();
	}

	@Listen("onSelect = #box")
	public void show () {
		Map arg = new LinkedHashMap();
		arg.put("nameWindow", box.getSelectedItem().getLabel());
		arg.put("humanfaceId", incomingList.get(box.getSelectedIndex()).getIdHuman());
		arg.put("userDialog", incomingList.get(box.getSelectedIndex()).getUserDialog());

		ComponentHelper.createWindow("/notification/windowChatDecanat.zul", "windowChatDecanat", arg).doModal();
		box.clearSelection();
		incomingList = notificationDecService.getAllIncoming();
		displayIncoming();
	}

	@Listen("onClick = #btnRefresh")
	public void incomingRefresh () {
		//Запрос всех диалогов
		incomingList = notificationDecService.getAllIncoming();
		displayIncoming();
	}

	@Listen("onClick = #btnSearch")
	public void searchHuman () {
		FormOfStudy selectedFos = cmbFormOfStudy.getSelectedItem().getValue();
		InstituteModel selectedInst = cmbInst.getSelectedItem().getValue();
		if (selectedFos.getType() == 3 || selectedInst.getIdInst() == null) {
			PopupUtil.showWarning("Выберите форму контроля и институт!");
			return;
		}
		if (radioEmployee.isSelected()) {
			//departments = notificationEsoService.getEmployeeDepartment()
		} else if (radioStudent.isSelected()) {

		}
	}

	private void displayIncoming () {
		box.getItems().clear();
		for (Incoming dialog : incomingList) {
			Listitem listitem = new Listitem();
			Listcell listcellName = new Listcell();
			HumanfaceModel humanfaceModel = notificationEsoManager.getHumanfaceModel(dialog.getIdHuman());
			listcellName.setLabel(humanfaceModel.getFio());
			Listcell listcellUnread = new Listcell();
			if (! dialog.getNumberUnreadMessages().equals(0)) {
				listcellUnread.setLabel(dialog.getNumberUnreadMessages().toString());
			} else {
				listcellUnread.setLabel("");
			}

			listitem.appendChild(listcellName);
			listitem.appendChild(listcellUnread);
			box.appendChild(listitem);
		}

	}
}