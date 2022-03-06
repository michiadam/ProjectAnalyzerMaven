package at.michaeladam.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class PackageData implements Comparator<PackageData> , Comparable<PackageData> {

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


    @Override
    public int compareTo(PackageData o) {
        return 0;
    }

    @Override
    public int compare(PackageData o1, PackageData o2) {
        return 0;
    }
}
