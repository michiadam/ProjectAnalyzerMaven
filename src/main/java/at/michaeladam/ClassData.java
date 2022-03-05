package at.michaeladam;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.metamodel.ClassOrInterfaceDeclarationMetaModel;
import lombok.Data;

import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ClassData {

    public String simpleName;

    public AnnotationData[] annotations;

    public String[] interfaces;

    public String[] imports;

    public FieldData[] fields;
    public MethodData[] methods;

    private String type;
    private String parent;
    private ClassData[] childrenClasses;

    public static ClassData of(ClassOrInterfaceDeclaration classDeclaration) {
        ClassData classData = new ClassData();
        classData.simpleName = classDeclaration.getNameAsString();
        classData.annotations = AnnotationData.of(classDeclaration.getAnnotations());
        classData.interfaces = classDeclaration.getImplementedTypes().stream().map(type -> type.asString()).collect(Collectors.toList()).toArray(new String[0]);
        classData.setType("class");

        Optional<Node> parentNode = classDeclaration.getParentNode();
        ClassOrInterfaceDeclarationMetaModel metaModel = classDeclaration.getMetaModel();


        classData.childrenClasses = classDeclaration.getMembers()
                .stream()
                .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                .map(BodyDeclaration::toClassOrInterfaceDeclaration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ClassData::of).toArray(ClassData[]::new);

        classData.fields = classDeclaration.getFields().stream().map(FieldData::of).toArray(FieldData[]::new);
        classData.methods = classDeclaration.getMethods().stream().map(MethodData::of).toArray(MethodData[]::new);
        return classData;
    }


    public static ClassData of(ClassOrInterfaceDeclaration classDeclaration, boolean isInterface) {
        ClassData classData = ClassData.of(classDeclaration);
        if (isInterface)
            classData.setType("interface");

        return classData;
    }


    public void extract(CompilationUnit compilationUnit) {
        var imports = compilationUnit.getImports();
        this.imports = imports.stream().map(ImportDeclaration::getNameAsString).collect(Collectors.toList()).toArray(new String[0]);
        compilationUnit.getTypes().forEach(type -> {

            Optional<Node> parentNode1 = type.getParentNode();

            if (parentNode1.isPresent()) {
                Node node = parentNode1.get();


                if (node.toString().contains("extends")) {
                    String name = node.toString();

                    //get the extends
                    String[] split = name.split("extends");
                    String extendClass = split[1].split("\n")[0].replace("\r","").replace(" ", "").replace("{", "");

                    System.out.println("extendClass: " + extendClass);
                    this.parent = extendClass;


                }
            }
        });
    }
}
