import { useEffect, useState } from "react";
import { getDevices } from "../api/devicesApi";

const fallbackDevices = [
    { id: 1, name: "Sensor principal", active: true },
    { id: 2, name: "Medidor secundario", active: false },
    { id: 3, name: "Analizador de consumo", active: true }
];

export default function DevicesPage() {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        loadDevices();
    }, []);

    const loadDevices = async () => {
        try {
            setError("");
            const data = await getDevices();
            setDevices(data.length ? data : fallbackDevices);
        } catch (err) {
            setDevices(fallbackDevices);
            setError("No se pudieron cargar los dispositivos. Mostrando datos de ejemplo.");
        }
    };

    return (
        <div className="page-grid">
            <div>
                <h1>Dispositivos</h1>
                <p className="subtitle">Listado de dispositivos registrados en el sistema</p>
            </div>

            {error ? <div className="error-banner">{error}</div> : null}

            <div className="card">
                <table className="table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Estado</th>
                    </tr>
                    </thead>
                    <tbody>
                    {devices.map((device) => (
                        <tr key={device.id}>
                            <td>{device.id}</td>
                            <td>{device.name}</td>
                            <td>
                                    <span className={device.active ? "badge-active" : "badge-inactive"}>
                                        {device.active ? "Activo" : "Inactivo"}
                                    </span>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}