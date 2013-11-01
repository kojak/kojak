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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.common.MojoUtils;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Property;
import com.redhat.rcm.maven.plugin.buildmetadata.maven.MavenPropertyHelper;

/**
 * Extracts information from the Maven project, session, and runtime
 * information.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MavenMetaDataProvider extends AbstractMetaDataProvider
{ // NOPMD
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The selection of properties to be added or hidden.
   */
  private final MavenMetaDataSelection selection;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Constructor.
   *
   * @param project the Maven project.
   * @param session the Maven session instance.
   * @param runtime the runtime information of the Maven instance being executed
   *          for the build.
   * @param selection the selection of properties to be added or hidden.
   * @see com.redhat.rcm.maven.plugin.buildmetadata.data.AbstractMetaDataProvider#AbstractMetaDataProvider()
   */
  public MavenMetaDataProvider(final MavenProject project,
      final MavenSession session, final RuntimeInformation runtime,
      final MavenMetaDataSelection selection)
  {
    this.project = project;
    this.session = session;
    this.runtime = runtime;
    this.selection = selection;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Adds the information of the Maven runtime as build properties.
   *
   * @param buildMetaDataProperties the build meta data properties.
   */
  public void provideBuildMetaData(final Properties buildMetaDataProperties)
  {
    if (runtime != null)
    {
      provideRuntimeInfo(buildMetaDataProperties);
    }

    if (session != null)
    {
      provideSessionInfo(buildMetaDataProperties);
    }

    if (project != null)
    {
      provideProjectInfo(buildMetaDataProperties);
    }

    final List<Property> selectedProperties =
        selection.getSelectedSystemProperties();
    if (selectedProperties != null && !selectedProperties.isEmpty())
    {
      provideSelectedProperties(buildMetaDataProperties, selectedProperties);
    }
  }

  private void provideRuntimeInfo(final Properties buildMetaDataProperties)
  {
    final ArtifactVersion mavenVersion = runtime.getApplicationVersion();
    final String mavenVersionString = mavenVersion.toString();
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_VERSION,
        mavenVersionString);

    if (selection.isAddJavaRuntimeInfo())
    {
      providePropertyIfExists(buildMetaDataProperties, "java.runtime.name",
          Constant.PROP_NAME_JAVA_RUNTIME_NAME);
      providePropertyIfExists(buildMetaDataProperties, "java.runtime.version",
          Constant.PROP_NAME_JAVA_RUNTIME_VERSION);
      providePropertyIfExists(buildMetaDataProperties, "java.vendor",
          Constant.PROP_NAME_JAVA_VENDOR);
      providePropertyIfExists(buildMetaDataProperties, "java.vm.name",
          Constant.PROP_NAME_JAVA_VM);
      providePropertyIfExists(buildMetaDataProperties,
          "sun.management.compiler", Constant.PROP_NAME_JAVA_COMPILER);
    }

    if (selection.isAddOsInfo())
    {
      providePropertyIfExists(buildMetaDataProperties, "os.name",
          Constant.PROP_NAME_OS_NAME);
      providePropertyIfExists(buildMetaDataProperties, "os.arch",
          Constant.PROP_NAME_OS_ARCH);
      providePropertyIfExists(buildMetaDataProperties, "os.version",
          Constant.PROP_NAME_OS_VERSION);
    }
  }

  private void providePropertyIfExists(
      final Properties buildMetaDataProperties,
      final String systemPropertyName, final String propName)
  {
    final String value = System.getProperty(systemPropertyName);
    if (StringUtils.isNotBlank(value))
    {
      buildMetaDataProperties.setProperty(propName, value);
    }
  }

  private void provideSelectedProperties(
      final Properties buildMetaDataProperties,
      final List<Property> selectedProperties)
  {
    final MavenPropertyHelper helper = new MavenPropertyHelper(project);
    for (final Property property : selectedProperties)
    {
      final String name = property.getName();

      final String fixedValue = property.getValue();
      if (fixedValue != null)
      {
        setProperty(buildMetaDataProperties, property, fixedValue);
      }
      else
      {
        final String mavenPropertyValue = helper.getProperty(name);
        if (mavenPropertyValue != null)
        {
          setProperty(buildMetaDataProperties, property, mavenPropertyValue);
        }
        else
        {
          final String value = System.getProperty(name);
          if (StringUtils.isNotBlank(value))
          {
            setProperty(buildMetaDataProperties, property, value);
          }
          else
          {
            final String envValue = System.getenv(name);
            // CHECKSTYLE:OFF
            if (StringUtils.isNotBlank(envValue))
            {
              setProperty(buildMetaDataProperties, property, envValue);
            }
            // CHECKSTYLE:ON
          }
        }
      }
    }
  }

  private static void setProperty(final Properties buildMetaDataProperties,
      final Property property, final String value)
  {
    final String name = property.getMappedName();
    buildMetaDataProperties.setProperty(name, value);
  }

  private void provideSessionInfo(final Properties buildMetaDataProperties)
  {
    final Properties executionProperties =
        provideExecutionProperties(buildMetaDataProperties);
    if (selection.isAddMavenExecutionInfo())
    {
      provideGoals(buildMetaDataProperties);
      provideSpecialEnvVars(buildMetaDataProperties, executionProperties);
    }
  }

  private void provideProjectInfo(final Properties buildMetaDataProperties)
  {
    if (selection.isAddMavenExecutionInfo())
    {
      provideActiveProfiles(buildMetaDataProperties);
      provideExecutedProjectInfo(buildMetaDataProperties);
      final String filters = MojoUtils.toPrettyString(project.getFilters());
      if (StringUtils.isNotBlank(filters))
      {
        buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_FILTERS,
            filters);
      }
    }

    if (selection.isAddProjectInfo())
    {
      final String projectUrl = project.getUrl();
      if (StringUtils.isNotBlank(projectUrl))
      {
        buildMetaDataProperties.setProperty(
            Constant.PROP_NAME_PROJECT_HOMEPAGE, projectUrl);
      }

      final Properties projectProperties = project.getProperties();
      if (projectProperties != null)
      {
        providePropertiesProperty(buildMetaDataProperties, projectProperties,
            Constant.PROP_NAME_PROJECT_OPS);
        providePropertiesProperty(buildMetaDataProperties, projectProperties,
            Constant.PROP_NAME_PROJECT_CATEGORY);
        providePropertiesProperty(buildMetaDataProperties, projectProperties,
            Constant.PROP_NAME_PROJECT_SUBCATEGORY);
        providePropertiesProperty(buildMetaDataProperties, projectProperties,
            Constant.PROP_NAME_PROJECT_TAGS);
      }
    }
  }

  private static void providePropertiesProperty(
      final Properties buildMetaDataProperties,
      final Properties projectProperties, final String name)
  {
    final String value = projectProperties.getProperty(name);
    if (StringUtils.isNotBlank(value))
    {
      buildMetaDataProperties.setProperty(name, value);
    }
  }

  private void provideExecutedProjectInfo(
      final Properties buildMetaDataProperties)
  {
    buildMetaDataProperties.setProperty(
        Constant.PROP_NAME_MAVEN_IS_EXECUTION_ROOT,
        String.valueOf(project.isExecutionRoot()));
    final MavenProject executionProject = project.getExecutionProject();
    if (executionProject != null)
    {
      buildMetaDataProperties.setProperty(
          Constant.PROP_NAME_MAVEN_EXECUTION_PROJECT, executionProject.getId());
    }
  }

  private void provideActiveProfiles(final Properties buildMetaDataProperties)
  {
    final List<Profile> profiles = getActiveProfiles();
    if (profiles != null && !profiles.isEmpty())
    {
      final List<String> profileIds = new ArrayList<String>(profiles.size());
      for (final Profile profile : profiles)
      {
        final String profileId = profile.getId();
        if (!profileIds.contains(profileId))
        {
          final String key =
              Constant.MAVEN_ACTIVE_PROFILE_PREFIX + '.' + profileId;
          final String value = profile.getSource();
          buildMetaDataProperties.setProperty(key, value);
          profileIds.add(profileId);
        }
      }
      buildMetaDataProperties.setProperty(
          Constant.PROP_NAME_MAVEN_ACTIVE_PROFILES,
          MojoUtils.toPrettyString(profileIds));
    }
  }

  /**
   * Delegates call to
   * {@link org.apache.maven.project.MavenProject#getActiveProfiles()}.
   *
   * @return the result of the call to
   *         {@link org.apache.maven.project.MavenProject#getActiveProfiles()}.
   * @see org.apache.maven.project.MavenProject#getActiveProfiles()
   */
  @SuppressWarnings("unchecked")
  private List<Profile> getActiveProfiles()
  {
    return project.getActiveProfiles();
  }

  private void provideGoals(final Properties buildMetaDataProperties)
  {
    final String goals = MojoUtils.toPrettyString(session.getGoals());
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_GOALS, goals);
  }

  private Properties provideExecutionProperties(
      final Properties buildMetaDataProperties)
  {
    final Properties executionProperties = session.getExecutionProperties();
    if (selection.isAddEnvInfo())
    {
      final Set<Object> sortedKeys = new TreeSet<Object>();
      sortedKeys.addAll(executionProperties.keySet());
      for (final Object originalKey : sortedKeys)
      {
        final String key =
            Constant.MAVEN_EXECUTION_PROPERTIES_PREFIX + '.' + originalKey;
        final String value =
            executionProperties.getProperty((String) originalKey);
        buildMetaDataProperties.setProperty(key, value);
      }
    }
    return executionProperties;
  }

  private void provideSpecialEnvVars(final Properties buildMetaDataProperties,
      final Properties executionProperties)
  {
    if (!selection.isHideCommandLineInfo())
    {
      final String commandLine = getCommandLine(executionProperties);
      if (!StringUtils.isEmpty(commandLine))
      {
        buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_CMDLINE,
            commandLine);
      }
    }

    if (!selection.isHideMavenOptsInfo())
    {
      final String mavenOpts =
          executionProperties.getProperty("env.MAVEN_OPTS");
      if (!StringUtils.isEmpty(mavenOpts))
      {
        buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_OPTS,
            mavenOpts);
      }
    }

    if (!selection.isHideJavaOptsInfo())
    {
      final String javaOpts = executionProperties.getProperty("env.JAVA_OPTS");
      if (!StringUtils.isEmpty(javaOpts))
      {
        buildMetaDataProperties.setProperty(Constant.PROP_NAME_JAVA_OPTS,
            javaOpts);
      }
    }
  }

  private static String getCommandLine(final Properties executionProperties)
  {
    String commandLine = probEnv(executionProperties);

    if (StringUtils.isNotBlank(commandLine))
    {
      return commandLine;
    }

    commandLine = probeSunVm(executionProperties);

    if (StringUtils.isNotBlank(commandLine))
    {
      return commandLine;
    }

    commandLine = probeLinuxPs();

    if (StringUtils.isBlank(commandLine))
    {
      commandLine = probeJmx();
    }

    return commandLine;
  }

  private static String probEnv(final Properties executionProperties)
  {
    // Typically works on Windows
    final String commandLine =
        executionProperties.getProperty("env.MAVEN_CMD_LINE_ARGS");
    return commandLine;
  }

  private static String probeSunVm(final Properties executionProperties)
  {
    // Might work on some machines and does not hurt too much to try ...
    String commandLine = executionProperties.getProperty("sun.java.command");
    if (commandLine != null)
    {
      final String prefix =
          "org.codehaus.plexus.classworlds.launcher.Launcher ";
      if (commandLine.startsWith(prefix))
      {
        commandLine = commandLine.substring(prefix.length());
      }
    }
    return commandLine;
  }

  private static String probeLinuxPs()
  {
    final Long pid = getProcessId();
    if (pid != null)
    {
      try
      {
        final Process process =
            Runtime.getRuntime().exec("ps -o args -p " + pid);
        try
        {
          process.waitFor();
        }
        catch (final InterruptedException e)
        {
          // continue
        }
        final int exit = process.exitValue();
        if (exit == 0)
        {
          final String commandLine = IOUtil.toString(process.getInputStream());
          return commandLine;
        }
      }
      catch (final IOException e)
      {
        // Silently ignore that the command failed.
      }
    }
    return null;
  }

  // see
  // http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
  private static Long getProcessId()
  {
    final String name = ManagementFactory.getRuntimeMXBean().getName();
    final int index = name.indexOf('@');

    if (index > 0)
    {
      try
      {
        return Long.parseLong(name.substring(0, index));
      }
      catch (final NumberFormatException e)
      {
        // return null at the end
      }
    }
    return null;
  }

  private static String probeJmx()
  {
    // Won't get too much useful, but may be better than nothing ...
    final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    final String commandLine = runtime.getInputArguments().toString();
    return commandLine;
  }

  // --- object basics --------------------------------------------------------

}
