package cyber;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class XmlExporter {
    public void write(String outputPath, Map<String, PersonInformation> personData) throws Exception {
        PeopleXml root = new PeopleXml();
        root.total = personData.size();

        ArrayList<Map.Entry<String, PersonInformation>> entries = new ArrayList<>(personData.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        for (Map.Entry<String, PersonInformation> entry : entries) {
            PersonInformation source = entry.getValue();
            PersonXml target = new PersonXml();
            target.id = trim(entry.getKey());
            target.name = buildName(source);
            target.gender = trim(source.gender);
            target.mother = toRef(source.motherIdName);
            target.father = toRef(source.fatherIdName);
            target.spouse = firstExistingRef(source.husbandIdName, source.wifeIdName, source.spouceCheck);
            target.parents = toParents(source.parentCheck);
            target.siblings = toSiblings(source.brothers, source.sisters);
            target.children = toChildren(source.sons, source.daughters);
            root.person.add(target);
        }

        JAXBContext context = JAXBContext.newInstance(PeopleXml.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setSchema(loadSchema());
        marshaller.marshal(root, new File(outputPath));
    }

    private NameXml buildName(PersonInformation source) {
        NameXml name = new NameXml();
        name.first = trim(source.firstname);
        name.last = trim(source.lastname);
        if (name.first == null && name.last == null && source.personIdName.name != null) {
            String[] parts = source.personIdName.name.trim().split("\\s+", 2);
            if (parts.length > 0) name.first = trim(parts[0]);
            if (parts.length > 1) name.last = trim(parts[1]);
        }
        return (name.first == null && name.last == null) ? null : name;
    }

    private ParentsXml toParents(Collection<IdName> source) {
        ParentsXml result = new ParentsXml();
        for (IdName value : source) addRef(result.parent, value);
        return result.parent.isEmpty() ? null : result;
    }

    private SiblingsXml toSiblings(Collection<IdName> brothers, Collection<IdName> sisters) {
        SiblingsXml result = new SiblingsXml();
        for (IdName value : brothers) addRef(result.brother, value);
        for (IdName value : sisters) addRef(result.sister, value);
        return (result.brother.isEmpty() && result.sister.isEmpty()) ? null : result;
    }

    private ChildrenXml toChildren(Collection<IdName> sons, Collection<IdName> daughters) {
        ChildrenXml result = new ChildrenXml();
        for (IdName value : sons) addRef(result.son, value);
        for (IdName value : daughters) addRef(result.daughter, value);
        return (result.son.isEmpty() && result.daughter.isEmpty()) ? null : result;
    }

    private RefXml firstExistingRef(IdName... values) {
        for (IdName value : values) {
            RefXml ref = toRef(value);
            if (ref != null) return ref;
        }
        return null;
    }

    private void addRef(Collection<RefXml> target, IdName source) {
        RefXml ref = toRef(source);
        if (ref != null) target.add(ref);
    }

    private RefXml toRef(IdName source) {
        if (source == null) return null;
        String id = trim(source.id);
        if (id == null || "UNKNOWN".equals(id) || "NONE".equals(id)) return null;
        RefXml ref = new RefXml();
        ref.ref = id;
        return ref;
    }

    private String trim(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private Schema loadSchema() throws Exception {
        URL schemaUrl = XmlExporter.class.getResource("/people-schema.xsd");
        if (schemaUrl == null) throw new IllegalStateException("Schema people-schema.xsd is missing");
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaUrl);
    }
}
