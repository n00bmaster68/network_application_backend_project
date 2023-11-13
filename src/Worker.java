import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ProjectPackage.*;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Worker implements Runnable {
    private static final String AES_KEY = "ABC123@#!udm1212";
    private static final String WEATHER_API = "https://api.tomorrow.io/v4/weather/realtime?location=";
    private static final String API_KEY = "&apikey=eS4NkSMYqTd393BDuJdWsclZm3XThLS5";
    private static final String ANSWER_MID = " có nhiệt độ hiện tại là ";
    private static final String CAL_API = "https://api.mathjs.org/v4/?expr=";
    private static final String PREFIX_CAL = "--> Kết quả phép tính: ";
    private static final String PREFIX_WEATHER = "--> Chức năng tra nhiệt độ: ";
    private static final String CAL_ERROR = "--> Phép tính có lỗi, không tính kết quả được.";
    private Socket socket;
    public Worker(Socket s) {
        this.socket = s;
    }
    
    public void run() {
        System.out.println("Client" + socket.toString() + " accepted");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String input = "";
            while(true) {
                input = in.readLine();
                System.out.println("Server received: " + input + " from " + socket.toString());
                if(input.equals("bye"))
                    break;
                else if (input.contains("weather") && input.split(" ").length >= 2) {
                    out.write(getWeatherInfo(input.replace("weather ", "")));
                }
                    
                else if (input.contains("calc ") && input.split(" ").length == 2) 
                    out.write(calculateExp(input.replace("calc ", "")));
                else out.write("Lỗi dữ liệu đầu vào!");
                out.newLine();
                out.flush();
            }
            System.out.println("Closed socket for client " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getWeatherInfo(String keyword) {
        String result = "";
        try {
            String url = WEATHER_API + URLEncoder.encode(keyword, "UTF-8") + API_KEY;
            System.out.println(url);
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).execute().parse();
            JSONObject obj = new JSONObject(doc.text());
            System.out.println(obj);
            Float temperature = obj.getJSONObject("data").getJSONObject("values").getFloat("temperature");
            String location = obj.getJSONObject("location").getString("name");
            result = PREFIX_WEATHER + location + ANSWER_MID + temperature;

        } catch (Exception e) {
            result = e.getMessage();
        }
        
        return result;
    }

    public static String calculateExp(String expression) {
        String result = "";
        try {
            String url = CAL_API + URLEncoder.encode(expression, "UTF-8");
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).execute().parse();
            result = PREFIX_CAL + doc.body().text();

        } catch (Exception e) {
            result = CAL_ERROR;
        }
        
        return result;
    }

    public static byte[] encrypt(String message) {
        try {
            byte[] plainData = message.getBytes();
            byte[] keyBytes = AES_KEY.getBytes();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(plainData);
        } catch (Exception e) {
            return null;
        }
        
    }

    public static String decrypt(byte[] cipherString) {
        try {
            byte[] keyBytes = AES_KEY.getBytes();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(cipherString));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static String jsonStringResult(String status, String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("data", data);

        return jsonObject.toString();
    }

    public static String getMoviesInfor(String keyword){
        try {
            Movie object = new Movie();
            return jsonStringResult("success", object.searchMovie("Iron man"));
        } catch (Exception e) {
            return jsonStringResult("fail", e.getMessage());
        }
    }

    public static void main(String args[]) {
        String text = "{'function':'abc', 'data': 'xyz'}";
        byte[] encrypted = encrypt(text);
        System.out.println(encrypted);

        // getWeatherInfo("Hồ Chí Minh");
    }
}
    