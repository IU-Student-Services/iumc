<p align="center">
	<img width="550" src="https://raw.githubusercontent.com/JSH32/iumc/master/.github/branding/banner.png"><br>
	<img src="https://img.shields.io/badge/license-MIT-blue.svg">
	<img src="https://img.shields.io/badge/contributions-welcome-orange.svg">
	<img src="https://img.shields.io/badge/Made%20with-%E2%9D%A4-ff69b4?logo=love">
</p>

## IU Minecraft
Custom code for the IU Minecraft server. This is currently in development phase and will be iterated upon.

### Functionality
- Verifying users with the [IU Login](https://kb.iu.edu/d/bhpr) to prevent griefing.
- Displaying users name and year/status in-game.
- Network wide cross-chat (in the case of multiple servers).

### Architecture
This repository contains all custom code written for IUMC. The entire project is stored in a MonoRepo for ease of organization. Currently, the need for a Spigot/Paper plugin is not needed as network wide verification will be done with a velocity plugin.
In the future that may change so the `iumc-core` contains the basis for a PaperSpigot plugin.