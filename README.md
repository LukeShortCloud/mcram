#MCRAM
MCRAM is designed to help run your Minecraft server entirely in RAM on UNIX-based systems. It will set up the automation of starting your server 100% in memory and then saving/syncing it back to your drive occasionally. This helps to improve performance and extend your drive's health and longevity.

### SETUP
For installation simply execute the install file:
```bash
sh installer.sh
```
The install will walk you through and help answer your questions to make the configuration file that will be run as a system cron. Currently the cron is setup to be run (A) after the installer has finished and then (B) after that it will always run on the boot-up of your server/PC. 
