import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import federation from "@originjs/vite-plugin-federation"

export default defineConfig({
  plugins: [
    vue(),
    federation({
      name: 'tasks_remote', // Nome do microfrontend
      filename: 'remoteEntry.js', // Arquivo que o Shell irá ler
      exposes: {
        // Expondo a View de tarefas como um módulo independente
        './TasksList': './src/views/TasksView.vue',
      },
      shared: ['vue', 'vue-router', 'axios'] // Compartilha bibliotecas para evitar duplicidade
    })
  ],
  build: {
    target: 'esnext', // Necessário para suporte a Top-level await do federation
    minify: false,
    cssCodeSplit: false
  }
})
