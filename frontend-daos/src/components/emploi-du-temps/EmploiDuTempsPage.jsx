import React, { useState, useEffect } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import EmploiDuTempsService from '../../services/EmploiDuTempsService';
import SeanceForm from './SeanceForm'; // Importer le formulaire
import ClasseService from '../../services/ClasseService';
import EnseignantService from '../../services/EnseignantService';
import SalleService from '../../services/SalleService';

// Configuration du localizer pour que react-big-calendar utilise moment.js
const localizer = momentLocalizer(moment);

function EmploiDuTempsPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [apiError, setApiError] = useState(null);

  // États pour la modale
  const [showModal, setShowModal] = useState(false);
  const [selectedSeance, setSelectedSeance] = useState(null);

  // États pour les filtres
  const [selectedClass, setSelectedClass] = useState('');
  const [selectedTeacher, setSelectedTeacher] = useState('');
  const [selectedRoom, setSelectedRoom] = useState('');
  const [currentView, setCurrentView] = useState('week'); // 'week' ou 'month'
  const [currentDate, setCurrentDate] = useState(new Date()); // Pour la navigation mensuelle/hebdomadaire

  // Données pour les filtres (listes déroulantes)
  const [classes, setClasses] = useState([]);
  const [enseignants, setEnseignants] = useState([]);
  const [salles, setSalles] = useState([]);
  const [loadingFilters, setLoadingFilters] = useState(true);
  const [errorFilters, setErrorFilters] = useState(null);

  // Charger les données des filtres au montage du composant
  useEffect(() => {
    const fetchFilterData = async () => {
      setLoadingFilters(true);
      try {
        const [classesRes, enseignantsRes, sallesRes] = await Promise.all([
          ClasseService.getAllClasses(),
          EnseignantService.getAllEnseignants(),
          SalleService.getAllSalles(),
        ]);
        setClasses(classesRes.data);
        setEnseignants(enseignantsRes.data);
        setSalles(sallesRes.data);
      } catch (err) {
        console.error("Erreur lors du chargement des données de filtre:", err);
        setErrorFilters("Impossible de charger les options de filtre.");
      } finally {
        setLoadingFilters(false);
      }
    };
    fetchFilterData();
  }, []);

  // Charger les événements du calendrier
  useEffect(() => {
    const fetchEvents = async () => {
      setLoading(true);
      setError(null);
      try {
        let response;
        const filters = {
          classId: selectedClass,
          teacherId: selectedTeacher,
          roomId: selectedRoom
        };

        if (currentView === 'week') {
          response = await EmploiDuTempsService.getEmploiDuTempsSemaine(
            currentDate, // Utiliser currentDate pour la semaine
            filters.classId,
            filters.teacherId,
            filters.roomId
          );
        } else { // 'month'
          response = await EmploiDuTempsService.getEmploiDuTempsSemestre(
            currentDate.getFullYear(), // Année
            currentDate.getMonth() + 1, // Mois (1-indexé)
            filters.classId,
            filters.teacherId,
            filters.roomId
          );
        }
        
        const formattedEvents = response.data.map(seance => ({
          id: seance.id,
          title: `${seance.type} - ${seance.enseignement?.nom || 'N/A'}`,
          start: new Date(seance.start),
          end: new Date(seance.end),
          resource: {
            type: seance.type,
            enseignementId: seance.enseignement?.id,
            enseignantId: seance.enseignant?.id,
            salleId: seance.salle?.id,
            classeId: seance.classe?.id,
          }
        }));
        setEvents(formattedEvents);

      } catch (err) {
        setError('Impossible de charger l\'emploi du temps.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [currentView, currentDate, selectedClass, selectedTeacher, selectedRoom]); // Recharger quand les filtres ou la vue/date changent

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedSeance(null);
    setApiError(null); // Réinitialiser les erreurs API à la fermeture
  };

  const handleSelectSlot = ({ start, end }) => {
    // Ouvre la modale pour une nouvelle séance
    setSelectedSeance({ start, end });
    setShowModal(true);
  };

  const handleSelectEvent = (event) => {
    // Ouvre la modale pour modifier une séance existante
    setSelectedSeance(event);
    setShowModal(true);
  };

  const handleSaveSeance = async (seanceData) => {
    setLoading(true);
    setApiError(null);
    try {
      let response;
      // Préparer les données pour l'API (convertir date/heure en objets Date ou format ISO)
      const formattedSeanceData = {
        ...seanceData,
        start: new Date(`${seanceData.date}T${seanceData.heureDebut}`).toISOString(),
        end: new Date(`${seanceData.date}T${seanceData.heureFin}`).toISOString(),
      };

      if (seanceData.id) {
        // Modification
        response = await EmploiDuTempsService.updateSeance(seanceData.id, formattedSeanceData);
        setEvents(events.map(ev => (ev.id === seanceData.id ? { ...ev, ...response.data } : ev)));
      } else {
        // Création
        response = await EmploiDuTempsService.createSeance(formattedSeanceData);
        setEvents([...events, { ...response.data, start: new Date(response.data.start), end: new Date(response.data.end) }]);
      }
      handleCloseModal();
    } catch (err) {
      console.error('Erreur lors de la sauvegarde de la séance:', err);
      setApiError(err.response?.data?.message || 'Une erreur est survenue lors de la sauvegarde.');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteSeance = async (id) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette séance ?')) {
      return;
    }
    setLoading(true);
    setApiError(null);
    try {
      await EmploiDuTempsService.deleteSeance(id);
      setEvents(events.filter(ev => ev.id !== id));
      handleCloseModal();
    } catch (err) {
      console.error('Erreur lors de la suppression de la séance:', err);
      setApiError(err.response?.data?.message || 'Une erreur est survenue lors de la suppression.');
    } finally {
      setLoading(false);
    }
  };

  const handleExportPdf = async () => {
    setLoading(true);
    setApiError(null);
    try {
      // Déterminer le type de filtre actif
      let filterType = '';
      let filterId = '';

      if (selectedClass) {
        filterType = 'classe';
        filterId = selectedClass;
      } else if (selectedTeacher) {
        filterType = 'enseignant';
        filterId = selectedTeacher;
      } else if (selectedRoom) {
        filterType = 'salle';
        filterId = selectedRoom;
      }

      const response = await EmploiDuTempsService.exportEmploiDuTempsPdf(
        currentView === 'week' ? 'semaine' : 'semestre',
        filterType,
        filterId
      );

      // Créer un lien de téléchargement pour le Blob reçu
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `emploi_du_temps_${currentView}_${moment(currentDate).format('YYYYMMDD')}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);

    } catch (err) {
      console.error('Erreur lors de l\'exportation PDF:', err);
      setApiError(err.response?.data?.message || 'Une erreur est survenue lors de l\'exportation PDF.');
    } finally {
      setLoading(false);
    }
  };

  const handleReplaceSeance = async (seanceId, newEnseignantId) => {
    setLoading(true);
    setApiError(null);
    try {
      const response = await EmploiDuTempsService.replaceEnseignant(seanceId, newEnseignantId);
      // Mettre à jour l'événement dans l'état local
      setEvents(events.map(ev => (ev.id === seanceId ? { ...ev, ...response.data } : ev)));
      handleCloseModal();
    } catch (err) {
      console.error('Erreur lors du remplacement de l\'enseignant:', err);
      setApiError(err.response?.data?.message || 'Une erreur est survenue lors du remplacement de l\'enseignant.');
    } finally {
      setLoading(false);
    }
  };

  if (loading || loadingFilters) {
    return <div>Chargement de l'emploi du temps et des filtres...</div>;
  }

  if (error || errorFilters) {
    return <div style={{ color: 'red' }}>Erreur : {error || errorFilters}</div>;
  }

  return (
    <div className="p-3">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3>Emploi du Temps</h3>
        <div>
          <button className="btn btn-primary me-2" onClick={() => handleSelectSlot({})}>
            <i className="bi bi-plus-circle me-2"></i>
            Ajouter une séance
          </button>
          <button className="btn btn-info" onClick={handleExportPdf}>
            <i className="bi bi-file-earmark-pdf me-2"></i>
            Exporter PDF
          </button>
        </div>
      </div>

      {apiError && (
        <div className="alert alert-danger" role="alert">
          {apiError}
        </div>
      )}

      {/* Filtres */}
      <div className="row mb-3">
        <div className="col-md-4">
          <label htmlFor="filterClass" className="form-label">Filtrer par Classe</label>
          <select id="filterClass" className="form-select" value={selectedClass} onChange={(e) => setSelectedClass(e.target.value)}>
            <option value="">Toutes les classes</option>
            {classes.map(cls => (
              <option key={cls.id} value={cls.id}>{cls.nom}</option>
            ))}
          </select>
        </div>
        <div className="col-md-4">
          <label htmlFor="filterTeacher" className="form-label">Filtrer par Enseignant</label>
          <select id="filterTeacher" className="form-select" value={selectedTeacher} onChange={(e) => setSelectedTeacher(e.target.value)}>
            <option value="">Tous les enseignants</option>
            {enseignants.map(ens => (
              <option key={ens.id} value={ens.id}>{ens.prenom} {ens.nom}</option>
            ))}
          </select>
        </div>
        <div className="col-md-4">
          <label htmlFor="filterRoom" className="form-label">Filtrer par Salle</label>
          <select id="filterRoom" className="form-select" value={selectedRoom} onChange={(e) => setSelectedRoom(e.target.value)}>
            <option value="">Toutes les salles</option>
            {salles.map(salle => (
              <option key={salle.id} value={salle.id}>{salle.nom}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Sélecteur de vue */}
      <div className="d-flex justify-content-center mb-3">
        <div className="btn-group" role="group" aria-label="Basic radio toggle button group">
          <input type="radio" className="btn-check" name="view-options" id="view-week" autoComplete="off" checked={currentView === 'week'} onChange={() => setCurrentView('week')} />
          <label className="btn btn-outline-primary" htmlFor="view-week">Vue Semaine</label>

          <input type="radio" className="btn-check" name="view-options" id="view-month" autoComplete="off" checked={currentView === 'month'} onChange={() => setCurrentView('month')} />
          <label className="btn btn-outline-primary" htmlFor="view-month">Vue Mois</label>
        </div>
      </div>

      <div style={{ height: '80vh' }}>
        <Calendar
          localizer={localizer}
          events={events}
          startAccessor="start"
          endAccessor="end"
          defaultView={currentView} // Utiliser la vue sélectionnée
          views={['week', 'month', 'day', 'agenda']} // Ajouter la vue 'month'
          min={new Date(0, 0, 0, 8, 0, 0)}
          max={new Date(0, 0, 0, 18, 0, 0)}
          selectable={true}
          onSelectSlot={handleSelectSlot}
          onSelectEvent={handleSelectEvent}
          onNavigate={(newDate) => setCurrentDate(newDate)} // Gérer la navigation
        />
      </div>

      <SeanceForm
        show={showModal}
        handleClose={handleCloseModal}
        seance={selectedSeance}
        handleSave={handleSaveSeance}
        handleDelete={handleDeleteSeance}
        handleReplace={handleReplaceSeance} // Passer la fonction de remplacement
      />
    </div>
  );
}

export default EmploiDuTempsPage;
