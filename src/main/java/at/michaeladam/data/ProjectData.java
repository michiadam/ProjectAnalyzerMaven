package at.michaeladam.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class ProjectData {

    //List of packages
    private List<PackageData> packages;

    public ProjectData() {
        packages = new ArrayList<>();
    }

    public void addPackage(PackageData packageData) {
        packages.add(packageData);
    }

    @JsonIgnore
    public PackageData getPackage(String packageName) {
        return packages.stream().filter(p -> p.getPackageName().equals(packageName)).findFirst().orElse(null);
    }
    public PackageData getOrCreatePackage(String packageName) {
        PackageData packageData = getPackage(packageName);
        if (packageData == null) {
            packageData = new PackageData(packageName);
            addPackage(packageData);
        }
        return packageData;
    }



}
