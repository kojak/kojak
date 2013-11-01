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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.ObjectUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Property;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant.Section;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;
import com.redhat.rcm.maven.plugin.buildmetadata.util.NoopNormalizer;
import com.redhat.rcm.maven.plugin.buildmetadata.util.Normalizer;

/**
 * Renders the build report.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class BuildReportRenderer
{ // NOPMD
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The normalizer to be applied to file name value to remove the base dir
   * prefix.
   */
  private final FilePathNormalizer filePathNormalizer;

  /**
   * The sink to write to.
   */
  private final Sink sink;

  /**
   * The resource bundle to access localized messages.
   */
  private final ResourceBundle messages;

  /**
   * The properties file to read the build information from.
   */
  private final File buildMetaDataPropertiesFile;

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
   *
   * @parameter
   */
  private final List<Property> properties;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param filePathNormalizer the normalizer to be applied to file name value
   *          to remove the base dir prefix.
   * @param messages the resource bundle to access localized messages.
   * @param sink the sink to write to.
   * @param buildMetaDataPropertiesFile the properties file to read the build
   *          information from.
   * @param properties the list of a system properties or environment variables
   *          to be selected by the user to include into the build meta data
   *          properties.
   */
  public BuildReportRenderer(final FilePathNormalizer filePathNormalizer,
      final ResourceBundle messages, final Sink sink,
      final File buildMetaDataPropertiesFile, final List<Property> properties)
  {
    this.filePathNormalizer = filePathNormalizer;
    this.sink = sink;
    this.messages = messages;
    this.buildMetaDataPropertiesFile = buildMetaDataPropertiesFile;
    this.properties = properties;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Renders the report to the instance's sink.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  public void renderReport() throws MavenReportException
  {
    sink.head();
    sink.title();
    sink.text(messages.getString("report.name"));
    sink.title_();
    sink.head_();

    sink.body();
    renderBody();
    sink.body_();
    sink.flush();
    sink.close();
  }

  /**
   * Renders the body of the report.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  private void renderBody() throws MavenReportException
  {
    sink.section1();

    sink.sectionTitle1();
    sink.text(messages.getString("report.name"));
    sink.sectionTitle1_();

    sink.paragraph();
    sink.text(messages.getString("report.description"));
    sink.paragraph_();

    final Properties buildMetaDataProperties = readBuildMetaDataProperties();

    renderSections(buildMetaDataProperties);

    renderFooter();
    sink.section1_();
  }

  private void renderSections(final Properties buildMetaDataProperties)
  {
    for (final Section section : Constant.REPORT_PROPERTIES)
    {
      final List<String> properties = section.getProperties();
      if (hasPropertiesProvided(buildMetaDataProperties, properties))
      {
        final String sectionKey = section.getTitleKey();
        sink.sectionTitle2();
        sink.text(messages.getString(sectionKey));
        sink.sectionTitle2_();
        renderTableStart();
        for (final String key : properties)
        {
          renderCell(buildMetaDataProperties, key);
        }
        renderSelectedPropertiesForSection(buildMetaDataProperties, sectionKey);
        renderTableEnd();
      }
    }

    renderNonStandardProperties(buildMetaDataProperties);
  }

  private boolean hasPropertiesProvided(
      final Properties buildMetaDataProperties, final List<String> properties)
  {
    for (final String key : properties)
    {
      final Object value = buildMetaDataProperties.get(key);
      if (value != null && StringUtils.isNotBlank(String.valueOf(value)))
      {
        return true;
      }
    }

    final Set<String> selectedProperties = createSelectedProperties();
    for (final String key : selectedProperties)
    {
      final Object value = buildMetaDataProperties.get(key);
      if (value != null && StringUtils.isNotBlank(String.valueOf(value)))
      {
        return true;
      }
    }

    return false;
  }

  private void renderSelectedPropertiesForSection(
      final Properties buildMetaDataProperties, final String sectionKey)
  {
    if (properties != null && !properties.isEmpty())
    {
      for (final Property property : properties)
      {
        if (sectionKey.equals(property.getSection()))
        {
          final String key = property.getName();
          renderCell(buildMetaDataProperties, key);
        }
      }
    }
  }

  private void renderNonStandardProperties(
      final Properties buildMetaDataProperties)
  {
    final Properties nonStandardProperties =
        Constant.calcNonStandardProperties(buildMetaDataProperties, properties);
    if (!nonStandardProperties.isEmpty())
    {
      sink.sectionTitle2();
      sink.text(messages.getString(Constant.SECTION_BUILD_MISC));
      sink.sectionTitle2_();
      renderTableStart();
      for (final Enumeration<Object> en = nonStandardProperties.keys(); en
          .hasMoreElements();)
      {
        final String key = String.valueOf(en.nextElement());
        if (Constant.isIntendedForMiscSection(key))
        {
          renderCell(nonStandardProperties, key);
        }
      }
      renderTableEnd();
    }
  }

  private Set<String> createSelectedProperties()
  {
    final Set<String> selectedProperties = new HashSet<String>();

    if (properties != null)
    {
      for (final Property property : properties)
      {
        selectedProperties.add(property.getName());
      }
    }

    return selectedProperties;
  }

  private void renderTableEnd()
  {
    sink.table_();
  }

  private void renderTableStart()
  {
    sink.table();
    sink.tableRow();
    sink.tableHeaderCell("200");
    final String topicLabel = messages.getString("report.table.header.topic");
    sink.text(topicLabel);
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    final String valueLabel = messages.getString("report.table.header.value");
    sink.text(valueLabel);
    sink.tableHeaderCell_();
    sink.tableRow_();
  }

  /**
   * Renders a single cell of the table.
   *
   * @param buildMetaDataProperties build meta data properties to access the
   *          data to be rendered.
   * @param key the key to the data to be rendered.
   */
  private void renderCell(final Properties buildMetaDataProperties,
      final String key)
  {
    final Object value = buildMetaDataProperties.get(key);
    if (value != null)
    {
      sink.tableRow();
      sink.tableCell();
      sink.text(getLabel(key));
      sink.tableCell_();
      sink.tableCell();
      if (Constant.PROP_NAME_MAVEN_ACTIVE_PROFILES.equals(key))
      {
        renderMultiTupleValue(buildMetaDataProperties, value,
            Constant.MAVEN_ACTIVE_PROFILE_PREFIX);
      }
      else if (Constant.PROP_NAME_SCM_LOCALLY_MODIFIED_FILES.equals(key))
      {
        final String filesValue = Constant.prettifyFilesValue(value);
        renderMultiValue(filesValue, NoopNormalizer.INSTANCE);
      }
      else if (Constant.PROP_NAME_MAVEN_GOALS.equals(key))
      {
        renderMultiValue(value, NoopNormalizer.INSTANCE);
      }
      else if (Constant.PROP_NAME_MAVEN_FILTERS.equals(key))
      {
        renderMultiValue(value, filePathNormalizer);
      }
      else
      {
        renderSingleValue(value);
      }
      sink.tableCell_();
      sink.tableRow_();
    }
  }

  private void renderSingleValue(final Object value)
  {
    final String stringValue = String.valueOf(value);
    if (stringValue != null && !isLink(stringValue))
    {
      sink.text(stringValue);
    }
    else
    {
      sink.link(stringValue);
      sink.text(stringValue);
      sink.link_();
    }
  }

  private boolean isLink(final String input)
  {
    return (input.startsWith("http://") || input.startsWith("https://"));
  }

  private void renderMultiTupleValue(final Properties buildMetaDataProperties,
      final Object value, final String subKeyPrefix)
  {
    final String stringValue = Constant.prettify((String) value);
    if (hasMultipleValues(stringValue))
    {
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      sink.numberedList(Sink.NUMBERING_DECIMAL);
      while (tokenizer.hasMoreTokens())
      {
        final String profileName = tokenizer.nextToken().trim();
        final String subKey = subKeyPrefix + '.' + profileName;
        final Object subValue = buildMetaDataProperties.get(subKey);
        final String item = profileName + ':' + subValue;
        sink.listItem();
        sink.text(item);
        sink.listItem_();
      }
      sink.numberedList_();
    }
    else
    {
      sink.text(String.valueOf(value));
    }
  }

  private void renderMultiValue(final Object value, final Normalizer normalizer)
  {
    final String stringValue = Constant.prettify(ObjectUtils.toString(value));
    if (hasMultipleValues(stringValue))
    {
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      sink.numberedList(Sink.NUMBERING_DECIMAL);
      while (tokenizer.hasMoreTokens())
      {
        final String subValue = tokenizer.nextToken().trim();
        final String textValue = normalizer.normalize(subValue);
        sink.listItem();
        sink.text(textValue);
        sink.listItem_();
      }
      sink.numberedList_();
    }
    else
    {
      final String textValue = normalizer.normalize(stringValue);
      sink.text(textValue);
    }
  }

  private boolean hasMultipleValues(final String stringValue)
  {
    return stringValue.indexOf(',') != -1;
  }

  private String getLabel(final String key)
  {
    try
    {
      return messages.getString(key);
    }
    catch (final MissingResourceException e)
    {
      if (properties != null)
      {
        for (final Property property : properties)
        {
          final String label = property.getLabel();
          if (StringUtils.isNotBlank(label)
              && key.equals(property.getMappedName()))
          {
            return label;
          }
        }
      }
      return key;
    }
  }

  /**
   * Renders the footer text.
   */
  private void renderFooter()
  {
    final String footerText = messages.getString("report.footer");
    if (StringUtils.isNotBlank(footerText))
    {
      sink.rawText(footerText);
    }
  }

  /**
   * Reads the build meta data properties from the well known location.
   *
   * @return the read properties.
   * @throws MavenReportException if the properties cannot be read.
   */
  private Properties readBuildMetaDataProperties() throws MavenReportException
  {
    final Properties buildMetaDataProperties = new Properties();
    InputStream inStream = null;
    try
    {
      inStream =
          new BufferedInputStream(new FileInputStream(
              this.buildMetaDataPropertiesFile));
      buildMetaDataProperties.load(inStream);
    }
    catch (final IOException e)
    {
      throw new MavenReportException("Cannot read build properties file '"
                                     + this.buildMetaDataPropertiesFile + "'.",
          e);
    }
    finally
    {
      IOUtil.close(inStream);
    }
    return buildMetaDataProperties;
  }

  // --- object basics --------------------------------------------------------

}
