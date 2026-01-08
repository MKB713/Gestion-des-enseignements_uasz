import React, { useState, useEffect } from 'react';
import { Calendar as CalendarIcon, Plus } from 'lucide-react';
import Timetable from '../../components/Timetable';
import { apiRequest, API_ENDPOINTS } from '../../config/api';
import './AdminDepartments.css'; // Reuse form styles

const AdminPlannings = () => {
    const [events, setEvents] = useState([]);
    const [currentDate, setCurrentDate] = useState(new Date());
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);

    // Lists for dropdowns
    const [salles, setSalles] = useState([]);
    const [enseignants, setEnseignants] = useState([]);
    const [ecs, setEcs] = useState([]);
    const [classes, setClasses] = useState([]);

    const [formData, setFormData] = useState({
        dateSeance: "",
        heureDebut: "",
        heureFin: "",
        salleId: "",
        enseignantId: "",
        ecId: "",
        classeId: "", // Optional if tied to EC? SeanceDTO has classeId.
        typeSeance: "CM"
    });

    useEffect(() => {
        fetchTimetable();
        fetchResources();
    }, [currentDate]);

    const fetchResources = async () => {
        try {
            const [grpSalles, grpEns, grpEcs, grpClasses] = await Promise.all([
                apiRequest(API_ENDPOINTS.SALLES.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.ENSEIGNANTS.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.ECS.LIST).catch(() => []),
                apiRequest(API_ENDPOINTS.DEROULEMENT_CLASSES.LIST).catch(() => [])
            ]);
            setSalles(Array.isArray(grpSalles) ? grpSalles : []);
            setEnseignants(Array.isArray(grpEns) ? grpEns : []);
            setEcs(Array.isArray(grpEcs) ? grpEcs : []);
            setClasses(Array.isArray(grpClasses) ? grpClasses : []);
        } catch (e) {
            console.error("Error fetching resources", e);
        }
    }

    const fetchTimetable = async () => {
        try {
            setLoading(true);
            const formattedDate = currentDate.toISOString().split('T')[0];
            const data = await apiRequest(`${API_ENDPOINTS.EMPLOI_TEMPS.WEEKLY}?date=${formattedDate}`);

            if (data && data.seancesParJour) {
                const allSeances = Object.values(data.seancesParJour).flat();
                setEvents(allSeances);
            } else {
                setEvents([]);
            }
        } catch (err) {
            console.error("Erreur chargement emploi du temps:", err);
            setEvents([]);
        } finally {
            setLoading(false);
        }
    };

    const handleInput = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Build DTO
            // SeanceDTO expects IDs as Long, Time as String "HH:mm:ss" or "HH:mm" depends on backend. 
            // LocalTime default JSON format is "HH:mm:ss" usually.
            // Start/End are times.

            const payload = {
                dateSeance: formData.dateSeance,
                heureDebut: formData.heureDebut + ":00", // Append seconds if needed by backend LocalTime
                heureFin: formData.heureFin + ":00",
                salleId: parseInt(formData.salleId),
                enseignantId: parseInt(formData.enseignantId),
                ecId: parseInt(formData.ecId),
                classeId: formData.classeId ? parseInt(formData.classeId) : null,
                typeSeance: formData.typeSeance
            };

            await apiRequest(API_ENDPOINTS.SEANCES.CREATE, {
                method: "POST",
                body: JSON.stringify(payload)
            });

            setShowModal(false);
            fetchTimetable(); // Refresh
            alert("Séance ajoutée !");
        } catch (err) {
            console.error(err);
            alert("Erreur lors de la création de la séance.");
        }
    }

    return (
        <div className="master-dashboard">
            <div className="dashboard-title-section" style={{ display: 'flex', justifyContent: 'space-between' }}>
                <h1 className="dashboard-title">
                    <CalendarIcon size={32} className="mr-2" />
                    Gestion du Planning
                </h1>
                <button className="add-btn" onClick={() => setShowModal(true)}>
                    <Plus size={18} className="mr-2" />
                    Nouvelle Séance
                </button>
            </div>

            <Timetable
                events={events}
                weekStart={currentDate}
                onWeekChange={setCurrentDate}
            />

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Planifier une Séance</h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Date</label>
                                    <input type="date" name="dateSeance" onChange={handleInput} required />
                                </div>
                                <div className="form-group">
                                    <label>Type</label>
                                    <select name="typeSeance" onChange={handleInput}>
                                        <option value="CM">CM</option>
                                        <option value="TD">TD</option>
                                        <option value="TP">TP</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Heure Début</label>
                                    <input type="time" name="heureDebut" onChange={handleInput} required />
                                </div>
                                <div className="form-group">
                                    <label>Heure Fin</label>
                                    <input type="time" name="heureFin" onChange={handleInput} required />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Élément Constitutif (EC)</label>
                                <select name="ecId" onChange={handleInput} required>
                                    <option value="">Sélectionner un EC</option>
                                    {ecs.map(ec => <option key={ec.id} value={ec.id}>{ec.libelle}</option>)}
                                </select>
                            </div>

                            <div className="form-group-row">
                                <div className="form-group">
                                    <label>Enseignant</label>
                                    <select name="enseignantId" onChange={handleInput} required>
                                        <option value="">Sélectionner</option>
                                        {enseignants.map(e => <option key={e.id} value={e.id}>{e.prenom} {e.nom}</option>)}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Salle</label>
                                    <select name="salleId" onChange={handleInput} required>
                                        <option value="">Sélectionner</option>
                                        {salles.map(s => <option key={s.id} value={s.id}>{s.libelle} ({s.capacite})</option>)}
                                    </select>
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Classe (Promotion)</label>
                                <select name="classeId" onChange={handleInput} required>
                                    <option value="">Sélectionner une classe</option>
                                    {classes.map(c => <option key={c.id} value={c.id}>{c.libelle}</option>)}
                                </select>
                            </div>

                            <div className="modal-actions">
                                <button type="button" onClick={() => setShowModal(false)} className="cancel-btn">Annuler</button>
                                <button type="submit" className="submit-btn">Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminPlannings;
