#!/bin/sh

APROX_VERSION=0.14.3
APROX_FLAVOR=aprox-launcher-easyprox

echo "Adding RPMForge yum repository for DAVfs support..."
yum -y localinstall http://pkgs.repoforge.org/rpmforge-release/rpmforge-release-0.5.3-1.el6.rf.x86_64.rpm

echo "Installing DAVfs to support mounting aprox-generated settings.xml files..."
yum -y install davfs2

echo "Installing aprox..."
curl http://repo.maven.apache.org/maven2/org/commonjava/aprox/launch/$APROX_FLAVOR/$APROX_VERSION/$APROX_FLAVOR-$APROX_VERSION-launcher.tar.gz | tar -C /opt -zxv
mv /opt/$APROX_FLAVOR /opt/aprox

echo "Setting up aprox init script..."
ln -s /opt/aprox/bin/init/aprox /etc/init.d/aprox
chmod +x /opt/aprox/bin/init/aprox

echo "Setting aprox port to 8090..."
cat > /opt/aprox/bin/boot.properties << 'EOF'
port=8090
EOF

echo "Changing ownership to koji:koji for /opt/aprox..."
chown -R koji:koji /opt/aprox

echo "AProx install complete."

