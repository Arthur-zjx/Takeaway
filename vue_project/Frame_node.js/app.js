// backend/app.js
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const connection = require('./config/dbConfig');  // Import connection
const authRoutes = require('./routes/auth');

const app = express();

app.use(cors());
app.use(bodyParser.json());

// Mount routes
app.use('/auth', authRoutes);

// Test route
app.get('/testdb', (req, res) => {
    connection.query('SELECT 1', (err, results) => {
        if (err) {
            console.error(err);
            return res.status(500).send('Database query failed');
        }
        res.send('Database connection is normal, query successful');
    });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Backend server started, port: ${PORT}`);
});
