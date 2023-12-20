package org.edec.order.ctrl.delegate;

import org.edec.order.ctrl.WinAddStudentCtrl;


public class WinAddStudentCtrlDelegate {
    private WinAddStudentCtrl win;

    public WinAddStudentCtrlDelegate (WinAddStudentCtrl win) {
        this.win = win;
    }

    public void updateUI () {
        win.updateUI();
    }
}
