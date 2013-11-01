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

import java.util.Date;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.io.BuildPropertiesFileHelper;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;

/**
 * Adds the build time to the properties file and runs all providers flagged
 * with
 * {@link com.redhat.rcm.maven.plugin.buildmetadata.data.Provider#RUN_AT_BUILD_POINT}
 * .
 *
 * @goal build-point
 * @phase prepare-package
 * @requiresProject
 * @threadSafe
 * @since 1.0
 * @description Provides the duration of the build at the given point to the
 *              build properties and runs all providers flagged with
 *              'runAtBuildPoint' with a value of 'true'. The mojo is run at the
 *              specified point. Note that dependent on the phase, the build
 *              information (such as the duration of the build) may or may not
 *              packaged with the artifacts.
 */
public final class BuildPointMojo extends AbstractBuildMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * Constant to store the current time stamp. Must be removed before the build
   * properties are written to the project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String TMP_BUILD_END = "build.tmp.timestamp";

  // --- members --------------------------------------------------------------

  /**
   * The name of the build point to append to the duration property name. If
   * blank, the duration property will be stored as <code>build.duration</code>.
   * <p>
   * This way build durations may be taken from different phases of the build.
   * </p>
   *
   * @parameter
   * @since 1.0
   */
  private String name;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    super.execute();

    if (propertiesOutputFile.exists())
    {
      final Properties buildMetaDataProperties = new Properties();
      final String baseDir = project.getBasedir().getAbsolutePath();
      final FilePathNormalizer filePathNormalizer =
          new FilePathNormalizer(baseDir);
      final BuildPropertiesFileHelper helper =
          new BuildPropertiesFileHelper(getLog(), propertiesOutputFile,
              filePathNormalizer);
      helper.readBuildPropertiesFile(buildMetaDataProperties);

      provideBuildPointInfo(buildMetaDataProperties, helper);
      provideBuildMetaData(buildMetaDataProperties, null, providers, true);

      helper.writePropertiesFile(buildMetaDataProperties);
      updateMavenEnvironment(buildMetaDataProperties, helper);
    }
    else
    {
      getLog()
          .info(
              "Skipping build point '" + name + "' since no "
                  + propertiesOutputFile.getName()
                  + " with build meta data found.");
    }
  }

  private void provideBuildPointInfo(final Properties buildMetaDataProperties,
      final BuildPropertiesFileHelper helper)
  {
    final Date start = session.getStartTime();
    final Date end = new Date();
    final long duration = end.getTime() - start.getTime();
    final String durationPropertyName = createDurationPropertyName();
    final String durationString = String.valueOf(duration);
    buildMetaDataProperties.setProperty(durationPropertyName, durationString);
    setTimeDifference(helper, buildMetaDataProperties, end, durationString,
        durationPropertyName);
  }

  private void setTimeDifference(
      final BuildPropertiesFileHelper helper, // NOPMD
      final Properties buildMetaDataProperties, final Date currentEnd,
      final String durationString, final String durationPropertyName)
  {
    final Properties projectProperties = helper.getProjectProperties(project);
    final String previousDurationEnd =
        projectProperties.getProperty(TMP_BUILD_END);
    final String diffPropertyName = durationPropertyName + ".diff";
    if (StringUtils.isNotBlank(previousDurationEnd))
    {
      final long previousTimestamp = Long.parseLong(previousDurationEnd);
      final long difference = currentEnd.getTime() - previousTimestamp;
      buildMetaDataProperties.setProperty(diffPropertyName,
          String.valueOf(difference));
    }
    else
    {
      buildMetaDataProperties.setProperty(diffPropertyName, durationString);
    }

    projectProperties.setProperty(TMP_BUILD_END,
        String.valueOf(currentEnd.getTime()));
  }

  private String createDurationPropertyName()
  {
    if (StringUtils.isNotBlank(name))
    {
      return Constant.PROP_NAME_BUILD_DURATION + '.' + name;
    }
    return Constant.PROP_NAME_BUILD_DURATION;
  }

  // --- object basics --------------------------------------------------------

}
