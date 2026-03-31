import client from "./client";

export async function getConfig() {
    const response = await client.get("/config");
    return response.data;
}

export async function updateConfig(payload) {
    const response = await client.put("/config", payload);
    return response.data;
}