package etl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OneHotEncoder {
    List<String> classesList = new ArrayList<>();
    private Set<String> classes = new HashSet<>();

    public OneHotEncoder(Set<String> classes) {
        this.classes = classes;
        classesList.addAll(classes);
    }

    public String getEncodedClass(String classId) throws IllegalArgumentException {
        if (!classes.contains(classId)) {
            throw new IllegalArgumentException("ClassId passed in was not part of the initial constructor. " + classId);
        }

        int index = findClassIndex(classId);

        String encodedString = "[ ";
        for (int i = 0; i < classes.size(); i++) {
            if (i == index) {
                if (i == (classes.size() - 1)) {
                    encodedString += " 1";
                } else {
                    encodedString += " 1,";
                }
            } else {
                if (i == (classes.size() - 1)) {
                    encodedString += " 0";
                } else {
                    encodedString += " 0,";
                }
            }
        }
        encodedString += " ]";
        return encodedString;
    }

    private int findClassIndex(String classId) {
        for (int i = 0; i < classesList.size(); i++) {
            String currentClass = classesList.get(i);
            if (currentClass.equals(classId)) {
                return i;
            }
        }

        return -1;
    }

    public String getClassIdForEncoding(String encoding) {
        for (int i = 0; i < classesList.size(); i++) {
            if (getEncodedClass(classesList.get(i)).equals(encoding)) {
                return classesList.get(i);
            }
        }
        return "Error";
    }

}
