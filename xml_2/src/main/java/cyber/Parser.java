package cyber;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.*;
import javax.xml.stream.XMLInputFactory;

public class Parser {
    HashMap<String, PersonInformation> personData = new HashMap<>();
    HashMap<String, String> nameToId = new HashMap<>();
    HashSet<String> ambiguousNames = new HashSet<>();
    HashMap<String, ArrayList<String>> nameToIds = new HashMap<>();
    String path;
    public Parser(String path){
        this.path=path;
    }

    private String normalizeName(String source) {
        if (source == null) {
            return null;
        }
        String normalized = source.trim().replaceAll("\\s+", " ");
        if(normalized.isEmpty()){
            return null;
        }
        else {
            return normalized;
        }
    }

    private String resolveIdByName(String name) {
        String normalized = normalizeName(name);
        if (normalized == null || ambiguousNames.contains(normalized)) {
            return null;
        }
        return nameToId.get(normalized);
    }

    private String safePersonName(String id) {
        PersonInformation person = personData.get(id);
        if (person == null) {
            return null;
        }
        return normalizeName(person.personIdName.name);
    }

    private void updateNameFromRaw(String[] nameConstruct) {
        if(nameConstruct[2]==null){
            return;
        }
        String[] temp = nameConstruct[2].trim().split("\\s+", 2);
        nameConstruct[0] = temp[0];
        nameConstruct[1] = temp[1];
    }

    private PersonInformation ensurePerson(String id) {
        return personData.computeIfAbsent(id, PersonInformation::new);
    }

    private void mergeCurrent(PersonInformation target, PersonInformation current) {
        if (current.motherIdName.id != null) target.motherIdName = current.motherIdName;
        if (current.fatherIdName.id != null) target.fatherIdName = current.fatherIdName;
        if (!current.parentCheck.isEmpty()) target.parentCheck.addAll(current.parentCheck);
        if (current.gender != null) target.gender = current.gender;
        if (current.wifeIdName.id != null) target.wifeIdName = current.wifeIdName;
        if (current.husbandIdName.id != null) target.husbandIdName = current.husbandIdName;
        if (current.spouceCheck.id != null) target.spouceCheck = current.spouceCheck;
        if (current.hasSiblingsNumber && !target.hasSiblingsNumber) {
            target.siblingsNumber = current.siblingsNumber;
            target.hasSiblingsNumber = true;
        }
        if (!current.brothers.isEmpty()) target.brothers.addAll(current.brothers);
        if (!current.sisters.isEmpty()) target.sisters.addAll(current.sisters);
        if (!current.siblingsCheck.isEmpty()) target.siblingsCheck.addAll(current.siblingsCheck);
        if (current.hasChildrenNumber && !target.hasChildrenNumber) {
            target.childrenNumber = current.childrenNumber;
            target.hasChildrenNumber = true;
        }
        if (!current.daughters.isEmpty()) target.daughters.addAll(current.daughters);
        if (!current.sons.isEmpty()) target.sons.addAll(current.sons);
        if (!current.childrenCheck.isEmpty()) target.childrenCheck.addAll(current.childrenCheck);
    }

    private void recomputeGenderBuckets(PersonInformation person, LinkedHashSet<IdName> resolved, boolean isSibling) {
        HashSet<IdName> males = new HashSet<>();
        HashSet<IdName> females = new HashSet<>();
        for (IdName record : resolved) {
            PersonInformation related = record.id == null ? null : personData.get(record.id);
            if (related != null && "Male".equals(related.gender)) {
                males.add(record);
            } else if (related != null && "Female".equals(related.gender)) {
                females.add(record);
            }
        }
        if (isSibling) {
            person.brothers.clear();
            person.sisters.clear();
            person.brothers.addAll(males);
            person.sisters.addAll(females);
        } else {
            person.sons.clear();
            person.daughters.clear();
            person.sons.addAll(males);
            person.daughters.addAll(females);
        }
    }

