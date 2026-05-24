# ビルド手順

## ディレクトリ構成

```
extra_video_settings/
├─ common/                       # 3 ローダー共通のソース・リソース
│  └─ src/main/
│     ├─ java/extravideoset/client/SettingsProfileManager.java
│     └─ resources/
│        ├─ assets/extra_video_settings/lang/{en_us,ja_jp}.json
│        └─ icon.png
├─ forge/
│  ├─ forge/                     # Forge 1.20.1 (47.3.0)
│  └─ neoforge/                  # NeoForge 1.20.2 (20.2.93)
└─ fabric/                       # Fabric 1.20.1
```

3 つの mod ローダーは `common/` の Java / resources を sourceSet 経由で
直接取り込む（コピー無し、シンボリックリンクも無し — Gradle 側で
`srcDir file('../../common/...')` 参照のみ）。重複ファイルは無い。

ローダー固有のファイル（mod 登録クラス、設定、画面イベント登録、
mods.toml / fabric.mod.json、pack.mcmeta）は各サブプロジェクトに残る。

| ローダー | MC バージョン | サブプロジェクト | プラグイン |
|----------|---------------|------------------|------------|
| **Forge**    | 1.20.1 (47.3.0)   | `forge/forge/`    | ForgeGradle 6 |
| **NeoForge** | 1.20.2 (20.2.93)  | `forge/neoforge/` | NeoGradle 7.0.50 |
| **Fabric**   | 1.20.1            | `fabric/`         | fabric-loom 1.7 |

> **NeoForge が 1.20.2 なのは仕様**：NeoForge は 1.20.1 をサポートしない（1.20.2 から fork された）。

## 前提

- **JDK 17** が必要
  - macOS: `brew install openjdk@17`
  - Windows: `C:\Program Files\Java\jdk-17`
  - Linux: 各ディストリのパッケージ
- 初回ビルド時のみインターネット接続必須（Minecraft / Forge / NeoForge / Fabric 等の依存ダウンロード）

## クイックスタート（プロジェクトルートから）

```bash
cd ~/Documents/github/mods/extra_video_settings   # macOS の例

# 全部ビルド
./build.sh

# 個別ビルド
./buildfo.sh    # Forge
./buildne.sh    # NeoForge
./buildfa.sh    # Fabric

# 開発用クライアント起動
./run_clientfo.sh
./run_clientne.sh
./run_clientfa.sh
```

Windows なら `.bat` 版（`buildfo.bat` / `run_clientne.bat` ...）を使う。

## 出力 JAR

| ローダー | パス |
|----------|------|
| Forge    | `forge/forge/build/libs/extra_video_settings-1.1.jar` |
| NeoForge | `forge/neoforge/build/libs/extra_video_settings-neoforge-1.1.jar` |
| Fabric   | `fabric/build/libs/extra_video_settings-fabric-1.20.1-1.1.jar` |

各 JAR には `common/` のリソース・クラスが自動的に同梱される。

## インストール

該当する JAR を `.minecraft/mods/` に放り込む。

