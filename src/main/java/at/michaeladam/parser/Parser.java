package at.michaeladam.parser;

import at.michaeladam.data.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static java.util.Objects.*;

public interface Parser{

    String parseEnum(EnumData input);
    String parseClass(ClassData input);

    default void parseProjectData(ProjectData input, String destination) throws IOException {
        parseProjectData(input, new File(destination));
    }
    default void parseProjectData(ProjectData input, File destination) throws IOException {

        for (PackageData packageData : input.getPackages()) {
            String packageName = ""+packageData.getPackageName();
            if (!packageName.equals("")) {
                String[] split = packageName.split("\\.", 3);
                packageName = split[0] + "." + split[1] + ".generated." + split[2];
            }

            File packageDirectory = new File(destination, packageName.replace(".", "/"));

            boolean mkdirs = packageDirectory.mkdirs();
            if (!mkdirs && packageDirectory.listFiles()!= null) {
                    Arrays.stream(requireNonNull(packageDirectory.listFiles())).forEach(File::delete);

            }
            for (ClassData classData : packageData.getClasses()) {
                File classFile = new File(packageDirectory, classData.getName() + ".java");
                FileUtils.writeStringToFile(classFile, parseClass(classData), Charset.defaultCharset());
            }
            for(EnumData enumData : packageData.getEnums()) {
                File classFile = new File(packageDirectory, enumData.getName() + ".java");
                FileUtils.writeStringToFile(classFile, parseEnum(enumData), Charset.defaultCharset());
            }

        }
    }
}
