export default function KpiCard({ title, value, subtitle }) {
    return (
        <div className="card kpi-card">
            <h3>{title}</h3>
            <p className="kpi-value">{value}</p>
            {subtitle ? <p className="kpi-subtitle">{subtitle}</p> : null}
        </div>
    );
}