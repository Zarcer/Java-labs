package cyber;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

public class XmlExporter {
    public void write(String outputPath, Map<String, PersonInformation> personData) throws Exception {
        try (FileOutputStream output = new FileOutputStream(outputPath)) {
            XMLStreamWriter writer = new IndentingXMLStreamWriter(
                    XMLOutputFactory.newInstance().createXMLStreamWriter(output, "UTF-8")
            );
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("people");
            writer.writeAttribute("total", String.valueOf(personData.size()));
            for (Map.Entry<String, PersonInformation> entry : personData.entrySet()) {
                PersonInformation person = entry.getValue();
                writer.writeStartElement("person");
                writer.writeAttribute("id", entry.getKey());
                if (person.personIdName.name != null && !person.personIdName.name.trim().isEmpty()) {
                    writer.writeAttribute("fullname", person.personIdName.name.trim());
                }
                if (person.gender != null && !person.gender.trim().isEmpty()) {
                    writer.writeAttribute("gender", person.gender.trim());
                }
                writeRef(writer, "mother", person.motherIdName);
                writeRef(writer, "father", person.fatherIdName);
                writeRef(writer, "husband", person.husbandIdName);
                writeRef(writer, "wife", person.wifeIdName);
                writeRef(writer, "spouce", person.spouceCheck);
                writeRefs(writer, "parent", person.parentCheck);
                writeRefs(writer, "brother", person.brothers);
                writeRefs(writer, "sister", person.sisters);
                writeRefs(writer, "son", person.sons);
                writeRefs(writer, "daughter", person.daughters);
                writer.writeEndElement();
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        }
    }

    private void writeRefs(XMLStreamWriter writer, String tag, Collection<IdName> values) throws Exception {
        for (IdName value : values) {
            writeRef(writer, tag, value);
        }
    }

    private void writeRef(XMLStreamWriter writer, String tag, IdName value) throws Exception {
        if (!valid(value)) return;
        writer.writeStartElement(tag);
        if (value.id != null && !value.id.trim().isEmpty()) writer.writeAttribute("id", value.id.trim());
        if (value.name != null && !value.name.trim().isEmpty()) writer.writeAttribute("name", value.name.trim());
        writer.writeEndElement();
    }

    private boolean valid(IdName value) {
        if (value == null) return false;
        boolean hasId = value.id != null && !value.id.trim().isEmpty();
        boolean hasName = value.name != null && !value.name.trim().isEmpty();
        if (!hasId && !hasName) return false;
        if (!hasName) return true;
        String n = value.name.trim();
        if ("NONE".equals(n) || "UNKNOWN".equals(n)) return hasId;
        return true;
    }
}
