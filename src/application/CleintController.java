package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CleintController {

    @FXML
    private TextField amountInput;

    @FXML
    private TextField conversionRateInput;

    @FXML
    private TextField userNameInput;

    @FXML
    private TextArea resultTextArea;

    private String serverHost;
    private int serverPort;

    public void setServerInfo(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    @FXML
    private void convertButtonClicked() {
        try (Socket socket = new Socket(serverHost, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            double amount = Double.parseDouble(amountInput.getText());
            double conversionRate = Double.parseDouble(conversionRateInput.getText());
            String userName = userNameInput.getText();

            String requestStr = String.format("%.2f,%.2f,%s", amount, conversionRate, userName);
            writer.println(requestStr);

            String response = reader.readLine();
            if (response != null) {
                double convertedAmount = Double.parseDouble(response);
                resultTextArea.setText(String.format("Converted Amount: %.2f", convertedAmount));
            } else {
                resultTextArea.setText("Empty response received from the server.");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            resultTextArea.setText( e.getMessage());
        }
    }

    @FXML
    private void retrieveLogsButtonClicked() {
        try (Socket socket = new Socket(serverHost, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String userName = userNameInput.getText();
            writer.println("RetrieveLogs," + userName);

            StringBuilder logs = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }

            resultTextArea.setText("Conversion logs:\n" + logs.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
