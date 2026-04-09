package com.ryandens.jlink.jib;

import java.nio.file.attribute.PosixFilePermission;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

public class Configuration {

  private final DirectoryProperty jlinkJre;
  private final SetProperty<PosixFilePermission> posixPermissions;

  /** Used by jib plugin */
  @Inject
  public Configuration(final Project project) {
    this(project.getObjects());
  }

  /** Used by tiny-jib */
  public Configuration(final ObjectFactory objectFactory) {
    jlinkJre = objectFactory.directoryProperty();
    posixPermissions = objectFactory.setProperty(PosixFilePermission.class);
  }

  /**
   * @return the JRE created by jlink. This is used as the entrypoint for the container.
   */
  @Input
  DirectoryProperty getJlinkJre() {
    return jlinkJre;
  }

  /**
   * @return the file permissions to set for the javaagent files being added to the image layer.
   */
  @Input
  SetProperty<PosixFilePermission> getPosixPermissions() {
    return posixPermissions;
  }
}
