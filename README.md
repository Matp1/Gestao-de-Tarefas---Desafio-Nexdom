# Gestão de Tarefas - Desafio Nexdom

Este projeto é uma aplicação completa de gerenciamento de tarefas (To-Do List) desenvolvida como desafio técnico para a **Nexdom**. A solução foca em segurança, persistência de dados e integração entre diferentes tecnologias.

---

## ⏱️ Horas de desenvolvimento: 6 Horas e 20 Minutos

---

## 🛠️ Tecnologias e Arquitetura

O sistema foi construído utilizando uma arquitetura moderna e escalável:

### **Backend (Spring Boot 3)**
- **Java 17**: Versão LTS para estabilidade e performance.
- **Spring Security + JWT**: Fluxo de autenticação *stateless* para proteção de rotas.
- **Spring Data JPA**: Gerenciamento eficiente da camada de persistência.
- **PostgreSQL**: Banco de dados relacional robusto.
- **Lombok**: Código mais limpo e legível.

### **Frontend (Vue.js 3)**
- **TypeScript**: Desenvolvimento tipado para evitar erros em tempo de execução.
- **Composition API**: Organização lógica superior e reutilização de código.
- **Axios Interceptors**: Injeção automática do token JWT em cada requisição.
- **Vue Router**: Controle de navegação e guardas de rota (Navigation Guards).
- **CSS3 Personalizado**: Interface moderna com foco em UX (Toasts, Glassmorphism).
- **MicroFrontend**: Interface modularizada com shell integrado e esquema de rotas

---

## 📋 Funcionalidades Principais (Requisitos do Desafio)

### 1. 🔐 Autenticação com JWT
Acesso restrito via Token. O frontend gerencia o ciclo de vida do token no `localStorage`, garantindo que apenas usuários autorizados visualizem as tarefas.

- **Login Mock**
  - Usuário: `admin`
  - Senha: `admin123`

### 2. 🌐 Integração com API Externa
Demonstração de interoperabilidade: Caso uma tarefa seja criada sem descrição, o backend consome a API **JSONPlaceholder** (`/posts/1`) para preencher automaticamente o campo com uma sugestão externa.

### 3. 💾 Persistência com Docker Volumes
Diferente de containers temporários, este projeto utiliza **Volumes Nomeados** no Docker Compose. Isso garante que, mesmo após um `docker-compose down`, suas tarefas permaneçam salvas no banco de dados físico.

### 4. ⚡ Gestão de Tarefas (CRUD)
- **Criação**: Título, descrição opcional e data de prazo.
- **Edição**: Alteração de status e textos em tempo real.
- **Exclusão**: Remoção segura com confirmação.
- **Busca**: Filtro instantâneo por título enquanto o usuário digita.

### 5. 🕒 Lógica de Prazos e Status
O sistema calcula automaticamente se uma tarefa está atrasada comparando a `dueDate` com a data atual, destacando visualmente os itens pendentes críticos.

---

## 🐳 Como Rodar a Aplicação

A aplicação está totalmente dockerizada. Para subir o ambiente completo, basta ter o Docker instalado:

1. Clone este repositório.
2. Na raiz do projeto, execute:
   ```bash
   docker-compose up --build
   ```

3. Acesse a interface web em:
   - http://localhost

---

## 📂 Estrutura de Containers

- **vue_frontend**: Servidor Nginx servindo o app Vue (Porta 80).
- **spring_backend**: API REST Java (Porta 8080).
- **task_db**: Banco PostgreSQL 15 (Porta 5432).

---

Desenvolvido com foco em qualidade técnica para o processo seletivo **Nexdom**.

**Autor:** Matheus Paiva
