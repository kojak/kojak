This is designed as a location where projects that are integrated into the kojak install process
can have post-release patches added, either to patch bugs or configure the project for use with kojak. 

The layout should be similar to:

patch.d
+- aprox
   +- 001-foo.patch
   +- 002-bar.patch

How and when patches for a specific project are applied are generally left up to the integrator
(whether in the install or configure phase), but the general logic should look somethind like this:

echo "Applying aprox patches..."
DIR=$(dirname $( cd $(dirname $0) ; pwd -P ))
for patch in $(ls -1 $DIR/patch.d/aprox/*.patch); do
  echo "...$(basename $patch)"
  patch -d /opt/aprox -p1 < $patch
done

