package at.michaeladam.data;

import at.michaeladam.data.shared.SharedData;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class MethodData extends SharedData {

    private String name;
    private TypeData returnType;
    private String[] modifier;
    private Map<String, TypeData> parameter;

    public static MethodData of(FieldHolder classData, MethodDeclaration methodDeclaration) {
        MethodData methodData = new MethodData();
        methodData.extractID(methodDeclaration.getComment().orElse(null));
        methodData.setName(methodDeclaration.getNameAsString());
        methodData.setReturnType(TypeData.ofString(classData, methodDeclaration.getType().asString()));
        methodData.setModifier(methodDeclaration.getModifiers().stream().map(Node::toString).map(String::trim).toArray(String[]::new));
        methodData.parameter = methodDeclaration.getParameters()
                .stream().collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, p -> TypeData.ofString(classData, p.getType().asString())));
        return methodData;
    }

    public static MethodData of(FieldHolder classData,ConstructorDeclaration constructor) {
        MethodData methodData = new MethodData();
        methodData.setName("<init>");
        methodData.setReturnType(null);
        methodData.extractID(constructor.getComment().orElse(null));
        methodData.setModifier(constructor.getModifiers().stream().map(Node::toString).map(String::trim).toArray(String[]::new));
        methodData.parameter = constructor.getParameters()
                .stream().collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, p -> TypeData.ofString(classData, p.getType().asString())));
        return methodData;
    }
}
