#MCRAM 1.0.0 (beta)

Dependencies: Java 7+ (Java 8 recommended)
Support Operating Systems: Linux, MacOS X, Windows

MCRAM is designed to help run a Minecraft server entirely in RAM on any operating system. It will set up the automation of starting the server 100% in memory and then saving/syncing it back to the drive occasionally. This helps to improve performance and extend the computer's drive's health and longevity.

## INSTALLATION
All: Install the Java Runtime Environment (JRE) 7 or 8.
Windows: Install "imdisk" from [here](http://www.ltr-data.se/files/imdiskinst.exe) for handling RAM disks. 


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
		default: 512
	--sync-time, -t
		set how long to wait (in minutes) before copying the server back to the disk
		default: 60
	--execute, -e
		send a command to the Minecraft server
	--verbose, -v
		display debugging information
	--version, -V
		show current MCRAM version
```
After a server has been started with MCRAM, commands for the Minecraft server can be run by using the "-e" or "--execute" arguements. For example, the command below will run on the server to change the user Skywalker's gamemode to creative (1).
```
$ java -jar mcramctl -e gamemode 1 Skywalker
```

## DEVELOPERS
### SOURCE CODE
All of the source code is located in the java/ directory. Currently there are two files, "mcramctl" to handle the user-interface and "Mcramd" to control the background daemon. After making any changes you should rebuild the latest jar file.
```
$ cd java/
$ rm built/mcramctl.jar
$ jar -cfmv built/mcramctl.jar manifest.txt *.class
```
This creates (c) a new jar file (f) with the manifest file (e; this specifies, at least, what is the main class) and will verbosely show us the process of building the jar (v). 

### SOCKETS
All commands are sent to the "mcramd" daemon via a socket-like text file.
```
Linux/MacOSX socket: /tmp/mcramd.sock
Windows socket: C:/Users/%USERNAME%/AppData/Local/Temp/mcramd.sock
```
Mcramd ONLY reads the first line in the socket. This means that it needs to be cleared out every time a new command is sent to it. An example below demonstrates the ability to manually run commands on a server via the socket. This essentially pushes "/say Use the Force" to standard input for the Minecraft server's console, executing the command.
```
$ echo "mcramd:exec,say Use the Force" > /tmp/mcramd.sock
```
Alternatively, server commands can be executed via the mcramctl utilty. Only the "-e" or "--execute" argument should be given and then the rest should be the full command.
```
$ java -jar mcramctl.jar --execute say Hello Imperial Senate
```
Mcramd currently recognizes 3 flags. Every flag is then seperated by a comma "," to give each of the options.
1. mcramd:start
2. mcramd:exec
3. mcramd:stop

## LIMITATIONS
These are current limitations that will be addressed in future releases.
* MCRAM currently only handles running one server at a time in RAM.
* Standard output from the Minecraft server is not displayed.
* No service utilies for automating the start of MCRAM on boot.

## DISCLAIMER
This program does NOT provide any binaries of the Minecraft game. These must be obtained by Mojang, Microsoft, and/or any other appropriate companies officially associated with the Minecraft brand. All intellectual properties, brands, trandmarks, and/or copyrights are owned by their respective owners/companies.

## License
This software is licensed under the GPLv3. More information about this can be found in the included "LICENSE" file or online at: http://www.gnu.org/licenses/gpl-3.0.en.html
