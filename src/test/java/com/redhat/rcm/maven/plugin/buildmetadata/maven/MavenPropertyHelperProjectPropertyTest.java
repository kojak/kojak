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
package com.redhat.rcm.maven.plugin.buildmetadata.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link MavenPropertyHelper} for accessing properties of the project
 * instance.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class MavenPropertyHelperProjectPropertyTest
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The name of the project version used to access the project property.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String PROJECT_VERSION_NAME = "project.version";

  // --- members --------------------------------------------------------------

  /**
   * The unit under test (UUT).
   */
  private MavenPropertyHelper uut;

  /**
   * The model passed to the Maven project used by the test.
   */
  private Model projectModel;

  /**
   * The project passed to the test.
   */
  private MavenProject project;

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  @Before
  public void setup()
  {
    projectModel = new Model();
    project = new MavenProject(projectModel);
    uut = new MavenPropertyHelper(project);
  }

  // --- helper ---------------------------------------------------------------

  // --- tests ----------------------------------------------------------------

  @Test
  public void propertyNotFoundSinceNoSuchProperty()
  {
    final String value = uut.getProperty("project.invalidName");

    assertNull(value);
  }

  @Test
  public void propertyNotFoundSincePropertyNotSet()
  {
    final String value = uut.getProperty(PROJECT_VERSION_NAME);

    assertNull(value);
  }

  @Test
  public void propertyFoundSincePropertyIsSet()
  {
    final String version = "1.0.0";
    projectModel.setVersion(version);

    final String value = uut.getProperty(PROJECT_VERSION_NAME);

    assertEquals(version, value);
  }

  @Test
  public void propertyFoundSincePropertyIsSetInParent()
  {
    final String version = "1.0.0";
    final Model parentProjectModel = new Model();
    parentProjectModel.setVersion(version);
    final MavenProject parentProject = new MavenProject(parentProjectModel);
    project.setParent(parentProject);

    final String value = uut.getProperty(PROJECT_VERSION_NAME);

    assertEquals(version, value);
  }

  @Test
  public void nestedPropertyFoundSincePropertyIsSet()
  {
    final String finalName = "buildmetadata-maven-plugin-1.0.0.jar";
    final Build build = new Build();
    build.setFinalName(finalName);
    projectModel.setBuild(build);

    final String value = uut.getProperty("project.build.finalName");

    assertEquals(finalName, value);
  }
}
