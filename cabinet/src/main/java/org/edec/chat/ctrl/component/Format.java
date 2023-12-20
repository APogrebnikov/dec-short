package org.edec.chat.ctrl.component;

import java.util.List;

public enum Format {

    PNG("png", TypeFormat.IMAGE), JPG("jpeg", TypeFormat.IMAGE), WEBP("webp", TypeFormat.IMAGE),

    PDF("pdf", TypeFormat.TEXT), DJVU("djvu", TypeFormat.TEXT), XML("xml", TypeFormat.TEXT), EPUB("epub", TypeFormat.TEXT), FB2("fb2",
                                                                                                                                TypeFormat.TEXT),

    DOC("doc", TypeFormat.TEXT), DOCX("docx", TypeFormat.TEXT), ODT("odt", TypeFormat.TEXT),

    PPT("ppt", TypeFormat.TEXT), PPTX("pptx", TypeFormat.TEXT), ODP("odp", TypeFormat.TEXT),

    XLS("xls", TypeFormat.TEXT), XLSX("xlsx", TypeFormat.TEXT), ODS("ods", TypeFormat.TEXT);

    private String name;

    private TypeFormat type;

    public String getName () {
        return name;
    }

    public TypeFormat getType () {
        return type;
    }

    Format (String name, TypeFormat type) {
        this.name = name;
        this.type = type;
    }

    public String toString () {
        return this.type + "/" + this.name;
    }

    public static boolean isImage (String formatStr) {
        return isType(formatStr, TypeFormat.IMAGE);
    }

    public static boolean isText (String formatStr) {
        return isType(formatStr, TypeFormat.TEXT);
    }

    private static boolean isType (String formatStr, TypeFormat type) {
        for (Format format : Format.values()) {
            if (format.getType().equals(type) && formatStr.toLowerCase().equals(format.getName())) {
                return true;
            }
        }

        return false;
    }

    private enum TypeFormat {
        TEXT, IMAGE
    }
}
