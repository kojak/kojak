#!/bin/bash
# 
# Copyright (C) 2013 Red Hat Inc.
# Author: Salim Badakhchani <sal@redhat.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>. 
#
########################################################################

# Uncomment to debug script
#set -x

if [ -e "${HOME}/workspace/buildmetadata-maven-plugin/build.log" ]; then
    koji maven-build git+https://github.com/sbadakhc/buildmetadata-maven-plugin.git#ce68bfc08000ada70a3aa04d92d7c88271ac5b5e
else
cd ~/workspace
    git clone https://github.com/sbadakhc/buildmetadata-maven-plugin.git
    cd buildmetadata-maven-plugin
    git checkout buildmetadata-maven-plugin-1.3.1
    mvn -B dependency:resolve-plugins clean install -Dmaven.test.skip=true >> build.log 2>&1
    koji add-pkg --owner=koji rcm-mw-tools-candidate com.redhat.rcm.maven.plugin-buildmetadata-maven-plugin
    POMS=$(find ~/.m2/repository/ -name '*.pom'); for pom in $POMS; do /opt/kojak/scripts/build/import-maven $pom; done
    echo -e "\nPlease ensure all tasks have completed before pressing any key to continue."
    read
    /opt/kojak/scripts/build/run-maven-build git+https://github.com/sbadakhc/buildmetadata-maven-plugin.git#ce68bfc08000ada70a3aa04d92d7c88271ac5b5e
    cd ~
fi

