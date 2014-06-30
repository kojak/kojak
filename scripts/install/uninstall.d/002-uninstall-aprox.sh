#!/bin/sh

echo "Unmounting all /aprox/* filesystems..."
umount /aprox/settings/

echo "Returning fstab to original state..."
cat /etc/fstab.orig > /etc/fstab

echo "Removing .m2 directory from koji home directory..."
rm -rf /home/koji/.m2

echo "Removing AProx install and mountpoints..."
rm -rf /aprox
rm -rf /opt/aprox

echo "Removing davfs2, and rpmforge-release via Yum..."
yum -y remove rpmforge-release davfs2

echo "Disabling/Removing aprox init.d link..."
chkconfig aprox off
rm /etc/init.d/aprox

echo "AProx uninstall complete."

