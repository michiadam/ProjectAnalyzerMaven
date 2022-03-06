package at.michaeladam.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PackageData{

    public final String packageName;
    public final List<ClassData> classes;

    public final List<String> compileWarnings;

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
