<script setup lang="ts">
import { ref } from 'vue'

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error';
}

const notifications = ref<Notification[]>([])
let nextId = 0

const add = (message: string, type: 'success' | 'error' = 'success') => {
  const id = nextId++
  notifications.value.push({ id, message, type })

  // Remove automaticamente após 3 segundos
  setTimeout(() => {
    notifications.value = notifications.value.filter(n => n.id !== id)
  }, 3000)
}

// Expõe a função para ser usada pelo componente pai
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
.toast-wrapper {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast {
  padding: 12px 24px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  min-width: 200px;
}

.success { background-color: #42b983; }
.error { background-color: #ff7675; }

/* Animação do Vue */
.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from { opacity: 0; transform: translateX(30px); }
.toast-leave-to { opacity: 0; transform: scale(0.9); }
</style>
