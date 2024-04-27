package com.lily.lilyojcommon.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by lily via on 2024/4/8 10:03
 */

@AllArgsConstructor
@Getter
public enum LanguageEnum {

    JAVA("JAVA", "1.8", "JAVA"),
    CPP("cplusplus", "1.5", "C++"),
    GOLANG("golang", "", "golang"),
    JAVASCRIPT("javascript", "", "javascript"),
    TYPESCRIPT("typescript", "", "typescript"),
    PYTHON("python", "3.0+", "python");


    private String name;

    private String version;

    private String message;

    public static Boolean isLanguageEnum(String name) {
        for (LanguageEnum languageEnum : LanguageEnum.values()) {
            if (languageEnum.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
