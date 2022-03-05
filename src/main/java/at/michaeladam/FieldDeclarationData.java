package at.michaeladam;

import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class FieldDeclarationData {

    private String type;
    private FieldDeclarationData[] generic;

    public static FieldDeclarationData of(FieldDeclaration fieldDeclaration) {
        //check if the field is generic
        String field = fieldDeclaration.getElementType().toString();

        return ofString(field);
    }
//          Map<ArrayList<LinkedList>, HashMap<String, List>> test;


    //ArrayList<LinkedList>, String, HashMap<String, List>
    //A < B < C , F>  , D < E, A>, E < A, B > >

    //B < C , F < A,B > >  , D < E, A>, E < A, B >
    public static FieldDeclarationData ofString(String field) {
        FieldDeclarationData fieldDeclarationData = new FieldDeclarationData();
        if (field.contains("<")) {
            fieldDeclarationData.type = field.substring(0, field.indexOf("<"));

            String generic = field.substring(field.indexOf("<") + 1, field.lastIndexOf(">"));
            //split generic into types by , but ignore , inside <>
            //e.g ArrayList<LinkedList>, String, HashMap<String, List>
            //split and ignore commas inside <>




            //loop through chars of test

            List<String> types = new ArrayList<>();
            boolean done = false;
            if (generic.contains("<")) {
                int startIndex = 0;
                do {
                    int openingCount = 0;
                    int endingCount = 0;

                    done = true;
                    for (int i = startIndex; i < generic.length(); i++) {
                        char c = generic.charAt(i);
                        if (c == '<') {
                            openingCount++;
                        }
                        if (c == '>') {
                            endingCount++;
                        }
                        if ((c == ',' || i == generic.length() - 1) && openingCount == endingCount) {
                            String type = generic.substring(startIndex, i + (startIndex != 0 ? 1 : 0));
                            types.add(type);
                            System.out.println("type: " + type);
                            startIndex = i + 2;
                            done = false;

                            break;
                        }

                    }

                } while (!done);

            } else {
                if (generic.contains(",")) {
                    types.addAll(Arrays.asList(generic.split(",")));
                } else {
                    types.add(generic);
                }
            }

            fieldDeclarationData.generic = types.stream().map(FieldDeclarationData::ofString).toArray(FieldDeclarationData[]::new);

        } else {
            fieldDeclarationData.type = field;
        }
        return fieldDeclarationData;
    }
}
