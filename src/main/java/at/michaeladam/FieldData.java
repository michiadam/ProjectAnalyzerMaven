package at.michaeladam;

import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;

import java.util.stream.Collectors;


@Data
public class FieldData {

    private String name;
    private FieldDeclarationData type;
    private String[] modifier;

    public static FieldData of(FieldDeclaration fieldDeclaration) {
        FieldData fieldData = new FieldData();
        fieldData.setName(fieldDeclaration.getVariables().get(0).getNameAsString());
        fieldData.setType(FieldDeclarationData.of(fieldDeclaration));
        //Parse the element type from A<B<C>> to A B C



        fieldData.setModifier(fieldDeclaration.getModifiers().stream().map(modifier1 -> modifier1.getKeyword().asString()).map(String::trim).toArray(String[]::new));


        return fieldData;
    }



}
