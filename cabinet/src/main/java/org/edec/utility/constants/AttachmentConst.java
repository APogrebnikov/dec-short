package org.edec.utility.constants;

public enum AttachmentConst {

    ALL(0, "Все"), ATTACHED(1, "Прикреплен"), NOT_ATTACHED(2, "Не прикреплен");

    private Integer value;
    private String name;

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    AttachmentConst (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AttachmentConst getName (Integer value) {
        for (AttachmentConst attachmentConst : AttachmentConst.values()) {
            if (attachmentConst.getValue().equals(value)) {
                return attachmentConst;
            }
        }
        return null;
    }

    public static AttachmentConst getValue (String name) {
        for (AttachmentConst attachmentConst : AttachmentConst.values()) {
            if (attachmentConst.getName().equals(name)) {
                return attachmentConst;
            }
        }
        return null;
    }

    public static String getNameByValue (int value) {
        for (AttachmentConst attachmentConst : AttachmentConst.values()) {
            if (attachmentConst.getValue().equals(value)) {
                return attachmentConst.getName();
            }
        }
        return "";
    }
}
