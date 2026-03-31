export default function UserModal({ title, children, onClose }) {
    return (
        <div className="modal-overlay">
            <div className="modal">
                <div className="modal-header">
                    <h3>{title}</h3>
                    <button className="button-secondary" onClick={onClose}>
                        Cerrar
                    </button>
                </div>

                <div className="modal-body">{children}</div>
            </div>
        </div>
    );
}