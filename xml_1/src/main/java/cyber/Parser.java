package cyber;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.*;

public class Parser {
    HashMap<String, PersonInformation> personData = new HashMap<>();
    HashMap<String, String> nameToId = new HashMap<>();

    public List<PersonInformation> parse(XMLStreamReader reader) throws Exception{
        while(reader.hasNext()){
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT){
                if("person".equals(reader.getLocalName())){
                    String id = reader.getAttributeValue(null, "id");
                    String[] nameConstruct = new String[3];
                    nameConstruct[2] = reader.getAttributeValue(null, "name");
                    nameConstruct[0]=null;
                    nameConstruct[1]=null;
                    while(reader.hasNext()){
                        reader.next();
                        event = reader.getEventType();
                        if(event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("person")){
                            break;
                        }
                        if(event == XMLStreamConstants.START_ELEMENT){
                            switch (reader.getLocalName()) {
                                case "id":
                                    id = reader.getAttributeValue(null, "value");
                                    break;
                                case "fullname":
                                    while(reader.hasNext()){
                                        reader.next();
                                        if(reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("fullname")){
                                            break;
                                        }
                                        if(reader.getEventType()==XMLStreamConstants.START_ELEMENT){
                                            switch (reader.getLocalName()){
                                                case "first":
                                                    nameConstruct[0]=reader.getElementText().trim();
                                                    break;
                                                case "family":
                                                    nameConstruct[1]=reader.getElementText().trim();
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "firstname":
                                    String firstname = reader.getAttributeValue(null, "value");
                                    if(firstname==null){
                                        nameConstruct[0]=reader.getElementText().trim();
                                    } else {
                                        nameConstruct[0]=firstname.trim();
                                    }
                                    break;
                                case "surname":
                                case "family":
                                case "family-name":
                                    if(reader.getLocalName().equals("surname")){
                                        nameConstruct[1]=reader.getAttributeValue(null, "value").trim();
                                    } else if(reader.getLocalName().equals("family") || reader.getLocalName().equals("family-name")){
                                        nameConstruct[1]=reader.getElementText().trim();
                                    }
                                    break;
                            }
                        }
                    }
                    if(id!=null){
                        if(!personData.containsKey(id)){
                            personData.put(id, new PersonInformation(id));
                        }
                        if(nameConstruct[2] != null){
                            String[] temp = nameConstruct[2].trim().split("\\s+", 2);
                            nameConstruct[0]=temp[0];
                            nameConstruct[1]=temp[1];
                            personData.get(id).firstname=nameConstruct[0];
                            personData.get(id).lastname=nameConstruct[1];
                        } else {
                            if(nameConstruct[0] != null){
                                personData.get(id).firstname=nameConstruct[0];
                            }
                            if(nameConstruct[1] != null){
                                personData.get(id).lastname=nameConstruct[1];
                            }
                        }
                        if(personData.get(id).firstname!=null && personData.get(id).lastname!=null){
                            personData.get(id).personIdName.name=personData.get(id).firstname+" "+personData.get(id).lastname;
                        }
                    }
                }
            }

        }
        for(Map.Entry<String, PersonInformation> entry : personData.entrySet()){
            nameToId.put(entry.getValue().personIdName.name, entry.getKey());
        }
    }

}
