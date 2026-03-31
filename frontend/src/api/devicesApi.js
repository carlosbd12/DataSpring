import client from "./client";

export async function getDevices() {
    const response = await client.get("/devices");
    const data = response.data;

    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    if (Array.isArray(data?.devices)) {
        return data.devices;
    }

    return [];
}