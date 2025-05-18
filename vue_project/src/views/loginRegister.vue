<template>
  <div class="login-register">
    <div class="contain">
      <div class="big-box" :class="{active:isLogin}">
        <!-- 登录区域 -->
        <div class="big-contain" key="bigContainLogin" v-if="isLogin">
          <div class="btitle">Account Login</div>
          <div class="bform">
            <!-- 修改为输入用户名 -->
            <input type="text" placeholder="Username" v-model="form.username">
            <span class="errTips" v-if="emailError">* Incorrect username *</span>
            <input type="password" placeholder="Password" v-model="form.userpwd">
            <span class="errTips" v-if="passwordError">* Incorrect password *</span>
          </div>
          <button class="bbutton" @click="login">Login</button>
          <!-- Google Login Button -->
          <button class="gsi-material-button" @click="googleLogin">
            <div class="gsi-material-button-state"></div>
            <div class="gsi-material-button-content-wrapper">
              <div class="gsi-material-button-icon">
                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" style="display: block;">
                  <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path>
                  <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path>
                  <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path>
                  <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path>
                  <path fill="none" d="M0 0h48v48H0z"></path>
                </svg>
              </div>
              <span class="gsi-material-button-contents">Sign in with Google</span>
              <span style="display: none;">Sign in with Google</span>
            </div>
          </button>
        </div>
        <!-- 注册区域 -->
        <div class="big-contain" key="bigContainRegister" v-else>
          <div class="btitle">Create Account</div>
          <div class="bform">
            <input type="text" placeholder="Username" v-model="form.username">
            <span class="errTips" v-if="existed">* Username already exists! *</span>
            <input type="email" placeholder="Email" v-model="form.useremail">
            <input type="password" placeholder="Password" v-model="form.userpwd">
          </div>
          <button class="bbutton" @click="register">Register</button>
        </div>
      </div>
      <!-- 切换面板 -->
      <div class="small-box" :class="{active:isLogin}">
        <div class="small-contain" key="smallContainRegister" v-if="isLogin">
          <div class="stitle">Hello, Friend!</div>
          <p class="scontent">Sign up and start your journey with us</p>
          <button class="sbutton" @click="changeType">Register</button>
        </div>
        <div class="small-contain" key="smallContainLogin" v-else>
          <div class="stitle">Welcome Back!</div>
          <p class="scontent">Stay connected with us, please log in to your account</p>
          <button class="sbutton" @click="changeType">Login</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const isLogin = ref(true)
const emailError = ref(false)
const passwordError = ref(false)
const existed = ref(false)

const form = reactive({
  username: '',
  useremail: '',
  userpwd: ''
})

const changeType = () => {
  isLogin.value = !isLogin.value
  form.username = ''
  form.useremail = ''
  form.userpwd = ''
  emailError.value = false
  passwordError.value = false
  existed.value = false
}

const login = async () => {
  if (!form.username || !form.userpwd) {
    return ElMessage.warning('Username and password are required!')
  }

  // 1. Try admin login
  try {
    const resAdmin = await axios.post(
        '/api/admin/login',
        { username: form.username, password: form.userpwd },
        { withCredentials: true }
    )
    const { token, role } = resAdmin.data
    if (role === 'ADMIN') {
      // Store admin token and inject into axios headers
      sessionStorage.setItem('ADMIN_TOKEN', token)
      axios.defaults.headers.common.Authorization = `Bearer ${token}`
      // Clear any user token
      localStorage.removeItem('userToken')
      // Mark as logged in
      localStorage.setItem('isLoggedIn', 'true')
      ElMessage.success('Admin login successful!')
      // Navigate to dashboard, clearing query params
      return router.replace({ name: 'Dashboard', query: {} })
    }
  } catch (err) {
    if (err.response?.status !== 401) {
      console.error(err)
      return ElMessage.error('Admin login error')
    }
    // 401 falls through to user login
  }

  // 2. Regular user login
  try {
    const resUser = await axios.post(
        '/auth/login',
        { username: form.username, password: form.userpwd }
    )
    const userToken = resUser.data.token
    localStorage.setItem('userToken', userToken)
    axios.defaults.headers.common.Authorization = `Bearer ${userToken}`
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('username', resUser.data.username)
    localStorage.setItem('userRole', resUser.data.role)
    localStorage.setItem('userId', resUser.data.id)

    ElMessage.success(resUser.data.message || 'Login successful!')
    return router.replace({ name: 'UserHome', query: {} })
  } catch (err) {
    console.error(err)
    passwordError.value = true
    ElMessage.error(err.response?.data?.error || 'Login failed')
  }
}

const register = async () => {
  if (form.username && form.useremail && form.userpwd) {
    try {
      const res = await axios.post(
          '/auth/register',
          { username: form.username, email: form.useremail, password: form.userpwd }
      )
      ElMessage.success(res.data.message || 'Registration successful!')
      changeType()
    } catch (err) {
      console.error(err)
      existed.value = true
      ElMessage.error(err.response?.data?.error || 'Registration failed')
    }
  } else {
    ElMessage.warning('All fields must be filled!')
  }
}

// Google login redirect
const googleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}
</script>








<style scoped>
/* 保持原有样式不变 */
.login-register {
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
}

