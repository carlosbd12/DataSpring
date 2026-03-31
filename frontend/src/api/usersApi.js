import client from "./client";

export async function getUsers() {
    const response = await client.get("/users");
    const data = response.data;

    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    if (Array.isArray(data?.users)) {
        return data.users;
    }

    return [];
}

export async function createUser(payload) {
    const response = await client.post("/users", payload);
    return response.data;
}

export async function updateUser(id, payload) {
    const response = await client.put(`/users/${id}`, payload);
    return response.data;
}

export async function deleteUser(id) {
    const response = await client.delete(`/users/${id}`);
    return response.data;
}

export async function activateUser(id) {
    const response = await client.patch(`/users/${id}/activate`);
    return response.data;
}

export async function deactivateUser(id) {
    const response = await client.patch(`/users/${id}/deactivate`);
    return response.data;
}