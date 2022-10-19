# Kingdom Clash
**Kingdom clash** is a Minecraft mod aimed at Void hub's *Create Civilizations event*. It delivers nation-like features with some cool tricks with the [Create Mod](https://github.com/Creators-of-Create/Create).

>**DISCLAIMER** All visuals are the bare minimum to display functionality.

## Kingdom
A kingdom is nothing more than a collection of players, like a *team*, with some shared stats:
* **Lives**. Your kingdom loses a life when a member dies or gets attacked by an enemy kingdom.
* **spawn**. All kingdom-members respawn at the same location. Beds are ignored.
* **Effects**. Bonus effects provided by the **Mechanical Beacon**.
* **Leader**. *tba*

## Blocks

### Mechanical Beacon
![alt text for screen readers](https://i.imgur.com/iLQUpTG.png "UI in top-left corner")

Provides all members of the kingdom with buffs when powered with mechanical power.
* **Resistance**: level 1 steam-engine at 24 *rpm*
* **Regeneration**: level 4 steam-engine at 128 *rpm*
* **Strength**: level 8 steam-engine at 256 *rpm*

You'll need quite some power to run this machine.

### Power Crystal

![alt text for screen readers](https://i.imgur.com/VJ4O3ry.png "UI in top-left corner")

This is the where enemies get the opportunity to destroy your kingdom. Protect it with all you have.

When mined by an enemy player, Your kingdom takes damage and **loses a life**. The block will *respawn*, giving the enemy
another opportunity to inflict more damage. Your kingdom falls when you lose all lives. When your kingdom has fallen, **no member can respawn**.

## UI
Every player will have an interface where some surface-level information will be displayed of all kingdoms.
![alt text for screen readers](https://i.imgur.com/mbJhBXx.png "UI in top-left corner")

## Commands

**All of these commands need the operator rank.**

`/kingdom create [name]`
Creates a kingdom with the name "*name*".

`/kingdom remove [kingdom]`
Removes kingdom "*kingdom*".

`/kingdom list`
Shows you a list of all the kingdoms

`/kingdom addmember [player] [kingdom]`
Adds "*player*" to "*kingdom*".

`/kingdom removemember [player] [kingdom]`
Removes "*player*" from "*kingdom*".

`/kingdom members [kingdom]`
Shows a list of all members in "*kingdom*".

`/kingdom setcolor [color]`
Sets the display-color of the kingdom name to "*color*"

`/kingdom setspawn [kingdom]`
Sets the spawn of "*kingdom*". Members of the kingdom will respawn at the location where the command was executed

`/kingdom setlives [amount] [kingdom]`
Sets the lives of "*kingdom*" "*amount*", which is a positive integer.