    private void enforceDeclaredCounts(PersonInformation person) {
        LinkedHashSet<IdName> allSiblings = new LinkedHashSet<>();
        for (IdName relation : person.siblingsCheck) {
            if (relation.id != null || relation.name != null) allSiblings.add(relation);
        }
        for (IdName relation : person.brothers) {
            if (relation.id != null || relation.name != null) allSiblings.add(relation);
        }
        for (IdName relation : person.sisters) {
            if (relation.id != null || relation.name != null) allSiblings.add(relation);
        }
        person.siblingsCheck.clear();
        person.siblingsCheck.addAll(allSiblings);
        recomputeGenderBuckets(person, allSiblings, true);

        LinkedHashSet<IdName> allChildren = new LinkedHashSet<>();
        for (IdName relation : person.childrenCheck) {
            if (relation.id != null || relation.name != null) allChildren.add(relation);
        }
        for (IdName relation : person.sons) {
            if (relation.id != null || relation.name != null) allChildren.add(relation);
        }
        for (IdName relation : person.daughters) {
            if (relation.id != null || relation.name != null) allChildren.add(relation);
        }
        person.childrenCheck.clear();
        person.childrenCheck.addAll(allChildren);
        recomputeGenderBuckets(person, allChildren, false);
    }

    private void prepareDuplicateMappings() {
        nameToId.clear();
        ambiguousNames.clear();
        nameToIds.clear();

        for (Map.Entry<String, PersonInformation> entry : personData.entrySet()) {
            String id = entry.getKey();
            String normalizedName = normalizeName(entry.getValue().personIdName.name);
            if (normalizedName == null) {
                continue;
            }
            nameToIds.computeIfAbsent(normalizedName, key -> new ArrayList<>()).add(id);
            if (ambiguousNames.contains(normalizedName)) {
                continue;
            }
            String existing = nameToId.get(normalizedName);
            if (existing == null) {
                nameToId.put(normalizedName, id);
            } else if (!existing.equals(id)) {
                nameToId.remove(normalizedName);
                ambiguousNames.add(normalizedName);
            }
        }
    }

    private static class PendingRecord {
        String id;
        String normalizedName;
        PersonInformation raw;

        PendingRecord(String id, String normalizedName, PersonInformation raw) {
            this.id = id;
            this.normalizedName = normalizedName;
            this.raw = raw;
        }
    }

    private String normalizeGender(String rawGender) {
        if (rawGender == null) return null;
        String g = rawGender.trim().toLowerCase();
        if (g.startsWith("m")) return "Male";
        if (g.startsWith("f")) return "Female";
        return null;
    }

    private boolean hasId(Collection<IdName> set, String id) {
        if (id == null) {
            return false;
        }
        for (IdName record : set) {
            if (id.equals(record.id)) {
                return true;
            }
        }
        return false;
    }

    private HashSet<String> referencedIds(PersonInformation raw) {
        HashSet<String> ids = new HashSet<>();
        if (raw.husbandIdName.id != null) ids.add(raw.husbandIdName.id);
        if (raw.wifeIdName.id != null) ids.add(raw.wifeIdName.id);
        for (IdName value : raw.parentCheck) if (value.id != null) ids.add(value.id);
        for (IdName value : raw.siblingsCheck) if (value.id != null) ids.add(value.id);
        for (IdName value : raw.sons) if (value.id != null) ids.add(value.id);
        for (IdName value : raw.daughters) if (value.id != null) ids.add(value.id);
        return ids;
    }

    private int candidateScore(String candidateId, HashSet<String> refs) {
        int score = 0;
        for (String refId : refs) {
            PersonInformation ref = personData.get(refId);
            if (ref == null) {
                continue;
            }
            if (hasId(ref.siblingsCheck, candidateId) || hasId(ref.brothers, candidateId) || hasId(ref.sisters, candidateId)) score += 3;
            if (Objects.equals(ref.fatherIdName.id, candidateId) || Objects.equals(ref.motherIdName.id, candidateId) || hasId(ref.parentCheck, candidateId)) score += 3;
            if (hasId(ref.sons, candidateId) || hasId(ref.daughters, candidateId) || hasId(ref.childrenCheck, candidateId)) score += 3;
            if (Objects.equals(ref.husbandIdName.id, candidateId) || Objects.equals(ref.wifeIdName.id, candidateId) || Objects.equals(ref.spouceCheck.id, candidateId)) score += 3;
        }
        return score;
    }

