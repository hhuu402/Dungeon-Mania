DungeonManiaController.java:
 - Create specific Dungeons, not a randomised seed.
 - Each dungeon has a certain preloaded map. Entities are preset at certain positions and players initial location is set.
 - Games will be saved in the resources/games folder as a JSON file

Player.java:
 - Attributes: maxHealth, currHealth, Attack, Status
 - Multiple potion effect can be active at the same time
 - Used potions or effects are removed in player the java and not the subclasses
 - Inventory and effects on a player is type of List
 - Health and damage is set not flexible
 - Player cannot have more than one sword, bow, shield and armour at a time
 - Player receiving damage is calculated in the following sequence: Armour -> shield
 - If a player has a potion effect active and reactives a new potion the duration is not additive but starts from its initial duration. I.e. duration of invisibility for 10 use for 5 ticks
 - Player uses invisibility potion again; remaining ticks goes from 5 to back to 10 (not 15).
 - Player will not pickup armour or sword if it already exists in their inventory
 - Player will not build if bow or shield is already in inventory

---------------------ENTITIES---------------------
 - Abstract class of all entities 
 - Common Attributes: id, type, position, isInteractable.
 - Type refers to the object itself.

Moving Entities:
 - Spiders, Mercenary and Zombie toast, cannot enter portals since they can move over portals.
 - Spider, zombie toast and mercenary will  attack player ONLY IF position is the same as entity position
 - Spider will not spawn in the same position as a boulder
 - Spider will not spawn outside of dungeon dimensions, but CAN move outside dungeon dimensions 
 - Spider will not spawn in between boulders such that it is unable to move anywhere
 - Mercenaries can be bribed in Peaceful mode
     - Once bribed it will follow the player around
 - Mercenaries will still go towards the player in Peaceful mode But it will not attack
 - Zombie toast spawner will always have 4 adjacent squares to create zombie toasts
 - Moving entities cannot overlay above each other
 - Player cannot walk over mercenary

Static Entities:
 - When a portal is created, it will have another half created with it
     - Portals will be created in every dungeon
 - If an player has a weapon (sword) and is cardinally adjacent to a zombie toast spawner it will automatically break the spawner
     - Durability of sword will go down by 1

Items:
 - Can only be picked up/collected once player is at its position
 - Sword and bow has a durability of 10
 - Bomb has a blast range of 1
 - Durability of sword decreases only when attacking enemy and spawner
 - Sword, shield, armour and bow durability will be DEPENDENT ON GAMEMODE. HOWEVER its damage/defence modifier will remain the SAME across all GAMEMODEs.
 - If armour cuts the damage in half such that it now becomes float/double it is rounded up
 - Potion effects only tick upon player movement

---------------------GAME---------------------
 - Each game has a selected game mode (Peace, standard, hard) and an associated Dungeon (with preset goals -> boulder moving, collecting entities, reaching an exit etc…)


Game.java:
 - Tick would be called either to move player or use an item. In both scenarios enemies would move.
 - For tick, a given direction of NONE would mean the tick was called to use an item. Any other given direction would mean the tick was called only to move a player and not to use an item.
 - Player is able to stack on some entities but it will always be represented as the top most layer (layer 2) for that position.
 - For any given position, there would be a maximum of two entities on the position.

GameModes.java:
 - Variables change depending on the game mode:
     - Standard:
        Tick Num: 20
        Player Health: 100
        Enemy Health: 50
        Player Attack: 10
        Enemy Attack: 10
     - Peaceful:
        Tick Num: 20
        Player Health: 100
        Enemy Health: 1
        Player Attack: 0
        Enemy Attack: 0
     - Hard
        Tick Num: 15
        Player Health: 70
        Enemy Health: 50
        Player Attack: 10
        Enemy Attack: 10

---------------------PATTERNS---------------------
- composite -> subgoals
- state pattern -> items, enemy
- observer pattern -> goals observes the entities in game 
