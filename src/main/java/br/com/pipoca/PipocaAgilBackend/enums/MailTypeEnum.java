package br.com.pipoca.PipocaAgilBackend.enums;

public enum MailTypeEnum {
    RECOVERYPASSWORD("RecoveryPassword"),
    WELCOME("Welcome");

    private final String value;

    MailTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}