package cyber;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try{
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream("people.xml"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}