import axios from 'axios';

const API_URL = "http://localhost:8081/api/auth";

class AuthService {

    // Appel POST vers le backend
    login(email, password) {
        return axios.post(API_URL + "/login", {
            email,
            password
        })
            .then(response => response.data); // ⚡ On retourne directement response.data
    }

    // Sauvegarder les tokens dans le navigateur
    saveTokens(accessToken, refreshToken) {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
    }

    logout() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    }

    getAccessToken() {
        return localStorage.getItem("accessToken");
    }

    getRefreshToken() {
        return localStorage.getItem("refreshToken");
    }
}

export default new AuthService();
