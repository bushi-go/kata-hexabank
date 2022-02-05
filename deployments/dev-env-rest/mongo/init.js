const username = _getEnv('MONGO_USER');
const password = _getEnv('MONGO_PASSWORD');
const authDb = _getEnv('MONGO_AUTH_DB');
const accountDb = _getEnv('MONGO_INITDB_DATABASE');
const port = _getEnv('MONGO_PORT')

const conn = new Mongo(`localhost:${port}`);

db = conn.getDB(authDb)
db.createUser({
    user: username,
    pwd: password,
    roles:[{
        role:"readWrite",
        db: accountDb
    }]
});
const data = [
  {
    "_id": "67489c31-2933-42f2-82c6-dd15f5add478",
    "balance": 936.0,
    "currency":"USD"
  },
  {
    "_id":"e8033e92-4f38-4d2a-b277-918084313949",
    "balance": 2000.0,
    "currency": "EUR"
  }
];
const hexDb = conn.getDB(accountDb);
hexDb.account_record.insertMany(data);