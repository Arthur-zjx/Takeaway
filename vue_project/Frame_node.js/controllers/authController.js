// backend/controllers/authController.js
const bcrypt = require('bcrypt');
const connection = require('../config/dbConfig');  // Reuse the same connection

const saltRounds = 10;

// Registration logic
exports.register = (req, res) => {
    const { username, password, email } = req.body;
    if (!username || !password || !email) {
        return res.status(400).json({ message: 'Missing required registration information' });
    }

    // Encrypt the password
    bcrypt.hash(password, saltRounds, (err, hash) => {
        if (err) {
            console.error('Password encryption failed:', err);
            return res.status(500).json({ message: 'Server error' });
        }

        const sql = 'INSERT INTO users (username, password, email) VALUES (?, ?, ?)';
        connection.query(sql, [username, hash, email], (error, results) => {
            if (error) {
                console.error('Database error:', error);
                return res.status(500).json({ message: 'Registration failed' });
            }
            return res.status(200).json({ message: 'Registration successful' });
        });
    });
};

// Login logic
exports.login = (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ message: 'Missing required login information' });
    }

    const sql = 'SELECT * FROM users WHERE username = ?';
    connection.query(sql, [username], (error, results) => {
        if (error) {
            console.error('Database error:', error);
            return res.status(500).json({ message: 'Login failed' });
        }
        if (results.length === 0) {
            return res.status(401).json({ message: 'User does not exist' });
        }

        const user = results[0];
        bcrypt.compare(password, user.password, (err, isMatch) => {
            if (err) {
                console.error('Password comparison error:', err);
                return res.status(500).json({ message: 'Server error' });
            }
            if (!isMatch) {
                return res.status(401).json({ message: 'Incorrect password' });
            }

            // Login successful, return token or other information
            return res.status(200).json({ message: 'Login successful', userId: user.id });
        });
    });
};
