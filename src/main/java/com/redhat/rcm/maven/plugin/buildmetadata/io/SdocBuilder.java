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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Property;
import com.redhat.rcm.maven.plugin.buildmetadata.common.SortedProperties;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;

/**
 * Creates an XML report with the build meta data. The report contains the same
 * information as the <code>build.properties</code> file. It is useful for use
 * cases where the build meta data information will be further processed by XSL
 * transformations which require XML documents as input.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Version:1.0 $
 */
public final class SdocBuilder
{ // NOPMD
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The URI of the XML schema instance.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String XML_SCHEMA_INSTANCE =
      "http://www.w3.org/2001/XMLSchema-instance";

  /**
   * The URI of the code doctype.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String CODE_URI =
      "http://github.com/sbadakhc/buildmetadata-maven-plugin";

  /**
   * The generic identifier of the element name containing a version
   * information.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String GI_VERSION = "version";

  /**
   * The generic identifier of the element name containing a name information.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String GI_NAME = "name";

  /**
   * Reference to the logger for this class.
   */
  private static final Log LOG = LogFactory.getLog(SdocBuilder.class);

  // --- members --------------------------------------------------------------

  /**
   * The normalizer to be applied to file name value to remove the base dir
   * prefix.
   */
  private final FilePathNormalizer filePathNormalizer;

  /**
   * The empty document to write to.
   */
  private final Document document;

  /**
   * The properties to write to the XML report.
   */
  private final Properties buildMetaDataProperties;

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
   * @param filePathNormalizer the normalizer to be applied to file name value
   *          to remove the base dir prefix.
   * @param document the empty document to write to.
   * @param buildMetaDataProperties the properties to write to the XML report.
   * @param selectedProperties the list of a system properties or environment
   *          variables to be selected by the user to include into the build
   *          meta data properties.
   */
  public SdocBuilder(final FilePathNormalizer filePathNormalizer,
      final Document document, final Properties buildMetaDataProperties,
      final List<Property> selectedProperties)
  {
    this.filePathNormalizer = filePathNormalizer;
    this.document = document;
    this.buildMetaDataProperties = buildMetaDataProperties;
    this.selectedProperties = selectedProperties;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Writes the content to the document.
   *
   * @return the written XML document.
   * @throws IOException on any problem writing to the XML document.
   */
  public Document writeDocumentContent() throws IOException
  {
    final Element docRoot = createDocRoot();

    createContentElement(GI_NAME, Constant.PROP_NAME_FULL_VERSION, docRoot);
    createContentElement("category", Constant.PROP_NAME_PROJECT_CATEGORY,
        docRoot);
    createContentElement("subcategory", Constant.PROP_NAME_PROJECT_SUBCATEGORY,
        docRoot);

    createContentElement(GI_VERSION, Constant.PROP_NAME_VERSION, docRoot);
    createContentElement("groupId", Constant.PROP_NAME_GROUP_ID, docRoot);
    createContentElement("artifactId", Constant.PROP_NAME_ARTIFACT_ID, docRoot);
    final String date = formatDate(Constant.PROP_NAME_BUILD_DATE);
    createValueElement("date", date, docRoot);
    createContentElement("timestamp", Constant.PROP_NAME_BUILD_TIMESTAMP,
        docRoot);
    createTagsElement(docRoot);
    createContentElement("build-year", Constant.PROP_NAME_BUILD_YEAR, docRoot);

    createProjectElement(docRoot);
    createScmElement(docRoot);
    createRuntimeElement(docRoot);
    createMiscElement(docRoot);

    return document;
  }

  private void createTagsElement(final Element docRoot)
  {
    final Element tags = document.createElement("tags");
    final String tagsString =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_PROJECT_TAGS);
    renderList(tags, "tag", tagsString);
    if (tags.hasChildNodes())
    {
      docRoot.appendChild(tags);
    }
  }

