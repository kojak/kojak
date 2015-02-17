# get & install virtualbox
http://download.virtualbox.org/virtualbox/4.3.18/VirtualBox-4.3.18-96516-OSX.dmg
hdiutil attach VirtualBox-4.3.18-96516-OSX.dmg
sudo installer -pkg /Volumes/VirtualBox/VirtualBox.pkg -target /
hdiutil detach /Volumes/VirtualBox

# get & install vagrant
curl -L -O https://dl.bintray.com/mitchellh/vagrant/vagrant_1.7.2.dmg;
hdiutil attach vagrant_1.7.2.dmg;
sudo installer -pkg /Volumes/Vagrant/Vagrant.pkg -target /
hdiutil detach /Volumes/Vagrant

# run vagrant
vagrant init chef/centos-6.5
vagrant up
vagrant ssh

