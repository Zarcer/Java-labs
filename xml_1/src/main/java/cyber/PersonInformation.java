package cyber;

import java.util.ArrayList;

public class PersonInformation {
    IdName personIdName = new IdName();
    String firstname=null;
    String lastname=null;
    IdName motherIdName = new IdName();
    IdName fatherIdName = new IdName();
    String gender;
    IdName wifeIdName = new IdName();
    IdName husbandIdName = new IdName();
    int siblingsNumber;
    ArrayList<IdName> brothers =new ArrayList<>();
    ArrayList<IdName> sisters =new ArrayList<>();
    int childrenNumber;
    ArrayList<IdName> daughters =new ArrayList<>();
    ArrayList<IdName> sons =new ArrayList<>();
    public PersonInformation(String key){
        personIdName.id=key;
    }
}

class IdName{
    String id;
    String name;
}
