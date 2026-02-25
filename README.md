# Extra Video Settings

Embeddiumがバニラのビデオ設定画面を置き換えた際に消えてしまう設定項目を復元するForge modです。

## 前提mod

- Minecraft Forge 1.20.1
- [Embeddium](https://www.curseforge.com/minecraft/mc-mods/embeddium) 0.1.0+

## 復元される設定項目

| 設定名 | 説明 |
|--------|------|
| FOV Effects | スピード上昇時のFOV（視野角）変化量。0%にすると画面の歪みがなくなる |
| Distortion Effects | ネザーポータル等の画面歪みエフェクトの強さ |
| Darkness Pulsing | 暗闇エフェクト（Deep Dark等）の脈動の強さ |
| Damage Tilt | ダメージを受けた時の画面の傾き量 |
| Glint Speed | エンチャントアイテムの光るアニメーション速度 |
| Glint Strength | エンチャントアイテムの光の強さ |
| Entity Shadows | エンティティ（Mob・プレイヤー）の足元の影の表示切替 |

## ボタンの場所

- **設定画面** → 「バニラ設定」ボタン
- **Embeddiumビデオ設定画面** → 右下に「Vanilla Options」ボタン

## ビルド方法

```
JAVA_HOME="C:/Program Files/Java/jdk-17" ./gradlew build
```

出力: `build/libs/extra_video_settings-1.0.jar`

## インストール

`extra_video_settings-1.0.jar` を `.minecraft/mods/` フォルダに入れる。
