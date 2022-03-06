package at.michaeladam.data;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data

public class MethodData {

    private String name;
    private TypeData returnType;
    private String[] modifier;
    private Map<String, TypeData> parameter;

    public static MethodData of(ClassData classData, MethodDeclaration methodDeclaration) {
        MethodData methodData = new MethodData();
        methodData.setName(methodDeclaration.getNameAsString());
        methodData.setReturnType(TypeData.ofString(classData, methodDeclaration.getType().asString()));
        methodData.setModifier(methodDeclaration.getModifiers().stream().map(Node::toString).map(String::trim).toArray(String[]::new));
        methodData.parameter = methodDeclaration.getParameters()
                .stream().collect(Collectors.toMap(NodeWithSimpleName::getNameAsString, p -> TypeData.ofString(classData, p.getType().asString())));
        return methodData;
    }
}
