To integrate a new tool, you need to implement the Kojak management SPI, which consists of a number of file "hooks":

scripts/services.d/<toolname>

  Contains the list of sysV services to manage during install/configuration/uninstall.

scripts/install/install.d/00n-<toolname>.sh

  Contains the install logic for your tool. The numeric prefix is used for sorting.

scripts/config/config.d/00n-<toolname>.sh

  Contains the configuration logic for your tool. Again, the numeric prefix is used for sorting.

scripts/install/uninstall.d/00n-<toolname>.sh

  Contains the uninstall logic for your tool. Again, the numeric prefix is used for sorting.
