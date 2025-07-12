# Minecraft Create Mod 村人取引自動化MOD 仕様書

## 概要

このMODは、Minecraft Forge 1.20.1環境でCreate MODと連携して動作する、村人取引自動化システムを実装します。プレイヤーは村人の前にアイテムを投げることで自動取引を行ったり、Create MODのDeployerを使用した自動化システムを構築できます。

## 開発環境

### 必要な環境
- **OS**: Ubuntu 20.04 LTS以降
- **Java**: OpenJDK 17 (Minecraft 1.20.1対応)
- **Minecraft**: 1.20.1
- **Forge**: 47.1.0以降
- **IDE**: IntelliJ IDEA Community Edition (推奨)
- **前提MOD**: Create 0.5.1f以降

### 開発ツール
- **Claude Code**: デバッグ・コード生成支援
- **Git**: バージョン管理
- **Gradle**: ビルドシステム
- **MCP**: Minecraft Mod Development Kit

## MOD仕様

### 1. 基本機能

#### 1.1 アイテム投げ取引システム
- **機能**: 村人の前にアイテムを投げると自動で取引が実行される
- **制限**: エメラルドを与える取引は自動化しない（安全性のため）
- **対象**: 鉄インゴット、火打石、木の棒などのバニラアイテム
- **経験値**: 通常取引と同様に経験値を付与

#### 1.2 Create MOD Deployer連携
- **機能**: Deployerにアイテムをセットすることで村人と自動取引
- **動作**: Deployerが村人に対してアイテムを「使用」する動作を実行
- **制限**: エメラルドを与える取引は除外

#### 1.3 取引選択システム
- **複数選択肢**: 同じアイテムで複数の取引が可能な場合はランダム選択
- **フィルター機能**: レシピフィルター機能で取引を制限可能
- **優先度設定**: 特定の取引を優先する設定

#### 1.4 取引制限システム
- **上限管理**: 通常の村人取引と同じ取引上限を適用
- **マスターレベル**: 村人のマスターレベルは通常取引と連動
- **クールダウン**: 取引後のクールダウン時間を適用

### 2. 実装コンポーネント

#### 2.1 新規ブロック
```java
// 村人取引ステーション
public class VillagerTradingStation extends Block {
    // 村人を配置し、アイテムを投入する専用ブロック
    // Create MODのDeployerと連携可能
}

// 取引フィルター
public class TradingFilter extends Block {
    // 特定の取引のみを許可するフィルター機能
}
```

#### 2.2 アイテム検出システム
```java
// アイテム投げ検出
public class ItemDropHandler {
    // 村人周辺のアイテムドロップを検出
    // 取引可能なアイテムかチェック
    // 自動取引を実行
}
```

#### 2.3 村人取引インターフェース
```java
// 村人取引管理
public class VillagerTradeManager {
    // 村人の取引データを管理
    // 取引制限・クールダウン管理
    // 経験値・取引回数の追跡
}
```

### 3. Create MOD連携

#### 3.1 Deployer拡張
```java
// Deployerの村人取引機能拡張
public class DeployerVillagerTradingCapability {
    // Deployerが村人に対してアイテムを使用する際の処理
    // 取引可能性の判定
    // 取引実行とアイテム交換
}
```

#### 3.2 コンベア・ファンネル連携
```java
// アイテム搬送システム連携
public class ConveyorTradingIntegration {
    // ベルトコンベアからの自動アイテム供給
    // 取引結果のアイテム回収
    // ファンネルとの連携
}
```

### 4. 安全性・制限事項

#### 4.1 エメラルド取引制限
- エメラルドを投げた際の自動取引は無効化
- 誤操作による意図しない取引を防止
- 設定で有効/無効を切り替え可能

#### 4.2 取引制限
- 村人の通常取引制限を継承
- 1日あたりの取引上限を適用
- 取引価格の変動システムを維持

#### 4.3 村人保護
- 村人の移動・消失を防ぐ保護機能
- 取引中の村人への攻撃を無効化
- 村人の職業変更を防止

### 5. 設定・カスタマイズ

#### 5.1 設定ファイル
```toml
[villager_trading]
# 自動取引の有効/無効
enable_auto_trading = true

# エメラルド取引の制限
restrict_emerald_trades = true

# 取引範囲（ブロック単位）
trading_range = 3

# 取引クールダウン時間（tick）
trading_cooldown = 20

# 経験値付与の有効/無効
give_experience = true

# デバッグログの出力
debug_logging = false
```

