<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'
import ToastNotification from '../components/ToastNotification.vue'

// Interface flexível para evitar erros de compilação
interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  createdAt?: string;
  dueDate?: string | null;
}

// Estado reativo
const tasks = ref<Task[]>([])
const searchQuery = ref('')
const newTaskTitle = ref('')
const newTaskDescription = ref('')
const newTaskDueDate = ref('')
const isLoading = ref(false)

// Tipagem segura para a referência do Toast
const toastRef = ref<{ add: (msg: string, type?: 'success' | 'error') => void } | null>(null)

// Formatação de data com tratamento de nulos e fuso horário
const formatDate = (dateStr: string | undefined | null): string => {
  if (!dateStr) return 'Sem data'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return 'Data inválida'
  // Ajuste para evitar que a data "volte um dia" devido ao fuso horário
  return new Date(date.getTime() + date.getTimezoneOffset() * 60000).toLocaleDateString('pt-BR')
}

// Lógica de atraso
const isOverdue = (dueDate: string | undefined | null): boolean => {
  if (!dueDate) return false

  const todayDate = new Date()
  const todayStr = todayDate.toISOString().split('T')[0]

  // Garantimos que todayStr nunca seja undefined antes da comparação
  return todayStr ? dueDate < todayStr : false
}

// Filtro e Ordenação Reativa
const filteredTasks = computed(() => {
  const list = tasks.value.filter(t =>
    t.title.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
  return list.sort((a, b) => {
    if (a.status === 'PENDENTE' && b.status === 'CONCLUIDA') return -1
    if (a.status === 'CONCLUIDA' && b.status === 'PENDENTE') return 1
    return 0
  })
})

const fetchTasks = async () => {
  isLoading.value = true
  try {
    const response = await api.get('/tasks')
    tasks.value = response.data
  } catch {
    toastRef.value?.add('Erro ao carregar tarefas.', 'error')
  } finally {
    isLoading.value = false
  }
}

const addTask = async () => {
  if (!newTaskTitle.value.trim()) {
    toastRef.value?.add('O título é obrigatório!', 'error')
    return
  }
  try {
    await api.post('/tasks', {
      title: newTaskTitle.value,
      description: newTaskDescription.value,
      dueDate: newTaskDueDate.value || null,
      status: 'PENDENTE'
    })
    newTaskTitle.value = ''
    newTaskDescription.value = ''
    newTaskDueDate.value = ''
    toastRef.value?.add('Tarefa adicionada com sucesso!')
    fetchTasks()
  } catch {
    toastRef.value?.add('Erro ao criar tarefa.', 'error')
  }
}

const editTask = async (task: Task) => {
  const newTitle = prompt('Editar título:', task.title)
  if (newTitle === null) return
  const newDescription = prompt('Editar descrição:', task.description)
  if (newDescription === null) return

  try {
    await api.put(`/tasks/${task.id}`, {
      ...task,
      title: newTitle.trim() || task.title,
      description: newDescription.trim()
    })
    toastRef.value?.add('Tarefa atualizada!')
    fetchTasks()
  } catch {
    toastRef.value?.add('Erro ao editar tarefa.', 'error')
  }
}

const toggleStatus = async (task: Task) => {
  const newStatus = task.status === 'PENDENTE' ? 'CONCLUIDA' : 'PENDENTE'
  try {
    await api.put(`/tasks/${task.id}`, { ...task, status: newStatus })
    toastRef.value?.add(newStatus === 'CONCLUIDA' ? 'Concluída! 🎉' : 'Tarefa reaberta.')
    fetchTasks()
  } catch {
    toastRef.value?.add('Erro ao atualizar status.', 'error')
  }
}

const deleteTask = async (id: number) => {
  if (confirm('Deseja excluir esta tarefa?')) {
    try {
      await api.delete(`/tasks/${id}`)
      toastRef.value?.add('Tarefa removida.', 'error')
      fetchTasks()
    } catch {
      toastRef.value?.add('Erro ao excluir tarefa.', 'error')
    }
  }
}

onMounted(fetchTasks)
</script>

<template>
  <div class="tasks-container">
    <ToastNotification ref="toastRef" />

    <header class="header">
      <h1>Nexdom Tasks</h1>
      <p>Gestão Eficiente de Projetos</p>
    </header>

    <section class="search-section">
      <input v-model="searchQuery" type="text" placeholder="🔍 Buscar tarefa..." class="input-search" />
    </section>

    <section class="add-task-form">
      <div class="form-group">
        <input v-model="newTaskTitle" type="text" placeholder="Título da tarefa" class="input-title" />

        <div class="date-input-group">
          <label>Data de Prazo:</label>
          <input v-model="newTaskDueDate" type="date" class="input-date" />
        </div>

        <textarea v-model="newTaskDescription" placeholder="Descrição da tarefa (opcional)" class="input-desc"></textarea>
        <button @click="addTask" :disabled="isLoading" class="btn-add">
          {{ isLoading ? 'Enviando...' : 'Adicionar Tarefa' }}
        </button>
      </div>
    </section>

    <section class="tasks-list">
      <div v-if="isLoading && tasks.length === 0" class="loading-text">Sincronizando...</div>

      <ul>
        <li v-for="task in filteredTasks" :key="task.id" :class="{ completed: task.status === 'CONCLUIDA' }">
          <div class="task-content">
            <div class="task-header">
              <span class="title">{{ task.title }}</span>
              <span class="badge" :class="task.status.toLowerCase()">{{ task.status }}</span>
            </div>
            <p v-if="task.description" class="desc">{{ task.description }}</p>

            <div class="task-footer">
              <span class="date-tag">🕒 Criada: {{ formatDate(task.createdAt) }}</span>
              <span v-if="task.dueDate" class="date-tag" :class="{ 'overdue': isOverdue(task.dueDate) && task.status === 'PENDENTE' }">
                🏁 Prazo: {{ formatDate(task.dueDate) }}
              </span>
            </div>
          </div>

          <div class="actions">
            <button @click="toggleStatus(task)" class="btn-action" title="Concluir/Reabrir">
              {{ task.status === 'PENDENTE' ? '✔️' : '↩️' }}
            </button>
            <button @click="editTask(task)" class="btn-action" title="Editar">✏️</button>
            <button @click="deleteTask(task.id)" class="btn-action btn-del" title="Excluir">🗑️</button>
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
/* Estilos consolidados para garantir consistência visual */
.tasks-container { max-width: 700px; margin: 40px auto; padding: 0 20px; font-family: 'Inter', sans-serif; }
.header { text-align: center; margin-bottom: 30px; }
.header h1 { color: #2c3e50; margin: 0; }
.header p { color: #95a5a6; margin: 5px 0 0; }

.search-section { margin-bottom: 20px; }
.input-search { width: 100%; padding: 12px; border: 2px solid #edf2f7; border-radius: 10px; box-sizing: border-box; font-size: 1rem; }
.input-search:focus { border-color: #42b983; outline: none; }

.add-task-form { background: #fff; padding: 20px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); margin-bottom: 30px; border: 1px solid #f0f0f0; }
.form-group { display: flex; flex-direction: column; gap: 12px; }
.input-title { padding: 12px; border: 1px solid #e2e8f0; border-radius: 6px; font-weight: 600; font-size: 1rem; }
.input-desc { padding: 12px; border: 1px solid #e2e8f0; border-radius: 6px; min-height: 70px; font-family: inherit; }

.date-input-group { display: flex; flex-direction: column; gap: 4px; }
.date-input-group label { font-size: 0.75rem; font-weight: bold; color: #718096; text-transform: uppercase; }
.input-date { padding: 8px; border: 1px solid #e2e8f0; border-radius: 6px; color: #4a5568; }

.btn-add { background: #42b983; color: white; border: none; padding: 14px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-add:hover { background: #38a169; }

li { background: white; margin-bottom: 15px; padding: 20px; border-radius: 12px; display: flex; justify-content: space-between; border-left: 6px solid #42b983; box-shadow: 0 2px 4px rgba(0,0,0,0.04); }
li.completed { border-left-color: #cbd5e0; opacity: 0.7; }

.task-content { flex: 1; padding-right: 15px; }
.task-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.title { font-size: 1.1rem; font-weight: 700; color: #2d3748; }

.badge { font-size: 0.65rem; padding: 4px 8px; border-radius: 20px; text-transform: uppercase; font-weight: 800; }
.badge.pendente { background: #feebc8; color: #9c4221; }
.badge.concluida { background: #c6f6d5; color: #22543d; }

.desc { font-size: 0.9rem; color: #4a5568; line-height: 1.5; margin: 0; }

.task-footer { margin-top: 15px; display: flex; gap: 15px; border-top: 1px solid #edf2f7; padding-top: 10px; }
.date-tag { font-size: 0.7rem; color: #718096; font-weight: 500; }
.overdue { color: #e53e3e; font-weight: 700; }

.actions { display: flex; gap: 8px; align-self: center; }
.btn-action { border: none; background: #edf2f7; padding: 10px; border-radius: 8px; cursor: pointer; transition: 0.2s; font-size: 1rem; }
.btn-action:hover { background: #e2e8f0; }
.btn-del:hover { background: #fed7d7; color: #c53030; }

.loading-text { text-align: center; color: #42b983; padding: 20px; font-weight: bold; }
</style>
