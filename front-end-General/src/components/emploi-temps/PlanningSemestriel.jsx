import React from "react";

const PlanningSemestriel = ({ data }) => {
    return (
        <div className="planning-semestriel">
            <h3>Planning Semestriel</h3>

            {data?.length ? (
                <p>{data.length} séances prévues pour le semestre</p>
            ) : (
                <p>Aucune donnée semestrielle</p>
            )}
        </div>
    );
};

export default PlanningSemestriel;