    private String resolvePendingId(PendingRecord pending) {
        if (pending.id != null) {
            return pending.id;
        }
        if (pending.normalizedName == null) {
            return null;
        }
        ArrayList<String> candidates = nameToIds.get(pending.normalizedName);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        HashSet<String> refs = referencedIds(pending.raw);
        if (refs.isEmpty()) {
            return null;
        }

        int best = Integer.MIN_VALUE;
        String bestId = null;
        boolean tie = false;
        for (String candidate : candidates) {
            int score = candidateScore(candidate, refs);
            if (score > best) {
                best = score;
                bestId = candidate;
                tie = false;
            } else if (score == best) {
                tie = true;
            }
        }
        if (best <= 0 || tie) {
            return null;
        }
        return bestId;
    }

    private void applyPendingRecord(PendingRecord pending, String id) {
        if (id == null) return;

        PersonInformation resolved = new PersonInformation(null);
        PersonInformation raw = pending.raw;
        resolved.gender = raw.gender;
        resolved.siblingsNumber = raw.siblingsNumber;
        resolved.hasSiblingsNumber = raw.hasSiblingsNumber;
        resolved.childrenNumber = raw.childrenNumber;
        resolved.hasChildrenNumber = raw.hasChildrenNumber;

        if (raw.husbandIdName.id != null) {
            String husbandId = raw.husbandIdName.id;
            resolved.husbandIdName.id = husbandId;
            resolved.husbandIdName.name = safePersonName(husbandId);
        }
        if (raw.wifeIdName.id != null) {
            String wifeId = raw.wifeIdName.id;
            resolved.wifeIdName.id = wifeId;
            resolved.wifeIdName.name = safePersonName(wifeId);
        }
        if (raw.spouceCheck.name != null && !"NONE".equals(raw.spouceCheck.name)) {
            resolved.spouceCheck.name = raw.spouceCheck.name;
            resolved.spouceCheck.id = resolveIdByName(raw.spouceCheck.name);
        }
        if (raw.motherIdName.name != null) {
            resolved.motherIdName.name = raw.motherIdName.name;
            resolved.motherIdName.id = resolveIdByName(raw.motherIdName.name);
        }
        if (raw.fatherIdName.name != null) {
            resolved.fatherIdName.name = raw.fatherIdName.name;
            resolved.fatherIdName.id = resolveIdByName(raw.fatherIdName.name);
        }

        for (IdName parent : raw.parentCheck) {
            if (parent.id != null && !"UNKNOWN".equals(parent.id)) {
                String parentId = parent.id;
                resolved.parentCheck.add(new IdName(safePersonName(parentId), parentId));
            }
        }
        for (IdName sibling : raw.siblingsCheck) {
            if (sibling.id != null) {
                String siblingId = sibling.id;
                resolved.siblingsCheck.add(new IdName(safePersonName(siblingId), siblingId));
            }
        }
        for (IdName brother : raw.brothers) {
            String name = normalizeName(brother.name);
            resolved.brothers.add(new IdName(name, resolveIdByName(name)));
        }
        for (IdName sister : raw.sisters) {
            String name = normalizeName(sister.name);
            resolved.sisters.add(new IdName(name, resolveIdByName(name)));
        }
        for (IdName son : raw.sons) {
            if (son.id != null) {
                String sonId = son.id;
                resolved.sons.add(new IdName(safePersonName(sonId), sonId));
            }
        }
        for (IdName daughter : raw.daughters) {
            if (daughter.id != null) {
                String daughterId = daughter.id;
                resolved.daughters.add(new IdName(safePersonName(daughterId), daughterId));
            }
        }
        for (IdName child : raw.childrenCheck) {
            String name = normalizeName(child.name);
            resolved.childrenCheck.add(new IdName(name, resolveIdByName(name)));
        }
        mergeCurrent(ensurePerson(id), resolved);
    }

    private boolean assignGender(String personId, String gender) {
        if (personId == null || gender == null) {
            return false;
        }
        PersonInformation person = personData.get(personId);
        if (person == null || person.gender != null) {
            return false;
        }
        person.gender = gender;
        return true;
    }

