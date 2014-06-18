#!/bin/sh

# Installing Jenkins Koji Plugin"
service jenkins stop
cp -p /etc/sysconfig/jenkins /etc/sysconfig/jenkins.orig
cat > /tmp/jenkins.patch << 'EOF'
--- /etc/sysconfig/jenkins.orig 2014-06-02 17:19:23.000000000 +0200
+++ /etc/sysconfig/jenkins  2014-06-05 15:22:00.665823455 +0200
@@ -26,7 +26,7 @@
 # Be careful when you change this, as you need to update
 # permissions of $JENKINS_HOME and /var/log/jenkins.
 #
-JENKINS_USER="jenkins"
+JENKINS_USER="koji"

 ## Type:        string
 ## Default:     "-Djava.awt.headless=true"
EOF
patch -p0 < /tmp/jenkins.patch
rm -f /tmp/jenkins.patch
tar zxfv /opt/kojak/scripts/contrib/jenkins/jenkins.tar.gz -C /var/lib
chown -R koji:koji /var/lib/jenkins /var/cache/jenkins /var/log/jenkins

touch $SERVICES/jenkins

