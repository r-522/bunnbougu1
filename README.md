# bunnbougu1（文房具業務システムの最小構成）

非エンジニアや初学者でも動かせるように、**フロントエンド（HTML/CSS/JS）**と**バックエンド（Vanilla Java）**を分けた構成です。

## 1. フォルダ構成

- `frontend/` : 画面（ダッシュボード → 商品 → 在庫 → 受注 → 出荷）
- `backend/` : APIサーバー（Controller / Service / Repository / Model）
- `データ/init.sql` : DB初期化SQL

## 2. 事前準備

- Java 17 以上
- Maven
- sqlite3 コマンド（DB初期化に使います）

## 3. DB初期化（`データ/init.sql` を読み込む）

リポジトリ直下で実行してください。

```bash
mkdir -p backend/data
sqlite3 backend/data/app.db < データ/init.sql
```

これで `backend/data/app.db` が作成され、商品・在庫のサンプルデータが入ります。

## 4. バックエンド起動

```bash
cd backend
mvn exec:java
```

起動すると `http://localhost:8080` で API が待ち受けます。

## 5. フロントエンド起動

別ターミナルでリポジトリ直下に戻って以下を実行します。

```bash
python3 -m http.server 5500
```

ブラウザで以下を開きます。

- `http://localhost:5500/frontend/index.html`

## 6. 使い方（業務導線）

1. **ダッシュボード**: 担当者コードでログイン（メールアドレス・パスワードなし）
2. **商品**: 商品マスタを登録
3. **在庫**: 在庫登録や数量調整
4. **受注**: 受注登録と一覧確認
5. **出荷**: 出荷登録と一覧確認

## 7. ログイン方針（簡易セッション）

- `/api/session/login` に担当者コードを送ると、サーバーでトークンを発行
- 以降は `X-Session-Token` ヘッダーでAPI呼び出し
- セッションはサーバー再起動でクリアされる簡易実装

## 8. 主なAPI

- セッション: `POST /api/session/login`
- 商品: `GET/POST/PUT/DELETE /api/products`
- 在庫: `GET/POST/PUT/DELETE /api/inventories`、`POST /api/inventories/{id}/adjust`
- 受注: `GET/POST/PUT/DELETE /api/orders`
- 出荷: `GET/POST/PUT/DELETE /api/shipments`
