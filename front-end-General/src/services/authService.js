import { API_ENDPOINTS } from '../config/api';

class AuthService {
  /**
   * Authentifier un utilisateur
   * @param {Object} credentials - { email, password }
   * @returns {Promise<Object>} - User data and tokens
   */
  async login(credentials) {
    try {
      const response = await fetch(API_ENDPOINTS.AUTH.LOGIN, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Erreur de connexion' }));
        throw new Error(error.message || 'Identifiants invalides');
      }

      const data = await response.json();

      // Stocker les tokens et les informations utilisateur
      this.setTokens(data.access_token, data.refresh_token);
      this.setUser(data.user);

      return {
        user: {
          id: data.user.id,
          email: data.user.email,
          nom: data.user.nom,
          prenom: data.user.prenom,
          role: data.user.role,
          name: `${data.user.prenom} ${data.user.nom}`
        },
        accessToken: data.access_token,
        refreshToken: data.refresh_token
      };
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  /**
   * Rafraîchir le token d'accès
   * @returns {Promise<string>} - New access token
   */
  async refreshToken() {
    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    try {
      const response = await fetch(API_ENDPOINTS.AUTH.REFRESH, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken }),
      });

      if (!response.ok) {
        throw new Error('Failed to refresh token');
      }

      const data = await response.json();
      this.setTokens(data.access_token, data.refresh_token);

      return data.access_token;
    } catch (error) {
      console.error('Refresh token error:', error);
      this.logout();
      throw error;
    }
  }

  /**
   * Déconnecter l'utilisateur
   */
  async logout() {
    const token = this.getAccessToken();

    if (token) {
      try {
        await fetch(API_ENDPOINTS.AUTH.LOGOUT, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
      } catch (error) {
        console.error('Logout error:', error);
      }
    }

    this.clearAuth();
  }

  /**
   * Stocker les tokens
   */
  setTokens(accessToken, refreshToken) {
    localStorage.setItem('access_token', accessToken);
    if (refreshToken) {
      localStorage.setItem('refresh_token', refreshToken);
    }
  }

  /**
   * Stocker les informations utilisateur
   */
  setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  /**
   * Récupérer le token d'accès
   */
  getAccessToken() {
    return localStorage.getItem('access_token');
  }

  /**
   * Récupérer le refresh token
   */
  getRefreshToken() {
    return localStorage.getItem('refresh_token');
  }

  /**
   * Récupérer les informations utilisateur
   */
  getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Vérifier si l'utilisateur est authentifié
   */
  isAuthenticated() {
    return !!this.getAccessToken();
  }

  /**
   * Nettoyer toutes les données d'authentification
   */
  clearAuth() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
  }

  /**
   * Valider le token actuel
   */
  async validateToken() {
    const token = this.getAccessToken();

    if (!token) {
      return false;
    }

    try {
      const response = await fetch(API_ENDPOINTS.AUTH.BASE + '/validate', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      return response.ok;
    } catch (error) {
      console.error('Token validation error:', error);
      return false;
    }
  }
}

export default new AuthService();
