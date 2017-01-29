package weather;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Julia on 11.11.2016.
 */
public class ServerS {
    public static void main(String[] ar) {
        int port = 2000;
        try {
            String responseToClient = null;
            String request = null;

            ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Ожидаем клиентов");

            while (true) {
                Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
                System.out.println("Клиент подключен");

                // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);
                request = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("Клиент запросил " + request);
                // check if cache/line.txt exist

                String filePathString = "src/main/cache/" + request + ".txt";

                List<String> resIfE = null; // response if file exists

                if (new File(filePathString).isFile() && new File(filePathString).exists() && !new File(filePathString).isDirectory()) {
                    System.out.println("\nFile exists! Loading data!\n");
                    try {
                        resIfE = Files.readAllLines(Paths.get(filePathString), Charset.defaultCharset());

                        // date
                        String stringDate = resIfE.get(0);
                        String stringDateFormat = "EEE MMM dd HH:mm:ss z yyyy";
                        SimpleDateFormat format = new SimpleDateFormat(stringDateFormat, Locale.US);
                        Date fileDate = format.parse(stringDate);


                        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.HOUR, -1);

                        boolean needToUpdate = cal.getTime().after(fileDate);

                        if (needToUpdate) {
                            System.out.println("\nRequest to Yahoo to update data!\n");
                            responseToClient = logWeather(filePathString, request);
                        } else {
                            responseToClient = resIfE.get(1);
                        }

                        System.out.println(responseToClient + " градусов по Цельсию");
                    } catch (Exception ex) {
                        responseToClient = "error";
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("\nRequest to Yahoo!\n");
                    try {
                        responseToClient = logWeather(filePathString, request);
                    } catch (NullPointerException ex) {
                        responseToClient = "error";
                        ex.printStackTrace();
                    }
                }
                System.out.println("Отсылаем клиенту " + responseToClient + " градусов по Цельсию");
                out.writeUTF(responseToClient); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static String logWeather(String filePathString, String request) throws java.io.FileNotFoundException {
        File f = new File(filePathString);
        PrintWriter outWriter = new PrintWriter(f);

        Weather newWeather = null; // get weather
        try {
            newWeather = new Weather(request);
        } catch (JAXBException e) {
            e.printStackTrace();
            System.out.println("JAXBException");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
        }

        outWriter.println(newWeather.showDate().toString());
        outWriter.println(Integer.toString(newWeather.showTemp()));
        outWriter.close();
        return Integer.toString(newWeather.showTemp());
    }
}
