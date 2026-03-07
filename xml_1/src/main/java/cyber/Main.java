package cyber;


public class Main {
    public static void main(String[] args) {
        try{
            Parser parser = new Parser("people.xml");
            XmlExporter exporter = new XmlExporter();
            exporter.write("output.xml", parser.parse());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}