  private String formatDate(final String datePropertyKey)
  {
    final String originalDateString =
        buildMetaDataProperties.getProperty(datePropertyKey);
    if (StringUtils.isNotBlank(originalDateString))
    {
      try
      {
        final String originalPattern =
            buildMetaDataProperties
                .getProperty(Constant.PROP_NAME_BUILD_DATE_PATTERN);
        final DateFormat format =
            new SimpleDateFormat(originalPattern, Locale.ENGLISH);
        final Date date = format.parse(originalDateString);
        final String dateString =
            DateFormatUtils.ISO_DATETIME_FORMAT.format(date);
        return dateString;
      }
      catch (final ParseException e)
      {
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Cannot parse date of property '" + datePropertyKey + "': "
                    + originalDateString + ". Skipping...");
        }
        return null;
      }
    }
    return null;
  }

  private void createScmElement(final Element docRoot)
  {
    final Element parent = document.createElement("scm");
    createContentElement("revision", Constant.PROP_NAME_SCM_REVISION_ID, parent);
    final String date = formatDate(Constant.PROP_NAME_SCM_REVISION_DATE);
    createValueElement("revision-date", date, parent);
    createContentElement("url", Constant.PROP_NAME_SCM_URL, parent);
    createLocallyModifiedFiles(parent);
    docRoot.appendChild(parent);
  }

  private void createLocallyModifiedFiles(final Element scm)
  {
    final String value =
        buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_SCM_LOCALLY_MODIFIED_FILES);

    if (StringUtils.isNotBlank(value))
    {
      final Element parent = document.createElement("locally-modified-files");

      final String filesValue = Constant.prettifyFilesValue(value);
      renderFiles(parent, filesValue);
      scm.appendChild(parent);
    }
  }

  private void renderFiles(final Element lmf, final String value)
  {
    final String stringValue = Constant.prettify(value);
    final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
    while (tokenizer.hasMoreTokens())
    {
      final String subValue = tokenizer.nextToken();
      final int colonIndex = subValue.indexOf(':');
      if (colonIndex > -1)
      {
        final String filePath = subValue.substring(0, colonIndex);
        final Element file = createValueElement("file", filePath, lmf);
        if (file != null && colonIndex < subValue.length() - 1)
        {
          final String modType = subValue.substring(colonIndex + 1).trim();
          file.setAttribute("modtype", modType);
        }
      }
    }
  }

  private void createProjectElement(final Element docRoot)
  {
    final Element parent = document.createElement("project");

    createContentElement("copyright-year", Constant.PROP_NAME_COPYRIGHT_YEAR,
        parent);
    createContentElement("home-page-url", Constant.PROP_NAME_PROJECT_HOMEPAGE,
        parent);
    createContentElement("ops-home-page-url", Constant.PROP_NAME_PROJECT_OPS,
        parent);

    if (parent.hasChildNodes())
    {
      docRoot.appendChild(parent);
    }
  }

  private void renderList(final Element tags, final String itemTag,
      final String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      final String stringValue = Constant.prettify(value);
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      while (tokenizer.hasMoreTokens())
      {
        final String item = tokenizer.nextToken();
        createValueElement(itemTag, item.trim(), tags);
      }
    }
  }

  private void createRuntimeElement(final Element docRoot)
  {
    final Element parent = document.createElement("runtime");

    createContentElement("build-server", Constant.PROP_NAME_HOSTNAME, parent);
    createContentElement("build-user", Constant.PROP_NAME_BUILD_USER, parent);

    createOsElement(parent);
    createJavaElement(parent);
    createMavenElement(parent);
    createEnvElement(parent);

    docRoot.appendChild(parent);
  }

  private void createOsElement(final Element runtime)
  {
    final Element parent = document.createElement("os");
    createContentElement("arch", Constant.PROP_NAME_OS_ARCH, parent);
    createContentElement(GI_NAME, Constant.PROP_NAME_OS_NAME, parent);
    createContentElement(GI_VERSION, Constant.PROP_NAME_OS_VERSION, parent);
    if (parent.hasChildNodes())
    {
      runtime.appendChild(parent);
    }
  }

  private void createJavaElement(final Element runtime)
  {
    final Element parent = document.createElement("java");
    createContentElement(GI_NAME, Constant.PROP_NAME_JAVA_RUNTIME_NAME, parent);
    createContentElement(GI_VERSION, Constant.PROP_NAME_JAVA_RUNTIME_VERSION,
        parent);
    createContentElement("vendor", Constant.PROP_NAME_JAVA_VENDOR, parent);
    createContentElement("vm", Constant.PROP_NAME_JAVA_VM, parent);
    createContentElement("compiler", Constant.PROP_NAME_JAVA_COMPILER, parent);
    createContentElement("options", Constant.PROP_NAME_JAVA_OPTS, parent);
    if (parent.hasChildNodes())
    {
      runtime.appendChild(parent);
    }
  }

  private void createMavenElement(final Element runtime)
  {
    final Element parent = document.createElement("maven");
    createContentElement(GI_VERSION, Constant.PROP_NAME_MAVEN_VERSION, parent);

    createContentElement("commandline", 
	Constant.PROP_NAME_MAVEN_CMDLINE,parent);
    createContentElement("execution-project",
        Constant.PROP_NAME_MAVEN_EXECUTION_PROJECT, parent);
    createContentElement("is-excution-root",
        Constant.PROP_NAME_MAVEN_IS_EXECUTION_ROOT, parent);

    final Element goals = document.createElement("goals");
    final String goalsString =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_MAVEN_GOALS);
    renderList(goals, "goal", goalsString);
    parent.appendChild(goals);

    final Element filters = document.createElement("filters");
    final String filtersString =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_MAVEN_FILTERS);
    renderFiles(filters, "filter", filtersString);
    if (filters.hasChildNodes())
    {
      parent.appendChild(filters);
    }

    final Element profiles = document.createElement("profiles");
    final String profilesString =
        buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_MAVEN_ACTIVE_PROFILES);
    if (StringUtils.isNotBlank(profilesString))
    {
      renderProfiles(profiles, profilesString);
      parent.appendChild(profiles);
    }

    createContentElement("options", Constant.PROP_NAME_MAVEN_OPTS, parent);
    if (parent.hasChildNodes())
    {
      runtime.appendChild(parent);
    }
  }

  private void renderFiles(final Element parent, final String itemTag,
      final String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      final String stringValue = Constant.prettify(value);
      final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
      while (tokenizer.hasMoreTokens())
      {
        final String item = tokenizer.nextToken();
        final String itemTrimmed = item.trim();
        final String itemNorm = filePathNormalizer.normalize(itemTrimmed);
        createValueElement(itemTag, itemNorm, parent);
      }
    }
  }

  private void renderProfiles(final Element profiles, final String value)
  {
    final String stringValue = Constant.prettify(value);
    final StringTokenizer tokenizer = new StringTokenizer(stringValue, ",");
    while (tokenizer.hasMoreTokens())
    {
      final String profileName = tokenizer.nextToken().trim();
      final Element profile =
          createValueElement("profile", profileName, profiles);
      if (profile != null)
      {
        final String profileSourceKey =
            Constant.MAVEN_ACTIVE_PROFILE_PREFIX + '.' + profileName;
        final String source =
            buildMetaDataProperties.getProperty(profileSourceKey);
        profile.setAttribute("source", source);
      }
    }
  }

  private void createEnvElement(final Element runtime)
  {
    final Element parent = document.createElement("env");

    final Properties sorted =
        SortedProperties.createSorted(buildMetaDataProperties);
    final String matchPrefix = Constant.MAVEN_EXECUTION_PROPERTIES_PREFIX + '.';
    for (final Map.Entry<Object, Object> entry : sorted.entrySet())
    {
      final String key = String.valueOf(entry.getKey());
      if (key.startsWith(matchPrefix))
      {
        final String value = String.valueOf(entry.getValue());
        final Element env = createValueElement("var", value, parent);
        if (env != null)
        {
          env.setAttribute(GI_NAME, key);
        }
      }
    }

    if (parent.hasChildNodes())
    {
      runtime.appendChild(parent);
    }
  }

  private void createMiscElement(final Element docRoot)
  {
    final Properties nonStandardProperties =
        Constant.calcNonStandardProperties(buildMetaDataProperties,
            selectedProperties);
    if (!nonStandardProperties.isEmpty())
    {
      final Element parent = document.createElement("misc");

      for (final Enumeration<Object> en = nonStandardProperties.keys(); en
          .hasMoreElements();)
      {
        final String key = String.valueOf(en.nextElement());
        createMetaDataElement(parent, key);
      }
      docRoot.appendChild(parent);
    }
  }

  private void createMetaDataElement(final Element parent, final String key)
  {
    if (Constant.isIntendedForMiscSection(key))
    {
      final Element metadata = createContentElement("metadata", key, parent);
      if (metadata != null)
      {
        metadata.setAttribute(GI_NAME, key);
      }
    }
  }

  private Element createDocRoot() throws DOMException
  {
    final Element docRoot = document.createElement("buildmetadata");
    docRoot.setAttribute("xmlns:xsi", XML_SCHEMA_INSTANCE);
    docRoot.setAttribute("xmlns", CODE_URI);
    docRoot.setAttribute("xsi:schemaLocation", CODE_URI + ' ' + CODE_URI);
    document.appendChild(docRoot);
    return docRoot;
  }

  private Element createContentElement(final String gi,
      final String propertyKey, final Element parent)
  {
    final String content = buildMetaDataProperties.getProperty(propertyKey);
    return createValueElement(gi, content, parent);
  }

  private Element createValueElement(final String gi, final String value,
      final Element parent)
  {
    if (value != null)
    {
      final Element element = document.createElement(gi);
      final Text text = document.createTextNode(value);
      element.appendChild(text);
      parent.appendChild(element);
      return element;
    }
    return null;
  }

  // --- object basics --------------------------------------------------------

}
