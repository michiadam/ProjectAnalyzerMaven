package at.michaeladam.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;

import java.util.ArrayList;
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

        if (field.contains(".")) {
            typeData.id = field;
        } else {
            if (classData.imports != null) {
                String plainField = field.replaceAll("\\[.*?\\]", "")
                        .replaceAll("\\<.*?\\>", "");
                Arrays.stream(classData.imports).filter(s -> s.contains(plainField)).findFirst().ifPresent(importData -> {
                    typeData.id = importData + "." + plainField;
                });
                if (typeData.id == null) {
                    System.out.println("parent class: " + classData.parent);
                    //ignore primitive types and Strings
                    if (Stream.of(".", "String", "int", "long", "double", "float", "boolean", "char", "void").noneMatch(field::contains)) {
                       typeData.id = classData.getPackage() + "." + plainField;
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

            typeData.generic = types.stream().map(declaration -> ofString(classData, declaration)).toArray(TypeData[]::new);

        } else {
            typeData.type = field;
        }
        return typeData;
    }

    public static TypeData ofString(ClassData classData, String field) {
        TypeData typeData = new TypeData();
        getTypeID(classData, typeData, field);
        return ofString(classData, typeData, field);
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
