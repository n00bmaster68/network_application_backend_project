import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        BufferedReader stdin = null;

        try {
            socket = new Socket("127.0.0.1", 1234);
            System.out.println("Client connected");
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input = "";
            String response = "";
            while (true) {
                System.out.print("Client sent: ");
                input = stdin.readLine();
                out.write(input + '\n');
                out.flush();
                if (input.equals("bye")) {
                    break;
                }
                response = in.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (socket != null) {
                in.close();
                out.close();
                socket.close();
                System.out.println("Client socket closed");
            }
        }
    }

}
