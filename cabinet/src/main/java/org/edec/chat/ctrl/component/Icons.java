package org.edec.chat.ctrl.component;

public enum Icons {

    INSERT_PHOTO("insert_photo"), INSERT_FILE("insert_drive_file"), INSERT_TEXT("insert_comment");

    private String name;

    Icons (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public String toString () {
        return this.name;
    }
}
