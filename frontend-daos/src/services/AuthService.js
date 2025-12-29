import axios from 'axios';

const API_URL = "http://localhost:8080/api/auth";

class AuthService {

    // Appel POST vers le backend
    login(email, password) {
        return axios.post(API_URL + "/login", {
            email,
            password
        });
    }

    // Sauvegarder l'utilisateur dans le navigateur
    saveUser(token, user) {
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);
    }

    logout() {
        localStorage.removeItem("user");
        localStorage.removeItem("token");
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem("user"));
    }
}

export default new AuthService();