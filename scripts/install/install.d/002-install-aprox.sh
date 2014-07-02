#!/bin/sh

# http://localhost:8080/api/1.0/hosted/local-deployments/org/commonjava/aprox/launch/aprox-launcher-savant/0.14.7-SNAPSHOT/aprox-launcher-savant-0.14.7-20140702.153309-1-launcher.tar.gz
APROX_FLAVOR=aprox-launcher-savant
APROX_VERSION=0.14.7-SNAPSHOT
APROX_VERSION_SNAP=0.14.7-20140702.153309-1

echo "Adding RPMForge yum repository for DAVfs support..."
yum -y localinstall http://pkgs.repoforge.org/rpmforge-release/rpmforge-release-0.5.3-1.el6.rf.x86_64.rpm

echo "Installing DAVfs to support mounting aprox-generated settings.xml files..."
yum -y install davfs2

echo "Backing up /etc/fstab"
cp -p /etc/fstab /etc/fstab.orig

echo "Installing AProx ($APROX_FLAVOR, version $APROX_VERSION)..."

#URL=http://repo.maven.apache.org/maven2/org/commonjava/aprox/launch/$APROX_FLAVOR/$APROX_VERSION/$APROX_FLAVOR-$APROX_VERSION-launcher.tar.gz
#URL=https://oss.sonatype.org/content/repositories/orgcommonjava-1076/org/commonjava/aprox/launch/$APROX_FLAVOR/$APROX_VERSION/$APROX_FLAVOR-$APROX_VERSION-launcher.tar.gz
URL=http://192.168.122.1:8080/api/1.0/hosted/local-deployments/org/commonjava/aprox/launch/$APROX_FLAVOR/$APROX_VERSION/$APROX_FLAVOR-$APROX_VERSION_SNAP-launcher.tar.gz
echo "Downloading AProx from: $URL"
curl $URL | tar -C /opt -zxv

echo "Setting up aprox init script..."
ln -s /opt/aprox/bin/init/aprox /etc/init.d/aprox
chmod +x /opt/aprox/bin/init/aprox

echo "Setting aprox port to 8090..."
cat > /opt/aprox/bin/boot.properties << 'EOF'
port=8090
EOF

echo "Applying aprox patches..."
DIR=$(dirname $( cd $(dirname $0) ; pwd -P ))
for patch in $(ls -1 $DIR/patch.d/aprox/*.patch); do
  echo "...$(basename $patch)"
  patch -d /opt/aprox -p1 < $patch
done

echo "Changing ownership to koji:koji for /opt/aprox..."
chown -R koji:koji /opt/aprox

echo "AProx install complete."