.contain {
  width: 60%;
  height: 60%;
  position: relative;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 0 3px #f0f0f0,
  0 0 6px #f0f0f0;
}

.big-box {
  width: 70%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 30%;
  transform: translateX(0%);
  transition: all 1s;
}

.big-contain {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.btitle {
  font-size: 1.5em;
  font-weight: bold;
  color: rgb(57, 167, 176);
}

.bform {
  width: 100%;
  height: 40%;
  padding: 2em 0;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
}

.bform .errTips {
  display: block;
  width: 50%;
  text-align: left;
  color: red;
  font-size: 0.7em;
  margin-left: 1em;
}

.bform input {
  width: 50%;
  height: 30px;
  border: none;
  outline: none;
  border-radius: 10px;
  padding-left: 2em;
  background-color: #f0f0f0;
}

.bbutton {
  width: 20%;
  height: 40px;
  border-radius: 24px;
  border: none;
  outline: none;
  background-color: rgb(57, 167, 176);
  color: #fff;
  font-size: 0.9em;
  cursor: pointer;
}

.google-button {
  width: 20%;
  height: 40px;
  border-radius: 24px;
  border: none;
  outline: none;
  background-color: #fff;
  color: #000;
  font-size: 0.9em;
  cursor: pointer;
  margin-top: 1em;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 3px #ccc;
}

.google-button img {
  height: 20px;
  width: 20px;
  margin-right: 0.5em;
}

.small-box {
  width: 30%;
  height: 100%;
  background: linear-gradient(135deg, rgb(57, 167, 176), rgb(56, 183, 145));
  position: absolute;
  top: 0;
  left: 0;
  transform: translateX(0%);
  transition: all 1s;
  border-top-left-radius: inherit;
  border-bottom-left-radius: inherit;
}

.small-contain {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.stitle {
  font-size: 1.5em;
  font-weight: bold;
  color: #fff;
}

.scontent {
  font-size: 0.8em;
  color: #fff;
  text-align: center;
  padding: 2em 4em;
  line-height: 1.7em;
}

.sbutton {
  width: 60%;
  height: 40px;
  border-radius: 24px;
  border: 1px solid #fff;
  outline: none;
  background-color: transparent;
  color: #fff;
  font-size: 0.9em;
  cursor: pointer;
}

.big-box.active {
  left: 0;
  transition: all 0.5s;
}

.small-box.active {
  left: 100%;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: inherit;
  border-bottom-right-radius: inherit;
  transform: translateX(-100%);
  transition: all 1s;
}

.gsi-material-button {
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
  -webkit-appearance: none;
  background-color: WHITE;
  background-image: none;
  border: 1px solid #747775;
  -webkit-border-radius: 20px;
  border-radius: 20px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  color: #1f1f1f;
  cursor: pointer;
  font-family: 'Roboto', arial, sans-serif;
  font-size: 14px;
  height: 40px;
  letter-spacing: 0.25px;
  outline: none;
  overflow: hidden;
  padding: 0 12px;
  position: relative;
  top: 25px;
  text-align: center;
  -webkit-transition: background-color .218s, border-color .218s, box-shadow .218s;
  transition: background-color .218s, border-color .218s, box-shadow .218s;
  vertical-align: middle;
  white-space: nowrap;
  width: auto;
  max-width: 400px;
  min-width: min-content;
}

.gsi-material-button .gsi-material-button-icon {
  height: 20px;
  margin-right: 12px;
  min-width: 20px;
  width: 20px;
}

.gsi-material-button .gsi-material-button-content-wrapper {
  -webkit-align-items: center;
  align-items: center;
  display: flex;
  -webkit-flex-direction: row;
  flex-direction: row;
  -webkit-flex-wrap: nowrap;
  flex-wrap: nowrap;
  height: 100%;
  justify-content: space-between;
  position: relative;
  width: 100%;
}

.gsi-material-button .gsi-material-button-contents {
  -webkit-flex-grow: 1;
  flex-grow: 1;
  font-family: 'Roboto', arial, sans-serif;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: top;
}

.gsi-material-button .gsi-material-button-state {
  -webkit-transition: opacity .218s;
  transition: opacity .218s;
  bottom: 0;
  left: 0;
  opacity: 0;
  position: absolute;
  right: 0;
  top: 0;
}

.gsi-material-button:disabled {
  cursor: default;
  background-color: #ffffff61;
  border-color: #1f1f1f1f;
}

.gsi-material-button:disabled .gsi-material-button-contents {
  opacity: 38%;
}

.gsi-material-button:disabled .gsi-material-button-icon {
  opacity: 38%;
}

.gsi-material-button:not(:disabled):active .gsi-material-button-state,
.gsi-material-button:not(:disabled):focus .gsi-material-button-state {
  background-color: #303030;
  opacity: 12%;
}

.gsi-material-button:not(:disabled):hover {
  -webkit-box-shadow: 0 1px 2px 0 rgba(60, 64, 67, .30), 0 1px 3px 1px rgba(60, 64, 67, .15);
  box-shadow: 0 1px 2px 0 rgba(60, 64, 67, .30), 0 1px 3px 1px rgba(60, 64, 67, .15);
}

.gsi-material-button:not(:disabled):hover .gsi-material-button-state {
  background-color: #303030;
  opacity: 8%;
}
</style>
