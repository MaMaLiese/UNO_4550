import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Help {
    public void printHelp() {
        File file = new File("help.txt");
        BufferedReader bufferedReader = null;

        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String zeile;
            while ((zeile = bufferedReader.readLine()) != null) {
                System.out.println(zeile);
            }
        } catch (IOException e) {
            System.out.println("Error...");
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}