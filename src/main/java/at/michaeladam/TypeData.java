package at.michaeladam;

import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class TypeData {

    private String type;
    private TypeData[] generic;

    public static TypeData of(FieldDeclaration fieldDeclaration) {
        String field = fieldDeclaration.getElementType().toString();

        return ofString(field);
    }

    public static TypeData ofString(String field) {
        TypeData typeData = new TypeData();
        if (field.contains("<")) {
            typeData.type = field.substring(0, field.indexOf("<"));

            String generic = field.substring(field.indexOf("<") + 1, field.lastIndexOf(">"));

            List<String> types = new ArrayList<>();
            if (generic.contains("<")) {
                extractGeneric(generic, types);

            } else {
                if (generic.contains(",")) {
                    types.addAll(Arrays.asList(generic.split(",")));
                } else {
                    types.add(generic);
                }
            }

            typeData.generic = types.stream().map(TypeData::ofString).toArray(TypeData[]::new);

        } else {
            typeData.type = field;
        }
        return typeData;
    }

    private static void extractGeneric(String generic, List<String> types) {
        boolean done;
        int startIndex = 0;
        do {
            int openingCount = 0;
            int endingCount = 0;

            done = true;
            for (int i = startIndex; i < generic.length(); i++) {
                char c = generic.charAt(i);
                if (c == '<') {
                    openingCount++;
                } else if (c == '>') {
                    endingCount++;
                } else if ((c == ',' || i == generic.length() - 1) && openingCount == endingCount) {
                    String type = generic.substring(startIndex, i + (startIndex != 0 ? 1 : 0));
                    types.add(type);
                    startIndex = i + 2;
                    done = false;

                    break;
                }

            }

        } while (!done);
    }
}
