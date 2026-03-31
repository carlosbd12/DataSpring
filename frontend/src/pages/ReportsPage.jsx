import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
    generateDailyReport,
    generateEfficiencyReport,
    generateMonthlyReport,
    generateWeeklyReport,
    getReports
} from "../api/reportsApi";

export default function ReportsPage() {
    const [reports, setReports] = useState([]);
    const [date, setDate] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        loadReports();
    }, []);

    const loadReports = async () => {
        try {
            setLoading(true);
            const data = await getReports();
            setReports(data);
        } catch {
            setMessage("No se pudieron cargar los informes.");
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateDaily = async () => {
        if (!date) {
            setMessage("Selecciona una fecha para generar el informe diario.");
            return;
        }

        try {
            setLoading(true);
            await generateDailyReport(date);
            await loadReports();
            setMessage("Informe diario generado correctamente.");
        } catch {
            setMessage("No se pudo generar el informe diario.");
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateWeekly = async () => {
        try {
            setLoading(true);
            await generateWeeklyReport();
            await loadReports();
            setMessage("Informe semanal generado correctamente.");
        } catch {
            setMessage("No se pudo generar el informe semanal.");
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateMonthly = async () => {
        try {
            setLoading(true);
            await generateMonthlyReport();
            await loadReports();
            setMessage("Informe mensual generado correctamente.");
        } catch {
            setMessage("No se pudo generar el informe mensual.");
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateEfficiency = async () => {
        try {
            setLoading(true);
            await generateEfficiencyReport();
            await loadReports();
            setMessage("Informe de eficiencia generado correctamente.");
        } catch {
            setMessage("No se pudo generar el informe de eficiencia.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page-grid">
            <div>
                <h1>Informes</h1>
                <p className="subtitle">Genera informes desde el dataset y consulta su detalle</p>
            </div>

            {message ? <div className="error-banner">{message}</div> : null}
            {loading ? <p>Cargando...</p> : null}

            <div className="card" style={{ marginBottom: 16 }}>
                <h3>Generar informe diario</h3>
                <div style={{ display: "flex", gap: 12, alignItems: "center", flexWrap: "wrap" }}>
                    <input
                        type="date"
                        value={date}
                        onChange={(e) => setDate(e.target.value)}
                    />
                    <button onClick={handleGenerateDaily}>Generar</button>
                </div>
            </div>

            <div className="card" style={{ marginBottom: 16 }}>
                <h3>Otros informes</h3>
                <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
                    <button onClick={handleGenerateWeekly}>Generar semanal</button>
                    <button onClick={handleGenerateMonthly}>Generar mensual</button>
                    <button onClick={handleGenerateEfficiency}>Generar eficiencia</button>
                </div>
            </div>

            <div className="card">
                <table className="table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Título</th>
                        <th>Tipo</th>
                        <th>Período</th>
                        <th>Generado</th>
                        <th>Acción</th>
                    </tr>
                    </thead>
                    <tbody>
                    {reports.map((report) => (
                        <tr key={report.id}>
                            <td>{report.id}</td>
                            <td>{report.title}</td>
                            <td>{report.type}</td>
                            <td>{report.period}</td>
                            <td>{report.generatedAt?.replace("T", " ")}</td>
                            <td>
                                <Link to={`/reports/${report.id}`}>Ver detalle</Link>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}