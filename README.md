# Mariiy-TPA

輕量、開源、好用的 **TPA（傳送請求）**。  
對方在**聊天欄**收到可點選的 `[接受]` / `[拒絕]`（可稍後按 T 再開聊天點），接受後**立刻傳送**。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 功能

| 指令 | 說明 |
|------|------|
| `/tpa <玩家>` | 請求傳到對方身邊 |
| `/tpahere <玩家>` | 請求對方傳到你身邊 |
| `/tpacancel` | 取消自己發出的請求 |
| `/tpaccept` / `/tpyes` | 接受 |
| `/tpdeny` / `/tpno` | 拒絕 |
| `/back` | 回到上一傳送點或死亡點 |

當前版本：**1.1.0**

聊天格式：

```
-------------------------------------------------
[傳送] Player 想傳送到你身邊（60 秒內有效）

               [接受]     [拒絕]
-------------------------------------------------
```

- 不擋畫面（不是 Dialog）
- 接受後立即傳送（無 3 秒等待／移動取消）
- 請求逾時、冷卻、離線自動清理

## 平台支援／下載指南

### Paper／Purpur／Spigot（1.20 → 26.2 同一顆）

| 伺服器版本 | 下載 |
|------------|------|
| 1.20 – 1.20.6 | `Mariiy-TPA-paper-mc1.20-26.2-*.jar` → `plugins/` |
| 1.21 – 1.21.x | **同上** |
| **26.1／26.2** | **同上** |

### Fabric／NeoForge／Forge（多檔案）

| 模組 | 遊戲版本 | 產物 |
|------|----------|------|
| `fabric-1.20.1` | 1.20.1 – 1.20.4 | `Mariiy-TPA-fabric-1.20-*.jar` |
| `fabric-1.21.1` | 1.21 – 1.21.1 | `Mariiy-TPA-fabric-1.21-*.jar` |
| `neoforge-1.21.1` | 1.21 – 1.21.1 | `Mariiy-TPA-neoforge-1.21-*.jar` |
| `forge-1.20.1` / `forge-1.21.1` | 見各模組 README | 需 Gradle 8 建置 |

**26.1／26.2 的 Fabric／NeoForge**：遊戲已去混淆，需新 Loom／官方 mappings，**1.0.0 尚未收錄**；該版本線請先用 Paper，或等後續 release。

\* Folia：目前未標 `folia-supported`。

## 建置

需要 **JDK 21+**（1.20 模組 toolchain 為 17）。Wrapper：**Gradle 9.1**。

```bash
# Paper
./gradlew -Pplatforms=paper :paper:shadowJar

# Fabric（兩顆：1.20 + 1.21）
./gradlew -Pplatforms=fabric buildFabric

# NeoForge 1.21
./gradlew -Pplatforms=neoforge :neoforge-1.21.1:build

# NeoForge 1.20（另開；工具鏈較舊）
./gradlew -Pplatforms=neoforge-1.20.1 :neoforge-1.20.1:build

# Forge：需 Gradle 8，見 forge-1.21.1/README.md
```

產物：

- `paper/build/libs/Mariiy-TPA-paper-*.jar`
- `fabric-1.20.1/build/libs/Mariiy-TPA-fabric-1.20-*.jar`
- `fabric-1.21.1/build/libs/Mariiy-TPA-fabric-1.21-*.jar`
- `neoforge-1.21.1/build/libs/Mariiy-TPA-neoforge-1.21-*.jar`
- …

## 安裝

### Paper / Purpur
1. 把 `Mariiy-TPA-paper-*.jar` 放入 `plugins/`
2. 重啟伺服器
3. 可改 `plugins/Mariiy-TPA/config.yml`

### Fabric / NeoForge / Forge
1. 安裝對應 loader +（Fabric 需 Fabric API）
2. 把對應 jar 放入 `mods/`
3. 重啟

## 權限（Paper）

| 權限 | 預設 | 說明 |
|------|------|------|
| `mariiytpa.use` | true | 使用指令 |
| `mariiytpa.bypass` | op | 略過冷卻 |
| `mariiytpa.reload` | op | 預留 |

## 專案結構

```
Mariiy-TPA/
  common/            # 共用邏輯 + 語言檔
  paper/             # Paper 1.20–26.2（單一 jar）
  fabric-1.20.1/     # Fabric 1.20.1–1.20.4
  fabric-1.21.1/     # Fabric 1.21–1.21.1
  neoforge-1.20.1/   # NeoForge 1.20.x
  neoforge-1.21.1/   # NeoForge 1.21.x
  forge-1.20.1/      # Forge 1.20.x
  forge-1.21.1/      # Forge 1.21.x
```

各平台只負責：指令註冊、聊天元件、傳送、排程；**規則全在 `common`**。

## 多語言

訊息會跟隨**每位玩家自己的 Minecraft 語言設定**（客戶端 Language）：

| 檔案 | 語言 |
|------|------|
| `en_us` | English |
| `zh_tw` | 繁體中文 |
| `zh_cn` | 简体中文 |
| `ja_jp` | 日本語 |

語言檔位置：`common/src/main/resources/assets/mariiy_tpa/lang/`。缺檔或未知語言時回退到 `en_us`。  
歡迎 PR 增加其他語言。

Paper `config.yml` 的 `prefix` 留空＝依語言檔；填字串則強制所有人同一前綴。

## 設定（Paper `config.yml`）

```yaml
enabled: true
timeout-seconds: 60
cooldown-seconds: 15
separator: "-------------------------------------------------"
prefix: ""
```

## 授權

[MIT](LICENSE) — 歡迎 Fork、改、商用，記得保留授權聲明。

## 貢獻

PR / Issue 歡迎：

- 更多 Minecraft 版本（Stonecutter / Architectury）
- 英文／其他語系訊息
- Folia 支援、跨服代理、黑名單等

## 為什麼再做一個 TPA？

很多現有 TPA：Dialog 擋畫面、冷卻太長、點了還要等、移動就取消、或綁一整包插件。  
Mariiy-TPA 只做一件事：**乾淨的聊天點選傳送**。
