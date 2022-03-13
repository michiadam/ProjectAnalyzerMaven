package at.michaeladam.data;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;

@Data
public class ClassData implements FieldHolder{

    protected String id;

    protected String name;

    protected AnnotationData[] annotations;

    protected String[] interfaces;

    protected String[] imports;

    protected FieldData[] fields;
    protected MethodData[] methods;

    protected String type;
    protected String parent;
    protected ClassData[] childrenClasses;

    private String extendType;
    private String packageName;


    public static ClassData of(CompilationUnit compilationUnit, ClassOrInterfaceDeclaration classDeclaration) {

        ClassData classData = new ClassData();
        classData.extract(compilationUnit);
        classData.name = classDeclaration.getNameAsString();
        classData.annotations = AnnotationData.of(classDeclaration.getAnnotations());
        classData.interfaces = classDeclaration.getImplementedTypes().stream()
                .map(ClassOrInterfaceType::asString).toArray(String[]::new);
        classData.setType("class");
        Optional<String> qualifiedNameOptional = classDeclaration.getFullyQualifiedName();
        qualifiedNameOptional.ifPresent(qualifiedName -> {
            classData.id = qualifiedName;
            classData.packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));

        });
        classData.childrenClasses = classDeclaration.getMembers()
                .stream()
                .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                .map(BodyDeclaration::toClassOrInterfaceDeclaration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(declaration -> ClassData.of(compilationUnit,declaration)).toArray(ClassData[]::new);

        classData.fields = classDeclaration.getFields().stream()
                .map(fieldDeclaration -> FieldData.of(classData,fieldDeclaration))
                .flatMap(Arrays::stream).toArray(FieldData[]::new);

        classData.methods = classDeclaration.getMethods().stream()
                .map((MethodDeclaration declaration) -> MethodData.of(classData, declaration)).toArray(MethodData[]::new);
        return classData;
    }


    public static ClassData of(CompilationUnit compilationUnit, ClassOrInterfaceDeclaration classDeclaration, boolean isInterface) {
        ClassData classData = ClassData.of(compilationUnit, classDeclaration);
        if (isInterface)
            classData.setType("interface");

        return classData;
    }


    private void extract(CompilationUnit compilationUnit) {
        this.imports = compilationUnit.getImports()
                .stream().map(ImportDeclaration::getNameAsString).toArray(String[]::new);
        compilationUnit.getTypes().forEach(typeRaw -> {

        });
    }
}
