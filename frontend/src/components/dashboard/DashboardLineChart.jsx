import {
    ResponsiveContainer,
    LineChart,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    Line
} from "recharts";

export default function DashboardLineChart({ title, data, color = "#16a34a", height = 360 }) {
    const safeData = Array.isArray(data) ? data : [];

    return (
        <div className="card chart-card">
            {title ? <h3>{title}</h3> : null}

            {safeData.length === 0 ? (
                <div className="chart-empty-state">
                    <p>No hay datos para mostrar.</p>
                </div>
            ) : (
                <div className="chart-container" style={{ minHeight: height, height }}>
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={safeData} margin={{ top: 10, right: 18, left: 0, bottom: 0 }}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                            <XAxis dataKey="label" tick={{ fill: "#64748b", fontSize: 12 }} />
                            <YAxis tick={{ fill: "#64748b", fontSize: 12 }} />
                            <Tooltip
                                contentStyle={{
                                    borderRadius: "12px",
                                    border: "1px solid #e5e7eb",
                                    boxShadow: "0 12px 24px rgba(15, 23, 42, 0.08)"
                                }}
                            />
                            <Line
                                type="monotone"
                                dataKey="value"
                                stroke={color}
                                strokeWidth={3}
                                dot={false}
                                activeDot={{ r: 5 }}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            )}
        </div>
    );
}