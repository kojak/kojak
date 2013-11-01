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
package com.redhat.rcm.maven.plugin.buildmetadata.data;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmInfo;

/**
 * Configuration instance to create instances of {@link MetaDataProvider}.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MetaDataProviderBuilder
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Maven project.
   */
  private final MavenProject project;

  /**
   * The Maven session instance.
   */
  private final MavenSession session;

  /**
   * The runtime information of the Maven instance being executed for the build.
   */
  private final RuntimeInformation runtime;

  /**
   * The information for the SCM provided to the build plugin.
   */
  private final ScmInfo scmInfo;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param project the Maven project.
   * @param session the Maven session instance.
   * @param runtime the runtime information of the Maven instance being executed
   *          for the build.
   * @param scmInfo the information for the SCM provided to the build plugin.
   */
  public MetaDataProviderBuilder(final MavenProject project,
      final MavenSession session, final RuntimeInformation runtime,
      final ScmInfo scmInfo)
  {
    this.project = project;
    this.session = session;
    this.runtime = runtime;
    this.scmInfo = scmInfo;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Creates a configured instance of the {@link MetaDataProvider} interface.
   *
   * @param config the configuration for the build.
   * @return the created instance.
   * @throws MojoExecutionException if the instance cannot be created.
   */
  public MetaDataProvider build(final Provider config)
    throws MojoExecutionException
  {
    final MetaDataProvider instance = create(config.getType());
    initialize(instance, config.getProperties());
    return instance;
  }

  @SuppressWarnings("unchecked")
  private MetaDataProvider create(final String metaDataProviderClassName)
    throws MojoExecutionException
  {
    try
    {
      final Class<? extends MetaDataProvider> metaDataProviderClass =
          (Class<? extends MetaDataProvider>) Class
              .forName(metaDataProviderClassName);
      final MetaDataProvider instance = metaDataProviderClass.newInstance();
      return instance;
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException(
          "Cannot create instance for meta data provider class '"
              + metaDataProviderClassName + "'.", e);
    }
  }

  private void initialize(final MetaDataProvider instance,
      final Map<String, String> properties) throws MojoExecutionException
  {
    setNonNullProperty(instance, "project", project, MavenProject.class);
    setNonNullProperty(instance, "session", session, MavenSession.class);
    setNonNullProperty(instance, "runtime", runtime, RuntimeInformation.class);
    setNonNullProperty(instance, "scmInfo", scmInfo, ScmInfo.class);

    setProperties(instance, properties);
  }

  private void setNonNullProperty(final MetaDataProvider instance,
      final String propertyName, final Object propertyValue,
      final Class<?> propertyType) throws MojoExecutionException
  {
    if (propertyValue != null)
    {
      final Class<? extends MetaDataProvider> metaDataProviderClass =
          instance.getClass();
      try
      {
        final Field field = findField(metaDataProviderClass, propertyName);
        final Class<?> type = field.getType();
        if (type.isAssignableFrom(propertyType))
        {
          field.setAccessible(true);
          field.set(instance, propertyValue);
        }
      }
      catch (final NoSuchFieldException e)
      {
        // OK, no such field, so we do not set it.
      }
      catch (final Exception e)
      {
        throw new MojoExecutionException("Cannot set property '" + propertyName
                                         + "' for the instance of class '"
                                         + metaDataProviderClass.getName()
                                         + "'.", e);
      }
    }
  }

  private Field findField(final Class<?> type, final String propertyName)
    throws NoSuchFieldException
  {
    try
    {
      return type.getDeclaredField(propertyName);
    }
    catch (final NoSuchFieldException e)
    {
      if (type.getSuperclass().equals(Object.class))
      {
        throw e;
      }
    }
    return findField(type.getSuperclass(), propertyName);
  }

  private void setProperties(final MetaDataProvider instance,
      final Map<String, String> properties) throws MojoExecutionException
  {
    if (properties != null && !properties.isEmpty())
    {
      for (final Map.Entry<String, String> entry : properties.entrySet())
      {
        final String propertyName = entry.getKey();
        if (!Provider.RUN_AT_BUILD_POINT.equals(propertyName))
        {
          final String propertyValue = entry.getValue();
          setProperty(instance, propertyName, propertyValue);
        }
      }
    }
  }

  private void setProperty(final MetaDataProvider instance,
      final String propertyName, final String propertyValue)
    throws MojoExecutionException
  {
    final Class<? extends MetaDataProvider> metaDataProviderClass =
        instance.getClass();

    try
    {
      final Field field = findField(metaDataProviderClass, propertyName);
      field.setAccessible(true);
      final Class<?> type = field.getType();
      final Object typedPropertyValue =
          ConvertUtils.convert(propertyValue, type);
      field.set(instance, typedPropertyValue);
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException(
          "Cannot set property '" + propertyName + "' to value '"
              + propertyValue + "' for the instance of class '"
              + metaDataProviderClass.getName() + "'.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
