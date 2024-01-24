package br.com.pipoca.PipocaAgilBackend.communication;

import br.com.pipoca.PipocaAgilBackend.enums.MailTypeEnum;
import br.com.pipoca.PipocaAgilBackend.exceptions.InternalErrorException;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Communication {
    public void MailServiceMessage(String userName, String userEmail, MailTypeEnum mailType, String bodyParam) throws InternalErrorException {
        try {
            URL url = new URL("http://localhost:3000/mail/send");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar a requisição
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Escrever os dados no corpo da requisição
            String requestBody = "{"
                    + "\"userName\":\"" + userName + "\","
                    + "\"userEmail\":\"" + userEmail + "\","
                    + "\"mailType\":\"" + mailType.getValue() + "\","
                    + "\"bodyParam\":\"" + bodyParam + "\""
                    + "}";
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(requestBody);
                wr.flush();
            }
            int responseCode = connection.getResponseCode();
            System.out.println("Código de resposta: " + responseCode);
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }
}
