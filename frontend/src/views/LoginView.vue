<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const username = ref('')
const password = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    })
    localStorage.setItem('token', response.data.token)
    router.push('/tasks') // Redireciona após o sucesso
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (error) {
    alert('Erro ao fazer login! Verifique usuário e senha.')
  }
}
</script>

<template>
  <div class="login-card">
    <h2>Login - Gestão de Tarefas</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="username" type="text" placeholder="Usuário" required />
      <input v-model="password" type="password" placeholder="Senha" required />
      <button type="submit">Entrar</button>
    </form>
  </div>
</template>

<style scoped>
.login-card {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  text-align: center;
}
input { display: block; width: 100%; margin-bottom: 10px; padding: 10px; }
button { width: 100%; padding: 10px; background: #42b983; color: white; border: none; cursor: pointer; }
</style>
