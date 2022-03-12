package at.michaeladam.data;

import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class TypeData {

    private String id;
    private String type;
    private TypeData[] generic;

    public static TypeData of(ClassData classData, FieldDeclaration fieldDeclaration) {
        TypeData typeData = new TypeData();
        String field = fieldDeclaration.getElementType().toString();

        ofString(classData, typeData, field);
        getTypeID(classData, typeData, field);
        return typeData;
    }

    private static void getTypeID(ClassData classData, TypeData typeData, String field) {
        field = field.replace(" extends ", "_extends_");
        field = field.trim();
        if (field.contains(".")) {
            typeData.id = field;
        } else {
            if (classData.imports != null) {
                String plainField = field.replaceAll("\\[.*?\\]", "")
                        .replaceAll("\\<.*?\\>", "");
                Arrays.stream(classData.imports).filter(s -> s.contains(plainField)).findFirst()
                        .ifPresent(importData -> typeData.id = importData + "." + plainField);
                if (typeData.id == null) {
                    //ignore primitive types and Strings
                    if(plainField.contains("?")) {
                        typeData.id = "wildcard";
                    } else if (Stream.of(".", "String", "int", "long", "double", "float", "boolean", "char", "void").noneMatch(field::contains)) {
                        typeData.id = field;
                    } else {

                        typeData.id = plainField;
                    }


                }
            }

        }
    }

    private static TypeData ofString(ClassData classData, TypeData typeData, String field) {
        if (field.contains("<")) {
            typeData.type = field.substring(0, field.indexOf("<"));

            List<String> types = ExtractionHelper.extractTypesOfGenericField(field);

            typeData.generic = types.stream().map(declaration -> ofString(classData, declaration)).toArray(TypeData[]::new);

        } else {
            typeData.type = field.replace("_extends_", " extends ");
        }
        return typeData;
    }

    public static TypeData ofString(ClassData classData, String field) {
        TypeData typeData = new TypeData();
        getTypeID(classData, typeData, field);
        return ofString(classData, typeData, field);
    }

    public static void extractGeneric(String generic, List<String> types) {
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
