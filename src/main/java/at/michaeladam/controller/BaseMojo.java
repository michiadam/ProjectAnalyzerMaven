package at.michaeladam.controller;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name ="shared")
public abstract class BaseMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File buildDirectory;

    @Parameter(defaultValue = "${project.build.directory}/classTree.yaml", required = true)
    protected File outputDirectory;

    @Parameter(required = false)
    protected File targetFolder;

    public File getTargetFolder() {
        if (targetFolder == null) {
            if(!buildDirectory.exists()) {
                if(buildDirectory.mkdirs()){
                    getLog().info("Created build directory: " + buildDirectory.getAbsolutePath());
                } else {
                    getLog().error("Could not create build directory: " + buildDirectory.getAbsolutePath());
                }
            }
            return outputDirectory;

        }

        return targetFolder;
    }

}
