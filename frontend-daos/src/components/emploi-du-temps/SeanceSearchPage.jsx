import React, { useState, useEffect } from 'react';
import EmploiDuTempsService from '../../services/EmploiDuTempsService';
import ClasseService from '../../services/ClasseService';
import EnseignantService from '../../services/EnseignantService';
import SalleService from '../../services/SalleService';
import EnseignementService from '../../services/EnseignementService';
import moment from 'moment';

function SeanceSearchPage() {
  const [searchParams, setSearchParams] = useState({
    ecId: '',
    enseignantId: '',
    salleId: '',
    classeId: '',
    date: '',
  });
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Données pour les listes déroulantes des filtres
  const [enseignements, setEnseignements] = useState([]);
  const [enseignants, setEnseignants] = useState([]);
  const [salles, setSalles] = useState([]);
  const [classes, setClasses] = useState([]);
  const [loadingDropdowns, setLoadingDropdowns] = useState(true);
  const [errorDropdowns, setErrorDropdowns] = useState(null);

  // Charger les données des listes déroulantes au montage du composant
  useEffect(() => {
    const fetchDropdownData = async () => {
      setLoadingDropdowns(true);
      try {
        const [
          enseignementsRes,
          enseignantsRes,
          sallesRes,
          classesRes
        ] = await Promise.all([
          EnseignementService.getAllEnseignements(),
          EnseignantService.getAllEnseignants(),
          SalleService.getAllSalles(),
          ClasseService.getAllClasses()
        ]);

        setEnseignements(enseignementsRes.data);
        setEnseignants(enseignantsRes.data);
        setSalles(sallesRes.data);
        setClasses(classesRes.data);
      } catch (err) {
        console.error("Erreur lors du chargement des données des listes déroulantes:", err);
        setErrorDropdowns("Impossible de charger les données nécessaires au formulaire de recherche.");
      } finally {
        setLoadingDropdowns(false);
      }
    };
    fetchDropdownData();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const response = await EmploiDuTempsService.searchSeances(searchParams);
      setSearchResults(response.data);
    } catch (err) {
      console.error("Erreur lors de la recherche des séances:", err);
      setError("Une erreur est survenue lors de la recherche des séances.");
    } finally {
      setLoading(false);
    }
  };

  if (loadingDropdowns) {
    return <div className="p-3">Chargement des options de recherche...</div>;
  }

  if (errorDropdowns) {
    return <div className="p-3 text-danger">Erreur : {errorDropdowns}</div>;
  }

  return (
    <div className="p-3">
      <h3>Rechercher des Séances</h3>

      <form onSubmit={handleSearch} className="mb-4 p-3 border rounded">
        <div className="row g-3">
          <div className="col-md-6">
            <label htmlFor="ecId" className="form-label">Enseignement (EC)</label>
            <select id="ecId" name="ecId" className="form-select" value={searchParams.ecId} onChange={handleChange}>
              <option value="">Tous les enseignements</option>
              {enseignements.map(ens => (
                <option key={ens.id} value={ens.id}>{ens.nom}</option>
              ))}
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="enseignantId" className="form-label">Enseignant</label>
            <select id="enseignantId" name="enseignantId" className="form-select" value={searchParams.enseignantId} onChange={handleChange}>
              <option value="">Tous les enseignants</option>
              {enseignants.map(ens => (
                <option key={ens.id} value={ens.id}>{ens.prenom} {ens.nom}</option>
              ))}
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="salleId" className="form-label">Salle</label>
            <select id="salleId" name="salleId" className="form-select" value={searchParams.salleId} onChange={handleChange}>
              <option value="">Toutes les salles</option>
              {salles.map(salle => (
                <option key={salle.id} value={salle.id}>{salle.nom}</option>
              ))}
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="classeId" className="form-label">Classe</label>
            <select id="classeId" name="classeId" className="form-select" value={searchParams.classeId} onChange={handleChange}>
              <option value="">Toutes les classes</option>
              {classes.map(cls => (
                <option key={cls.id} value={cls.id}>{cls.nom}</option>
              ))}
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="date" className="form-label">Date spécifique</label>
            <input type="date" id="date" name="date" className="form-control" value={searchParams.date} onChange={handleChange} />
          </div>
          <div className="col-12">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Recherche en cours...' : 'Rechercher'}
            </button>
          </div>
        </div>
      </form>

      {error && (
        <div className="alert alert-danger mt-3" role="alert">
          {error}
        </div>
      )}

      <h4 className="mt-4">Résultats de la recherche</h4>
      {searchResults.length === 0 && !loading && !error && <p>Aucune séance trouvée pour les critères spécifiés.</p>}

      {searchResults.length > 0 && (
        <div className="table-responsive">
          <table className="table table-striped table-hover mt-3">
            <thead>
              <tr>
                <th>ID</th>
                <th>Titre</th>
                <th>Début</th>
                <th>Fin</th>
                <th>Type</th>
                <th>Enseignement</th>
                <th>Enseignant</th>
                <th>Salle</th>
                <th>Classe</th>
              </tr>
            </thead>
            <tbody>
              {searchResults.map(seance => (
                <tr key={seance.id}>
                  <td>{seance.id}</td>
                  <td>{seance.title || `${seance.type} - ${seance.enseignement?.nom || 'N/A'}`}</td>
                  <td>{moment(seance.start).format('DD/MM/YYYY HH:mm')}</td>
                  <td>{moment(seance.end).format('DD/MM/YYYY HH:mm')}</td>
                  <td>{seance.type}</td>
                  <td>{seance.enseignement?.nom || 'N/A'}</td>
                  <td>{seance.enseignant?.prenom} {seance.enseignant?.nom || 'N/A'}</td>
                  <td>{seance.salle?.nom || 'N/A'}</td>
                  <td>{seance.classe?.nom || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default SeanceSearchPage;
