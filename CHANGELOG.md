# Changelog

## Unreleased

- クリエイティブタブとJEIの検索対象にアイテムIDを追加しました（例: `minecraft:stone`、`modid:item_name`）。表示言語や高度なツールチップの設定に関係なく検索できます。
- v1.4で追加した多言語のアイテム名検索と併用できます。
- JEIをForge / NeoForge / FabricのMOD定義に任意依存として登録しました。JEIを導入していない環境でも利用できます。
- NeoForgeのEclipse用クライアント起動準備タスクを追加し、Gradleでコンパイルした最新クラスをIDEの起動用フォルダに反映するようにしました。

検証: Minecraftの検索クラスでアイテムIDの完全指定・部分指定と、日本語・英語の名前検索を確認済み。JEIへのアイテムID追加処理と、全3ローダーの任意依存定義を確認済み。
ゲーム内動作は未確認です。NeoForgeの起動準備・再ビルドは、ローカルのGradleキャッシュ破損とダウンロード失敗により完了していません。

## v1.4 (2026-10-21)

### New: Multilingual Item Search

- 表示言語とは別の言語のアイテム名で検索できるようになりました。
  - 英語設定で「ダイヤモンド」、日本語設定で `diamond` と検索できます。
  - クリエイティブインベントリ・レシピブック・JEI 15 の検索に対応します。
- Minecraft、MOD、有効なリソースパックに収録された各言語の翻訳を検索対象に追加します。
  - 同じ言語ファイルではリソースパックの優先順位を反映します。
  - 翻訳データに含まれない名前の自動翻訳やローマ字変換は行いません。
- 言語変更やリソース再読み込み時に検索用データを更新します。
- JEIを導入していない環境でも動作します。
- Forge / Fabric は Minecraft 1.20.1、NeoForge は Minecraft 1.20.2 に対応します。
- 全ローダーの配布JAR名に対応するMinecraftバージョンを明記します。

検証: 全3ローダーの配布用JARのビルド・内容確認済み。
英語・日本語・ドイツ語の検索名収集、パックの優先順位、再読み込み時の検索名更新を検証済み。
ゲーム内での動作確認は未実施です。

---

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
