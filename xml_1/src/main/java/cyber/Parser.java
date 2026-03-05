package cyber;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.*;

public class Parser {
    HashMap<String, PersonInformation> personData = new HashMap<>();
    HashMap<String, String> nameToId = new HashMap<>();

    public HashMap<String, PersonInformation> parse(XMLStreamReader reader) throws Exception {
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
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("person".equals(reader.getLocalName())) {
                    String id = reader.getAttributeValue(null, "id");
                    String[] nameConstruct = new String[3];
                    nameConstruct[2] = reader.getAttributeValue(null, "name");
                    nameConstruct[0] = null;
                    nameConstruct[1] = null;
                    PersonInformation current = new PersonInformation(null);
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
                                            nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
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
                                    if (nameConstruct[1] != null) {
                                        nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
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
                                    if (nameConstruct[0] != null) {
                                        nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
                                    }
                                    break;
                                case "gender":
                                    String gender = reader.getAttributeValue(null, "value");
                                    if(gender!=null){
                                        if(gender.trim().toLowerCase().startsWith("m")){
                                            current.gender="Male";
                                        }
                                        else if(gender.trim().toLowerCase().startsWith("f")){
                                            current.gender="Female";
                                        }
                                    }
                                    else {
                                        gender=reader.getElementText().trim().toLowerCase();
                                        if(gender.startsWith("m")){
                                            current.gender="Male";
                                        }
                                        else if(gender.startsWith("f")){
                                            current.gender="Female";
                                        }
                                    }
                                    break;
                                case "husband":
                                    current.husbandIdName.id=reader.getAttributeValue(null, "value").trim();
                                    current.husbandIdName.name=personData.get(reader.getAttributeValue(null,"value")).personIdName.name;
                                    break;
                                case "wife":
                                    current.wifeIdName.id=reader.getAttributeValue(null, "value").trim();
                                    current.wifeIdName.name=personData.get(reader.getAttributeValue(null,"value")).personIdName.name;
                                    break;
                                case "spouce":
                                    String spouceName = reader.getAttributeValue(null, "value");
                                    if(spouceName!=null){
                                        current.spouceCheck.name=spouceName.trim().replaceAll("\\s+", " ");
                                        current.spouceCheck.id=nameToId.get(spouceName.trim().replaceAll("\\s+", " "));
                                    }
                                    break;
                                case "mother":
                                    String motherName=reader.getElementText().trim().replaceAll("\\s+", " ");
                                    current.motherIdName.name=motherName;
                                    current.motherIdName.id=nameToId.get(motherName);
                                    break;
                                case "father":
                                    String fatherName=reader.getElementText().trim().replaceAll("\\s+", " ");
                                    current.fatherIdName.name=fatherName;
                                    current.fatherIdName.id=nameToId.get(fatherName);
                                    break;
                                case "parent":
                                    String parentId = reader.getAttributeValue(null, "value");
                                    if(parentId!=null && !parentId.equals("UNKNOWN")){
                                        current.parentCheck.add(new IdName(personData.get(parentId).personIdName.name, parentId));
                                    }
                                    break;
                                case "siblings-number":
                                    current.siblingsNumber=Integer.parseInt(reader.getAttributeValue(null, "value").trim());
                                    break;
                                case "siblings":
                                    String siblingsId = reader.getAttributeValue(null, "val");
                                    if(siblingsId != null){
                                        String[] parts = siblingsId.trim().split("\\s+");
                                        for(String record : parts){
                                            current.siblingsCheck.add(new IdName(personData.get(record).personIdName.name, record));
                                        }
                                    }
                                    else {
                                        while(reader.hasNext()){
                                            reader.next();
                                            if(reader.getEventType()==XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("siblings")){
                                                break;
                                            }
                                            if(reader.getEventType()==XMLStreamConstants.START_ELEMENT){
                                                switch (reader.getLocalName()){
                                                    case "brother":
                                                        String brotherName=reader.getElementText().trim().replaceAll("\\s+", " ");
                                                        current.brothers.add(new IdName(brotherName, nameToId.get(brotherName)));
                                                        break;
                                                    case "sister":
                                                        String sisterName=reader.getElementText().trim().replaceAll("\\s+", " ");
                                                        current.sisters.add(new IdName(sisterName, nameToId.get(sisterName)));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case "children-number":
                                    current.childrenNumber=Integer.parseInt(reader.getAttributeValue(null, "value").trim());
                                    break;
                                case "children":
                                    while(reader.hasNext()){
                                        reader.next();
                                        if(reader.getEventType()==XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("children")){
                                            break;
                                        }
                                        if(reader.getEventType()==XMLStreamConstants.START_ELEMENT){
                                            switch (reader.getLocalName()){
                                                case "son":
                                                    current.sons.add(new IdName(personData.get(reader.getAttributeValue(null, "id").trim()).personIdName.name, reader.getAttributeValue(null, "id").trim()));
                                                    break;
                                                case "daughter":
                                                    current.daughters.add(new IdName(personData.get(reader.getAttributeValue(null, "id").trim()).personIdName.name, reader.getAttributeValue(null, "id").trim()));
                                                    break;
                                                case "child":
                                                    String childName = reader.getElementText().trim().replaceAll("\\s+", " ");
                                                    current.childrenCheck.add(new IdName(childName, nameToId.get(childName)));
                                                    break;
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }

        }
        for (Map.Entry<String, PersonInformation> entry : personData.entrySet()) {
            if(!entry.getValue().parentCheck.isEmpty()){
                for(IdName record : entry.getValue().parentCheck){
                    if(personData.get(record.id).gender.equals("Male")){
                        personData.get(entry.getValue().personIdName.id).fatherIdName.merge(record);
                    }
                    if(personData.get(record.id).gender.equals("Female")){
                        personData.get(entry.getValue().personIdName.id).motherIdName.merge(record);
                    }
                }
            }
            if(!entry.getValue().spouceCheck.name.equals("NONE")){
                if(Objects.equals(personData.get(entry.getValue().spouceCheck.id).gender, "Male")){
                    personData.get(entry.getValue().personIdName.id).husbandIdName.merge(entry.getValue().spouceCheck);
                }
            }
            if(!entry.getValue().siblingsCheck.isEmpty()){
                for(IdName record : entry.getValue().siblingsCheck){
                    if(personData.get(record.id).gender.equals("Male")){
                        personData.get(entry.getValue().personIdName.id).brothers.add(record);
                    }
                    if(personData.get(record.id).gender.equals("Female")){
                        personData.get(entry.getValue().personIdName.id).sisters.add(record);
                    }
                }
            }
            if(!entry.getValue().childrenCheck.isEmpty()){
                for(IdName record : entry.getValue().childrenCheck){
                    if(personData.get(record.id).gender.equals("Male")){
                        personData.get(entry.getValue().personIdName.id).sons.add(record);
                    }
                    if(personData.get(record.id).gender.equals("Female")){
                        personData.get(entry.getValue().personIdName.id).daughters.add(record);
                    }
                }
            }
            if(entry.getValue().siblingsNumber!=(entry.getValue().brothers.size()+entry.getValue().sisters.size())){
                System.out.println("SIBLINGS NUMBER WRONG");
            }
            if(entry.getValue().childrenNumber!=(entry.getValue().daughters.size()+entry.getValue().sons.size())){
                System.out.println("CHILDREN NUMBER WRONG");
            }
        }
        return personData;
    }
}