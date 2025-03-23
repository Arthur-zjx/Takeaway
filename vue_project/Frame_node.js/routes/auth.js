// backend/routes/auth.js
const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

// 注册接口
router.post('/register', authController.register);

// 登录接口
router.post('/login', authController.login);

module.exports = router;
