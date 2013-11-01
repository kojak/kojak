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

import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link MavenPropertyHelper} accessing properties from the properties
 * section of a project instance.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class MavenPropertyHelperPropertiesTest
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * A property test name.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String PROPERTY_NAME = "my.property";

  /**
   * A property test value.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String PROPERTY_VALUE = "my.value";

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

  private void setupParentProjectWithProperty(final String propertyName,
      final String propertyValue)
  {
    final Properties properties = new Properties();
    properties.setProperty(propertyName, propertyValue);

    final Model parentProjectModel = new Model();
    parentProjectModel.setProperties(properties);
    final MavenProject parentProject = new MavenProject(parentProjectModel);
    project.setParent(parentProject);
  }

  // --- tests ----------------------------------------------------------------

  @Test(expected = NullPointerException.class)
  public void noPropertyName()
  {
    uut.getProperty(null);
  }

  @Test
  public void propertyNotFoundSinceThereAreNoProperties()
  {
    final String value = uut.getProperty(PROPERTY_NAME);

    assertNull(value);
  }

  @Test
  public void propertyNotFoundSincePropertiesIsEmpty()
  {
    projectModel.setProperties(new Properties());

    final String value = uut.getProperty(PROPERTY_NAME);

    assertNull(value);
  }

  @Test
  public void propertyFound()
  {
    final String propertyName = PROPERTY_NAME;
    final String propertyValue = PROPERTY_VALUE;
    final Properties properties = new Properties();
    properties.setProperty(propertyName, propertyValue);
    projectModel.setProperties(properties);

    final String value = uut.getProperty(propertyName);

    assertEquals(propertyValue, value);
  }

  @Test
  public void propertyFoundEvenWithProjectPrefix()
  {
    final String propertyName = "project.valid";
    final String propertyValue = PROPERTY_VALUE;
    final Properties properties = new Properties();
    properties.setProperty(propertyName, propertyValue);
    projectModel.setProperties(properties);

    final String value = uut.getProperty(propertyName);

    assertEquals(propertyValue, value);
  }

  @Test
  public void propertyFoundInParentWithNullPropertiesInProject()
  {
    final String propertyName = PROPERTY_NAME;
    final String propertyValue = PROPERTY_VALUE;
    setupParentProjectWithProperty(propertyName, propertyValue);

    final String value = uut.getProperty(propertyName);

    assertEquals(propertyValue, value);
  }

  @Test
  public void propertyFoundInParentWithPropertiesInProject()
  {
    final String propertyName = PROPERTY_NAME;
    final String propertyValue = PROPERTY_VALUE;
    setupParentProjectWithProperty(propertyName, propertyValue);
    projectModel.setProperties(new Properties());

    final String value = uut.getProperty(propertyName);

    assertEquals(propertyValue, value);
  }
}
