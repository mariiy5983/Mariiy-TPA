# Mariiy-TPA 1.0.0

Lightweight open-source **Teleport Ask (TPA)** for Minecraft multiplayer.  
Players receive a **clickable chat invitation** (`[Accept]` / `[Deny]`). Accepting teleports **immediately** (no warmup / move-cancel). Messages follow each player’s **in-game language** setting.

---

## English

### Which file should I download?

**Important:** Minecraft version numbering changed in 2026. Servers may show `1.20.x`, `1.21.x`, or the newer `26.1` / `26.2` style. Pick the file that matches your **server platform** (Paper vs Fabric vs NeoForge), not only the version number.

#### Paper / Purpur / Spigot (recommended for most servers)

| Your server version | Download | Install to |
|---------------------|----------|------------|
| **1.20 – 1.20.6** | `Mariiy-TPA-paper-mc1.20-26.2-1.0.0.jar` | `plugins/` |
| **1.21 – 1.21.x** | **same Paper jar** | `plugins/` |
| **26.1 / 26.2** (Paper / Purpur) | **same Paper jar** | `plugins/` |

> One Paper plugin jar covers **Minecraft / Paper from 1.20 through 26.2**. You do **not** need a different Paper jar per minor version.

#### Fabric

| Your Minecraft version | Download | Install to |
|------------------------|----------|------------|
| **1.20.1 – 1.20.4** | `Mariiy-TPA-fabric-1.20-1.0.0.jar` | `mods/` (+ Fabric API) |
| **1.21 – 1.21.1** | `Mariiy-TPA-fabric-1.21-1.0.0.jar` | `mods/` (+ Fabric API) |
| **26.1 / 26.2** | *Not included in 1.0.0* — requires a new Fabric toolchain (unobfuscated game). Planned for a follow-up release. Use **Paper** on 26.x if you need TPA today. |

#### NeoForge

| Your Minecraft version | Download | Install to |
|------------------------|----------|------------|
| **1.21 – 1.21.1** | `Mariiy-TPA-neoforge-1.21-1.0.0.jar` | `mods/` |
| **1.20.x / 26.x** | Source modules exist / planned; binaries for every line will expand in later releases. Prefer **Paper** for widest 1.20–26.2 coverage in 1.0.0. |

### Features

- `/tpa`, `/tpahere`, `/tpacancel`, `/tpaccept` (`/tpyes`), `/tpdeny` (`/tpno`)
- Clickable chat UI (not a blocking dialog)
- Instant teleport on accept
- Request timeout & cooldown
- Offline cleanup
- i18n: `en_us`, `zh_tw`, `zh_cn`, `ja_jp` (follows client language)

### Requirements

- **Paper jar:** Java 17+ (1.20–1.20.4) or Java 21+ (1.20.5+)
- **Fabric / NeoForge 1.21:** Java 21+

### License

MIT

---

## 繁體中文

### 我該下載哪一個檔案？

**重要：** 2026 年起 Minecraft／Paper 版號可能是 `1.20.x`、`1.21.x`，也可能是新式 **`26.1`／`26.2`**。請依**伺服器平台**（Paper／Fabric／NeoForge）選擇，不要只看版號數字。

#### Paper／Purpur／Spigot（多數伺服器建議）

| 你的伺服器版本 | 下載檔 | 放置位置 |
|----------------|--------|----------|
| **1.20 – 1.20.6** | `Mariiy-TPA-paper-mc1.20-26.2-1.0.0.jar` | `plugins/` |
| **1.21 – 1.21.x** | **同一顆 Paper jar** | `plugins/` |
| **26.1／26.2**（Paper／Purpur） | **同一顆 Paper jar** | `plugins/` |

> **一顆 Paper 插件即可涵蓋 1.20 到 26.2。** 不需要每個小版本各下一顆 Paper。

#### Fabric

| 你的 Minecraft 版本 | 下載檔 | 放置位置 |
|---------------------|--------|----------|
| **1.20.1 – 1.20.4** | `Mariiy-TPA-fabric-1.20-1.0.0.jar` | `mods/`（需 Fabric API） |
| **1.21 – 1.21.1** | `Mariiy-TPA-fabric-1.21-1.0.0.jar` | `mods/`（需 Fabric API） |
| **26.1／26.2** | *1.0.0 尚未收錄*（需新 Loom／官方 mappings）。後續版本會補上。若現在就要用，請在 26.x 使用 **Paper** 版。 |

#### NeoForge

| 你的 Minecraft 版本 | 下載檔 | 放置位置 |
|---------------------|--------|----------|
| **1.21 – 1.21.1** | `Mariiy-TPA-neoforge-1.21-1.0.0.jar` | `mods/` |
| **1.20.x／26.x** | 原始碼／後續 release 會持續補齊二進位檔。1.0.0 若要涵蓋最廣版本範圍，請優先使用 **Paper**。 |

### 功能

- `/tpa`、`/tpahere`、`/tpacancel`、`/tpaccept`（`/tpyes`）、`/tpdeny`（`/tpno`）
- 聊天欄可點選（非擋畫面 Dialog）
- 接受後立即傳送
- 請求逾時與冷卻
- 離線自動清理
- 多語言：`en_us`、`zh_tw`、`zh_cn`、`ja_jp`（跟隨玩家遊戲語言）

### 需求

- **Paper：** Java 17+（1.20–1.20.4）或 Java 21+（1.20.5 起）
- **Fabric／NeoForge 1.21：** Java 21+

### 授權

MIT

---

**Repository:** https://github.com/mariiy5983/Mariiy-TPA  
**Issues / feedback welcome.**
