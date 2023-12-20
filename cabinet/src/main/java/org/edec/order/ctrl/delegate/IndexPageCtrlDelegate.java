package org.edec.order.ctrl.delegate;

import org.edec.order.ctrl.IndexPageCtrl;


public class IndexPageCtrlDelegate {
    private IndexPageCtrl ctrl;

    public IndexPageCtrlDelegate (IndexPageCtrl ctrl) {
        this.ctrl = ctrl;
    }

    public void updateUI () {
        ctrl.refreshData();
    }
}
