package cyber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    HashSet<IdName> brothers = new HashSet<>();
    HashSet<IdName> sisters =new HashSet<>();
    int childrenNumber;
    HashSet<IdName> daughters = new HashSet<>();
    HashSet<IdName> sons =new HashSet<>();
    public PersonInformation(String key){
        personIdName.id=key;
    }
}

class IdName{
    String id;
    String name;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        IdName idName = (IdName) object;
        return Objects.equals(id, idName.id) && Objects.equals(name, idName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
