buildmetadata-maven-plugin
==========================

The build metadata maven plugin creates a detailed report of the various build time parameters employed during a build.
The information includes useful data that can be used to provide better transparency and accountability of the build
process. Meta data includes build times and dates, user enviroment information and Java and Maven command line options.
The reporting is configurable and extendible as well as being adaptable for single and multiple artifacts.

The meta data includes

SCM information

1. revision number
2. revision date
3. locally modified files
4. URL of SCM server

System information

5. build time
6. operating system
7. name
8. architecture
9. version

Java runtime

10. vendor
11. name
12. version
13. virtual machine
14. compiler
15. JAVA_OPTS

Maven execution information

16. Maven version
17. active profiles
18. environment properties
19. command line and executed goals

MAVEN_OPTS

20. build user
21. build host name
22. Artifact and version
        group ID
        artifact ID
        build version
23. project info
24. home page URL
25. categories
26. tags

System Requirements
-------------------

The following specifies the minimum requirements to run this Maven plugin:

1.  Maven 2.0
2.  JDK 1.5

Installation Instructions
-------------------------

Clone the repository to your workspace and build with maven:

1. `git clone https://github.com/sbadakhc/buildmetadata-maven-plugin.git`
2. `mvn install`

Goals
-----

buildmetadata:build-point
buildmetadata:buildmetadata-report
buildmetadata:provide-buildmetadata
                                              
Usage
-----

    '<project>  
      ...
      <build>
        <plugins>
          <plugin>
            <groupId>com.redhat.rcm.maven.plugin</groupId>
            <artifactId>buildmetadata-maven-plugin</artifactId
            <version>1.0</version>
            <executions>
              <execution>
                <phase>initialize</phase>
                <goals>
                  <goal>provide-buildmetadata</goal>
                </goals>
                <configuration>
                  <createPropertiesReport>false</createPropertiesReport>
                  <xmlOutputFile>${project.build.outputDirectory}/META-INF/buildmetadata.xml</xmlOutputFile>
                  <hideCommandLineInfo>false</hideCommandLineInfo>
                  <hideMavenOptsInfo>false</hideMavenOptsInfo>
                  <hideJavaOptsInfo>false</hideJavaOptsInfo>
                  <buildDatePattern>dd.MM.yyyy HH:mm:ss</buildDatePattern>
                </configuration>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    ...
    </project>


Runtime Example
-------

Assuming the plugin configuration in pom matches the example provisded above the following command will create a
buildmetadata.xml file in the generated jar file under the META-INF direcory of the archive.

`mvn install`

You can view the generated build.properties file in the archive without extracting it with the following command:

`unzip -p example/HelloWorld/target/HelloWorld-1.0-SNAPSHOT.jar META-INF/buildmetadata.xml`

Known Issues
------------

With *NIX implementations of Maven the mvn shell wrapper script will need to explicitly declare the following varible 
to capture Maven command line arguments:

    export MAVEN_CMD_LINE_ARGS="$@"

Red Hat/Fedora users should edit the /usr/bin/mvn wrapper file for /usr/share/maven/bin/mvn to include this declaration
and prevent the changes being lost as a result of future packages upgrades.

Notes
-----

For further infoprmation please visit the upstream providers web site at 
http://www.smartics.eu/buildmetadata-maven-plugin/

