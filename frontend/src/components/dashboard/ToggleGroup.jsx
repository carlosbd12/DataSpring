export default function ToggleGroup({
                                        value,
                                        onChange,
                                        leftLabel = "Total",
                                        rightLabel = "Promedio"
                                    }) {
    return (
        <div className="toggle-group">
            <button
                type="button"
                className={value === "total" ? "toggle-button active" : "toggle-button"}
                onClick={() => onChange("total")}
            >
                {leftLabel}
            </button>

            <button
                type="button"
                className={value === "average" ? "toggle-button active" : "toggle-button"}
                onClick={() => onChange("average")}
            >
                {rightLabel}
            </button>
        </div>
    );
}