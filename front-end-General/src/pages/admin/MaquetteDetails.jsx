import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { apiRequest, API_ENDPOINTS } from "../../config/api";
import "./MaquetteDetails.css";

const MaquetteDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [maquette, setMaquette] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [semesters, setSemesters] = useState([]);

    // For selector
    const [maquettesList, setMaquettesList] = useState([]);
    const [selectedId, setSelectedId] = useState(id || "");

    useEffect(() => {
        fetchMaquettesList();
    }, []);

    useEffect(() => {
        if (id) {
            setSelectedId(id);
            fetchMaquetteDetails(id);
        } else {
            setMaquette(null);
            setSemesters([]);
        }
    }, [id]);

    const fetchMaquettesList = async () => {
        try {
            const response = await apiRequest(API_ENDPOINTS.MAQUETTES.LIST);
            setMaquettesList(response.data || (Array.isArray(response) ? response : []) || []);
        } catch (err) {
            console.error("Error fetching list:", err);
        }
    };

    const handleSelectChange = (e) => {
        const newId = e.target.value;
        setSelectedId(newId);
        if (newId) {
            navigate(`/admin/maquettes/${newId}`);
        } else {
            navigate("/admin/maquette-details");
        }
    };

    const fetchMaquetteDetails = async (maquetteId) => {
        if (!maquetteId) return;
        try {
            setLoading(true);
            const response = await apiRequest(API_ENDPOINTS.MAQUETTES.BY_ID(maquetteId));
            const data = response.data || response;
            setMaquette(data);

            if (data.semestres && data.semestres.length > 0) {
                const processedSemesters = data.semestres.map(sem => {
                    const totalCredits = sem.ues?.reduce((sum, ue) => sum + ue.credits, 0) || 0;
                    const totalCoeffUE = sem.ues?.reduce((sum, ue) => sum + ue.coefficientUE, 0) || 0;

                    let totalCM = 0, totalTD = 0, totalTP = 0, totalTPE = 0, totalVHT = 0, totalCoeffEC = 0;

                    sem.ues?.forEach(ue => {
                        ue.ecs?.forEach(ec => {
                            totalCM += ec.cm;
                            totalTD += ec.td;
                            totalTP += ec.tp;
                            totalTPE += ec.tpe;
                            totalVHT += ec.vht;
                            totalCoeffEC += ec.coefficient;
                        });
                    });

                    return {
                        name: sem.libelle || `Semestre ${sem.numero}`,
                        totalCredits,
                        totalCoeffUE,
                        totalCM,
                        totalTD,
                        totalTP,
                        totalPresenciel: totalCM + totalTD + totalTP,
                        totalTPE,
                        totalVHT,
                        totalCoeffEC,
                        ues: sem.ues || []
                    };
                });
                setSemesters(processedSemesters);
            } else {
                setSemesters([]);
            }

            setError(null);
        } catch (err) {
            console.error("Error loading details:", err);
            setSemesters([]);
            setError("Erreur de chargement des données.");
        } finally {
            setLoading(false);
        }
    };

    // if (loading) return <div className="loading">Chargement des détails...</div>; 
    // Commented out global loading to allow selector to be visible while loading details

    return (
        <div className="maquette-details-container">
            <div className="header-actions" style={{ flexDirection: 'column', alignItems: 'center', gap: '15px' }}>
                <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <button onClick={() => navigate("/admin/maquettes")} className="back-btn">
                        &larr; Liste
                    </button>
                    <h2>{maquette?.libelle || "Détails Maquette"} <span style={{ fontSize: '0.8em', color: '#666' }}>({maquette?.code || "Aucune sélection"})</span></h2>
                    <button className="print-btn" onClick={() => window.print()} disabled={!maquette}>Imprimer</button>
                </div>

                <div className="selector-bar" style={{ width: '50%' }}>
                    <select value={selectedId} onChange={handleSelectChange} style={{ padding: '8px', width: '100%', fontSize: '1rem' }}>
                        <option value="">-- Sélectionner une maquette --</option>
                        {maquettesList.map(m => (
                            <option key={m.id} value={m.id}>{m.libelle} (v{m.version})</option>
                        ))}
                    </select>
                </div>
            </div>

            {loading && <div style={{ textAlign: "center", margin: "20px" }}>Chargement des détails...</div>}
            {error && <div className="error-message">{error}</div>}

            <div className="maquette-content">
                {!maquette && !loading && (
                    <div style={{ textAlign: "center", padding: "50px", fontStyle: "italic", color: "#666" }}>
                        <h3>Veuillez sélectionner une maquette ci-dessus pour voir les détails.</h3>
                    </div>
                )}

                {maquette && semesters.length === 0 && !loading ? (
                    <div style={{ textAlign: "center", padding: "20px" }}>
                        Aucun semestre ou UE associé à cette maquette.
                        <br />
                        <em>Veuillez ajouter des semestres, UEs et ECs.</em>
                    </div>
                ) : (
                    semesters.map((sem, index) => (
                        <div key={index} className="semester-block">
                            <table className="maquette-table">
                                <thead>
                                    <tr className="main-header">
                                        <th colSpan="3" className="ue-header">UNITÉS D'ENSEIGNEMENT {sem.name}</th>
                                        <th colSpan="8" className="ec-header">ELEMENTS CONSTITUTIFS</th>
                                    </tr>
                                    <tr className="sub-header">
                                        <th>Intitulés</th>
                                        <th>Crédits</th>
                                        <th>Coef UE</th>
                                        <th>Intitulés</th>
                                        <th>CM</th>
                                        <th>TD</th>
                                        <th>TP</th>
                                        <th>CM+TD/TP</th>
                                        <th>TPE</th>
                                        <th>VHT</th>
                                        <th>Coeff.</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {sem.ues.map((ue, ueIndex) => (
                                        <React.Fragment key={ueIndex}>
                                            {ue.ecs && ue.ecs.length > 0 ? (
                                                ue.ecs.map((ec, ecIndex) => (
                                                    <tr key={`${ueIndex}-${ecIndex}`} className={ueIndex % 2 === 0 ? "even-ue" : "odd-ue"}>
                                                        {ecIndex === 0 && (
                                                            <>
                                                                <td rowSpan={ue.ecs.length} className="ue-title">
                                                                    <strong>{ue.code}</strong> - {ue.libelle}
                                                                </td>
                                                                <td rowSpan={ue.ecs.length} className="ue-credits">{ue.credits}</td>
                                                                <td rowSpan={ue.ecs.length} className="ue-coeff">{ue.coefficientUE}</td>
                                                            </>
                                                        )}
                                                        <td className="ec-title">{ec.code} - {ec.libelle}</td>
                                                        <td>{ec.cm}</td>
                                                        <td>{ec.td}</td>
                                                        <td>{ec.tp}</td>
                                                        <td className="highlight-column">{ec.cm + ec.td + ec.tp}</td>
                                                        <td>{ec.tpe}</td>
                                                        <td className="highlight-column">{ec.vht}</td>
                                                        <td>{ec.coefficient}</td>
                                                    </tr>
                                                ))
                                            ) : (
                                                /* Handle UE with no ECs */
                                                <tr key={ueIndex} className={ueIndex % 2 === 0 ? "even-ue" : "odd-ue"}>
                                                    <td className="ue-title"><strong>{ue.code}</strong> - {ue.libelle}</td>
                                                    <td className="ue-credits">{ue.credits}</td>
                                                    <td className="ue-coeff">{ue.coefficientUE}</td>
                                                    <td colSpan="8" style={{ fontStyle: 'italic', color: '#999' }}>Aucun EC défini</td>
                                                </tr>
                                            )}
                                        </React.Fragment>
                                    ))}
                                    <tr className="semester-total-row">
                                        <td className="total-label">TOTAL {sem.name.toUpperCase()}</td>
                                        <td>{sem.totalCredits}</td>
                                        <td>{sem.totalCoeffUE}</td>
                                        <td className="total-label-right">TOTAL {sem.name.toUpperCase()}</td>
                                        <td>{sem.totalCM}</td>
                                        <td>{sem.totalTD}</td>
                                        <td>{sem.totalTP}</td>
                                        <td>{sem.totalPresenciel}</td>
                                        <td>{sem.totalTPE}</td>
                                        <td>{sem.totalVHT}</td>
                                        <td>{sem.totalCoeffEC}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    )))}
            </div>
        </div>
    );
};

