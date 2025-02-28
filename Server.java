package application;


import java.io.*;
import java.net.*;

public class Server {
   
    private static final String LogFilePath = "log.txt";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server is waitting........ " );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from client.... " );

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String[] parts = reader.readLine().split(",");
                String command = parts[0];

                if ("RetrieveLogs".equals(command)) {
                    String userName = parts[1];
                    retrieveAndSendLogs(userName, writer);
                } else {
                    double amount = Double.parseDouble(parts[0]);
                    double conversionRate = Double.parseDouble(parts[1]);
                    String clientName = parts[2];

                    double convertedAmount = convertCurrency(amount, conversionRate);
                    logConversion(clientName, amount, conversionRate, convertedAmount);

                    writer.println(convertedAmount);
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void retrieveAndSendLogs(String userName, PrintWriter writer) {
            try (BufferedReader logReader = new BufferedReader(new FileReader(LogFilePath))) {
                String line;
                while ((line = logReader.readLine()) != null) {
                    if (line.contains(userName)) {
                        writer.println(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                writer.println("Error retrieving logs: " + e.getMessage());
            }
        }

        private static double convertCurrency(double amount, double conversionRate) {
            return amount * conversionRate;
        }

        private static void logConversion(String clientName, double amount, double conversionRate, double result) {
            try (PrintWriter logWriter = new PrintWriter(new FileWriter(LogFilePath, true))) {
                String logEntry = String.format("User: %s  Amount: %.2f  Conversion rate : %.2f.  Result: %.2f", clientName, amount, conversionRate, result);
                logWriter.println(logEntry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
