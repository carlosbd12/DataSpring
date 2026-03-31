import client from "./client";

export async function getDashboardSummary() {
    const response = await client.get("/dashboard/summary");
    return response.data;
}

export async function getDatasetInfo() {
    const response = await client.get("/dashboard/dataset");
    return response.data;
}

export async function getHourlyChart(date) {
    const response = await client.get("/dashboard/hourly", {
        params: date ? { date } : {}
    });
    return Array.isArray(response.data) ? response.data : [];
}

export async function getLoadTypeChart(mode) {
    const response = await client.get("/dashboard/load-type", {
        params: { mode }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export async function getDayOfWeekChart(mode) {
    const response = await client.get("/dashboard/day-of-week", {
        params: { mode }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export async function getCo2Chart(mode) {
    const response = await client.get("/dashboard/co2", {
        params: { mode }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export async function getWeekStatusChart() {
    const response = await client.get("/dashboard/week-status");
    return Array.isArray(response.data) ? response.data : [];
}