# Changelog

## v1.1

### New: Settings Profiles (Save & Load)

キー設定やビデオ設定をプロファイルとしてテキストファイルに保存・読み込みできるようになりました。
バニラだけでなく、MODが追加したキーバインドやビデオ設定も含まれます。

- **GUI対応**: 設定画面の「完了」ボタンの横に「プロファイル...」ボタンを追加
  - プロファイル名を入力して保存・読込・削除が可能
  - 保存済みプロファイル一覧を表示
- **コマンド対応**: `/evs` コマンドでチャットから操作可能（Tab補完対応）
  - `/evs save keys <name>` / `/evs load keys <name>` - キー設定の保存・読込
  - `/evs save video <name>` / `/evs load video <name>` - ビデオ設定の保存・読込
  - `/evs list keys|video` - プロファイル一覧
  - `/evs delete keys|video <name>` - プロファイル削除
- 保存先: `config/extra_video_settings/profiles/` (テキストファイル、手動編集・共有可能)
- Embeddiumなしでもプロファイル機能は動作します

---

## v1.0

### Initial Release

- Embeddiumが削除するバニラのビデオ設定をEmbeddium設定画面に復元
  - FOV Effects, Distortion Effects, Darkness Pulsing, Damage Tilt
  - Glint Speed, Glint Strength, Entity Shadows
- 日本語・英語対応
- デバッグログ設定
