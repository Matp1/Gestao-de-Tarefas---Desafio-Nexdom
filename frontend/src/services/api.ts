import axios from 'axios';

/**
 * Configuração central do Axios para comunicação com a API.
 * Centraliza a URL base e as configurações de cabeçalho comuns.
 */
const api = axios.create({
  // Define a URL base para o backend.
  // Em ambiente Docker, aponta para a porta exposta 8080.
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Interceptor de Requisição.
 * Este bloco de código é executado automaticamente antes de cada chamada à API.
 * Ele verifica a existência de um token no LocalStorage e o injeta no Header.
 */
api.interceptors.request.use((config) => {
  // Recupera o token JWT que foi armazenado durante o login
  const token = localStorage.getItem('token');

  if (token) {
    /**
     * Adiciona o cabeçalho de autorização padrão RFC 6750.
     * O formato 'Bearer <token>' é o esperado pelo JwtAuthenticationFilter no backend.
     */
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  // Trata erros que ocorrem antes mesmo da requisição sair do cliente
  return Promise.reject(error);
});

export default api;
