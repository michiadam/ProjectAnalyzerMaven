package at.michaeladam;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.Data;

import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ClassData {

    protected String simpleName;

    protected AnnotationData[] annotations;

    protected String[] interfaces;

    protected String[] imports;

    protected FieldData[] fields;
    protected MethodData[] methods;

    protected String type;
    protected String parent;
    protected ClassData[] childrenClasses;

    public static ClassData of(ClassOrInterfaceDeclaration classDeclaration) {
        ClassData classData = new ClassData();
        classData.simpleName = classDeclaration.getNameAsString();
        classData.annotations = AnnotationData.of(classDeclaration.getAnnotations());
        classData.interfaces = classDeclaration.getImplementedTypes().stream().map(ClassOrInterfaceType::asString).toArray(String[]::new);
        classData.setType("class");

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
        this.imports = compilationUnit.getImports()
                .stream().map(ImportDeclaration::getNameAsString).collect(Collectors.toList()).toArray(new String[0]);
        compilationUnit.getTypes().forEach(typeRaw -> {

            Optional<Node> parentNode1 = typeRaw.getParentNode();

            if (parentNode1.isPresent()) {
                Node node = parentNode1.get();


                if (node.toString().contains("extends")) {
                    String name = node.toString();

                    //get the extends
                    String[] split = name.split("extends");
                    String extendClass = split[1].split("\n")[0].replace("\r","").replace(" ", "").replace("{", "");

                    this.parent = extendClass;


                }
            }
        });
    }
}
