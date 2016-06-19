#MCRAM 1.0.0 (beta)

Dependencies: Java 7+ (Java 8 recommended)
Support Operating Systems: Linux, MacOS X, Windows

MCRAM is designed to help run your Minecraft server entirely in RAM on any operating system. It will set up the automation of starting your server 100% in memory and then saving/syncing it back to your drive occasionally. This helps to improve performance and extend your drive's health and longevity.

## INSTALLATION
This will work out-of-the-box with all operating systems, as long as Java 7 or 8 is installed, EXCEPT for Windows. You must first install the "imdisk" utility for MCRAM to be able to allocate RAM disks. Download and install imdisk from [here](http://www.ltr-data.se/files/imdiskinst.exe).


## USAGE
MCRAM is bundled into a convient jar file. If you run it with no commands, it will interactively prompt you for all of the information it needs. At the bare minimum, you need to specify the source directory (a full path to the Minecraft server's jar file) and the destination directory (on Windows, an unused drive letter to create a RAM disk on). If you do not have "git" installed on your local machine, simply download the zip file from [here](https://github.com/ekultails/mcram/archive/master.zip) instead.
```
$ git clone https://github.com/ekultails/mcram.git
$ cd mcram/java/built/
$ java -jar mcramctl.jar --help
mcramctl options

        --help, -h
                display these help options
        --source-dir, -s
                provide the full path to the Minecraft server's jar file
                Windows: folders should be seperated by "/" and not "\"
        --destination-dir, -d
                where to mount the RAM disk to
                Unix: provide an empty directory
                        default: /mcram/
                Windows: provide an unused drive letter
                        default: M:/
        --run-ram, -rr
                specify (in MB) the amount of RAM to run the Minecraft server
                default: 512
        --mount-ram, -mr
                specify (in MB) the amount of RAM to use for mounting the RAM disk
        --sync-time, -t
                set how long to wait (in minutes) before copying the server back to the disk
                default: 60
        --verbose, -v
                display debugging information
        --version, -V
                show current MCRAM version

```

## DEVELOPERS
All of the source code is located in the java/ directory. Currently there are two files, "mcramctl" to handle the user-interface and "Mcramd" to control the background daemon. After making any changes you should rebuild the latest jar file.
```
$ cd java/
$ jar -cfmv built/mcramctl.jar manifest *.class
```
This creates (c) a new jar file (f) with the manifest file (e; this specifies, at least, what is the main class) and will verbosely show us the process of building the jar (v). 


## DISCLAIMER
This program does NOT provide any binaries of the Minecraft game. These must be obtained by Mojang, Microsoft, and/or any other appropriate companies officially associated with the Minecraft brand. All intellectual properties, brands, trandmarks, and/or copyrights are owned by their respective owners/companies.

## License
This software is licensed under the GPLv3. More information about this can be found in the included "LICENSE" file or online at: http://www.gnu.org/licenses/gpl-3.0.en.html
