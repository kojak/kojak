#!/bin/sh

echo "Disabling/Removing aprox init.d link..."
chkconfig aprox off
rm /etc/init.d/aprox

echo "Unmounting all /aprox/* filesystems..."
umount /aprox/settings/

echo "Removing aprox mounts from /etc/fstab..."
cp /etc/fstab /etc/fstab.aprox
sed '/aprox/d' /etc/fstab.aprox t> /etc/fstab
rm /etc/fstab.aprox

echo "Removing reverse proxy from httpd conf..."
cp /etc/httpd/conf/httpd.conf /etc/httpd/conf/httpd.conf.aprox
sed '/aprox/d' /etc/httpd/conf/httpd.conf.aprox > /etc/httpd/conf/httpd.conf
rm /etc/httpd/conf/httpd.conf.aprox

rm /etc/httpd/conf.d/aprox.conf

echo "Removing davfs config..."
cp /etc/davfs2/secrets /etc/davfs2/secrets.aprox
sed '/aprox/d' /etc/davfs2/secrets.aprox > /etc/davfs2/secrets
rm /etc/davfs2/secrets.aprox

for userdir in '/home/koji' '/root'; do
  user=$(basename $userdir)
  echo "Removing AProx files from .m2 directory for '${user}'..."
  rm -rf $userdir/.m2/repo-*
  rm -f $userdir/.m2/settings.xml
  test -f $userdir/.m2/settings.pre-aprox && mv $userdir/.m2/settings.pre-aprox $userdir/.m2/settings.xml
done

echo "Removing AProx install and mountpoints..."
rm -rf /aprox
rm -rf /opt/aprox

echo "Removing davfs2 and rpmforge-release via Yum..."
yum -y remove rpmforge-release davfs2

echo "AProx uninstall complete."

