package at.michaeladam.data;

import at.michaeladam.data.shared.SharedData;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.expr.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnumConstantData extends SharedData {

    private String name;
    private String [] arguments;

    public static EnumConstantData of(EnumConstantDeclaration enumConstantDeclaration) {
        EnumConstantData enumConstantData = new EnumConstantData();
        enumConstantData.extractID(enumConstantDeclaration.getComment().orElse(null));
        enumConstantData.setName(enumConstantDeclaration.getNameAsString());
        List<String> list = new ArrayList<>();
        for (Expression expression : enumConstantDeclaration.getArguments()) {
            String s = expression.toString();
            list.add(s);
        }
        enumConstantData.setArguments(list.toArray(new String[0]));

        return enumConstantData;
    }
}
