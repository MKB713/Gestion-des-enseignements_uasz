import React from 'react';

const Navbar = () => {
    return (
        <nav className="top-navbar d-flex justify-content-between align-items-center px-4 shadow-sm bg-white"
             style={{height: '70px', position: 'fixed', top: 0, right: 0, left: '260px', zIndex: 99}}>

            <div className="d-flex align-items-center">
                <i className="bi bi-list fs-3 text-success me-3 d-md-none" style={{cursor:'pointer'}}></i>
                <span className="fw-bold text-success fs-5">
                    Portail Académique
                </span>
            </div>

            {/* Partie droite (Année / Notifications) */}
            <div className="d-flex align-items-center gap-3">
                <div className="text-end d-none d-sm-block">
                    <div className="fw-bold small text-dark">Année Universitaire</div>
                    <span className="badge bg-success bg-opacity-10 text-success border border-success">2023-2024</span>
                </div>
                <div className="vr h-50 mx-2"></div>
                <button className="btn btn-light rounded-circle position-relative">
                    <i className="bi bi-bell"></i>
                    <span className="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle">
                        <span className="visually-hidden">New alerts</span>
                    </span>
                </button>
            </div>
        </nav>
    );
};

export default Navbar;