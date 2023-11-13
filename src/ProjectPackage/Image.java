package ProjectPackage;
import java.io.*;

public class Image {
    private String filepath;

    public Image() {
        this.filepath = null;
    }

    public static String callingObjectDetectionService(String filepath){
        try {
            ProcessBuilder builder = new ProcessBuilder("python", "C:\\Users\\84901\\object_dectection.py", filepath);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        Image test = new Image();
        test.callingObjectDetectionService("C:\\Users\\84901\\Downloads\\test.jpg");
    }
}
