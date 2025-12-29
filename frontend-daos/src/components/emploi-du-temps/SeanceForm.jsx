import React, { useState, useEffect } from 'react';
import EnseignantService from '../../services/EnseignantService';
import EnseignementService from '../../services/EnseignementService';
import SalleService from '../../services/SalleService';
import ClasseService from '../../services/ClasseService';

function SeanceForm({ show, handleClose, seance, handleSave, handleDelete, handleReplace }) {
  const [formData, setFormData] = useState({});
  const [enseignements, setEnseignements] = useState([]);
  const [enseignants, setEnseignants] = useState([]);
  const [salles, setSalles] = useState([]);
  const [classes, setClasses] = useState([]);
  const [loadingData, setLoadingData] = useState(true);
  const [errorData, setErrorData] = useState(null);
  const [showReplaceSection, setShowReplaceSection] = useState(false); // État pour afficher la section de remplacement
  const [replacementTeacherId, setReplacementTeacherId] = useState(''); // ID de l'enseignant remplaçant

  useEffect(() => {
    const fetchDropdownData = async () => {
      setLoadingData(true);
      setErrorData(null);
      try {
        const [
          enseignementsRes,
          enseignantsRes,
          sallesRes,
          classesRes
        ] = await Promise.all([
          EnseignementService.getAllEnseignements(), // Assurez-vous que cette fonction existe
          EnseignantService.getAllEnseignants(), // Assurez-vous que cette fonction existe
          SalleService.getAllSalles(),
          ClasseService.getAllClasses()
        ]);

        setEnseignements(enseignementsRes.data);
        setEnseignants(enseignantsRes.data);
        setSalles(sallesRes.data);
        setClasses(classesRes.data);
      } catch (err) {
        console.error("Erreur lors du chargement des données des listes déroulantes:", err);
        setErrorData("Impossible de charger les données nécessaires au formulaire.");
      }
      finally {
        setLoadingData(false);
      }
    };

    if (show) { // Charger les données uniquement quand la modale est affichée
      fetchDropdownData();
      setShowReplaceSection(false); // Cacher la section de remplacement à l'ouverture
      setReplacementTeacherId(''); // Réinitialiser l'enseignant remplaçant
    }
  }, [show]); // Dépendance à 'show' pour recharger si la modale s'ouvre

  useEffect(() => {
    // Si une 'seance' est passée en props, on pré-remplit le formulaire pour la modification
    if (seance) {
      setFormData({
        ...seance,
        // Assurez-vous que les dates sont au format YYYY-MM-DD pour les inputs de type 'date'
        date: seance.start ? new Date(seance.start).toISOString().split('T')[0] : '',
        heureDebut: seance.start ? new Date(seance.start).toTimeString().substring(0, 5) : '',
        heureFin: seance.end ? new Date(seance.end).toTimeString().substring(0, 5) : '',
      });
    } else {
      // Sinon, on initialise un formulaire vide pour la création
      setFormData({
        date: '',
        heureDebut: '',
        heureFin: '',
        type: 'CM',
        enseignementId: '',
        salleId: '',
        enseignantId: '',
        classeId: '',
      });
    }
  }, [seance, show]); // Dépendance à 'seance' et 'show' pour réinitialiser si la modale s'ouvre pour une nouvelle séance ou une séance différente

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    handleSave(formData);
  };

  const handleConfirmReplace = () => {
    if (seance && seance.id && replacementTeacherId) {
      handleReplace(seance.id, replacementTeacherId);
    }
  };

  if (!show) {
    return null;
  }

  if (loadingData) {
    return (
      <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
        <div className="modal-dialog modal-lg">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Chargement...</h5>
              <button type="button" className="btn-close" onClick={handleClose}></button>
            </div>
            <div className="modal-body">
              <p>Chargement des données du formulaire...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (errorData) {
    return (
      <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
        <div className="modal-dialog modal-lg">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Erreur</h5>
              <button type="button" className="btn-close" onClick={handleClose}></button>
            </div>
            <div className="modal-body">
              <p className="text-danger">{errorData}</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-lg">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">{seance ? 'Modifier la séance' : 'Ajouter une séance'}</h5>
            <button type="button" className="btn-close" onClick={handleClose}></button>
          </div>
          <div className="modal-body">
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="date" className="form-label">Date</label>
                  <input type="date" className="form-control" id="date" name="date" value={formData.date || ''} onChange={handleChange} required />
                </div>
                <div className="col-md-3 mb-3">
                  <label htmlFor="heureDebut" className="form-label">Heure de début</label>
                  <input type="time" className="form-control" id="heureDebut" name="heureDebut" value={formData.heureDebut || ''} onChange={handleChange} required />
                </div>
                <div className="col-md-3 mb-3">
                  <label htmlFor="heureFin" className="form-label">Heure de fin</label>
                  <input type="time" className="form-control" id="heureFin" name="heureFin" value={formData.heureFin || ''} onChange={handleChange} required />
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="type" className="form-label">Type de séance</label>
                <select className="form-select" id="type" name="type" value={formData.type || 'CM'} onChange={handleChange}>
                  <option value="CM">CM (Cours Magistral)</option>
                  <option value="TD">TD (Travaux Dirigés)</option>
                  <option value="TP">TP (Travaux Pratiques)</option>
                </select>
              </div>

              <div className="mb-3">
                <label htmlFor="enseignementId" className="form-label">Enseignement (EC)</label>
                <select className="form-select" id="enseignementId" name="enseignementId" value={formData.enseignementId || ''} onChange={handleChange} required>
                  <option value="">Sélectionner un enseignement...</option>
                  {enseignements.map(ens => (
                    <option key={ens.id} value={ens.id}>{ens.nom}</option>
                  ))}
                </select>
              </div>

              <div className="mb-3">
                <label htmlFor="enseignantId" className="form-label">Enseignant</label>
                <select className="form-select" id="enseignantId" name="enseignantId" value={formData.enseignantId || ''} onChange={handleChange} required>
                  <option value="">Sélectionner un enseignant...</option>
                  {enseignants.map(ens => (
                    <option key={ens.id} value={ens.id}>{ens.prenom} {ens.nom}</option>
                  ))}
                </select>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label htmlFor="salleId" className="form-label">Salle</label>
                  <select className="form-select" id="salleId" name="salleId" value={formData.salleId || ''} onChange={handleChange} required>
                    <option value="">Sélectionner une salle...</option>
                    {salles.map(salle => (
                      <option key={salle.id} value={salle.id}>{salle.nom}</option>
                    ))}
                  </select>
                </div>
                <div className="col-md-6 mb-3">
                  <label htmlFor="classeId" className="form-label">Classe</label>
                  <select className="form-select" id="classeId" name="classeId" value={formData.classeId || ''} onChange={handleChange} required>
                    <option value="">Sélectionner une classe...</option>
                    {classes.map(classe => (
                      <option key={classe.id} value={classe.id}>{classe.nom}</option>
                    ))}
                  </select>
                </div>
              </div>

            </form>

            {showReplaceSection && seance && seance.id && (
              <div className="mt-4 p-3 border rounded bg-light">
                <h5>Remplacer l'enseignant pour cette séance</h5>
                <div className="mb-3">
                  <label htmlFor="replacementTeacher" className="form-label">Nouvel Enseignant</label>
                  <select
                    id="replacementTeacher"
                    name="replacementTeacher"
                    className="form-select"
                    value={replacementTeacherId}
                    onChange={(e) => setReplacementTeacherId(e.target.value)}
                    required
                  >
                    <option value="">Sélectionner un enseignant...</option>
                    {enseignants.map(ens => (
                      <option key={ens.id} value={ens.id}>{ens.prenom} {ens.nom}</option>
                    ))}
                  </select>
                </div>
                <button
                  type="button"
                  className="btn btn-success"
                  onClick={handleConfirmReplace}
                  disabled={!replacementTeacherId}
                >
                  Confirmer Remplacement
                </button>
              </div>
            )}
          </div>
          <div className="modal-footer d-flex justify-content-between">
            <div>
              <button type="button" className="btn btn-secondary me-2" onClick={handleClose}>Annuler</button>
              {seance && seance.id && (
                <button type="button" className="btn btn-danger" onClick={() => handleDelete(seance.id)}>Supprimer</button>
              )}
            </div>
            <div>
              {seance && seance.id && (
                <button type="button" className="btn btn-warning me-2" onClick={() => setShowReplaceSection(!showReplaceSection)}>
                  <i className="bi bi-person-fill-up me-2"></i>
                  {showReplaceSection ? 'Annuler Remplacement' : 'Remplacer Enseignant'}
                </button>
              )}
              <button type="button" className="btn btn-primary" onClick={handleSubmit}>Enregistrer</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SeanceForm;
