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

    @Parameter(defaultValue = "${project.build.directory}/classTree.json", required = true)
    protected File outputDirectory;

    @Parameter(required = false)
    protected File targetFolder;

}
