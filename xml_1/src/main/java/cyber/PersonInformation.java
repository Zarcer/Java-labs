package cyber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PersonInformation {
    IdName personIdName = new IdName();
    String firstname=null;
    String lastname=null;
    IdName motherIdName = new IdName("UNKNOWN");
    IdName fatherIdName = new IdName("UNKNOWN");
    HashSet<IdName> parentCheck = new HashSet<>();
    String gender=null;
    IdName wifeIdName = new IdName("NONE");
    IdName husbandIdName = new IdName("NONE");
    IdName spouceCheck = new IdName("NONE");
    int siblingsNumber=0;
    HashSet<IdName> brothers = new HashSet<>();
    HashSet<IdName> sisters = new HashSet<>();
    HashSet<IdName> siblingsCheck = new HashSet<>();
    int childrenNumber=0;
    HashSet<IdName> daughters = new HashSet<>();
    HashSet<IdName> sons = new HashSet<>();
    HashSet<IdName> childrenCheck = new HashSet<>();
    public PersonInformation(String key){
        personIdName.id=key;
    }
}

class IdName{
    String id;
    String name;

    public IdName(){
        id=null;
        name=null;
    }

    public IdName(String name){
        id=null;
        this.name=name;
    }

    public IdName(String name, String id){
        this.id=id;
        this.name=name;
    }

    public void merge(IdName src){
        this.id=src.id;
        this.name=src.name;
    }

    @Override
    public String toString(){
        return name+" "+id;
    }

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
