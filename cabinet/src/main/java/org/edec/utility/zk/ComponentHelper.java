package org.edec.utility.zk;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.Window;

import java.util.Map;

public class ComponentHelper {
    public static Window createWindow (String pathZul, String idWindow, Map arg) {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny(idWindow) != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny(idWindow).detach();
        }
        return (Window) Executions.createComponents(pathZul, null, arg);
    }

    public static Object findArg (String arg) {
        return Executions.getCurrent().getArg().get(arg);
    }

    public static Component find (Component root, String id) {
        if (root instanceof IdSpace) {
            Component found = root.query("#" + id);
            if (found != null) {
                return found;
            }
        }

        for (Component child : root.getChildren()) {
            Component found = find(child, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}