<script setup lang="ts">
/**
 * Componente de Notificação (Toast).
 * Gerencia a exibição de mensagens temporárias de sucesso ou erro no canto da tela.
 */
import { ref } from 'vue'

// Definição da estrutura de uma notificação
interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error';
}

const notifications = ref<Notification[]>([])
let nextId = 0

/**
 * Adiciona uma nova notificação à fila.
 * @param message Texto a ser exibido.
 * @param type Tipo da notificação (success ou error) para definição de cores.
 */
const add = (message: string, type: 'success' | 'error' = 'success') => {
  const id = nextId++
  notifications.value.push({ id, message, type })

  // Remove a notificação automaticamente após 3 segundos para não poluir a tela
  setTimeout(() => {
    notifications.value = notifications.value.filter(n => n.id !== id)
  }, 3000)
}

// Expõe a função 'add' para que componentes pais possam disparar notificações
defineExpose({ add })
</script>

<template>
  <div class="toast-wrapper">
    <TransitionGroup name="toast">
      <div v-for="n in notifications" :key="n.id" :class="['toast', n.type]">
        {{ n.message }}
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
/* Container principal das notificações */
.toast-wrapper {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999; /* Garante que fique acima de qualquer outro elemento */
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* Estilização base do balão de notificação */
.toast {
  padding: 12px 24px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  min-width: 200px;
  font-family: 'Inter', sans-serif;
}

/* Variações de cores baseadas no tipo */
.success { background-color: #42b983; }
.error { background-color: #ff7675; }

/* Configurações de Animação do Vue (TransitionGroup) */
.toast-enter-active, .toast-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Estado inicial da animação (surge da direita) */
.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

/* Estado final da animação (encolhe e some) */
.toast-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
