<script setup lang="ts">
/**
 * View de Login.
 * Responsável por autenticar o usuário, processar o token JWT e gerenciar
 * o estado visual de carregamento e mensagens de erro.
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const username = ref('')
const password = ref('')
const isLoading = ref(false)
const errorMessage = ref('')
const router = useRouter()

/**
 * Função principal de autenticação.
 * Valida a estrutura do retorno do backend para salvar o token corretamente como String.
 */
const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    })

    let tokenValue = ''

    // Lógica para extrair a string do token e evitar o erro [object Object]
    if (typeof response.data === 'object' && response.data.token) {
      tokenValue = response.data.token
    } else if (typeof response.data === 'string') {
      tokenValue = response.data
    } else {
      tokenValue = String(response.data)
    }

    localStorage.setItem('token', tokenValue)

    // Delay de segurança para garantir a gravação no LocalStorage
    setTimeout(() => {
      router.push('/tasks')
    }, 100)

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (error) {
    errorMessage.value = 'Usuário ou senha inválidos'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="blob"></div>
    <div class="blob blob-2"></div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo-box">N</div>
        <h1>Bem-vindo de volta</h1>
        <p>Acesse sua conta para gerenciar suas tarefas</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <label for="username">Usuário</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input
              id="username"
              v-model="username"
              type="text"
              placeholder="Digite seu usuário"
              required
            />
          </div>
        </div>

        <div class="input-group">
          <label for="password">Senha</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="••••••••"
              required
            />
          </div>
        </div>

        <transition name="fade">
          <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
        </transition>

        <button type="submit" :disabled="isLoading" class="btn-login">
          <span v-if="!isLoading">Entrar na plataforma</span>
          <span v-else class="loader"></span>
        </button>
      </form>

      <div class="login-footer">
        <p>Esqueceu a senha? <span>Contate o administrador</span></p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f4f8;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', sans-serif;
}

.blob {
  position: absolute;
  width: 500px;
  height: 500px;
  background: #42b983;
  filter: blur(80px);
  opacity: 0.15;
  border-radius: 50%;
  top: -100px;
  right: -100px;
}

.blob-2 {
  background: #34a853;
  bottom: -100px;
  left: -100px;
  top: auto;
  right: auto;
}

.login-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 48px;
  border-radius: 24px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 440px;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-box {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #42b983 0%, #34a853 100%);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 auto 16px;
}

h1 {
  font-size: 1.75rem;
  color: #1e293b;
  font-weight: 800;
  margin-bottom: 8px;
}

.login-header p {
  color: #64748b;
  font-size: 0.95rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  opacity: 0.5;
}

input {
  width: 100%;
  padding: 12px 12px 12px 42px;
  border: 1.5px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}

/* BOTÃO RESTAURADO */
.btn-login {
  margin-top: 10px;
  background: #1e293b;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-login:hover {
  background: #0f172a;
  transform: translateY(-1px);
}

/* RODAPÉ RESTAURADO */
.login-footer {
  margin-top: 32px;
  text-align: center;
  font-size: 0.875rem;
  color: #64748b;
}

.login-footer span {
  color: #42b983;
  font-weight: 600;
  cursor: pointer;
}

.loader {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  display: inline-block;
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
