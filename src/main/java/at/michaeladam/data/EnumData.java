package at.michaeladam.data;

import at.michaeladam.data.shared.SharedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnumData extends SharedData implements FieldHolder {



    private String[] imports;
    private String name;

    private List<EnumConstantData> enumConstants;

    private FieldData[] fields;

    private MethodData[] constructors;

    private MethodData[] methods;

    private String packageName;

    public static EnumData of(CompilationUnit compilationUnit, EnumDeclaration enumDeclaration) {
        EnumData enumData = new EnumData();
        enumData.extractID(enumDeclaration.getComment().orElse(null));
        enumData.imports = compilationUnit.getImports().stream().map(NodeWithName::getNameAsString).toArray(String[]::new);

        enumData.name = enumDeclaration.getNameAsString();

        enumData.enumConstants = enumDeclaration.getEntries().stream().map(EnumConstantData::of).collect(Collectors.toList());

        enumData.constructors = enumDeclaration.getConstructors().stream()
                .map(constructor -> MethodData.of(enumData, constructor)).toArray(MethodData[]::new);

        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        enumData.packageName = packageDeclaration.isPresent() ? packageDeclaration.get().getNameAsString() : "";
        ArrayList<FieldData> fields = new ArrayList<>();
        ArrayList<MethodData> methods = new ArrayList<>();

        enumDeclaration.getMembers().forEach(member -> {
            if (member.isFieldDeclaration()) {

                fields.addAll(Arrays.asList(FieldData.of(enumData, member.asFieldDeclaration())));
            }
            if (member.isMethodDeclaration()) {
                methods.add(MethodData.of(enumData, member.asMethodDeclaration()));
            }
        });
        enumData.fields = fields.toArray(new FieldData[0]);
        enumData.methods = methods.toArray(new MethodData[0]);
        return enumData;
    }

    @JsonIgnore
    public String[] getImplements() {
        return new String[0];
    }
}
