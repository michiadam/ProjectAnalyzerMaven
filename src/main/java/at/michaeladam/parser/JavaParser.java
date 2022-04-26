package at.michaeladam.parser;

import at.michaeladam.data.ClassData;
import at.michaeladam.data.EnumData;
import at.michaeladam.data.MethodData;
import at.michaeladam.data.shared.SharedData;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class JavaParser implements Parser {


    @Override
    public CompilationUnit parseEnum(EnumData input) {
        CompilationUnit cu = new CompilationUnit();

        String packageName = input.getPackageName();
        if (packageName != null) {
            String[] split = packageName.split("\\.", 3);
            packageName = split[0] + "." + split[1] + ".generated." + split[2];
        }
        cu.setPackageDeclaration(packageName);
        EnumDeclaration enumDeclaration = new EnumDeclaration();

        enumDeclaration.setName(input.getName());
        enumDeclaration.setComment(new BlockComment(input.getComment()));
        enumDeclaration.setImplementedTypes(
                new NodeList<>(Arrays.stream(input.getImplements())
                        .map(s -> new ClassOrInterfaceType().setName(s)).collect(Collectors.toList())));


        Arrays.stream(input.getFields())
                .forEach(field -> enumDeclaration.addField(field.getType().getTypeString(),
                        field.getName(), Arrays.stream(field.getModifier())
                                .map(String::toUpperCase)
                                .map(Modifier.Keyword::valueOf).toArray(Modifier.Keyword[]::new)).setComment(new BlockComment(field.getComment())));


        input.getEnumConstants().forEach(enumConstant -> enumDeclaration.addEnumConstant(enumConstant.getName())
                .setArguments(new NodeList<>(
                        Arrays.stream(enumConstant.getArguments())
                                .map(NameExpr::new)
                                .collect(Collectors.toList())))
                .setComment(new BlockComment(enumConstant.getComment())));


        List.of(input.getConstructors()).forEach(constructor -> enumDeclaration.addConstructor().setBody(getNotYetImplemented()).setParameters(
                new NodeList<>(constructor.getParameter().entrySet().stream().map(entry -> {
                    Parameter parameter = new Parameter();
                    parameter.setName(entry.getKey());
                    parameter.setType(new ClassOrInterfaceType().setName(entry.getValue().getTypeString()));
                    return parameter;
                }).collect(Collectors.toList()))).setComment(new BlockComment().setContent(constructor.getComment()))
        );


        cu.addType(enumDeclaration);
        return cu;
    }


    public CompilationUnit parseClass(ClassData input) {

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
        classOrInterfaceDeclaration.setInterface(input.getType().equals("interface"));
        classOrInterfaceDeclaration.setName(input.getName());
        classOrInterfaceDeclaration.setComment(new BlockComment(input.getComment()));
        classOrInterfaceDeclaration.setModifiers(Modifier.Keyword.PUBLIC);
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
                    methodDeclaration.setModifiers(Arrays.stream(method.getModifier())
                            .map(String::toUpperCase).map(Modifier.Keyword::valueOf).toArray(Modifier.Keyword[]::new));
                    methodDeclaration.setComment(new BlockComment(method.getComment()));
                    methodDeclaration.setParameters(
                            new NodeList<>(
                                    method.getParameter().entrySet().stream().map(stringEntry -> {
                                        Type type = new TypeParameter(stringEntry.getValue().getType());

                                        return new Parameter(type, stringEntry.getKey());
                                    }).collect(Collectors.toList())));

                    methodDeclaration.setBody(getNotYetImplemented());

                    classOrInterfaceDeclaration.addMember(methodDeclaration);
                });


        cu.addType(classOrInterfaceDeclaration);
        return cu;
    }

    @Override
    public void parseChanges(Node oldCompilation, Node compilationUnit) {

        Map<UUID, Node> oldEntries = oldCompilation.getChildNodes().stream().filter(node -> node.getComment().isPresent())
                .filter(node -> SharedData.extractID(node.getComment().toString()) != null)
                .collect(Collectors.toMap(node -> SharedData.extractID(node.getComment().toString()), node -> node));

        Map<UUID, Node> newEntries = compilationUnit.getChildNodes().stream().filter(node -> node.getComment().isPresent())
                .filter(node -> SharedData.extractID(node.getComment().toString()) != null)
                .collect(Collectors.toMap(node -> SharedData.extractID(node.getComment().toString()), node -> node));

        if(oldEntries.size() != newEntries.size()){
            System.out.println("Different number of entries");
        }

        oldEntries.forEach((key, oldNode) -> {
            if (newEntries.containsKey(key)) {
                Node newNode = newEntries.get(key);
                if (oldNode.getClass().equals(newNode.getClass())) {
                    switch (oldNode.getClass().getSimpleName()) {
                        case "ClassOrInterfaceDeclaration":
                            ClassOrInterfaceDeclaration oldClass = (ClassOrInterfaceDeclaration) oldNode;
                            ClassOrInterfaceDeclaration newClass = (ClassOrInterfaceDeclaration) newNode;
                            JavaChangeHandler.handleClassOrInterfaceDeclarationChange(oldClass, newClass);
                            parseChanges(oldClass, newClass);
                            break;
                        case "EnumDeclaration":
                            EnumDeclaration oldEnum = (EnumDeclaration) oldNode;
                            EnumDeclaration newEnum = (EnumDeclaration) newNode;
                            JavaChangeHandler.handleEnumDeclarationChange(oldEnum, newEnum);
                            parseChanges(oldEnum, newEnum);
                            break;
                        case "MethodDeclaration":
                            MethodDeclaration oldMethod = (MethodDeclaration) oldNode;
                            MethodDeclaration newMethod = (MethodDeclaration) newNode;
                            JavaChangeHandler.handleMethodDeclarationChange(oldMethod, newMethod);
                            break;
                        case "ConstructorDeclaration":
                            ConstructorDeclaration oldConstructor = (ConstructorDeclaration) oldNode;
                            ConstructorDeclaration newConstructor = (ConstructorDeclaration) newNode;
                            JavaChangeHandler.handleConstructorDeclarationChange(oldConstructor, newConstructor);
                            break;
                        case "FieldDeclaration":
                            FieldDeclaration oldField = (FieldDeclaration) oldNode;
                            FieldDeclaration newField = (FieldDeclaration) newNode;
                            JavaChangeHandler.handleFieldDeclarationChange(oldField, newField);
                            break;
                        case "AnnotationDeclaration":
                            AnnotationDeclaration oldAnnotation = (AnnotationDeclaration) oldNode;
                            AnnotationDeclaration newAnnotation = (AnnotationDeclaration) newNode;
                            JavaChangeHandler.handleAnnotationDeclarationChange(oldAnnotation, newAnnotation);
                            break;
                        case "EnumConstantDeclaration":
                            EnumConstantDeclaration oldEnumConstant = (EnumConstantDeclaration) oldNode;
                            EnumConstantDeclaration newEnumConstant = (EnumConstantDeclaration) newNode;
                            JavaChangeHandler.handleEnumConstantDeclarationChange(oldEnumConstant, newEnumConstant);
                            break;
                        default:
                            log.warn("Unhandled node type: " + oldNode.getClass().getSimpleName());

                    }

                }

            }
        });

    }

    private boolean sharedCompare(CompilationUnit oldCompilation, CompilationUnit compilationUnit) {
        boolean equals = Objects.equals(oldCompilation.getPackageDeclaration().orElse(null), compilationUnit.getPackageDeclaration().orElse(null));
        return !equals;
    }

    @Override
    public boolean compareEnums(CompilationUnit oldCompilation, CompilationUnit compilationUnit) {
        return sharedCompare(oldCompilation, compilationUnit);
    }

    @Override
    public boolean compareClasses(CompilationUnit oldCompilation, CompilationUnit compilationUnit) {
        return sharedCompare(oldCompilation, compilationUnit);
    }


    private BlockStmt getNotYetImplemented() {
        return new BlockStmt(new NodeList<>(List.of(new ThrowStmt(
                new ObjectCreationExpr()
                        .setType("java.lang.RuntimeException")
                        .setArguments(new NodeList<>(List.of(new StringLiteralExpr("Not yet implemented"))))
        ))));
    }


}
