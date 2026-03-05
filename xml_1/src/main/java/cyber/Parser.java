package cyber;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;

public class Parser {
    HashMap<String, PersonInformation> personData = new HashMap<>();
    HashMap<String, String> nameToId = new HashMap<>();
    String path;
    public Parser(String path){
        this.path=path;

    }
    public HashMap<String, PersonInformation> parse() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(path));
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("person".equals(reader.getLocalName())) {
                    String id = reader.getAttributeValue(null, "id");
                    String[] nameConstruct = new String[3];
                    nameConstruct[2] = reader.getAttributeValue(null, "name");
                    nameConstruct[0] = null;
                    nameConstruct[1] = null;
                    while (reader.hasNext()) {
                        reader.next();
                        event = reader.getEventType();
                        if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("person")) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT) {
                            switch (reader.getLocalName()) {
                                case "id":
                                    id = reader.getAttributeValue(null, "value");
                                    break;
                                case "fullname":
                                    while (reader.hasNext()) {
                                        reader.next();
                                        if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("fullname")) {
                                            break;
                                        }
                                        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                            switch (reader.getLocalName()) {
                                                case "first":
                                                    nameConstruct[0] = reader.getElementText().trim();
                                                    break;
                                                case "family":
                                                    nameConstruct[1] = reader.getElementText().trim();
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "firstname":
                                    String firstname = reader.getAttributeValue(null, "value");
                                    if (firstname == null) {
                                        nameConstruct[0] = reader.getElementText().trim();
                                    } else {
                                        nameConstruct[0] = firstname.trim();
                                    }
                                    break;
                                case "surname":
                                case "family":
                                case "family-name":
                                    if (reader.getLocalName().equals("surname")) {
                                        nameConstruct[1] = reader.getAttributeValue(null, "value").trim();
                                    } else if (reader.getLocalName().equals("family") || reader.getLocalName().equals("family-name")) {
                                        nameConstruct[1] = reader.getElementText().trim();
                                    }
                                    break;
                            }
                        }
                    }
                    if (id != null) {
                        if (!personData.containsKey(id)) {
                            personData.put(id, new PersonInformation(id));
                        }
                        if (nameConstruct[2] != null) {
                            String[] temp = nameConstruct[2].trim().split("\\s+", 2);
                            nameConstruct[0] = temp[0];
                            nameConstruct[1] = temp[1];
                            personData.get(id).firstname = nameConstruct[0];
                            personData.get(id).lastname = nameConstruct[1];
                        } else {
                            if (nameConstruct[0] != null) {
                                personData.get(id).firstname = nameConstruct[0];
                            }
                            if (nameConstruct[1] != null) {
                                personData.get(id).lastname = nameConstruct[1];
                            }
                        }
                        if (personData.get(id).firstname != null && personData.get(id).lastname != null) {
                            personData.get(id).personIdName.name = personData.get(id).firstname + " " + personData.get(id).lastname;
                        }
                    }
                }
            }

        }
        for (Map.Entry<String, PersonInformation> entry : personData.entrySet()) {
            nameToId.put(entry.getValue().personIdName.name, entry.getKey());
        }
        XMLStreamReader readerS = factory.createXMLStreamReader(new FileInputStream(path));
        while (readerS.hasNext()) {
            int event = readerS.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("person".equals(readerS.getLocalName())) {
                    String id = readerS.getAttributeValue(null, "id");
                    String[] nameConstruct = new String[3];
                    nameConstruct[2] = readerS.getAttributeValue(null, "name");
                    nameConstruct[0] = null;
                    nameConstruct[1] = null;
                    PersonInformation current = new PersonInformation(null);
                    while (readerS.hasNext()) {
                        readerS.next();
                        event = readerS.getEventType();
                        if (event == XMLStreamConstants.END_ELEMENT && readerS.getLocalName().equals("person")) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT) {
                            switch (readerS.getLocalName()) {
                                case "id":
                                    id = readerS.getAttributeValue(null, "value");
                                    break;
                                case "fullname":
                                    while (readerS.hasNext()) {
                                        readerS.next();
                                        if (readerS.getEventType() == XMLStreamConstants.END_ELEMENT && readerS.getLocalName().equals("fullname")) {
                                            break;
                                        }
                                        if (readerS.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                            switch (readerS.getLocalName()) {
                                                case "first":
                                                    nameConstruct[0] = readerS.getElementText().trim();
                                                    break;
                                                case "family":
                                                    nameConstruct[1] = readerS.getElementText().trim();
                                                    break;
                                            }
                                            nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
                                        }
                                    }
                                    break;
                                case "firstname":
                                    String firstname = readerS.getAttributeValue(null, "value");
                                    if (firstname == null) {
                                        nameConstruct[0] = readerS.getElementText().trim();
                                    } else {
                                        nameConstruct[0] = firstname.trim();
                                    }
                                    if (nameConstruct[1] != null) {
                                        nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
                                    }
                                    break;
                                case "surname":
                                case "family":
                                case "family-name":
                                    if (readerS.getLocalName().equals("surname")) {
                                        nameConstruct[1] = readerS.getAttributeValue(null, "value").trim();
                                    } else if (readerS.getLocalName().equals("family") || readerS.getLocalName().equals("family-name")) {
                                        nameConstruct[1] = readerS.getElementText().trim();
                                    }
                                    if (nameConstruct[0] != null) {
                                        nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
                                    }
                                    break;
                                case "gender":
                                    String gender = readerS.getAttributeValue(null, "value");
                                    if (gender != null) {
                                        if (gender.trim().toLowerCase().startsWith("m")) {
                                            current.gender = "Male";
                                        } else if (gender.trim().toLowerCase().startsWith("f")) {
                                            current.gender = "Female";
                                        }
                                    } else {
                                        gender = readerS.getElementText().trim().toLowerCase();
                                        if (gender.startsWith("m")) {
                                            current.gender = "Male";
                                        } else if (gender.startsWith("f")) {
                                            current.gender = "Female";
                                        }
                                    }
                                    break;
                                case "husband":
                                    current.husbandIdName.id = readerS.getAttributeValue(null, "value").trim();
                                    current.husbandIdName.name = personData.get(readerS.getAttributeValue(null, "value")).personIdName.name;
                                    break;
                                case "wife":
                                    current.wifeIdName.id = readerS.getAttributeValue(null, "value").trim();
                                    current.wifeIdName.name = personData.get(readerS.getAttributeValue(null, "value")).personIdName.name;
                                    break;
                                case "spouce":
                                    String spouceName = readerS.getAttributeValue(null, "value");
                                    if (spouceName != null && !spouceName.equals("NONE")) {
                                        current.spouceCheck.name = spouceName.trim().replaceAll("\\s+", " ");
                                        current.spouceCheck.id = nameToId.get(spouceName.trim().replaceAll("\\s+", " "));
                                    }
                                    break;
                                case "mother":
                                    String motherName = readerS.getElementText().trim().replaceAll("\\s+", " ");
                                    current.motherIdName.name = motherName;
                                    current.motherIdName.id = nameToId.get(motherName);
                                    break;
                                case "father":
                                    String fatherName = readerS.getElementText().trim().replaceAll("\\s+", " ");
                                    current.fatherIdName.name = fatherName;
                                    current.fatherIdName.id = nameToId.get(fatherName);
                                    break;
                                case "parent":
                                    String parentId = readerS.getAttributeValue(null, "value");
                                    if (parentId != null && !parentId.equals("UNKNOWN")) {
                                        current.parentCheck.add(new IdName(personData.get(parentId).personIdName.name, parentId));
                                    }
                                    break;
                                case "siblings-number":
                                    current.siblingsNumber = Integer.parseInt(readerS.getAttributeValue(null, "value").trim());
                                    break;
                                case "siblings":
                                    String siblingsId = readerS.getAttributeValue(null, "val");
                                    if (siblingsId != null) {
                                        String[] parts = siblingsId.trim().split("\\s+");
                                        for (String record : parts) {
                                            current.siblingsCheck.add(new IdName(personData.get(record).personIdName.name, record));
                                        }
                                    } else {
                                        while (readerS.hasNext()) {
                                            readerS.next();
                                            if (readerS.getEventType() == XMLStreamConstants.END_ELEMENT && readerS.getLocalName().equals("siblings")) {
                                                break;
                                            }
                                            if (readerS.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                                switch (readerS.getLocalName()) {
                                                    case "brother":
                                                        String brotherName = readerS.getElementText().trim().replaceAll("\\s+", " ");
                                                        current.brothers.add(new IdName(brotherName, nameToId.get(brotherName)));
                                                        break;
                                                    case "sister":
                                                        String sisterName = readerS.getElementText().trim().replaceAll("\\s+", " ");
                                                        current.sisters.add(new IdName(sisterName, nameToId.get(sisterName)));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case "children-number":
                                    current.childrenNumber = Integer.parseInt(readerS.getAttributeValue(null, "value").trim());
                                    break;
                                case "children":
                                    while (readerS.hasNext()) {
                                        readerS.next();
                                        if (readerS.getEventType() == XMLStreamConstants.END_ELEMENT && readerS.getLocalName().equals("children")) {
                                            break;
                                        }
                                        if (readerS.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                            switch (readerS.getLocalName()) {
                                                case "son":
                                                    current.sons.add(new IdName(personData.get(readerS.getAttributeValue(null, "id").trim()).personIdName.name, readerS.getAttributeValue(null, "id").trim()));
                                                    break;
                                                case "daughter":
                                                    current.daughters.add(new IdName(personData.get(readerS.getAttributeValue(null, "id").trim()).personIdName.name, readerS.getAttributeValue(null, "id").trim()));
                                                    break;
                                                case "child":
                                                    String childName = readerS.getElementText().trim().replaceAll("\\s+", " ");
                                                    current.childrenCheck.add(new IdName(childName, nameToId.get(childName)));
                                                    break;
                                            }
                                        }
                                    }
                            }
                        }
                    }
                    if (nameConstruct[2] != null) {
                        nameConstruct[2] = nameConstruct[2].trim().replaceAll("\\s+", " ");
                    }
                    if (id == null && nameConstruct[2] != null) {
                        id = nameToId.get(nameConstruct[2]);
                    }
                    if (id != null) {
                        if (current.motherIdName.id != null) {
                            personData.get(id).motherIdName = current.motherIdName;
                        }
                        if (current.fatherIdName.id != null) {
                            personData.get(id).fatherIdName = current.fatherIdName;
                        }
                        if (!current.parentCheck.isEmpty()) {
                            personData.get(id).parentCheck.addAll(current.parentCheck);
                        }
                        if (current.gender != null) {
                            personData.get(id).gender = current.gender;
                        }
                        if (current.wifeIdName.id != null) {
                            personData.get(id).wifeIdName = current.wifeIdName;
                        }
                        if (current.husbandIdName.id != null) {
                            personData.get(id).husbandIdName = current.husbandIdName;
                        }
                        if (current.spouceCheck.id != null) {
                            personData.get(id).spouceCheck = current.spouceCheck;
                        }
                        if (current.siblingsNumber != 0) {
                            personData.get(id).siblingsNumber = current.siblingsNumber;
                        }
                        if (!current.brothers.isEmpty()) {
                            personData.get(id).brothers.addAll(current.brothers);
                        }
                        if (!current.sisters.isEmpty()) {
                            personData.get(id).sisters.addAll(current.sisters);
                        }
                        if (!current.siblingsCheck.isEmpty()) {
                            personData.get(id).siblingsCheck.addAll(current.siblingsCheck);
                        }
                        if (current.childrenNumber != 0) {
                            personData.get(id).childrenNumber = current.childrenNumber;
                        }
                        if (!current.daughters.isEmpty()) {
                            personData.get(id).daughters.addAll(current.daughters);
                        }
                        if (!current.sons.isEmpty()) {
                            personData.get(id).sons.addAll(current.sons);
                        }
                        if (!current.childrenCheck.isEmpty()) {
                            personData.get(id).childrenCheck.addAll(current.childrenCheck);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, PersonInformation> entry : personData.entrySet()) {
            if (!entry.getValue().parentCheck.isEmpty()) {
                for (IdName record : entry.getValue().parentCheck) {
                    PersonInformation parentInfo = personData.get(record.id);
                    if (parentInfo != null && "Male".equals(parentInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).fatherIdName.merge(record);
                    }
                    if (parentInfo != null && "Female".equals(parentInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).motherIdName.merge(record);
                    }
                }
            }
            if (!entry.getValue().spouceCheck.name.equals("NONE")) {
                if (Objects.equals(personData.get(entry.getValue().spouceCheck.id).gender, "Male")) {
                    personData.get(entry.getValue().personIdName.id).husbandIdName.merge(entry.getValue().spouceCheck);
                }
            }
            if (!entry.getValue().siblingsCheck.isEmpty()) {
                for (IdName record : entry.getValue().siblingsCheck) {
                    PersonInformation siblingInfo = personData.get(record.id);
                    if (siblingInfo != null && "Male".equals(siblingInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).brothers.add(record);
                    }
                    if (siblingInfo != null && "Female".equals(siblingInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).sisters.add(record);
                    }
                }
            }
            if (!entry.getValue().childrenCheck.isEmpty()) {
                for (IdName record : entry.getValue().childrenCheck) {
                    PersonInformation childInfo = personData.get(record.id);
                    if (childInfo != null && "Male".equals(childInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).sons.add(record);
                    }
                    if (childInfo != null && "Female".equals(childInfo.gender)) {
                        personData.get(entry.getValue().personIdName.id).daughters.add(record);
                    }
                }
            }
            if (entry.getValue().siblingsNumber != (entry.getValue().brothers.size() + entry.getValue().sisters.size())) {
                System.out.println("SIBLINGS NUMBER WRONG");
                System.out.println(entry.getValue().siblingsNumber + "should be");
                System.out.println(entry.getValue().brothers.size() + "brothers");
                System.out.println(entry.getValue().sisters.size() + "sisters");
                System.out.println(entry.getValue().personIdName.id+ " "+entry.getValue().personIdName.name);

            }
            if (entry.getValue().childrenNumber != (entry.getValue().daughters.size() + entry.getValue().sons.size())) {
                System.out.println("CHILDREN NUMBER WRONG");
                System.out.println(entry.getValue().childrenNumber + " should be");
                System.out.println(entry.getValue().daughters.size() + " daughters");
                System.out.println(entry.getValue().sons.size() + " sons");
                System.out.println(entry.getValue().personIdName.id+ " "+entry.getValue().personIdName.name);
            }
        }
        return personData;
    }
}