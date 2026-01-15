<script setup lang="ts">
/**
 * Componente Raiz (App Shell).
 * Define a estrutura mestre da aplicação, incluindo a Sidebar global
 * e a área de visualização dinâmica (router-view).
 */
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

/**
 * Lógica Reativa: Controle de visibilidade da Sidebar.
 * A Sidebar não deve ser exibida na tela de login ou se o usuário não estiver autenticado.
 */
const showSidebar = computed(() => {
  return route.path !== '/login' && !!localStorage.getItem('token')
})

/**
 * Gerencia o encerramento da sessão.
 * Remove as credenciais do LocalStorage e redireciona para a autenticação.
 */
const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <aside v-if="showSidebar" class="sidebar">
      <div class="sidebar-header">
        <div class="logo-icon">N</div>
        <span class="logo-text">Nexdom</span>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section">
          <p class="section-title">Menu Principal</p>
          <router-link to="/tasks" class="nav-item" active-class="active">
            <span class="icon">📋</span>
            <span class="label">Tarefas</span>
          </router-link>
        </div>

        <div class="nav-section bottom">
          <button @click="handleLogout" class="nav-item logout">
            <span class="icon">🚪</span>
            <span class="label">Sair</span>
          </button>
        </div>
      </nav>
    </aside>

    <main :class="['main-content', { 'full-width': !showSidebar }]">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
/* Estrutura Base do Shell */
.app-shell {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: #f8fafc;
  overflow: hidden;
}

/* Sidebar Estilizada com Design Moderno */
.sidebar {
  width: 260px;
  background-color: #ffffff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  padding: 24px 16px;
  transition: all 0.3s ease;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px 32px;
}

.logo-icon {
  background: linear-gradient(135deg, #42b983 0%, #34a853 100%);
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
  box-shadow: 0 4px 6px -1px rgba(66, 185, 131, 0.2);
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.section-title {
  font-size: 0.75rem;
  text-transform: uppercase;
  color: #94a3b8;
  font-weight: 600;
  letter-spacing: 0.05em;
  margin-bottom: 12px;
  padding-left: 12px;
}

/* Itens de Menu e Estados de Hover/Active */
.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  color: #64748b;
  text-decoration: none;
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  background: transparent;
  width: 100%;
  cursor: pointer;
  text-align: left;
}

.nav-item:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.nav-item.active {
  background-color: #ecfdf5;
  color: #059669;
}

.icon {
  font-size: 1.2rem;
}

/* Botão de Logout com Destaque de Perigo */
.logout:hover {
  background-color: #fef2f2;
  color: #dc2626;
}

/* Área de Conteúdo Dinâmico e Customização de Scroll */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 40px;
}

.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

/* Estado de Tela Cheia (Ex: Login) */
.full-width {
  width: 100vw;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
