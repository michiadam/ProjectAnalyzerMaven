package at.michaeladam.controller;


import at.michaeladam.data.ClassData;
import at.michaeladam.data.PackageData;
import at.michaeladam.data.ProjectData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "export", defaultPhase = LifecyclePhase.VALIDATE)
public class ExportClassesMojo extends BaseMojo {



    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    public void buildTree(ProjectData tree, String packageName, File buildDirectory) {
        File[] files = buildDirectory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String newPackagename;
                switch (packageName) {
                    case "":
                        newPackagename = file.getName();
                        break;
                    case "src":
                    case "main":
                        newPackagename = "src/" + file.getName();
                        break;
                    case "java":
                        newPackagename = "java/" + file.getName();
                        break;
                    case "test":
                        newPackagename = "test/" + file.getName();
                        break;
                    default:
                        newPackagename = packageName + "." + file.getName();
                        break;
                }

                buildTree(tree, newPackagename, file);

            } else {
                if (file.getName().endsWith(".java")) {
                    parseJavaFile(tree, packageName, file);
                }
            }
        }

    }

    private void parseJavaFile(ProjectData tree, String packageName, File file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            //extract all classes of the compilation unity


            compilationUnit.getTypes().forEach(type -> {
                //check if type is an class
                PackageData packageData = tree.getOrCreatePackage(packageName);

                if (type.isClassOrInterfaceDeclaration()) {

                        //add extract class to package if exists other wise add an error to package

                        Optional<ClassData> classData = extractClass(compilationUnit, type);
                        if (classData.isPresent()) {
                            ClassData extractedClassData = classData.get();
                            packageData.addClass(extractedClassData);

                        } else {
                            packageData.addCompileWarning("Couldnt find Interface/Class " + type.getName() + " (is this a broken interface or class?)");
                        }


                }
                //todo implement enum

            });
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private Optional<ClassData> extractClass(CompilationUnit compilationUnit, TypeDeclaration<?> type) {

        var classByName = compilationUnit.getClassByName(String.valueOf(type.getName()));
        if (classByName.isPresent()) {

            var classDeclaration = classByName.get();



            return Optional.of(ClassData.of(compilationUnit, classDeclaration, false));

        }
        var interfaceByName = compilationUnit.getInterfaceByName(String.valueOf(type.getName()));
        if (interfaceByName.isPresent()) {
            return Optional.of(ClassData.of(compilationUnit, interfaceByName.get(), true));
        }
        return Optional.empty();
    }


    public void execute() throws MojoExecutionException {

        ProjectData tree = new ProjectData();

        buildTree(tree, "", project.getBasedir());

        try {
            getObjectMapper().writeValue(outputDirectory, tree);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static final boolean PRETTY_PRINT = true;
    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        if(PRETTY_PRINT) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        return mapper;
    }

}
