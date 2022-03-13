package at.michaeladam.parser;

import at.michaeladam.data.ClassData;
import at.michaeladam.data.EnumData;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaParser implements Parser {


    @Override
    public String parseEnum(EnumData input) {
        CompilationUnit cu = new CompilationUnit();

        String packageName = input.getPackageName();
        if (packageName != null) {
            String[] split = packageName.split("\\.", 3);
            packageName = split[0] + "." + split[1] + ".generated." + split[2];
        }
        cu.setPackageDeclaration(packageName);
        EnumDeclaration enumDeclaration = new EnumDeclaration();

        enumDeclaration.setName(input.getName());
        enumDeclaration.setImplementedTypes(
                new NodeList<>(Arrays.stream(input.getImplements())
                        .map(s -> new ClassOrInterfaceType().setName(s)).collect(Collectors.toList())));

        input.getEnumConstants().forEach((key, value) -> enumDeclaration.addEnumConstant(key).setArguments(
                new NodeList<>(Arrays.stream(value).map(s -> new NameExpr().setName(s)).collect(Collectors.toList()))));


        List.of(input.getConstructors()).forEach(constructor -> enumDeclaration.addConstructor().setBody(getNotYetImplemented()).setParameters(
                new NodeList<>(constructor.getParameter().entrySet().stream().map(entry -> {
                    Parameter parameter = new Parameter();
                    parameter.setName(entry.getKey());
                    parameter.setType(new ClassOrInterfaceType().setName(entry.getValue().getTypeString()));
                    return parameter;
                }).collect(Collectors.toList()))).setComment(new BlockComment().setContent(constructor.getComment()))
        );


        cu.addType(enumDeclaration);
        return cu.toString();
    }


    public String parseClass(ClassData input) {

        CompilationUnit cu = new CompilationUnit();
        String packageName = input.getPackageName();
        //insert .generated. after the second dot
        if (packageName != null) {
            String[] split = packageName.split("\\.", 3);
            packageName = split[0] + "." + split[1] + ".generated." + split[2];
        }

        cu.setPackageDeclaration(packageName);

        for (String importString : input.getImports()) {
            cu.addImport(importString);

        }
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        createDeclaration(input, classOrInterfaceDeclaration);


        cu.addType(classOrInterfaceDeclaration);
        return cu.toString();
    }

    private void createDeclaration(ClassData input, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        classOrInterfaceDeclaration.setInterface(input.getType().equals("interface"));
        classOrInterfaceDeclaration.setName(input.getName());
        classOrInterfaceDeclaration.setComment(new BlockComment(input.getComment()));
        classOrInterfaceDeclaration.setImplementedTypes(
                new NodeList<>(Arrays.stream(input.getInterfaces())
                        .map(s -> new ClassOrInterfaceType().setName(s)).collect(Collectors.toList())));

        Arrays.stream(input.getFields())
                .forEach(field -> classOrInterfaceDeclaration.addField(field.getType().getTypeString(),
                        field.getName(), Arrays.stream(field.getModifier())
                                .map(String::toUpperCase)
                                .map(Modifier.Keyword::valueOf).toArray(Modifier.Keyword[]::new)).setComment(new BlockComment(field.getComment())));


        Arrays.stream(input.getMethods())
                .forEach(method -> {
                    MethodDeclaration methodDeclaration = new MethodDeclaration();
                    methodDeclaration.setName(method.getName());
                    methodDeclaration.setType(method.getReturnType().getTypeString());
                    methodDeclaration.setComment(new BlockComment(method.getComment()));
                    methodDeclaration.setParameters(
                            new NodeList<>(
                                    method.getParameter().entrySet().stream().map(stringEntry -> {
                                        Type type = new TypeParameter(stringEntry.getValue().getTypeString());

                                        return new Parameter(type, stringEntry.getKey());
                                    }).collect(Collectors.toList())));

                    methodDeclaration.setBody(getNotYetImplemented());

                    classOrInterfaceDeclaration.addMember(methodDeclaration);
                });
    }

    private BlockStmt getNotYetImplemented() {
        return new BlockStmt(new NodeList<>(List.of(new ThrowStmt(
                new ObjectCreationExpr()
                        .setType("java.lang.RuntimeException")
                        .setArguments(new NodeList<>(List.of(new StringLiteralExpr("Not yet implemented"))))
        ))));
    }


}
