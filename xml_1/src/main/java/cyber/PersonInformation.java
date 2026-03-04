package cyber;

import java.util.ArrayList;

public class PersonInformation {
    IdName personIdName;
    String firstname;
    String lastname;
    IdName motherIdName;
    IdName fatherIdName;
    String gender;
    IdName wifeIdName;
    IdName husbandIdName;
    int siblingsNumber;
    ArrayList<IdName> brothers =new ArrayList<>();
    ArrayList<IdName> sisters =new ArrayList<>();
    int childrenNumber;
    ArrayList<IdName> daughters =new ArrayList<>();
    ArrayList<IdName> sons =new ArrayList<>();
}

class IdName{
    String id;
    String name;
}
