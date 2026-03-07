package cyber;

import java.util.HashSet;
import java.util.Objects;

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
    boolean hasSiblingsNumber=false;
    HashSet<IdName> brothers = new HashSet<>();
    HashSet<IdName> sisters = new HashSet<>();
    HashSet<IdName> siblingsCheck = new HashSet<>();
    int childrenNumber=0;
    boolean hasChildrenNumber=false;
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
        if (id != null && idName.id != null) {
            return Objects.equals(id, idName.id);
        }
        String thisName = normalizeForCompare(name);
        String otherName = normalizeForCompare(idName.name);
        return Objects.equals(thisName, otherName);
    }


    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(normalizeForCompare(name));
    }

    private String normalizeForCompare(String source) {
        if (source == null) {
            return null;
        }
        return source.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
