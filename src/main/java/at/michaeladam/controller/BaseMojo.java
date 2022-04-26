package at.michaeladam.controller;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "shared")
public abstract class BaseMojo extends AbstractMojo {


    @Parameter( defaultValue = "${session}", readonly = true )
    protected MavenSession mavenSession;

    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;

    @Parameter( property = "your-plugin.customProperty", defaultValue = "custom" )
    private String customProperty;


    public File getTargetFolder(MavenProject module) {
        File umlDirectory = new File(module.getBasedir(),"packuml");
        if(!umlDirectory.exists()) {
            boolean mkdirs = umlDirectory.mkdirs();
            if(!mkdirs) {
                throw new RuntimeException("Could not create directory: " + umlDirectory.getAbsolutePath());
            }
        }
        return new File(umlDirectory, "projectStructure.yaml");
    }



}
