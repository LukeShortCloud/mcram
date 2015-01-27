#MCRAM v0.2

MCRAM is designed to help run your Minecraft server entirely in RAM on UNIX-based systems. It will set up the automation of starting your server 100% in memory and then saving/syncing it back to your drive occasionally. This helps to improve performance and extend your drive's health and longevity.

### INSTALL
For installation simply execute the install file:
```bash
sh installer.sh
```
The install will walk you through and help answer your questions to make the configuration file that will be run as a system cron. Currently the cron is setup to be run (A) after the installer has finished and then (B) after that it will always run on the boot-up of your server/PC. 

### SAMPLE SETUP
````
# sh install.sh
All dependencies met!
Please provide full folder paths. For example: /home/myuser/Documents/MC/
Where is your server located? /home/myuser/games/Minecraft/
Here is a list of possible server jar files:
minecraft_server.1.8.jar
spigot-1.8-R0.1-SNAPSHOT.jar
Which one would you like to use to start your server with? spigot-1.8-R0.1-SNAPSHOT.jar
What directory do you want RAM mounted onto for MC? /ram/
Your computer has 2048MB of RAM
What percentage of RAM would you like to use for mounting Minecraft into RAM? (Default is 10):
What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): 300
How long (in minutes) do you want to wait for the server to sync to the drive again? (Default is 60): 120
Mounting 204MB of RAM onto /ram/
```

#### DISCLAIMER
This program does NOT provide any binaries of the Minecraft game. These must be obtained by Mojang, Microsoft, and/or any other appropriate companies officially associated with the Minecraft brand. All intellectual properties, brands, trandmarks, and/or copyrights are owned by their respective owners/companies.
