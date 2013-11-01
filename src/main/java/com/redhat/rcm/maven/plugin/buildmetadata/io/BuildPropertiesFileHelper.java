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
package com.redhat.rcm.maven.plugin.buildmetadata.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.common.MojoUtils;
import com.redhat.rcm.maven.plugin.buildmetadata.common.SortedProperties;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;

/**
 * Helper to handle the build meta data properties file.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class BuildPropertiesFileHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The logger to use.
   */
  private final Log log;

  /**
   * The file to write to.
   */
  private final File propertiesOutputFile;

  /**
   * The normalizer to be applied to file name value to remove the base dir
   * prefix.
   */
  private final FilePathNormalizer filePathNormalizer;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param log the logger to use.
   * @param propertiesOutputFile the file to write to.
   * @param filePathNormalizer the normalizer to be applied to file name value
   *          to remove the base dir prefix.
   */
  public BuildPropertiesFileHelper(final Log log,
      final File propertiesOutputFile,
      final FilePathNormalizer filePathNormalizer)
  {
    this.log = log;
    this.propertiesOutputFile = propertiesOutputFile;
    this.filePathNormalizer = filePathNormalizer;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Writes the build meta data properties to the target file.
   *
   * @param buildMetaDataProperties the properties to write.
   * @return the reference to the written file.
   * @throws MojoExecutionException on any problem encountered while writing the
   *           properties.
   */
  public File writePropertiesFile(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    final File buildMetaDataFile =
        createBuildMetaDataFile(propertiesOutputFile);
    if (log.isInfoEnabled())
    {
      log.info("Writing properties '" + buildMetaDataFile.getAbsolutePath()
               + "'...");
    }

    OutputStream out = null;
    try
    {
      out = new BufferedOutputStream(new FileOutputStream(buildMetaDataFile));
      final String comments = "Created by buildmetadata-maven-plugin.";
      final Properties sortedBuildMetaDataProperties =
          SortedProperties.createSorted(buildMetaDataProperties);
      normalizeProperties(sortedBuildMetaDataProperties);
      sortedBuildMetaDataProperties.store(out, comments);
    }
    catch (final FileNotFoundException e)
    {
      final String message =
          "Cannot find file '" + buildMetaDataFile
              + "' to write properties to.";
      throw MojoUtils.createException(log, e, message);
    }
    catch (final IOException e)
    {
      final String message =
          "Cannot write properties to file '" + buildMetaDataFile + "'.";
      throw MojoUtils.createException(log, e, message);
    }
    finally
    {
      IOUtil.close(out);
    }

    return buildMetaDataFile;
  }

  private void normalizeProperties(final Properties buildMetaDataProperties)
  {
    final String filters =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_MAVEN_FILTERS);
    if (filters != null)
    {
      final String slashedFilters = filters.trim().replace('\\', '/');
      final String slashedBaseDir =
          filePathNormalizer.getBaseDir().replace('\\', '/');
      final String normBaseDir =
          slashedBaseDir.endsWith("/") ? slashedBaseDir : slashedBaseDir + '/';
      final String normFilters =
          StringUtils.replace(slashedFilters, normBaseDir, "");
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_MAVEN_FILTERS,
          normFilters);
    }
  }

  /**
   * Creates the properties file for the build meta data. If the directory to
   * place it in is not present, it will be created.
   *
   * @return the file to write the build properties to.
   * @throws MojoExecutionException if the output directory is not present and
   *           cannot be created.
   */
  private File createBuildMetaDataFile(final File propertiesOutputFile)
    throws MojoExecutionException
  {
    final File outputDirectory = propertiesOutputFile.getParentFile();
    if (!outputDirectory.exists())
    {
      final boolean created = outputDirectory.mkdirs();
      if (!created)
      {
        throw new MojoExecutionException("Cannot create output directory '"
                                         + outputDirectory + "'.");
      }
    }
    return propertiesOutputFile;
  }

  /**
   * Reads the build properties file from stream. The properties file is passed
   * to this instance via the {@link #BuildPropertiesFileHelper(Log, File)
   * constructor} {@code propertiesOutputFile}.
   *
   * @param buildMetaDataProperties the properties instance to append the read
   *          properties to.
   * @throws MojoExecutionException if the properties cannot be read.
   */
  public void readBuildPropertiesFile(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    InputStream inStream = null;
    try
    {
      inStream =
          new BufferedInputStream(new FileInputStream(propertiesOutputFile));
      buildMetaDataProperties.load(inStream);
    }
    catch (final IOException e)
    {
      throw new MojoExecutionException(
          "Cannot read provided properties file: "
              + propertiesOutputFile.getAbsolutePath(), e);
    }
    finally
    {
      IOUtil.close(inStream);
    }
  }

  /**
   * Fetches the project properties and if <code>null</code> returns a new empty
   * properties instance that is associated with the project.
   *
   * @param project the project whose properties are requested.
   * @return the properties of the project.
   */
  public Properties getProjectProperties(final MavenProject project)
  {
    Properties projectProperties = project.getProperties();
    if (projectProperties == null)
    {
      projectProperties = new Properties();
      project.getModel().setProperties(projectProperties);
    }

    return projectProperties;
  }

  // --- object basics --------------------------------------------------------

}
