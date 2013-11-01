/*
 * Copyright 2006-2013 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.rcm.maven.plugin.buildmetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;

/**
 * Maps project types to property files.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
final class PropertyOutputFileMapper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Maven project.
   */
  private final MavenProject project;

  /**
   * The mapping property.
   */
  private List<FileMapping> propertyOutputFileMapping;

  /**
   * The name of the file to create the path for.
   */
  private final String fileName;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  PropertyOutputFileMapper(final MavenProject project,
      final List<FileMapping> propertyOutputFileMapping, final String fileName)
  {
    this.project = project;
    this.propertyOutputFileMapping = propertyOutputFileMapping;
    this.fileName = fileName;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  List<FileMapping> initOutputFileMapping()
  {
    if (propertyOutputFileMapping == null)
    {
      propertyOutputFileMapping = new ArrayList<FileMapping>(10);
      final Build build = project.getBuild();
      final String classesDir = build.getOutputDirectory();
      final File jarFile = new File(classesDir, "META-INF/" + fileName);
      final File targetDir = new File(build.getDirectory());
      final String finalName = build.getFinalName();
      final File deploymentUnitFile =
          new File(targetDir, finalName + "/META-INF/" + fileName);

      propertyOutputFileMapping.add(new FileMapping("pom", new File(targetDir,
          fileName))); // NOPMD
      propertyOutputFileMapping.add(new FileMapping("war", deploymentUnitFile));
      propertyOutputFileMapping.add(new FileMapping("ear", deploymentUnitFile));
      propertyOutputFileMapping.add(new FileMapping("sar", deploymentUnitFile));
      propertyOutputFileMapping.add(new FileMapping("rar", deploymentUnitFile));
      propertyOutputFileMapping.add(new FileMapping("par", deploymentUnitFile));
      propertyOutputFileMapping.add(new FileMapping("jar", jarFile));
      propertyOutputFileMapping.add(new FileMapping("ejb", jarFile));
      propertyOutputFileMapping.add(new FileMapping("maven-plugin", jarFile));
      propertyOutputFileMapping
          .add(new FileMapping("maven-archetype", jarFile));
      propertyOutputFileMapping.add(new FileMapping("eclipse-plugin", new File(
          targetDir, fileName)));
      propertyOutputFileMapping.add(new FileMapping("eclipse-feature",
          new File(targetDir, fileName)));
      propertyOutputFileMapping.add(new FileMapping("eclipse-repository",
          new File(targetDir, fileName)));
      propertyOutputFileMapping.add(new FileMapping("eclipse-update-site",
          new File(targetDir, fileName)));
      propertyOutputFileMapping.add(new FileMapping("targetplatform", new File(
          targetDir, fileName)));
      return propertyOutputFileMapping;

    }

    return propertyOutputFileMapping;
  }

  /**
   * Returns the output location for the build meta data properties.
   *
   * @return the output location for the build meta data properties.
   */
  File getPropertiesOutputFile(final boolean activatePropertyOutputFileMapping,
      final File propertiesOutputFile)
  {
    if (activatePropertyOutputFileMapping)
    {
      final String packaging = project.getPackaging();
      for (final FileMapping mapping : propertyOutputFileMapping)
      {
        if (packaging.equals(mapping.getPackaging()))
        {
          return mapping.getOutputFile();
        }
      }
    }

    return propertiesOutputFile;
  }

  // --- object basics --------------------------------------------------------

}
