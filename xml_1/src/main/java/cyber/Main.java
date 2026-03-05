package cyber;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {
        try{
            Parser parser = new Parser("people.xml");
            parser.parse();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}