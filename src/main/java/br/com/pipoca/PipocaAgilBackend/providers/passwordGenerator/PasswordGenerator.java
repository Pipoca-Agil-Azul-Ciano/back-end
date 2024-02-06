package br.com.pipoca.PipocaAgilBackend.providers.passwordGenerator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class PasswordGenerator {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%&*";

    public String generate() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        appendRandomCharacter(password, UPPERCASE_LETTERS, random);
        appendRandomCharacter(password, LOWERCASE_LETTERS, random);

        appendRandomCharacter(password, DIGITS, random);
        appendRandomCharacter(password, DIGITS, random);

        appendRandomCharacter(password, SPECIAL_CHARACTERS, random);

        String allCharacters = UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS + SPECIAL_CHARACTERS;
        for (int i = 5; i < 12; i++) {
            appendRandomCharacter(password, allCharacters, random);
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }

        return new String(passwordArray);
    }

    private static void appendRandomCharacter(StringBuilder sb, String characters, SecureRandom random) {
        char randomChar = characters.charAt(random.nextInt(characters.length()));
        sb.append(randomChar);
    }
}