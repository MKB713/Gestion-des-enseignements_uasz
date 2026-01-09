import React, { useState, useEffect } from "react";
import { apiRequest, API_ENDPOINTS, api } from "../../config/api";
import "./RepartitionEnseignements.css";

const RepartitionEnseignements = () => {
    const [loading, setLoading] = useState(false);

    // State for selectors
    const [maquettes, setMaquettes] = useState([]);
    const [selectedMaquetteId, setSelectedMaquetteId] = useState("");
    const [enseignants, setEnseignants] = useState([]);

    // State for displayed data
    const [viewData, setViewData] = useState([]);

    // Data cache
    const [repartitions, setRepartitions] = useState([]);

    useEffect(() => {
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        try {
            setLoading(true);
            const [maquettesRes, enseignantsRes] = await Promise.all([
                apiRequest(API_ENDPOINTS.MAQUETTES.LIST),
                apiRequest(API_ENDPOINTS.ENSEIGNANTS.LIST)
            ]);

            const maquettesData = maquettesRes.data || (Array.isArray(maquettesRes) ? maquettesRes : []);
            setMaquettes(maquettesData);

            const enseignantsData = enseignantsRes.data || (Array.isArray(enseignantsRes) ? enseignantsRes : []);
            setEnseignants(enseignantsData);

        } catch (err) {
            console.error("Erreur chargement:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleMaquetteChange = async (e) => {
        const id = e.target.value;
        setSelectedMaquetteId(id);
        if (!id) {
            setViewData([]);
            setRepartitions([]);
            return;
        }

        fetchMaquetteDetails(id);
    };

    const fetchMaquetteDetails = async (maquetteId) => {
        setLoading(true);
        try {
            // Recup detail maquette
            const maquetteResponse = await apiRequest(API_ENDPOINTS.MAQUETTES.BY_ID(maquetteId));
            const maquette = maquetteResponse.data || maquetteResponse;

            // Recup repartitions existantes
            const repartitionsResponse = await apiRequest(API_ENDPOINTS.REPARTITIONS.BY_MAQUETTE(maquetteId));
            const existingRepartitions = repartitionsResponse.data || (Array.isArray(repartitionsResponse) ? repartitionsResponse : []);
            setRepartitions(existingRepartitions);

            // Process structure
            processViewData(maquette, existingRepartitions);

        } catch (err) {
            console.error("Erreur details maquette:", err);
            // Fallback empty view
            setViewData([]);
        } finally {
            setLoading(false);
        }
    };

    const processViewData = (maquette, currentRepartitions) => {
        const processedData = [];

        if (maquette.semestres && maquette.semestres.length > 0) {
            maquette.semestres.forEach(sem => {
                const uesRows = [];
                sem.ues?.forEach(ue => {
                    ue.ecs?.forEach(ec => {
                        // Trouver les enseignants assignés pour chaque type
                        const cmRep = currentRepartitions.find(r => r.ecId === ec.id && r.type === 'CM');
                        const tdRep = currentRepartitions.find(r => r.ecId === ec.id && r.type === 'TD');
                        const tpRep = currentRepartitions.find(r => r.ecId === ec.id && r.type === 'TP');

                        uesRows.push({
                            ueId: ue.id,
                            ecId: ec.id,
                            ueLibelle: `${ue.code} - ${ue.libelle}`,
                            credit: ue.credits,
                            duree: ue.vht,
                            cm: ec.cm,
                            td: ec.td,
                            tp: ec.tp,
                            // Assignment Info
                            cmEnseignant: cmRep ? getEnseignantName(cmRep.enseignantId) : null,
                            cmEnseignantId: cmRep ? cmRep.enseignantId : null,
                            cmRepId: cmRep ? cmRep.id : null,

                            tdEnseignant: tdRep ? getEnseignantName(tdRep.enseignantId) : null,
                            tdEnseignantId: tdRep ? tdRep.enseignantId : null,
                            tdRepId: tdRep ? tdRep.id : null,

                            tpEnseignant: tpRep ? getEnseignantName(tpRep.enseignantId) : null,
                            tpEnseignantId: tpRep ? tpRep.enseignantId : null,
                            tpRepId: tpRep ? tpRep.id : null,

                            ecLibelle: ec.libelle
                        });
                    });
                });

                if (uesRows.length > 0) {
                    processedData.push({
                        classe: maquette.libelle,
                        effectif: "N/A", // Default
                        nbGroupe: 1,     // Default
                        semestre: sem.numero,
                        rows: uesRows
                    });
                }
            });
            setViewData(processedData);
        }
    };

    const getEnseignantName = (id) => {
        const ens = enseignants.find(e => e.id === id);
        return ens ? `${ens.prenom} ${ens.nom}` : "Inconnu";
    };

    const handleAssignmentChange = async (ecId, type, enseignantId, currentRepId, semestre) => {
        if (!selectedMaquetteId) return;

        try {
            if (currentRepId) {
                // Si une répartition existe déjà
                if (!enseignantId || enseignantId === "") {
                    // Suppression si "Aucun" sélectionné
                    await api.delete(API_ENDPOINTS.REPARTITIONS.DELETE(currentRepId));
                } else {
                    // Modification
                    await api.put(API_ENDPOINTS.REPARTITIONS.UPDATE(currentRepId), {
                        id: currentRepId,
                        ueId: 0, // Pas nécessaire pour update service mais requis par DTO si strict
                        ecId: ecId,
                        maquetteId: selectedMaquetteId,
                        semestre: semestre,
                        enseignantId: enseignantId,
                        type: type,
                        nombreGroupes: 1
                    });
                }
            } else {
                // Création
                if (enseignantId && enseignantId !== "") {
                    // Need UE ID? usually EC is enough but checking entity model... 
                    // Entity has ueId Not Null. We need to find the UE ID from viewData.
                    // But simpler to just refresh or pass it in args.
                    // Le backend attend ueId. On va le chercher dans viewData ou le passer.
                    const row = findRowByEcId(ecId);

                    await api.post(API_ENDPOINTS.REPARTITIONS.CREATE, {
                        ueId: row ? row.ueId : 0,
                        ecId: ecId,
                        maquetteId: selectedMaquetteId,
                        semestre: semestre,
                        enseignantId: enseignantId,
                        type: type,
                        nombreGroupes: 1
                    });
                }
            }

            // Refresh
            fetchMaquetteDetails(selectedMaquetteId);

        } catch (err) {
            console.error("Erreur lors de l'assignation:", err);
            alert("Erreur lors de l'enregistrement : " + err.message);
        }
    };

    const findRowByEcId = (ecId) => {
        for (const group of viewData) {
            const row = group.rows.find(r => r.ecId === ecId);
            if (row) return row;
        }
        return null;
    };

    return (
        <div className="repartition-container">
            <h2 className="title-header">Répartition des Unités d'Enseignement</h2>

            <div className="filter-bar" style={{ margin: '20px', textAlign: 'center' }}>
                <label style={{ marginRight: '10px', fontWeight: 'bold' }}>Sélectionner la Maquette / Classe : </label>
                <select value={selectedMaquetteId} onChange={handleMaquetteChange} style={{ padding: '5px' }}>
                    <option value="">-- Choisir une maquette --</option>
                    {maquettes.map(m => (
                        <option key={m.id} value={m.id}>{m.libelle} (v{m.version})</option>
                    ))}
                </select>
            </div>

            {loading && <div style={{ textAlign: "center" }}>Chargement...</div>}

            {!loading && viewData.length === 0 && selectedMaquetteId && (
                <div style={{ textAlign: "center", fontStyle: "italic" }}>Aucune donnée trouvée pour cette maquette.</div>
            )}
            {!loading && viewData.length === 0 && !selectedMaquetteId && (
                <div style={{ textAlign: "center", fontStyle: "italic" }}>Veuillez sélectionner une maquette pour afficher la répartition.</div>
            )}

            {viewData.map((group, gIndex) => (
                <div key={gIndex} style={{ marginTop: '30px' }}>
                    <h3 className="semester-header">Semestre {group.semestre}</h3>
                    <div className="table-wrapper">
                        <table className="repartition-table">
                            <thead>
                                <tr>
                                    <th>Classe</th>
                                    <th>Effectif</th>
                                    <th>Nbre de groupe</th>
                                    <th>Semestre</th>
                                    <th>Unité d'Enseignement</th>
                                    <th>Crédit</th>
                                    <th>Durée Cours</th>
                                    <th>Enseignant (CM)</th>
                                    <th>CM (h)</th>
                                    <th>Responsables TD</th>
                                    <th>Responsables TP</th>
                                    <th>TD (h)</th>
                                    <th>TP (h)</th>
                                </tr>
                            </thead>
                            <tbody>
                                {group.rows.map((row, rIndex) => (
                                    <tr key={rIndex} className={rIndex % 2 === 0 ? "even-row" : "odd-row"}>
                                        {rIndex === 0 && (
                                            <>
                                                <td rowSpan={group.rows.length} className="merged-cell red-bg">{group.classe}</td>
                                                <td rowSpan={group.rows.length} className="merged-cell bold">{group.effectif}</td>
                                                <td rowSpan={group.rows.length} className="merged-cell">{group.nbGroupe}</td>
                                            </>
                                        )}
                                        <td>{group.semestre}</td>
                                        <td className="ue-cell">
                                            {row.ueLibelle} <br />
                                            <span style={{ fontSize: '0.8em', color: '#666' }}>({row.ecLibelle})</span>
                                        </td>
                                        <td className="bold">{row.credit}</td>
                                        <td>{row.duree}</td>

                                        {/* CM COLUMN */}
                                        <td className="editable-cell">
                                            {row.cm > 0 ? (
                                                <select
                                                    className="teacher-select"
                                                    value={row.cmEnseignantId || ""}
                                                    onChange={(e) => handleAssignmentChange(row.ecId, 'CM', e.target.value, row.cmRepId, group.semestre)}
                                                >
                                                    <option value="">Non Assigné</option>
                                                    {enseignants.map(e => <option key={e.id} value={e.id}>{e.prenom} {e.nom}</option>)}
                                                </select>
                                            ) : "-"}
                                        </td>
                                        <td className="bold">{row.cm}</td>

                                        {/* TD COLUMN */}
                                        <td className="editable-cell">
                                            {row.td > 0 ? (
                                                <select
                                                    className="teacher-select"
                                                    value={row.tdEnseignantId || ""}
                                                    onChange={(e) => handleAssignmentChange(row.ecId, 'TD', e.target.value, row.tdRepId, group.semestre)}
                                                >
                                                    <option value="">Non Assigné</option>
                                                    {enseignants.map(e => <option key={e.id} value={e.id}>{e.prenom} {e.nom}</option>)}
                                                </select>
                                            ) : "-"}
                                        </td>

                                        {/* TP COLUMN */}
                                        <td className="editable-cell">
                                            {row.tp > 0 ? (
                                                <select
                                                    className="teacher-select"
                                                    value={row.tpEnseignantId || ""}
                                                    onChange={(e) => handleAssignmentChange(row.ecId, 'TP', e.target.value, row.tpRepId, group.semestre)}
                                                >
                                                    <option value="">Non Assigné</option>
                                                    {enseignants.map(e => <option key={e.id} value={e.id}>{e.prenom} {e.nom}</option>)}
                                                </select>
                                            ) : "-"}
                                        </td>

                                        <td>{row.td}</td>
                                        <td>{row.tp}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default RepartitionEnseignements;
