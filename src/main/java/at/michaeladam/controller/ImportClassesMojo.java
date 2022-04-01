package at.michaeladam.controller;


import at.michaeladam.data.ProjectData;
import at.michaeladam.parser.JavaParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;


@Mojo(name = "import", defaultPhase = LifecyclePhase.VALIDATE)
public class ImportClassesMojo extends BaseMojo {


    public void execute() throws MojoExecutionException {

        mavenSession.getAllProjects().parallelStream().forEach(project -> {

            File structureInput = getTargetFolder(project);
            getLog().info("Importing classes from " + structureInput);
            try {
                ProjectData projectData = getObjectMapper().readValue(structureInput, ProjectData.class);
                getLog().info("Found " + getClassCount(projectData) + " classes");

                File targetFolder = new File(project.getBasedir(), "src/generated");

                if (!targetFolder.exists()) {
                    boolean mkdirs = targetFolder.mkdirs();
                    if (!mkdirs) {
                        throw new RuntimeException("Could not create target directory " + targetFolder);
                    }
                }
                new JavaParser().parseProjectData(projectData, targetFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private int getClassCount(ProjectData projectData) {
        return projectData
                .getPackages()
                .stream()
                .mapToInt(value -> value.getClasses().size())
                .sum();
    }

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private ObjectMapper getObjectMapper() {
        return mapper;
    }

}
