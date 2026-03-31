export default function DashboardHeader({ summary, datasetInfo, authUser }) {
    const displayName =
        authUser?.displayName ||
        summary?.userDisplayName ||
        "Invitado";

    const roleLabel =
        authUser?.roleLabel ||
        summary?.userRoleLabel ||
        "Sin rol";

    return (
        <div className="dashboard-header">
            <div>
                <h1>Dashboard energético</h1>
                <p className="dashboard-user-line">
                    {displayName} · {roleLabel}
                </p>
            </div>

            <div className="dataset-box">
                <strong>Dataset actual</strong>
                <span>{datasetInfo?.fileName || "steel_industry_data.csv"}</span>
                <small>{summary?.datasetInfo || "-"}</small>
            </div>
        </div>
    );
}