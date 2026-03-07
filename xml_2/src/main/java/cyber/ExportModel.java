package cyber;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "people")
@XmlAccessorType(XmlAccessType.FIELD)
class PeopleXml {
    @XmlAttribute(required = true)
    int total;

    @XmlElement(name = "person")
    List<PersonXml> person = new ArrayList<>();
}

@XmlAccessorType(XmlAccessType.FIELD)
class PersonXml {
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "ID")
    String id;

    NameXml name;
    String gender;
    RefXml mother;
    RefXml father;
    RefXml spouse;
    ParentsXml parents;
    SiblingsXml siblings;
    ChildrenXml children;
}

@XmlAccessorType(XmlAccessType.FIELD)
class NameXml {
    @XmlAttribute
    String first;

    @XmlAttribute
    String last;
}

@XmlAccessorType(XmlAccessType.FIELD)
class RefXml {
    @XmlAttribute(name = "ref", required = true)
    @XmlSchemaType(name = "IDREF")
    String ref;
}

@XmlAccessorType(XmlAccessType.FIELD)
class ParentsXml {
    @XmlElement(name = "parent")
    List<RefXml> parent = new ArrayList<>();
}

@XmlAccessorType(XmlAccessType.FIELD)
class SiblingsXml {
    @XmlElement(name = "brother")
    List<RefXml> brother = new ArrayList<>();

    @XmlElement(name = "sister")
    List<RefXml> sister = new ArrayList<>();
}

@XmlAccessorType(XmlAccessType.FIELD)
class ChildrenXml {
    @XmlElement(name = "son")
    List<RefXml> son = new ArrayList<>();

    @XmlElement(name = "daughter")
    List<RefXml> daughter = new ArrayList<>();
}
