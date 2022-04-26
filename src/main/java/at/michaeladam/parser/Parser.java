package at.michaeladam.parser;

import at.michaeladam.data.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public interface Parser {

    CompilationUnit parseEnum(EnumData input);

    CompilationUnit parseClass(ClassData input);

    default void parseProjectData(ProjectData input, String destination) throws IOException {
        parseProjectData(input, new File(destination));
    }

    default void parseProjectData(ProjectData input, File destination) throws IOException {

        for (PackageData packageData : input.getPackages()) {
            String packageName = "" + packageData.getPackageName();
            if (!packageName.equals("")) {
                String[] split = packageName.split("\\.", 3);
                packageName = split[0] + "." + split[1] + ".generated." + split[2];
            }

            File packageDirectory = new File(destination, packageName.replace(".", "/"));

            packageDirectory.mkdirs();

            packageData.getClasses().parallelStream().forEach(classData -> {

                try {
                    handleClassOrInterface(packageDirectory, classData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            packageData.getEnums().parallelStream().forEach(enumData -> {
                try {
                    handleEnum(packageDirectory, enumData);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            });

        }
    }

    private void handleClassOrInterface(File packageDirectory, ClassData classData) throws IOException {
        File classFile = new File(packageDirectory, classData.getName() + ".java");


        CompilationUnit oldCompilation = null;
        CompilationUnit compilationUnit = parseClass(classData);

        if (classFile.exists()) {
            oldCompilation = new JavaParser().parse(classFile).getResult().orElse(null);
        }

        if (oldCompilation == null) {
            FileUtils.writeStringToFile(classFile, compilationUnit.toString(), Charset.defaultCharset());
            return;
        }


        if (compareClasses(oldCompilation, compilationUnit)) {
            //todo implement to optimise
        }
        parseChanges(oldCompilation, compilationUnit);
        FileUtils.writeStringToFile(classFile, oldCompilation.toString(), Charset.defaultCharset());
    }

    private void handleEnum(File packageDirectory, EnumData enumData) throws IOException {
        File classFile = new File(packageDirectory, enumData.getName() + ".java");

        CompilationUnit compilationUnit = parseEnum(enumData);
        CompilationUnit oldCompilation = null;

        if (classFile.exists()) {
            oldCompilation = new JavaParser().parse(classFile).getResult().orElse(null);
        }

        if (oldCompilation == null) {
            FileUtils.writeStringToFile(classFile, compilationUnit.toString(), Charset.defaultCharset());
            return;
        }

        if (compareEnums(oldCompilation, compilationUnit)) {
            //todo implement to optimise
        }
        parseChanges(oldCompilation, compilationUnit);
        FileUtils.writeStringToFile(classFile, oldCompilation.toString(), Charset.defaultCharset());
    }

    void parseChanges(Node oldCompilation, Node compilationUnit);

    boolean compareEnums(CompilationUnit oldCompilation, CompilationUnit compilationUnit);

    boolean compareClasses(CompilationUnit oldCompilation, CompilationUnit compilationUnit);
}
