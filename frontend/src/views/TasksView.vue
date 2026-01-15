<script setup lang="ts">
/**
 * View Principal de Gestão de Tarefas.
 * Centraliza as operações de CRUD, filtragem em tempo real, lógica de prazos
 * e notificações de feedback para o usuário.
 */
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'
import ToastNotification from '../components/ToastNotification.vue'

// Interface para garantir tipagem forte e evitar erros de propriedade indefinida
interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  createdAt?: string;
  dueDate?: string | null;
}

// Estados reativos para dados e formulários
const tasks = ref<Task[]>([])
const searchQuery = ref('')
const newTaskTitle = ref('')
const newTaskDescription = ref('')
const newTaskDueDate = ref('')
const isLoading = ref(false)

// Referência para acessar os métodos do componente ToastNotification
const toastRef = ref<{ add: (msg: string, type?: 'success' | 'error') => void } | null>(null)

/**
 * Formata datas vindas do backend (ISO) para o padrão brasileiro (DD/MM/YYYY).
 * Inclui correção de fuso horário para evitar divergências na exibição.
 */
const formatDate = (dateStr: string | undefined | null): string => {
  if (!dateStr) return 'Sem data'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return 'Data inválida'
  // Ajuste para compensar o fuso horário local e manter a data correta
  return new Date(date.getTime() + date.getTimezoneOffset() * 60000).toLocaleDateString('pt-BR')
}

/**
 * Verifica se uma tarefa está atrasada comparando o prazo com a data atual.
 */
const isOverdue = (dueDate: string | undefined | null): boolean => {
  if (!dueDate) return false
  const todayStr = new Date().toISOString().split('T')[0]
  return todayStr ? dueDate < todayStr : false
}

/**
 * Propriedade Computada: Filtra as tarefas por título e as ordena.
 * Tarefas 'PENDENTE' aparecem primeiro para priorizar o fluxo de trabalho.
 */
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

/**
 * Busca a lista de tarefas atualizada do servidor.
 */
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

/**
 * Envia uma nova tarefa para o backend.
 */
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
    // Reseta o formulário após sucesso
    newTaskTitle.value = ''
    newTaskDescription.value = ''
    newTaskDueDate.value = ''
    toastRef.value?.add('Tarefa adicionada com sucesso!')
    fetchTasks()
  } catch {
    toastRef.value?.add('Erro ao criar tarefa.', 'error')
  }
}

/**
 * Abre diálogos nativos para edição rápida de dados da tarefa.
 */
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

/**
 * Alterna entre os status PENDENTE e CONCLUIDA.
 */
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

/**
 * Remove uma tarefa após confirmação do usuário.
 */
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

// Inicializa a busca de dados ao montar o componente
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

        <textarea v-model="newTaskDescription" placeholder="Descrição (se vazia, gerará sugestão automática)" class="input-desc"></textarea>

        <button @click="addTask" :disabled="isLoading" class="btn-add">
          {{ isLoading ? 'Enviando...' : 'Adicionar Tarefa' }}
        </button>
      </div>
    </section>

    <section class="tasks-list">
      <div v-if="isLoading && tasks.length === 0" class="loading-text">Sincronizando com servidor...</div>

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
/* Layout e Tipografia */
.tasks-container { max-width: 700px; margin: 40px auto; padding: 0 20px; font-family: 'Inter', sans-serif; }
.header { text-align: center; margin-bottom: 30px; }
.header h1 { color: #2c3e50; font-weight: 800; letter-spacing: -1px; }
.header p { color: #95a5a6; font-size: 0.9rem; }

/* Estilização de Inputs */
.input-search { width: 100%; padding: 14px; border: 2px solid #edf2f7; border-radius: 12px; font-size: 1rem; transition: 0.3s; }
.input-search:focus { border-color: #42b983; outline: none; box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1); }

/* Card de Formulário */
.add-task-form { background: #fff; padding: 24px; border-radius: 16px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05); margin-bottom: 30px; border: 1px solid #f0f0f0; }
.form-group { display: flex; flex-direction: column; gap: 14px; }
.input-title { padding: 12px; border: 1.5px solid #e2e8f0; border-radius: 8px; font-weight: 600; }
.input-desc { padding: 12px; border: 1.5px solid #e2e8f0; border-radius: 8px; min-height: 80px; resize: vertical; }

.date-input-group { display: flex; flex-direction: column; gap: 6px; }
.date-input-group label { font-size: 0.7rem; font-weight: 800; color: #a0aec0; text-transform: uppercase; }
.input-date { padding: 10px; border: 1.5px solid #e2e8f0; border-radius: 8px; }

.btn-add { background: #42b983; color: white; border: none; padding: 16px; border-radius: 10px; font-weight: 700; cursor: pointer; transition: 0.3s; }
.btn-add:hover { background: #38a169; transform: translateY(-1px); }

/* Estilização dos Itens da Lista */
li { background: white; margin-bottom: 16px; padding: 20px; border-radius: 14px; display: flex; justify-content: space-between; border-left: 6px solid #42b983; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02); transition: 0.3s; }
li.completed { border-left-color: #cbd5e0; opacity: 0.6; }

.task-header { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.title { font-size: 1.15rem; font-weight: 700; color: #1a202c; }

/* Status Badges */
.badge { font-size: 0.6rem; padding: 4px 10px; border-radius: 12px; font-weight: 900; }
.badge.pendente { background: #fffaf0; color: #9c4221; border: 1px solid #feebc8; }
.badge.concluida { background: #f0fff4; color: #22543d; border: 1px solid #c6f6d5; }

.desc { font-size: 0.95rem; color: #4a5568; margin: 8px 0; }
.task-footer { margin-top: 14px; display: flex; gap: 16px; border-top: 1px solid #f7fafc; padding-top: 12px; }
.date-tag { font-size: 0.75rem; color: #718096; }
.overdue { color: #e53e3e; font-weight: 800; }

/* Botões de Ação */
.actions { display: flex; gap: 8px; }
.btn-action { border: none; background: #f7fafc; padding: 10px; border-radius: 10px; cursor: pointer; transition: 0.2s; }
.btn-action:hover { background: #edf2f7; transform: scale(1.1); }
.btn-del:hover { background: #fff5f5; color: #c53030; }

.loading-text { text-align: center; color: #42b983; padding: 30px; font-weight: 600; }
</style>
