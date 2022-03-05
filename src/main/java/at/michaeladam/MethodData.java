package at.michaeladam;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Data;

import java.util.stream.Collectors;

@Data

public class MethodData {

    private String name;
    private String returnType;
    private String[] modifier;
    private String[] parameter;

    public static MethodData of(MethodDeclaration methodDeclaration) {
        MethodData methodData = new MethodData();
        methodData.setName(methodDeclaration.getNameAsString());
        methodData.setReturnType(methodDeclaration.getType().toString());
        methodData.setModifier(methodDeclaration.getModifiers().stream().map(Node::toString).map(String::trim).toArray(String[]::new));
        methodData.setParameter(methodDeclaration.getParameters().stream().map(parameter1 -> parameter1.getType()+" "+parameter1.getName()).toArray(String[]::new));
        return methodData;
    }
}
