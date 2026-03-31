import { useEffect, useState } from "react";
import {
    getCo2Chart,
    getDashboardSummary,
    getDatasetInfo,
    getDayOfWeekChart,
    getHourlyChart,
    getLoadTypeChart,
    getWeekStatusChart
} from "../api/dashboardApi";
import DashboardBarChart from "../components/dashboard/DashboardBarChart";
import DashboardHeader from "../components/dashboard/DashboardHeader";
import DashboardLineChart from "../components/dashboard/DashboardLineChart";
import KpiCard from "../components/dashboard/KpiCard";
import ToggleGroup from "../components/dashboard/ToggleGroup";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
    const auth = useAuth();
    const authUser = auth?.authUser;

    const [summary, setSummary] = useState(null);
    const [datasetInfo, setDatasetInfo] = useState(null);

    const [selectedDate, setSelectedDate] = useState("");

    const [hourlyData, setHourlyData] = useState([]);
    const [loadTypeData, setLoadTypeData] = useState([]);
    const [dayOfWeekData, setDayOfWeekData] = useState([]);
    const [co2Data, setCo2Data] = useState([]);
    const [weekStatusData, setWeekStatusData] = useState([]);

    const [loadTypeMode, setLoadTypeMode] = useState("total");
    const [dayOfWeekMode, setDayOfWeekMode] = useState("total");
    const [co2Mode, setCo2Mode] = useState("total");

    const [loading, setLoading] = useState(true);
    const [chartsLoading, setChartsLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        loadInitialDashboard();
    }, []);

    useEffect(() => {
        if (!loading && selectedDate) {
            loadHourlyData(selectedDate);
        }
    }, [selectedDate]);

    useEffect(() => {
        if (!loading) {
            loadLoadTypeData(loadTypeMode);
        }
    }, [loadTypeMode]);

    useEffect(() => {
        if (!loading) {
            loadDayOfWeekData(dayOfWeekMode);
        }
    }, [dayOfWeekMode]);

    useEffect(() => {
        if (!loading) {
            loadCo2Data(co2Mode);
        }
    }, [co2Mode]);

    const loadInitialDashboard = async () => {
        try {
            setLoading(true);
            setError("");

            const [
                summaryResponse,
                datasetResponse,
                hourlyResponse,
                loadTypeResponse,
                dayOfWeekResponse,
                co2Response,
                weekStatusResponse
            ] = await Promise.all([
                getDashboardSummary(),
                getDatasetInfo(),
                getHourlyChart(),
                getLoadTypeChart("total"),
                getDayOfWeekChart("total"),
                getCo2Chart("total"),
                getWeekStatusChart()
            ]);

            setSummary(summaryResponse);
            setDatasetInfo(datasetResponse);
            setHourlyData(hourlyResponse);
            setLoadTypeData(loadTypeResponse);
            setDayOfWeekData(dayOfWeekResponse);
            setCo2Data(co2Response);
            setWeekStatusData(weekStatusResponse);

            if (datasetResponse?.firstAvailableDate) {
                setSelectedDate(datasetResponse.firstAvailableDate);
            }
        } catch (err) {
            setError(readError(err, "No se pudo cargar el dashboard"));
        } finally {
            setLoading(false);
        }
    };

    const loadHourlyData = async (date) => {
        try {
            setChartsLoading(true);
            const data = await getHourlyChart(date || undefined);
            setHourlyData(data);
        } catch (err) {
            setError(readError(err, "No se pudo cargar el gráfico horario"));
        } finally {
            setChartsLoading(false);
        }
    };

    const loadLoadTypeData = async (mode) => {
        try {
            setChartsLoading(true);
            const data = await getLoadTypeChart(mode);
            setLoadTypeData(data);
        } catch (err) {
            setError(readError(err, "No se pudo cargar el gráfico por tipo de carga"));
        } finally {
            setChartsLoading(false);
        }
    };

    const loadDayOfWeekData = async (mode) => {
        try {
            setChartsLoading(true);
            const data = await getDayOfWeekChart(mode);
            setDayOfWeekData(data);
        } catch (err) {
            setError(readError(err, "No se pudo cargar el gráfico por día de la semana"));
        } finally {
            setChartsLoading(false);
        }
    };

    const loadCo2Data = async (mode) => {
        try {
            setChartsLoading(true);
            const data = await getCo2Chart(mode);
            setCo2Data(data);
        } catch (err) {
            setError(readError(err, "No se pudo cargar el gráfico de CO₂"));
        } finally {
            setChartsLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="dashboard-page">
                <div className="card">
                    <p>Cargando dashboard...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="dashboard-page">
            <DashboardHeader summary={summary} datasetInfo={datasetInfo} authUser={authUser} />

            {error ? <div className="error-banner">{error}</div> : null}

            <div className="kpi-grid">
                <KpiCard title="Total mediciones" value={formatInteger(summary?.totalMeasurements)} />
                <KpiCard title="Consumo promedio" value={`${formatNumber(summary?.averageConsumption)} kWh`} />
                <KpiCard
                    title="Rango de consumo"
                    value={`${formatNumber(summary?.minConsumption)} - ${formatNumber(summary?.maxConsumption)} kWh`}
                />
                <KpiCard title="CO₂ total" value={`${formatNumber(summary?.totalCo2)} kg`} />
                <KpiCard title="Pico de consumo" value={`${formatNumber(summary?.peakConsumption)} kWh`} />
                <KpiCard
                    title="Fechas dataset"
                    value={`${summary?.firstAvailableDate || "-"} → ${summary?.lastAvailableDate || "-"}`}
                />
            </div>

            <div className="card filters-card">
                <div className="date-filter-block">
                    <label htmlFor="dashboard-date">Fecha para consumo por hora</label>
                    <input
                        id="dashboard-date"
                        type="date"
                        value={selectedDate}
                        min={datasetInfo?.firstAvailableDate || ""}
                        max={datasetInfo?.lastAvailableDate || ""}
                        onChange={(e) => setSelectedDate(e.target.value)}
                    />
                </div>

                {chartsLoading ? <span className="loading-pill">Actualizando gráfico...</span> : null}
            </div>

            <DashboardLineChart
                title={selectedDate ? `Consumo por hora · ${selectedDate}` : "Consumo total por hora"}
                data={hourlyData}
                color="#16a34a"
                height={380}
            />

            <div className="section-block">
                <div className="section-header">
                    <h3>Consumo por tipo de carga</h3>
                    <ToggleGroup
                        value={loadTypeMode}
                        onChange={setLoadTypeMode}
                        leftLabel="Total"
                        rightLabel="Promedio"
                    />
                </div>

                <DashboardBarChart data={loadTypeData} color="#2563eb" height={340} />
            </div>

            <div className="section-block">
                <div className="section-header">
                    <h3>Consumo por día de la semana</h3>
                    <ToggleGroup
                        value={dayOfWeekMode}
                        onChange={setDayOfWeekMode}
                        leftLabel="Total"
                        rightLabel="Promedio"
                    />
                </div>

                <DashboardBarChart data={dayOfWeekData} color="#7c3aed" height={340} />
            </div>

            <div className="section-block">
                <div className="section-header">
                    <h3>Emisiones CO₂ por día</h3>
                    <ToggleGroup
                        value={co2Mode}
                        onChange={setCo2Mode}
                        leftLabel="Total"
                        rightLabel="Promedio"
                    />
                </div>

                <DashboardBarChart data={co2Data} color="#dc2626" height={340} />
            </div>

            <DashboardBarChart
                title="Consumo: laborables vs fin de semana"
                data={weekStatusData}
                color="#f59e0b"
                height={320}
            />
        </div>
    );
}

function formatNumber(value) {
    if (value == null) {
        return "-";
    }
    return Number(value).toFixed(2);
}

function formatInteger(value) {
    if (value == null) {
        return "-";
    }
    return Number(value).toLocaleString();
}

function readError(err, fallback) {
    return err?.response?.data?.detail || err?.response?.data?.message || fallback;
}