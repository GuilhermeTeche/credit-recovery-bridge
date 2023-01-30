package br.com.itau.creditrecoverybridge.util;

public final class Constants {
    public static final String INVALID_SENDER = "Possível tentativa de fraude - Remetente não corresponde a transação atual";
    public static final String FAILED_COMUNICATION_PAYSTUDIO = "Falha na comunicação com o Servidor de autorização";

    public static final String MESSAGE_REGEX_UUID = "permitido somente letras, números e traço(-), na forma 8-4-4-4-12";
    public static final String MESSAGE_ONLY_NUMBERS = "permitido somente números";
    public static final String MESSAGE_ONLY_LETTERS_SPACE = "permitido somente letras e espaço";
    public static final String MESSAGE_15_CARACTERS = "deve conter 15 caracteres";
    public static final String MESSAGE_VALID_MONTH = "utilize um mês válido";
    public static final String MESSAGE_VALID_YEAR = "utilize um ano válido";

    public static final String REGEX_UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    private Constants() {}
}
