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

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.maven.project.MavenProject;

/**
 * Fetches properties from a Maven project. This includes properties of the
 * Maven project instance as well as properties from its properties section.
 * <p>
 * It is allowed to change the project passed to the helper instance after
 * creation of the instance as long as the caller makes sure that it is not
 * changed within a call of this instance. No synchronization is cared for by
 * this instance.
 * </p>
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MavenPropertyHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * All properties referring to the Maven project instance start with this
   * prefix.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String PROJECT_PROPERTY_NAME_PREFIX = "project.";

  /**
   * The length of the {@link #PROJECT_PROPERTY_NAME_PREFIX project property
   * prefix}.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final int PROJECT_PROPERTY_NAME_PREFIX_LENGTH =
      PROJECT_PROPERTY_NAME_PREFIX.length();

  // --- members --------------------------------------------------------------

  /**
   * The project to fetch properties from.
   */
  private final MavenProject project;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param project the project to fetch properties from.
   */
  public MavenPropertyHelper(final MavenProject project)
  {
    if (project == null)
    {
      throw new NullPointerException(
          "The property helper requires the project reference to be not 'null'.");
    }
    this.project = project;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Returns the property value specified by the given name.
   *
   * @param name the name of the property to return.
   * @return the property value for the given {@code name}.
   */
  public String getProperty(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException(
          "Name of requested property must not be 'null'");
    }

    String value = null;
    if (isProjectProperty(name))
    {
      value = getProjectProperty(name);
    }
    if (value == null)
    {
      value = getPropertiesProperty(name);
    }
    return value;
  }

  private boolean isProjectProperty(final String name)
  {
    return name != null && name.length() >= PROJECT_PROPERTY_NAME_PREFIX_LENGTH
           && name.startsWith(PROJECT_PROPERTY_NAME_PREFIX);
  }

  /**
   * Returns a property from the project instance.
   */
  private String getProjectProperty(final String name)
  {
    final String projectName =
        name.substring(PROJECT_PROPERTY_NAME_PREFIX_LENGTH);
    if (PropertyUtils.isReadable(project, projectName))
    {
      try
      {
        return getProjectProperty(project, projectName);
      }
      catch (final Exception e)
      {
        throw new IllegalStateException("Cannot access project property '"
                                        + name + "'.");
      }
    }

    return null;
  }

  private static String getProjectProperty(final MavenProject project,
      final String projectName) throws IllegalAccessException,
    InvocationTargetException, NoSuchMethodException
  {
    if (project != null)
    {
      final Object value = PropertyUtils.getProperty(project, projectName);
      if (value != null)
      {
        return String.valueOf(value);
      }
      else
      {
        return getProjectProperty(project.getParent(), projectName);
      }
    }
    return null;
  }

  /**
   * Returns a property from the <code>properties</code> section.
   */
  private String getPropertiesProperty(final String name)
  {
    return getPropertiesProperty(project, name);
  }

  private static String getPropertiesProperty(final MavenProject project,
      final String name)
  {
    String value = null;
    final Properties properties = project.getProperties();
    if (properties != null)
    {
      value = properties.getProperty(name);
    }

    if (value == null)
    {
      value = getPropertiesPropertyFromParent(project, name);
    }

    return value;
  }

  private static String getPropertiesPropertyFromParent(
      final MavenProject project, final String name)
  {
    final String value;
    final MavenProject parentProject = project.getParent();
    if (parentProject != null)
    {
      value = getPropertiesProperty(parentProject, name);
    }
    else
    {
      value = null;
    }
    return value;
  }

  // --- object basics --------------------------------------------------------

}
