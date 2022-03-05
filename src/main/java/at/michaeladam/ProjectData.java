package at.michaeladam;

import lombok.Data;

import java.util.*;
import java.util.stream.Stream;

@Data
public class ProjectData extends lablaSuper {

    private class BlaBLa {
        Map<ArrayList<LinkedList>, HashMap<String, List>> test;

    };
    //List of packages
    private final ArrayList<PackageData> packages;

    public ProjectData() {
        packages = new ArrayList<>();
    }

    public void addPackage(PackageData packageData) {
        packages.add(packageData);
    }

    public PackageData getPackage(int index) {
        return packages.get(index);
    }
    public PackageData getPackage(String packageName) {
        return packages.stream().filter(p -> p.packageName.equals(packageName)).findFirst().orElse(null);
    }
    public PackageData getOrCreatePackage(String packageName) {
        PackageData packageData = getPackage(packageName);
        if (packageData == null) {
            packageData = new PackageData(packageName);
            addPackage(packageData);
        }
        return packageData;
    }
    public int getPackageCount() {
        return packages.size();
    }
    //package stream
    public Stream<PackageData> streamPackages() {
        return packages.stream();
    }



}
