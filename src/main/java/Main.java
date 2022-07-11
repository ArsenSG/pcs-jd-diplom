import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var srcDir = new File("pdfs");
        var engine = new BooleanSearchEngine(srcDir);
        var port = 8989;
        var server = new Server(port, engine);
        server.start();
    }
}