import client from "./client";

export async function getReports() {
    const response = await client.get("/reports");
    const data = response.data;

    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    if (Array.isArray(data?.reports)) {
        return data.reports;
    }

    return [];
}

export async function getReportById(id) {
    const response = await client.get(`/reports/${id}`);
    return response.data;
}

export async function generateDailyReport(date) {
    const response = await client.post(`/reports/generate/daily?date=${date}`);
    return response.data;
}

export async function generateWeeklyReport() {
    const response = await client.post("/reports/generate/weekly");
    return response.data;
}

export async function generateMonthlyReport() {
    const response = await client.post("/reports/generate/monthly");
    return response.data;
}

export async function generateEfficiencyReport() {
    const response = await client.post("/reports/generate/efficiency");
    return response.data;
}

export function exportReportPdf(id) {
    return client.get(`/reports/${id}/export/pdf`, {
        responseType: "blob"
    });
}

function normalizeList(data) {
    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    if (Array.isArray(data?.reports)) {
        return data.reports;
    }

    return [];
}