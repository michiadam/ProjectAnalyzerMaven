package at.michaeladam.data;

import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;


@Data
public class FieldData {

    private String name;
    private TypeData type;
    private String[] modifier;

    public static FieldData[] of(ClassData classData, FieldDeclaration fieldDeclaration) {
        TypeData type = TypeData.of(classData, fieldDeclaration);
        String[] modifier = fieldDeclaration
                .getModifiers().stream()
                .map(mod -> mod.getKeyword().asString()).map(String::trim).toArray(String[]::new);

        return fieldDeclaration.getVariables().stream().map(variable -> {
            FieldData fieldData = new FieldData();
            fieldData.setName(variable.getNameAsString());
            fieldData.setType(type);
            fieldData.setModifier(modifier);
            return fieldData;
        }).toArray(FieldData[]::new);

    }


}
