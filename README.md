# OminousChestLock

Paper plugin for Minecraft 1.21.0+ that locks chests, double chests, barrels, and shulker boxes to a named ominous trial key.

## How it works
- Rename an ominous trial key in an anvil (example: `secretkeyname`).
- Right-click a chest, barrel, or shulker box while holding that key to lock it.
- Only players holding a matching named key can open, break, or otherwise interact with the locked container.
- Locks are key-name based, not player based. Keys can be stolen or copied.
- One key name can only lock one container at a time until that container is unlocked/destroyed.
- Optional: normal trial keys can be enabled. The first successful open with a normal key arms the lock; the next successful open consumes the key and permanently unlocks the container.

Vault sounds are used for success/failure feedback.

## Data
Lock data is stored in `plugins/OminousChestLock/data.yml`. Entries include the key name, creator, and last user.
Existing entries from older versions are automatically read and upgraded when saved.

Example:
```yaml
locked-chests:
  world:10,64,20:
    key: secretkeyname
    world:
      name: world
      realm: OVERWORLD
      uuid: 01234567-89ab-cdef-0123-456789abcdef
    creator:
      name: Bingo
      uuid: 01234567-89ab-cdef-0123-456789abcdef
    last-user:
      name: Steve
      uuid: fedcba98-7654-3210-cake-ba9876543210
```

## Admin commands
Requires `chestlock.admin` (default: op).

- `/chestlock info` - Show key name, creator, and last user for the looked-at container.
- `/chestlock unlock` - Force unlock the looked-at container.
- `/chestlock keyinfo` - Show the locked container location and owner info for the key in hand.
- `/chestlock reload` - Reload lock data from disk.
- `/chestlock loglevel <0-3>` - Set log verbosity.
- `/chestlock normalkeys <on|off>` - Allow normal trial keys.
- `/chestlock help` - Show help.

## Logging
Config in `plugins/OminousChestLock/config.yml`:
- `logging.level`:
  - `0` = nothing
  - `1` = everything
  - `2` = failed unlock + destruction/automation attempts
  - `3` = destruction/automation attempts only
- `keys.allow-normal`:
  - `false` = ominous keys only
  - `true` = ominous + normal trial keys

### Example log entries
```
[OminousChestLock] EXPLOSION_DENY actor=TNT:CevAPI usedKey=none lockKey=secretkeyname creator=Bingo lastUser=George location=test:-9,104,40 (OVERWORLD)
[OminousChestLock] INVENTORY_MOVE_DENY actor=HOPPER:CevAPI usedKey=none lockKey=secretkeyname creator=Bingo lastUser=Bingo location=test:-21,104,22 (OVERWORLD) detail=source=CHEST dest=HOPPER
[OminousChestLock] BREAK_DENY actor=CevAPI usedKey=none lockKey=secretkeyname creator=Bingo lastUser=George location=test:-9,104,40 (OVERWORLD)
[OminousChestLock] INTERACT_DENY actor=CevAPI usedKey=cevapcici lockKey=secretkeyname creator=Bingo lastUser=Bingo location=test:-52,104,66 (OVERWORLD)
[OminousChestLock] OPEN_ALLOWED actor=CevAPI usedKey=secretkeeey lockKey=secretkeeey creator=CevAPI lastUser=CevAPI location=test:-6,104,37 (OVERWORLD)
[OminousChestLock] BREAK_ALLOWED actor=CevAPI usedKey=secretkeeey lockKey=secretkeeey creator=CevAPI lastUser=CevAPI location=test:-6,104,37 (OVERWORLD)
```

## Build
```shell
./gradlew clean build
```
The jar will be in `build/libs/OminousChestLock-1.0.0.jar`.

## Permissions
- `chestlock.admin` - Allows use of admin commands (default: op).
