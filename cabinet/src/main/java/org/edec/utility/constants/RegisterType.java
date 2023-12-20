package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.edec.utility.fileManager.FileModel;

@Getter
@AllArgsConstructor
public enum RegisterType {
    MAIN(1, -1, "","основая", FileModel.SubTypeDocument.MAIN),
    MAIN_RETAKE(2, -2, "/о", "общая", FileModel.SubTypeDocument.COMMON_RETAKE),
    INDIVIDUAL_RETAKE(4, -4, "/и", "индивидуальная", FileModel.SubTypeDocument.INDIVIDUAL_RETAKE),
    COMMISSION(3, -3, "/к", "комиссионная", FileModel.SubTypeDocument.COMMISSION_RETAKE);

    private int retakeCount;
    private int notSignRetakeCount;
    private String suffix;
    private String name;
    private FileModel.SubTypeDocument subTypeDocument;

    public static RegisterType getRegisterTypeByRetakeCount(int retakeCount) {
        for (RegisterType type : RegisterType.values()) {
            if (type.retakeCount == Math.abs(retakeCount)) {
                return type;
            }
        }

        return null;
    }
}
