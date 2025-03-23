// backend/config/dbConfig.js
const mysql = require('mysql');

const connection = mysql.createConnection({
    host: 'takeaway-db.cdgms28wqagg.ap-southeast-2.rds.amazonaws.com',
    user: 'admin',
    password: 'Zjx123456',
    database: 'takeaway'
});

connection.connect((err) => {
    if (err) {
        console.error('Database connection failed:', err);
        return;
    }
    console.log('Database connected successfully!');
});

module.exports = connection;
