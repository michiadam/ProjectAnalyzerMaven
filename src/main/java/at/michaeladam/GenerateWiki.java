package at.michaeladam;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.sun.source.tree.Tree;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "analyze", defaultPhase = LifecyclePhase.VALIDATE)
public class GenerateWiki extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/classTree.json", required = true)
    private File outputDirectory;
    @Parameter(defaultValue = "${project.build.directory}/classTreeFull.json", required = true)
    private File outputDirectoryFull;


    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    public List<CompilationUnit> compilationUnits = new ArrayList<>();;
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

                }

                buildTree(tree, newPackagename, file);

            } else {
                if (file.getName().endsWith(".java")) {
                    try {
                        CompilationUnit compilationUnit = StaticJavaParser.parse(file);
                        //extract all classes of the compilation unity
                        compilationUnits.add(compilationUnit);


                        compilationUnit.getTypes().forEach(type -> {
                            //check if type is an class
                            PackageData packageData = tree.getOrCreatePackage(packageName);

                            if (type.isClassOrInterfaceDeclaration()) {

                                    //add extract class to package if exists other wise add an error to package

                                    Optional<ClassData> classData = extractClass(compilationUnit, type);
                                    if (classData.isPresent()) {
                                        ClassData extractedClassData = classData.get();
                                        extractedClassData.extract(compilationUnit);
                                        packageData.addClass(extractedClassData);

                                    } else {
                                        packageData.addCompileWarning("Couldnt find Interface/Class " + type.getName() + " (is this a broken interface or class?)");
                                    }


                            }

                        });
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
        }

    }

    private Optional<ClassData> extractClass(CompilationUnit compilationUnit, TypeDeclaration<?> type) {

        var classByName = compilationUnit.getClassByName(String.valueOf(type.getName()));
        if (classByName.isPresent()) {

            var classDeclaration = classByName.get();



            return Optional.of(ClassData.of(classDeclaration, false));

        }
        var interfaceByName = compilationUnit.getInterfaceByName(String.valueOf(type.getName()));
        if (interfaceByName.isPresent()) {
            return Optional.of(ClassData.of(interfaceByName.get(), true));
        }
        return Optional.empty();
    }


    public void execute() throws MojoExecutionException {

        ProjectData tree = new ProjectData();
        System.out.println(project.getBasedir());

        buildTree(tree, "", project.getBasedir());

        try {
            getObjectMapper().writeValue(outputDirectory, tree);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        ////make mapper pretty
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }

}
