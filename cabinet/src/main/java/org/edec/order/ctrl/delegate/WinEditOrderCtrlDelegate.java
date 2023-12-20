package org.edec.order.ctrl.delegate;

import org.edec.order.ctrl.WinEditOrderCtrl;


public final class WinEditOrderCtrlDelegate {
    private WinEditOrderCtrl win;

    public WinEditOrderCtrlDelegate (WinEditOrderCtrl win) {
        this.win = win;
    }

    public void updateUI () {
        win.fillContent();
    }

    public boolean isReadOnlyMode () { return win.isReadOnly(); }
}
