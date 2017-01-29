package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Controller {
    @FXML
    private TextField textCity;
    @FXML
    private TextArea textResponse;
    private Alert alert;

    @FXML
    public void sendServer() {
        textResponse.clear();
        int serverPort = 2000; // здесь обязательно нужно указать порт к которому привязывается сервер.
        String address = "169.254.114.205"; // это IP-адрес компьютера, где исполняется наша серверная программа.
        // Здесь указан адрес того самого компьютера где будет исполняться и клиент.
        if (textCity.getText() == null || textCity.getText().length() == 0) {
            error("Пожалуйста, заполните поле");
        } else {
            try {
                InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.
                textResponse.appendText("IP-адрес " + address + " и порт " + serverPort + "\n");
                Socket socket = new Socket(ipAddress, serverPort); // создаем сокет используя IP-адрес и порт сервера.

                // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом.
                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);

                while (true) {
                    String line = textCity.getText();
                    textResponse.appendText("Город: "+line+"\n");
                    textResponse.appendText("Отсылаем серверу\n");
                    out.writeUTF(line); // отсылаем введенную строку текста серверу.
                    out.flush(); // заставляем поток закончить передачу данных.
                    line = in.readUTF(); // ждем пока сервер отошлет строку текста.
                    if(line.equals("error")) {
                        textResponse.appendText("Не удалось получить погоду для заданного города\n");
                        error("Введите корректное название города. Например, Oslo");
                    } else {
                        textResponse.appendText("Ответ: " + line + " градусов Цельсия\n");
                    }
                    break;
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    public void error(String s) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Некорректный ввод");
        alert.setHeaderText(s);
        alert.showAndWait();
    }
}
