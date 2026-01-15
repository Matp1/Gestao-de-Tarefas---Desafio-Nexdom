import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
// O TasksView criaremos no próximo passo
import TasksView from '../views/TasksView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/tasks',
      name: 'tasks',
      component: TasksView,
      // Guarda de rota: Só entra se tiver token
      beforeEnter: (to, from, next) => {
        if (!localStorage.getItem('token')) next('/')
        else next()
      }
    }
  ]
})

export default router
