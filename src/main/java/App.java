import java.io.*;

public class App {
    public static void main(String[] args) {
        try(Reader in = new BufferedReader(new FileReader(args[0]));
            BufferedWriter out = new BufferedWriter(new FileWriter(args[1]))) {
            String result = MacroProcessor.apply(MacroProcessor.readerToString(in));
            out.write(result, 0, result.length());
            out.flush();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        catch(IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
