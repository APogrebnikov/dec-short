package org.edec.utility.pdfViewer.manager;

import org.edec.dao.DAO;
import org.edec.utility.pdfViewer.model.SignVis;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;

import java.util.List;

public class SignService {
    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    public List<SignVis> getSignsForOrder(Long id){
        List<WorkflowModel> wflist = workflowService.getArchiveTasksConfirmingByIdBP(id);
        return null;
    }
}
