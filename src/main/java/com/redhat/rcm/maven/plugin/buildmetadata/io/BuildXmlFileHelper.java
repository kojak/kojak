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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;
import org.w3c.dom.Document;

import com.redhat.rcm.maven.plugin.buildmetadata.common.MojoUtils;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Property;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;
import com.redhat.rcm.maven.plugin.buildmetadata.util.MojoIoUtils;

/**
 * Helper to handle the build meta data properties file.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class BuildXmlFileHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The path to the project folder on the file system. Used to trim references
   * to project files.
   */
  private final String projectRootPath;

  /**
   * The logger to use.
   */
  private final Log log;

  /**
   * The file to write to.
   */
  private final File xmlOutputFile;

  /**
   * The list of a system properties or environment variables to be selected by
   * the user to include into the build meta data properties.
   * <p>
   * The name is the name of the property, the section is relevant for placing
   * the property in one of the following sections:
   * </p>
   * <ul>
   * <li><code>build.scm</code></li>
   * <li><code>build.dateAndVersion</code></li>
   * <li><code>build.runtime</code></li>
   * <li><code>build.java</code></li>
   * <li><code>build.maven</code></li>
   * <li><code>build.misc</code></li>
   * </ul>
   * <p>
   * If no valid section is given, the property is silently rendered in the
   * <code>build.misc</code> section.
   * </p>
   */
  private final List<Property> selectedProperties;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param projectRootPath the path to the project folder on the file system.
   * @param log the logger to use.
   * @param xmlOutputFile the file to write to.
   * @param selectedProperties the list of a system properties or environment
   *          variables to be selected by the user to include into the build
   *          meta data properties.
   */
  public BuildXmlFileHelper(final String projectRootPath, final Log log,
      final File xmlOutputFile, final List<Property> selectedProperties)
  {
    this.projectRootPath = projectRootPath;
    this.log = log;
    this.xmlOutputFile = xmlOutputFile;
    this.selectedProperties = selectedProperties;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Writes the build meta data XML file to the target file.
   *
   * @param buildMetaDataProperties the properties to write.
   * @return the reference to the written file.
   * @throws MojoExecutionException on any problem encountered while writing the
   *           XML file.
   */
  public File writeXmlFile(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    final File buildMetaDataFile = createBuildMetaDataFile(xmlOutputFile);
    if (log.isInfoEnabled())
    {
      log.info("Writing XML report '" + buildMetaDataFile.getAbsolutePath()
               + "'...");
    }

    writeContent(buildMetaDataProperties, buildMetaDataFile);

    return buildMetaDataFile;
  }

  private void writeContent(final Properties buildMetaDataProperties,
      final File buildMetaDataFile) throws MojoExecutionException
  {
    OutputStream out = null;
    try
    {
      out = new BufferedOutputStream(new FileOutputStream(buildMetaDataFile));
      serializeDocument(buildMetaDataProperties, out);
    }
    catch (final FileNotFoundException e)
    {
      final String message =
          "Cannot find file '" + buildMetaDataFile
              + "' to write XML report to.";
      throw MojoUtils.createException(log, e, message);
    }
    catch (final IOException e)
    {
      final String message =
          "Cannot write XML report to file '" + buildMetaDataFile + "'.";
      throw MojoUtils.createException(log, e, message);
    }
    catch (final ParserConfigurationException e)
    {
      final String message =
          "Cannot create XML report to write to file '" + buildMetaDataFile
              + "'.";
      throw MojoUtils.createException(log, e, message);
    }
    catch (final TransformerException e)
    {
      final String message =
          "Cannot transform build meta data to XML to write to file '"
              + buildMetaDataFile + "'.";
      throw MojoUtils.createException(log, e, message);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private void serializeDocument(final Properties buildMetaDataProperties,
      final OutputStream out) throws ParserConfigurationException, IOException,
    TransformerException
  {
    final Document document = createDocument();
    final SdocBuilder builder =
        new SdocBuilder(new FilePathNormalizer(projectRootPath), document,
            buildMetaDataProperties, selectedProperties);
    builder.writeDocumentContent();
    MojoIoUtils.serialize(document, out);
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

  private Document createDocument() throws ParserConfigurationException
  {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document document = builder.newDocument();
    return document;
  }

  // --- object basics --------------------------------------------------------

}
