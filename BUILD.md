# ビルド手順

JAVA_HOMEの設定はシェルスクリプトに含まれているので、セットアップ不要。

## テストプレイ

```bash
bash run_clientfo.sh   # Forge (1.20.1)
bash run_clientfa.sh   # Fabric (1.20.4)
```

## ビルド

```bash
bash buildfo.sh   # Forge (1.20.1) → build/libs/
bash buildfa.sh   # Fabric (1.20.4) → fabric/build/libs/
```