#### 5.2 レシピフィルター
- GUIでの直感的な設定
- アイテムタグによる一括指定
- 取引優先度の設定
- 除外アイテムの指定

## 開発フロー

### 1. 環境構築
```bash
# Java 17インストール
sudo apt update
sudo apt install openjdk-17-jdk

# IntelliJ IDEA Community Edition インストール
sudo snap install intellij-idea-community --classic

# Forge MDK ダウンロード・セットアップ
wget https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.20.1-47.1.0/forge-1.20.1-47.1.0-mdk.zip
```

### 2. プロジェクト作成
```bash
# MDK 展開
unzip forge-1.20.1-47.1.0-mdk.zip -d villager-trading-mod/
cd villager-trading-mod/

# Gradle 設定
./gradlew genIntellijRuns
```

### 3. Claude Code 連携
```bash
# Claude Code インストール
npm install -g @anthropic-ai/claude-code

# プロジェクト内で Claude Code 起動
claude

# デバッグセッション開始
claude > analyze the villager trading system
claude > debug the item drop detection logic
claude > refactor the deployer integration code
```

### 4. 開発・テスト
```bash
# 開発環境でのテスト実行
./gradlew runClient

# Create MOD 連携テスト
./gradlew runServer
```

## Claude Code 活用法

### 1. コード生成
```
Claude Code プロンプト例:
"Create a Minecraft Forge 1.20.1 event handler that detects when items are dropped near villagers and automatically initiates trades. The system should:
- Check if the dropped item matches any villager trade
- Exclude emerald trades for safety
- Respect trading limits and cooldowns
- Provide experience points like normal trades"
```

### 2. デバッグ支援
```
Claude Code デバッグプロンプト:
"Debug this villager trading code - when I drop items near villagers, the trade detection isn't working properly. Here's the error log: [paste error]
The issue seems to be in the item entity detection range. Can you help identify and fix the problem?"
```

### 3. Create MOD 連携
```
Claude Code 連携プロンプト:
"Help me integrate this villager trading system with Create mod's Deployer. The Deployer should be able to:
- Detect villagers in front of it
- Check available trades for the held item
- Execute trades automatically when powered
- Handle item input/output with conveyor belts"
```

## テスト計画

### 1. 単体テスト
- アイテム検出機能
- 村人取引判定
- 取引制限システム
- 経験値付与システム

### 2. 統合テスト
- Create MOD との連携
- 複数村人での同時取引
- 大規模自動化システム
- パフォーマンステスト

### 3. ユーザビリティテスト
- 設定画面の使いやすさ
- エラーハンドリング
- 互換性チェック

## リリース・配布

### 1. ビルド
```bash
# リリースビルド
./gradlew build

# JAR ファイルの確認
ls build/libs/
```

### 2. 配布
- **CurseForge**: メインの配布プラットフォーム
- **Modrinth**: 代替配布プラットフォーム
- **GitHub**: ソースコード・開発版

### 3. ドキュメント
- **README.md**: 基本的な使用方法
- **CHANGELOG.md**: 更新履歴
- **API Documentation**: 他MOD開発者向け

## 注意事項・制限

### 1. 互換性
- Create MOD のバージョンに依存
- 他の村人関連MODとの競合可能性
- Minecraft/Forge バージョンの制限

### 2. パフォーマンス
- 大量の村人・アイテムでの負荷
- ティック処理の最適化が必要
- メモリ使用量の管理

### 3. バランス
- ゲームバランスへの影響
- 自動化による経済システムの変化
- サーバーでの制限設定

## 将来の拡張

### 1. 高度な自動化
- AIによる最適取引選択
- 在庫管理システム
- 需要予測機能

### 2. 他MOD連携
- Applied Energistics 2 連携
- Refined Storage 連携
- Industrial Foregoing 連携

### 3. 管理機能
- 取引統計・分析
- 村人管理GUI
- 自動村人補充システム

---

この仕様書は、効率的で安全な村人取引自動化MODの開発を目的として作成されています。Claude Code を活用することで、開発効率を大幅に向上させ、高品質なコードの作成が可能になります。
