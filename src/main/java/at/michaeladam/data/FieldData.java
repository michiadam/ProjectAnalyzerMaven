package at.michaeladam.data;

import at.michaeladam.data.shared.SharedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.ast.body.FieldDeclaration;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class FieldData extends SharedData {

    private String name;
    private TypeData type;
    private String[] modifier;

    public static FieldData[] of(FieldHolder classData, FieldDeclaration fieldDeclaration) {
        TypeData type = TypeData.of(classData, fieldDeclaration);
        String[] modifier = fieldDeclaration
                .getModifiers().stream()
                .map(mod -> mod.getKeyword().asString()).map(String::trim).toArray(String[]::new);

        return fieldDeclaration.getVariables().stream().map(variable -> {
            FieldData fieldData = new FieldData();
            fieldData.setName(variable.getNameAsString());
            fieldData.setType(type);
            fieldData.setModifier(modifier);
            fieldData.extractID(fieldDeclaration.getComment().orElse(null));
            return fieldData;
        }).toArray(FieldData[]::new);

    }


}
