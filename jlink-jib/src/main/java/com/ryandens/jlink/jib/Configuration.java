package com.ryandens.jlink.jib;

import java.nio.file.attribute.PosixFilePermission;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

public class Configuration {

  private final DirectoryProperty jlinkJre;
  private final SetProperty<PosixFilePermission> posixPermissions;

  @Inject
  public Configuration(final Project project) {
    jlinkJre = project.getObjects().directoryProperty();
    posixPermissions = project.getObjects().setProperty(PosixFilePermission.class);
  }

  @Input
  DirectoryProperty getJlinkJre() {
    return jlinkJre;
  }

  @Input
  SetProperty<PosixFilePermission> getPosixPermissions() {
    return posixPermissions;
  }
}