- **Forge 版**：[Embeddium](https://www.curseforge.com/minecraft/mc-mods/embeddium) 任意
- **NeoForge 版**：Embeddium 連携は現状未配線（→ トラブルシューティング参照）
- **Fabric 版**：[Sodium](https://modrinth.com/mod/sodium) 0.5.x 必須、Iris 推奨

## 手動で gradlew

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS

cd forge/forge && ./gradlew build       # Forge
cd forge/neoforge && ./gradlew build    # NeoForge
cd fabric && ./gradlew build            # Fabric
```

開発クライアントは `./gradlew runClient`。

## ビルド速度

`gradle.properties` で daemon / parallel / caching / config-on-demand を全部有効。

```
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

| 種別 | 所要時間（実測 / Mac M-series） |
|------|---------------------------------|
| 初回コールド (全 3 ローダー) | 約 4 分（依存 DL 含む） |
| 再ビルド（変更なし） | 約 5 秒（UP-TO-DATE） |
| 単一ローダー差分ビルド | 約 8〜15 秒 |

## クリーンビルド

```bash
# 各サブプロジェクトで:
rm -rf .gradle build
./gradlew clean build

# NeoForge は、もし decompile が「UP-TO-DATE なのにファイル無い」状態になったら下も消す
rm -rf ~/.gradle/caches/neoformruntime ~/.gradle/undefined-build
```

## 実 Minecraft へインストールしてテスト

```bash
./test-full.sh   # Forge JAR をビルドして .minecraft/mods/ に配置
```

スクリプト内の `MINECRAFT_MODS` パスを環境に合わせて編集。

---

## トラブルシューティング

### Fabric: `Unexpected IllegalAccessException occurred (Gson 2.9.1)`

`fabric/settings.gradle` の foojay-resolver プラグインが古いと、Gson 2.9.1 が
transitive で混入する。JDK 17 の final フィールド反射制限と衝突して
fabric-loom の MinecraftMetadataProvider を壊す。

**修正済み**：foojay-resolver を `1.0.0` 指定（Gson 2.10+ を持ってくる）。
再発時は `fabric/settings.gradle` の以下が古くないか確認：

```groovy
id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
```

### NeoForge: `neoFormApplyForgesAccessTransformer FAILED ... output.jar doesn't exist`

NeoGradle のタスクキャッシュが「decompile 済み」と判定したのに出力 JAR が無い状態。
グローバルキャッシュを掃除：

```bash
rm -rf ~/.gradle/caches/neoformruntime ~/.gradle/undefined-build
rm -rf forge/neoforge/build forge/neoforge/.gradle
./buildne.sh
```

### NeoForge: 初回ビルドの decompile が無言で落ちる

vineflower のヒープ不足。`forge/neoforge/gradle.properties` の
`org.gradle.jvmargs=-Xmx4G` を `-Xmx6G` に上げる。

### NeoForge: Embeddium 連携を有効にしたい

`forge/neoforge/build.gradle` の `sourceSets` で `EmbeddiumIntegration.java` を
除外している（1.20.2 用 Embeddium の CurseForge file ID 未確定のため）。

1. CurseForge で Embeddium プロジェクト (ID 908741) の 1.20.2 用ファイル ID を確認
2. `forge/neoforge/build.gradle` の `dependencies {}` に追加：
   ```groovy
   compileOnly "curse.maven:embeddium-908741:<file-id>"
   runtimeOnly "curse.maven:embeddium-908741:<file-id>"
   ```
3. 同ファイルの `sourceSets.main.java.exclude` 行を削除
4. `EmbeddiumIntegration.java` を `common/src/main/java/.../client/` に移して
   全ローダー共通にしてもよい（API が一致する前提で）

### Forge: `JAVA_HOME` が見つからない

スクリプトは macOS で `/usr/libexec/java_home -v 17` を使って自動検出。
Linux なら `JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./buildfo.sh`。

### Windows / WSL でビルドしたい

`.bat` 系を使うか、WSL なら `.sh` をそのまま実行（`/mnt/c` 検知で
`cmd.exe` を叩く）。

### common/ のファイルを編集したら？

特に何もしなくて OK。各ローダーが `srcDir file('../../common/...')` で
直接参照しているので、編集 → 各ローダーで再ビルド、で反映される。
日本語訳の追加・修正は `common/src/main/resources/.../lang/ja_jp.json`
を編集すれば 3 ローダー全部に効く。

---

## バージョン情報（要点）

| 項目 | Forge | NeoForge | Fabric |
|------|-------|----------|--------|
| Minecraft | 1.20.1 | 1.20.2 | 1.20.1 |
| Loader | Forge 47.3.0 | NeoForge 20.2.93 | Fabric Loader 0.16.0 |
| Gradle wrapper | 8.8 | 8.5 | 8.10 |
| ビルドプラグイン | net.minecraftforge.gradle 6 | net.neoforged.gradle.userdev 7.0.50 | fabric-loom 1.7-SNAPSHOT |
| pack_format | 15 | 18 | 15 |
| Sodium / Embeddium | Embeddium 0.3.31 (任意) | （未配線 — 上記参照） | Sodium 0.5.3+mc1.20.1 (必須) |

## リポジトリ軽量化メモ

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| 追跡ファイル数 | 123 | 約 50 |
| 追跡サイズ合計 | ~47 MB | ~0.2 MB |
| 重複 Java/lang/icon | 各ローダーごとに複製 | `common/` に集約 |

- `build/`, `run/`, `bin/` を `.gitignore`（IDE 出力、Gradle 出力、Minecraft 実行時データ）
- `*.log.gz`, `*.iml`, `.classpath`, `.project` も除外
