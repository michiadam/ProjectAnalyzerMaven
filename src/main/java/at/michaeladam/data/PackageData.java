package at.michaeladam.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageData{

    private String packageName;
    private List<ClassData> classes;

    private List<String> compileWarnings;

    public PackageData(String packageName) {
        this.packageName = packageName;
        this.classes = new ArrayList<>();
        this.compileWarnings = new ArrayList<>();
    }

    public void addClass(ClassData classData) {
        classes.add(classData);
    }
    public void addCompileWarning(String warning) {
        compileWarnings.add(warning);
    }



}