    private void inferMissingGenders() {
        boolean changed;
        do {
            changed = false;
            for (PersonInformation person : personData.values()) {
                if (person.gender == null) {
                    if (person.wifeIdName.id != null) changed |= assignGender(person.personIdName.id, "Male");
                    if (person.husbandIdName.id != null) changed |= assignGender(person.personIdName.id, "Female");
                }

                changed |= assignGender(person.motherIdName.id, "Female");
                changed |= assignGender(person.fatherIdName.id, "Male");
                changed |= assignGender(person.wifeIdName.id, "Female");
                changed |= assignGender(person.husbandIdName.id, "Male");

                for (IdName relation : person.brothers) changed |= assignGender(relation.id, "Male");
                for (IdName relation : person.sisters) changed |= assignGender(relation.id, "Female");
                for (IdName relation : person.sons) changed |= assignGender(relation.id, "Male");
                for (IdName relation : person.daughters) changed |= assignGender(relation.id, "Female");
            }
        } while (changed);
    }

    public HashMap<String, PersonInformation> parse() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(path));
        ArrayList<PendingRecord> pendingRecords = new ArrayList<>();

        while (reader.hasNext()) {
            int event = reader.next();
            if (event != XMLStreamConstants.START_ELEMENT || !"person".equals(reader.getLocalName())) continue;

            String id = reader.getAttributeValue(null, "id");
            String[] nameConstruct = new String[3];
            nameConstruct[2] = reader.getAttributeValue(null, "name");
            PersonInformation current = new PersonInformation(null);

            while (reader.hasNext()) {
                reader.next();
                event = reader.getEventType();
                if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("person")) break;
                if (event != XMLStreamConstants.START_ELEMENT) continue;

                switch (reader.getLocalName()) {
                    case "id":
                        id = reader.getAttributeValue(null, "value");
                        break;
                    case "fullname":
                        while (reader.hasNext()) {
                            reader.next();
                            if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("fullname")) break;
                            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) continue;
                            if ("first".equals(reader.getLocalName())) nameConstruct[0] = reader.getElementText().trim();
                            if ("family".equals(reader.getLocalName())) nameConstruct[1] = reader.getElementText().trim();
                        }
                        break;
                    case "firstname":
                        nameConstruct[0] = reader.getAttributeValue(null, "value");
                        if(nameConstruct[0]==null){
                            nameConstruct[0]=reader.getElementText().trim();
                        }
                        else {
                            nameConstruct[0]=nameConstruct[0].trim();
                        }
                        break;
                    case "surname":
                        String surname = reader.getAttributeValue(null, "value");
                        if (surname != null) nameConstruct[1] = surname.trim();
                        break;
                    case "family":
                    case "family-name":
                        nameConstruct[1] = reader.getElementText().trim();
                        break;
                    case "gender":
                        current.gender = normalizeGender(reader.getAttributeValue(null, "value"));
                        if (current.gender == null) current.gender = normalizeGender(reader.getElementText());
                        break;
                    case "husband":
                        current.husbandIdName.id = reader.getAttributeValue(null, "value");
                        break;
                    case "wife":
                        current.wifeIdName.id = reader.getAttributeValue(null, "value");
                        break;
                    case "spouce":
                        String spouce = reader.getAttributeValue(null, "value");
                        if (spouce != null && !"NONE".equals(spouce)) current.spouceCheck.name = normalizeName(spouce);
                        break;
                    case "mother":
                        current.motherIdName.name = normalizeName(reader.getElementText());
                        break;
                    case "father":
                        current.fatherIdName.name = normalizeName(reader.getElementText());
                        break;
                    case "parent":
                        String parentId = reader.getAttributeValue(null, "value");
                        if (parentId != null && !"UNKNOWN".equals(parentId)) current.parentCheck.add(new IdName(null, parentId));
                        break;
                    case "siblings-number":
                        current.siblingsNumber = Integer.parseInt(reader.getAttributeValue(null, "value").trim());
                        current.hasSiblingsNumber = true;
                        break;
                    case "siblings":
                        String siblingsIds = reader.getAttributeValue(null, "val");
                        if (siblingsIds != null) {
                            for (String ref : siblingsIds.trim().split("\\s+")) current.siblingsCheck.add(new IdName(null, ref));
                        } else {
                            while (reader.hasNext()) {
                                reader.next();
                                if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("siblings")) break;
                                if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) continue;
                                if ("brother".equals(reader.getLocalName())) current.brothers.add(new IdName(normalizeName(reader.getElementText()), null));
                                if ("sister".equals(reader.getLocalName())) current.sisters.add(new IdName(normalizeName(reader.getElementText()), null));
                            }
                        }
                        break;
                    case "children-number":
                        current.childrenNumber = Integer.parseInt(reader.getAttributeValue(null, "value").trim());
                        current.hasChildrenNumber = true;
                        break;
                    case "children":
                        while (reader.hasNext()) {
                            reader.next();
                            if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("children")) break;
                            if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) continue;
                            if ("son".equals(reader.getLocalName())) current.sons.add(new IdName(null, reader.getAttributeValue(null, "id")));
                            if ("daughter".equals(reader.getLocalName())) current.daughters.add(new IdName(null, reader.getAttributeValue(null, "id")));
                            if ("child".equals(reader.getLocalName())) current.childrenCheck.add(new IdName(normalizeName(reader.getElementText()), null));
                        }
                        break;
                }
            }

            if (nameConstruct[2] != null) {
                updateNameFromRaw(nameConstruct);
            } else if (nameConstruct[0] != null && nameConstruct[1] != null) {
                nameConstruct[2] = nameConstruct[0] + " " + nameConstruct[1];
            }
            String normalizedName = normalizeName(nameConstruct[2]);
            if (id != null) {
                PersonInformation person = ensurePerson(id);
                if (nameConstruct[0] != null) person.firstname = nameConstruct[0];
                if (nameConstruct[1] != null) person.lastname = nameConstruct[1];
                if (person.firstname != null && person.lastname != null) person.personIdName.name = person.firstname + " " + person.lastname;
            }
            pendingRecords.add(new PendingRecord(id, normalizedName, current));
        }

        prepareDuplicateMappings();
        ArrayList<PendingRecord> unresolved = new ArrayList<>();
        for (PendingRecord pending : pendingRecords) {
            if (pending.id != null) {
                applyPendingRecord(pending, resolvePendingId(pending));
            } else {
                unresolved.add(pending);
            }
        }
        boolean progressed;
        do {
            progressed = false;
            Iterator<PendingRecord> iterator = unresolved.iterator();
            while (iterator.hasNext()) {
                PendingRecord pending = iterator.next();
                String resolvedId = resolvePendingId(pending);
                if (resolvedId != null) {
                    applyPendingRecord(pending, resolvedId);
                    iterator.remove();
                    progressed = true;
                }
            }
        } while (progressed);
        inferMissingGenders();
        HashSet<String> idsWithAmbiguousSource = new HashSet<>();
        for (PendingRecord pending : unresolved) {
            if (pending.normalizedName == null) {
                continue;
            }
            ArrayList<String> candidates = nameToIds.get(pending.normalizedName);
            if (candidates != null && candidates.size() > 1) {
                idsWithAmbiguousSource.addAll(candidates);
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
                PersonInformation spouse = personData.get(entry.getValue().spouceCheck.id);
                if (spouse != null && Objects.equals(spouse.gender, "Male")) {
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
            enforceDeclaredCounts(entry.getValue());
            int computedSiblings = entry.getValue().siblingsCheck.size();
            if (!idsWithAmbiguousSource.contains(entry.getKey()) && entry.getValue().hasSiblingsNumber && entry.getValue().siblingsNumber != computedSiblings) {
                System.out.println("SIBLINGS NUMBER WRONG");
                System.out.println(entry.getValue().siblingsNumber + "should be");
                System.out.println(entry.getValue().brothers.size() + "brothers");
                System.out.println(entry.getValue().sisters.size() + "sisters");
                System.out.println(entry.getValue().personIdName.id+ " "+entry.getValue().personIdName.name);
                System.out.println(entry.getValue().siblingsCheck + "to check siblings");
                System.out.println(entry.getValue().brothers + " brothers");
                System.out.println(entry.getValue().sisters + " sisters");
            }
            int computedChildren = entry.getValue().childrenCheck.size();
            if (!idsWithAmbiguousSource.contains(entry.getKey()) && entry.getValue().hasChildrenNumber && entry.getValue().childrenNumber != computedChildren) {
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
