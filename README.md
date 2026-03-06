# Extra Video Settings

Embeddiumがバニラのビデオ設定画面を置き換えた際に消えてしまう設定項目を復元するForge modです。
さらに、キー設定やビデオ設定をプロファイルとしてテキストファイルに保存・読み込みできます（MOD設定含む）。

## 前提mod

- Minecraft Forge 1.20.1
- [Embeddium](https://www.curseforge.com/minecraft/mc-mods/embeddium) 0.1.0+（任意）

## 機能

### 復元される設定項目（Embeddium導入時）

| 設定名 | 説明 |
|--------|------|
| FOV Effects | スピード上昇時のFOV（視野角）変化量。0%にすると画面の歪みがなくなる |
| Distortion Effects | ネザーポータル等の画面歪みエフェクトの強さ |
| Darkness Pulsing | 暗闇エフェクト（Deep Dark等）の脈動の強さ |
| Damage Tilt | ダメージを受けた時の画面の傾き量 |
| Glint Speed | エンチャントアイテムの光るアニメーション速度 |
| Glint Strength | エンチャントアイテムの光の強さ |
| Entity Shadows | エンティティ（Mob・プレイヤー）の足元の影の表示切替 |

### 設定プロファイル（保存・読み込み）

キー設定とビデオ設定をプロファイルとしてテキストファイルに保存・読み込みできます。
バニラだけでなく、MODが追加したキーバインドやoptions.txtに書き込まれる設定も含まれます。

#### GUI から操作

**設定画面** → 「完了」ボタンの横にある「プロファイル...」ボタンからプロファイル管理画面を開けます。

- プロファイル名を入力して保存・読込・削除が可能
- 保存済みプロファイル一覧が画面下部に表示される

#### コマンドから操作

ゲーム内チャットで以下のコマンドが使えます（Tab補完対応）：

| コマンド | 説明 |
|----------|------|
| `/evs save keys <名前>` | キー設定をプロファイルに保存 |
| `/evs load keys <名前>` | キー設定をプロファイルから読み込み |
| `/evs save video <名前>` | ビデオ設定をプロファイルに保存 |
| `/evs load video <名前>` | ビデオ設定をプロファイルから読み込み |
| `/evs list keys` | キー設定プロファイル一覧を表示 |
| `/evs list video` | ビデオ設定プロファイル一覧を表示 |
| `/evs delete keys <名前>` | キー設定プロファイルを削除 |
| `/evs delete video <名前>` | ビデオ設定プロファイルを削除 |

#### 保存先

`config/extra_video_settings/profiles/` フォルダにテキストファイルとして保存されます。
手動で編集したり、他の環境にコピーして共有することも可能です。

## ビルド・テスト

### JAR ビルド

```bash
./build.sh
```

出力: `build/libs/extra_video_settings-1.1.jar`

### テストプレイ（Embeddium + Oculus 入り）

```bash
./test.sh
```

Embeddium 0.3.31 と Oculus 1.8.0 が自動で読み込まれた状態で Minecraft が起動する。

### 手動で実行する場合

```bash
# ビルドのみ
JAVA_HOME="C:/Program Files/Java/jdk-17" ./gradlew build

# テストプレイ
JAVA_HOME="C:/Program Files/Java/jdk-17" ./gradlew runClient
```

> WSL の場合は `JAVA_HOME="/mnt/c/Program Files/Java/jdk-17"` を使用する。

## インストール

`extra_video_settings-1.1.jar` を `.minecraft/mods/` フォルダに入れる。

## ライセンス

MIT License