// Removed Mock Data
// ...

// MOCK DATA BASED ON IMAGES
const mockSemesters = [
    {
        name: "Semestre 1",
        totalCredits: 30,
        totalCoeffUE: 15,
        totalCM: 178,
        totalTD: 132,
        totalTP: 50,
        totalPresenciel: 360,
        totalTPE: 240,
        totalVHT: 600,
        totalCoeffEC: 13,
        ues: [
            {
                code: "INF111",
                libelle: "Architecture et Système d'exploitation (2)",
                credits: 8,
                coeff: 4,
                ecs: [
                    { code: "INF1111", libelle: "Architecture et technologie des ordinateurs", cm: 36, td: 24, tp: 12, totalPresenciel: 72, tpe: 48, vht: 120, coeff: 3 },
                    { code: "INF1112", libelle: "Initiation aux Systèmes d'exploitations", cm: 10, td: 0, tp: 14, totalPresenciel: 24, tpe: 16, vht: 40, coeff: 1 }
                ]
            },
            {
                code: "INF112",
                libelle: "Mathématiques 1(3)",
                credits: 8,
                coeff: 4,
                ecs: [
                    { code: "INF1121", libelle: "Mathématiques discrètes 1", cm: 24, td: 24, tp: 0, totalPresenciel: 48, tpe: 32, vht: 80, coeff: 1 },
                    { code: "INF1122", libelle: "Mathématiques pour l'informatique 1", cm: 24, td: 24, tp: 0, totalPresenciel: 48, tpe: 32, vht: 80, coeff: 1 }
                ]
            },
            {
                code: "INF113",
                libelle: "Algorithmique et Programmation 1 (1)",
                credits: 8,
                coeff: 4,
                ecs: [
                    { code: "INF1131", libelle: "Programmation 1(2)", cm: 12, td: 12, tp: 12, totalPresenciel: 36, tpe: 24, vht: 60, coeff: 2 },
                    { code: "INF1132", libelle: "Algorithmique 1(1)", cm: 24, td: 24, tp: 12, totalPresenciel: 60, tpe: 40, vht: 100, coeff: 3 }
                ]
            },
            {
                code: "INF114",
                libelle: "Langues et Humanités 1(4)",
                credits: 6,
                coeff: 3,
                ecs: [
                    { code: "INF1141", libelle: "Techniques d'expression en Français", cm: 24, td: 12, tp: 0, totalPresenciel: 36, tpe: 24, vht: 60, coeff: 1 },
                    { code: "INF1142", libelle: "Anglais 1", cm: 24, td: 12, tp: 0, totalPresenciel: 36, tpe: 24, vht: 60, coeff: 1 }
                ]
            }
        ]
    },
    {
        name: "Semestre 2",
        totalCredits: 30,
        totalCoeffUE: 15,
        totalCM: 162,
        totalTD: 114,
        totalTP: 84,
        totalPresenciel: 360,
        totalTPE: 240,
        totalVHT: 600,
        totalCoeffEC: 11,
        ues: [
            {
                code: "INF121",
                libelle: "Algorithmique et programmation 2(1)",
                credits: 10,
                coeff: 5,
                ecs: [
                    { code: "INF1211", libelle: "Introduction à la programmation WEB", cm: 12, td: 0, tp: 12, totalPresenciel: 24, tpe: 16, vht: 40, coeff: 1 },
                    { code: "INF1212", libelle: "Algorithmique 2", cm: 24, td: 24, tp: 0, totalPresenciel: 48, tpe: 32, vht: 80, coeff: 2 },
                    { code: "INF1213", libelle: "Programmation 2", cm: 12, td: 12, tp: 24, totalPresenciel: 48, tpe: 32, vht: 80, coeff: 2 }
                ]
            },
            // ... (Other UEs truncated for brevity in initial mock to save space, user can expand)
        ]
    }
];

export default MaquetteDetails;
