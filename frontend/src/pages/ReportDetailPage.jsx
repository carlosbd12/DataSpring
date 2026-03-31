import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { exportReportPdf, getReportById } from "../api/reportsApi";

export default function ReportDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [report, setReport] = useState(null);
    const [message, setMessage] = useState("");

    useEffect(() => {
        loadReport();
    }, [id]);

    const loadReport = async () => {
        try {
            setMessage("");
            const data = await getReportById(id);
            setReport(data);
        } catch {
            setMessage("No se pudo cargar el informe.");
        }
    };

    const handleExport = async () => {
        try {
            const response = await exportReportPdf(id);
            const blobUrl = window.URL.createObjectURL(response.data);

            const link = document.createElement("a");
            link.href = blobUrl;
            link.download = `report-${id}.pdf`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(blobUrl);
        } catch {
            setMessage("No se pudo exportar el informe.");
        }
    };

    const handleBack = () => {
        navigate("/reports");
    };

    if (message && !report) {
        return <p>{message}</p>;
    }

    if (!report) {
        return <p>Cargando informe...</p>;
    }

    return (
        <div className="page-grid">
            <div>
                <h1>{report.title}</h1>
                <p className="subtitle">Detalle del informe generado desde el dataset</p>
            </div>

            {message ? <div className="error-banner">{message}</div> : null}

            <div className="card">
                <p><strong>ID:</strong> {report.id}</p>
                <p><strong>Tipo:</strong> {report.type}</p>
                <p><strong>Período:</strong> {report.period}</p>
                <p><strong>Generado:</strong> {report.generatedAt?.replace("T", " ")}</p>
                <p><strong>Consumo total:</strong> {report.totalConsumption?.toFixed(2)} kWh</p>
                <p><strong>Consumo promedio:</strong> {report.averageConsumption?.toFixed(2)} kWh</p>
                <p><strong>CO₂ total:</strong> {report.totalCo2?.toFixed(2)} kg</p>
                <p><strong>Resumen:</strong> {report.extraSummary}</p>

                <div style={{ display: "flex", gap: 12, marginTop: 16 }}>
                    <button onClick={handleExport}>Exportar</button>
                    <button onClick={handleBack}>Volver</button>
                </div>
            </div>

            <div className="card">
                <h3>Contenido</h3>
                <pre style={{ whiteSpace: "pre-wrap" }}>{report.content}</pre>
            </div>
        </div>
    );
}