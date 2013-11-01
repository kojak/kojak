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
package com.redhat.rcm.maven.plugin.buildmetadata.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;

/**
 * Constants used in this package.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 8936 $
 */
public final class Constant
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ... sections .............................................................

  /**
   * The name of the SCM section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_SCM = "build.scm";

  /**
   * The name of the artifact section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_ARTIFACT = "build.artifact";

  /**
   * The name of the build date section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_DATE = "build.timeAndDate";

  /**
   * The name of the runtime build section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_RUNTIME = "build.runtime";

  /**
   * The name of the Java build section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_JAVA = "build.java";

  /**
   * The name of the Maven build section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_MAVEN = "build.maven";

  /**
   * The name of the miscellaneous build section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_BUILD_MISC = "build.misc";

  /**
   * The name of the project section.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String SECTION_PROJECT = "project";

  // ... build property names .................................................

  /**
   * The name of the project property that stores the URL to the SCM server.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_URL = "build.scmRevision.url";

  /**
   * The name of the project property that stores the revision number provided
   * by the SCM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_REVISION_ID = "build.scmRevision.id";

  /**
   * The name of the project property that stores the date the revision number
   * was set.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_REVISION_DATE =
      "build.scmRevision.date";

  /**
   * The name of the project property that stores the information if the local
   * sources are modified.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_LOCALLY_MODIFIED =
      "build.scmLocallyModified";

  /**
   * The name of the project property that stores the files that are locally
   * modified.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_SCM_LOCALLY_MODIFIED_FILES =
      "build.scmLocallyModified.files";

  /**
   * The name of the project property that stores the formatted build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_DATE = "build.date";

  /**
   * The name of the project property that stores the build timestamp.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_TIMESTAMP =
      "build.timestamp.millis";

  /**
   * The name of the project property that stores the pattern of the build date.
   * This way it is easy for the reading client to parse the build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_DATE_PATTERN =
      "build.date.pattern";

  /**
   * The name of the project property that stores the group ID as read from the
   * POM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_GROUP_ID = "build.groupId";

  /**
   * The name of the project property that stores the artifact ID as read from
   * the POM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_ARTIFACT_ID = "build.artifactId";

  /**
   * The name of the project property that stores the version as read from the
   * POM.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_VERSION = "build.version";

  /**
   * The name of the project property that stores the full version that may
   * include the version, the build date, the build number and the revision
   * number.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_FULL_VERSION = "build.version.full";

  /**
   * The name of the project property that stores the build year.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_YEAR = "build.year";

  /**
   * The name of the project property that stores the duration of the build in
   * milliseconds. The duration is taken at the end of the build when the last
   * plugin is run. The plugin cannot guarantee that there is no work done
   * afterwards. The install and deploy phase are not measured since the plugin
   * is running in the verify phase.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_DURATION = "build.duration";

  /**
   * The name of the project property that stores the copyright year. The
   * copyright year is either the inception year (if inception and build year
   * are the same) or the period starting with the inception year and ending
   * with the build year.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_COPYRIGHT_YEAR = "build.copyright.year";

  /**
   * The default pattern for the (locale independent) build date.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";

  /**
   * The name of the project property that stores the build user. This is the
   * person or system that run the build. It is either a configured value or the
   * value of the system property <code>user.name</code>.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_BUILD_USER = "build.user";

  /**
   * The name of the property that stores the name of the host the build has
   * been run on.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_HOSTNAME = "build.host.name";

  /**
   * The name of the property that stores the name of the operating system the
   * build has been run on.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_OS_NAME = "build.os.name";

  /**
   * The name of the property that stores the architecture of the operating
   * system the build has been run on.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_OS_ARCH = "build.os.arch";

  /**
   * The name of the property that stores the version of the operating system
   * the build has been run on.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_OS_VERSION = "build.os.version";

  /**
   * The name of the property that stores the name of Java runtime being
   * executed for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_RUNTIME_NAME =
      "build.java.runtime.name";

  /**
   * The name of the property that stores the version of Java runtime being
   * executed for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_RUNTIME_VERSION =
      "build.java.runtime.version";

  /**
   * The name of the property that stores the name of the vendor of Java being
   * executed for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_VENDOR = "build.java.vendor";

  /**
   * The name of the property that stores the name of the Java VM being executed
   * for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_VM = "build.java.vm";

  /**
   * The name of the property that stores the name of the Java compiler being
   * executed for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_COMPILER = "build.java.compiler";

  /**
   * The name of the property that stores the version of Maven being executed
   * for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_VERSION = "build.maven.version";

  /**
   * The name of the property that stores the goals given on the command line
   * for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_GOALS =
      "build.maven.execution.goals";

  /**
   * The name of the property that stores the command line to start the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_CMDLINE =
      "build.maven.execution.cmdline";

  /**
   * The name of the property that stores the Maven opts set in the environment.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_OPTS =
      "build.maven.execution.opts";

  /**
   * The name of the property that stores the Java opts set in the environment.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_JAVA_OPTS =
      "build.maven.execution.java.opts";

  /**
   * The name of the property that flags if the artifact is build within the
   * project that is the execution root.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_IS_EXECUTION_ROOT =
      "build.maven.execution.isRoot";

  /**
   * The name of the property that contains the name of the execution project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_EXECUTION_PROJECT =
      "build.maven.execution.project";

  /**
   * The name of the property that contains the name of the filters being
   * registered for the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_FILTERS =
      "build.maven.execution.filters";

  /**
   * The prefix used to provide execution properties to the build properties
   * file.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String MAVEN_EXECUTION_PROPERTIES_PREFIX =
      "execution.property";

  /**
   * The name of the property that contains names of active profiles during the
   * build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_MAVEN_ACTIVE_PROFILES =
      "build.maven.execution.profiles.active";

  /**
   * The prefix used to provide active profile information of the build.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String MAVEN_ACTIVE_PROFILE_PREFIX =
      "build.maven.execution.profile.active";

  /**
   * The name of the property that stores URL of the project homepage.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_PROJECT_HOMEPAGE =
      "project.page.home.url";

  /**
   * The name of the property that stores URL to a page useful for operations
   * teams.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_PROJECT_OPS = "project.page.ops.url";

  /**
   * The name of the property that stores a value to categorize the project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_PROJECT_CATEGORY = "project.category";

  /**
   * The name of the property that stores a value to further categorize the
   * project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_PROJECT_SUBCATEGORY =
      "project.subcategory";

  /**
   * The name of the property that stores a comma separated list of tags to
   * categorize the project.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String PROP_NAME_PROJECT_TAGS = "project.tags";

  /**
   * List of properties, organized in sections, relevant to the build report.
   * The order of properties in this list determines the order of the properties
   * listed in the report.
   */
  public static final List<Section> REPORT_PROPERTIES;

  /**
   * The set of standard keys known to be rendered in sections. This list allows
   * to render properties within a separate section that have not yet been
   * rendered.
   */
  public static final Set<String> STANDARD_PROPERTIES;

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  static
  {
    final List<Section> sections = new ArrayList<Section>();
    final Section scm =
        new Section(SECTION_BUILD_SCM, PROP_NAME_SCM_REVISION_ID,
            PROP_NAME_SCM_REVISION_DATE, PROP_NAME_SCM_URL,
            PROP_NAME_SCM_LOCALLY_MODIFIED_FILES);
    sections.add(scm);

    final Section artifact =
        new Section(SECTION_ARTIFACT, PROP_NAME_GROUP_ID,
            PROP_NAME_ARTIFACT_ID, PROP_NAME_VERSION, PROP_NAME_FULL_VERSION);
    sections.add(artifact);

    final Section dateAndVersion =
        new Section(SECTION_BUILD_DATE, PROP_NAME_BUILD_DATE,
            PROP_NAME_BUILD_TIMESTAMP, PROP_NAME_BUILD_YEAR,
            PROP_NAME_COPYRIGHT_YEAR, DEFAULT_DATE_PATTERN);
    sections.add(dateAndVersion);

    final Section buildRuntime =
        new Section(SECTION_BUILD_RUNTIME, PROP_NAME_HOSTNAME,
            PROP_NAME_OS_NAME, PROP_NAME_OS_ARCH, PROP_NAME_OS_VERSION,
            PROP_NAME_BUILD_USER);
    sections.add(buildRuntime);

    final Section buildJava =
        new Section(SECTION_BUILD_JAVA, PROP_NAME_JAVA_VENDOR,
            PROP_NAME_JAVA_RUNTIME_NAME, PROP_NAME_JAVA_RUNTIME_VERSION,
            PROP_NAME_JAVA_VM, PROP_NAME_JAVA_COMPILER, PROP_NAME_JAVA_OPTS);
    sections.add(buildJava);

    final Section buildMaven =
        new Section(SECTION_BUILD_MAVEN, PROP_NAME_MAVEN_VERSION,
            PROP_NAME_MAVEN_CMDLINE, PROP_NAME_MAVEN_GOALS,
            PROP_NAME_MAVEN_OPTS, PROP_NAME_MAVEN_EXECUTION_PROJECT,
            PROP_NAME_MAVEN_IS_EXECUTION_ROOT, PROP_NAME_MAVEN_FILTERS,
            PROP_NAME_MAVEN_ACTIVE_PROFILES);
    sections.add(buildMaven);

    final Section project =
        new Section(SECTION_PROJECT, PROP_NAME_PROJECT_HOMEPAGE,
            PROP_NAME_PROJECT_OPS, PROP_NAME_PROJECT_CATEGORY,
            PROP_NAME_PROJECT_SUBCATEGORY, PROP_NAME_PROJECT_TAGS);
    sections.add(project);

    final Set<String> properties =
        new HashSet<String>(Arrays.asList(new String[]
        { PROP_NAME_SCM_REVISION_ID, PROP_NAME_SCM_REVISION_DATE,
         PROP_NAME_SCM_URL, PROP_NAME_SCM_LOCALLY_MODIFIED_FILES,
         PROP_NAME_BUILD_DATE, PROP_NAME_BUILD_TIMESTAMP, PROP_NAME_BUILD_YEAR,
         PROP_NAME_COPYRIGHT_YEAR, DEFAULT_DATE_PATTERN, PROP_NAME_GROUP_ID,
         PROP_NAME_ARTIFACT_ID, PROP_NAME_VERSION, PROP_NAME_FULL_VERSION,
         PROP_NAME_HOSTNAME, PROP_NAME_OS_NAME, PROP_NAME_OS_ARCH,
         PROP_NAME_OS_VERSION, PROP_NAME_BUILD_USER, PROP_NAME_JAVA_VENDOR,
         PROP_NAME_JAVA_RUNTIME_NAME, PROP_NAME_JAVA_RUNTIME_VERSION,
         PROP_NAME_JAVA_VM, PROP_NAME_JAVA_COMPILER, PROP_NAME_JAVA_OPTS,
         PROP_NAME_MAVEN_VERSION, PROP_NAME_MAVEN_CMDLINE,
         PROP_NAME_MAVEN_GOALS, PROP_NAME_MAVEN_OPTS,
         PROP_NAME_MAVEN_EXECUTION_PROJECT, PROP_NAME_MAVEN_IS_EXECUTION_ROOT,
         PROP_NAME_MAVEN_FILTERS, PROP_NAME_MAVEN_ACTIVE_PROFILES,
         PROP_NAME_PROJECT_HOMEPAGE, PROP_NAME_PROJECT_OPS }));

    STANDARD_PROPERTIES = Collections.unmodifiableSet(properties);
    REPORT_PROPERTIES = Collections.unmodifiableList(sections);
  }

  // ****************************** Constructors ******************************

  /**
   * Constant pattern.
   */
  private Constant()
  {
  }

  // ****************************** Inner Classes *****************************

  /**
   * The section allows to group build meta data properties.
   */
  public static final class Section
  {
    /**
     * The resource key to access the title of the section.
     */
    private final String titleKey;

    /**
     * The build meta data properties to be displayed in this section.
     */
    private final List<String> properties;

    /**
     * Default constructor.
     *
     * @param titleKey the resource key to access the title of the section.
     * @param properties the build meta data properties to be displayed in this
     *          section.
     */
    private Section(final String titleKey, final String... properties)
    {
      this.titleKey = titleKey;
      this.properties = Arrays.asList(properties);
    }

    /**
     * Returns the resource key to access the title of the section.
     *
     * @return the resource key to access the title of the section.
     */
    public String getTitleKey()
    {
      return titleKey;
    }

    /**
     * Returns the build meta data properties to be displayed in this section.
     *
     * @return the build meta data properties to be displayed in this section.
     */
    public List<String> getProperties()
    {
      return properties;
    }
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Checks if the given property is rejected from being displayed in the misc
   * section of a report.
   *
   * @param name the name of the property to check.
   * @return <code>true</code> if the property is to be rendered in a misc
   *         section, <code>false</code> otherwise.
   */
  public static boolean isIntendedForMiscSection(final String name)
  {
    return !name.startsWith(MAVEN_ACTIVE_PROFILE_PREFIX);
  }

  /**
   * Calculates the non standard properties relevant for the misc section.
   *
   * @param buildMetaDataProperties the build meta data.
   * @param userProperties the list of a system properties or environment
   *          variables to be selected by the user to include into the build
   *          meta data properties.
   * @return the non standard properties.
   */
  public static Properties calcNonStandardProperties(
      final Properties buildMetaDataProperties,
      final List<Property> userProperties)
  {
    final SortedProperties nonStandardProperties = new SortedProperties();
    final Set<String> selectedProperties =
        createSelectedPropertiesExcludeMiscSection(userProperties);

    for (final Map.Entry<Object, Object> entry : buildMetaDataProperties
        .entrySet())
    {
      final String key = String.valueOf(entry.getKey());
      if (!Constant.STANDARD_PROPERTIES.contains(key)
          && !selectedProperties.contains(key))
      {
        nonStandardProperties.put(key, entry.getValue());
      }
    }
    return nonStandardProperties;
  }

  private static Set<String> createSelectedPropertiesExcludeMiscSection(
      final List<Property> userProperties)
  {
    final Set<String> selectedProperties = new HashSet<String>();

    if (userProperties != null)
    {
      for (final Property property : userProperties)
      {
        if (isNotTargetedForMiscSection(property.getSection()))
        {
          selectedProperties.add(property.getName());
        }
      }
    }
    return selectedProperties;
  }

  private static boolean isNotTargetedForMiscSection(final String section)
  {
    return SECTION_BUILD_SCM.equals(section)
           || SECTION_BUILD_DATE.equals(section)
           || SECTION_BUILD_RUNTIME.equals(section)
           || SECTION_BUILD_JAVA.equals(section)
           || SECTION_BUILD_MAVEN.equals(section);
  }

  /**
   * Prettifies a multi value string that contains brackets. It simply removes
   * the brackets.
   *
   * @param string the string to prettify.
   * @return the prettified string.
   */
  public static String prettify(final String string)
  {
    final String trimmed = string.trim();
    final int end = trimmed.length() - 1;
    if (trimmed.charAt(0) == '[' && trimmed.charAt(end) == ']')
    {
      return trimmed.substring(1, end);
    }
    return trimmed;
  }

  /**
   * Prettifies a value string that contains brackets. It simply removes the
   * brackets.
   *
   * @param value the object whose string representation is to be prettified.
   * @return the prettified string.
   */
  public static String prettifyFilesValue(final Object value)
  {
    if (value == null)
    {
      return null;
    }

    String string = String.valueOf(value);
    if (StringUtils.isNotBlank(string))
    {
      string = string.replace(']', ' ');
      string = string.replace('[', ',');
      if (string.indexOf(0) == ',')
      {
        return string.substring(1);
      }
    }

    return string;
  }

  // --- object basics --------------------------------------------------------
}
