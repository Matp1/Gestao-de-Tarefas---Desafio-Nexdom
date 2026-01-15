import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import TasksView from '../views/TasksView.vue'

/**
 * Configuração de Roteamento do Vue Router.
 * Define as rotas disponíveis e o comportamento de navegação da aplicação.
 */
const router = createRouter({
  // Utiliza o histórico do navegador para URLs amigáveis (sem o #)
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      // Redirecionamento automático da raiz para o login para garantir fluxo padrão
      redirect: '/login'
    },
    {
      path: '/tasks',
      name: 'tasks',
      component: TasksView
    },
    {
      /**
       * Rota de Perfil.
       * Utiliza carregamento preguiçoso (Lazy Loading) para otimizar a performance.
       * Atualmente aponta para TasksView como fallback estrutural.
       */
      path: '/perfil',
      name: 'perfil',
      component: () => import('../views/TasksView.vue')
    }
  ]
})

/**
 * Guarda Global de Navegação (Navigation Guard).
 * Executado antes de cada mudança de rota para validar a autenticação.
 */
router.beforeEach((to, from, next) => {
  // Recupera o token JWT armazenado no LocalStorage
  const token = localStorage.getItem('token')

  // Regra 1: Bloqueia acesso a rotas privadas se o usuário não tiver um token
  if (to.name !== 'login' && !token) {
    next({ name: 'login' })
  }
  // Regra 2: Se o usuário já está logado, impede que ele volte para a tela de login desnecessariamente
  else if (to.name === 'login' && token) {
    next({ name: 'tasks' })
  }
  // Regra 3: Se as condições acima forem atendidas, permite o prosseguimento da navegação
  else {
    next()
  }
})

export default router
