import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "../components/layout/AppLayout";
import ConfigPage from "../pages/ConfigPage";
import DashboardPage from "../pages/DashboardPage";
import DevicesPage from "../pages/DevicesPage";
import LoginPage from "../pages/LoginPage";
import ReportsPage from "../pages/ReportsPage";
import ProtectedRoute from "./ProtectedRoute";
import ReportDetailPage from "../pages/ReportDetailPage.jsx";

export default function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />

                <Route
                    path="/"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <DashboardPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <DashboardPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/devices"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <DevicesPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/reports"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <ReportsPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/reports/:id"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <ReportDetailPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/config"
                    element={
                        <ProtectedRoute>
                            <AppLayout>
                                <ConfigPage />
                            </AppLayout>
                        </ProtectedRoute>
                    }
                />

                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
        </BrowserRouter>
    );
}