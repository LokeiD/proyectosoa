// ================= CONFIGURACIÓN =================
const API_URL = 'http://localhost:8080/api/v1'; // Ajusta si es necesario
const URL_VER_DOC = `${API_URL}/creditos/documentos/ver`; // Endpoint para descargar/ver PDF

// Tipos de decisión (según tu BD)
const DECISION = {
    APROBADO: 1,
    OBSERVADO: 2,
    RECHAZADO: 3
};

// Modales de Bootstrap
let modalAprobar = null;
let modalObservar = null;
let modalRechazar = null;

// Mapa de documentos requeridos (Misma lógica que tu módulo anterior)
const DOCUMENTOS_REQUERIDOS = [
    { nombre: 'FICHA SOCIOECONOMICA', clave: 'ficha' },
    { nombre: 'VISITA DE CAMPO', clave: 'visita' },
    { nombre: 'DNI DEL ASOCIADO', clave: 'dni' },
    { nombre: 'DOCUMENTO DE DOMICILIO', clave: 'domicilio' },
    { nombre: 'CENTRAL DE RIESGOS', clave: 'riesgos' },
    { nombre: 'FLUJO DE EFECTIVO', clave: 'flujo' },
    { nombre: 'ACREDITACION DE INGRESOS', clave: 'ingresos' },
    { nombre: 'OTROS DOCUMENTOS', clave: 'otros' }
];

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar Modales
    modalAprobar = new bootstrap.Modal(document.getElementById('modalAprobar'));
    modalObservar = new bootstrap.Modal(document.getElementById('modalObservar'));
    modalRechazar = new bootstrap.Modal(document.getElementById('modalRechazar'));

    // Cargar Tabla
    listarEvaluaciones();
});

// ================= 1. LISTADO Y TABLA =================

async function listarEvaluaciones() {
    try {
        const response = await fetch(`${API_URL}/creditos/evaluaciones/pendientes`);
        if (!response.ok) throw new Error("Error obteniendo datos del servidor");

        const datos = await response.json();

        // Actualizar contador visual
        const lblContador = document.getElementById('lblContadorSolicitudes');
        if(lblContador) lblContador.textContent = `${datos.length} SOLICITUDES POR EVALUAR`;

        renderizarTabla(datos);
    } catch (error) {
        console.error(error);
        document.getElementById('tablaEvaluacion').innerHTML =
            '<tr><td colspan="11" class="text-center text-danger fw-bold py-4">Error cargando las evaluaciones. Intente nuevamente.</td></tr>';
    }
}

function renderizarTabla(data) {
    const tbody = document.getElementById('tablaEvaluacion');
    tbody.innerHTML = '';

    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="11" class="text-center py-4 text-muted">No hay solicitudes pendientes de evaluación</td></tr>';
        return;
    }

    data.forEach(sol => {
        // Datos básicos (Null Safety)
        const id = sol.idExpediente;
        const nroExp = sol.numeroExpediente || `EXP-${id}`;
        const fecha = sol.fechaSolicitud || '---';
        const dni = sol.dni || '---';
        const nombre = sol.nombreCompleto || '---';
        const producto = sol.producto || '---';
        const monto = sol.montoSolicitado ? `S/ ${sol.montoSolicitado.toFixed(2)}` : 'S/ 0.00';
        const cuotas = sol.cuotas || 0;
        const periodo = sol.periodo || '---';
        const tea = sol.tea ? `${sol.tea}%` : '-';

        // --- LÓGICA DE DOCUMENTOS (Adaptada de tu código anterior) ---
        // 1. Convertimos la lista de nombres de archivo en un Mapa { clave: nombreArchivo }
        const docsMap = {};
        if (sol.documentos && sol.documentos.length > 0) {
            sol.documentos.forEach(nombreArchivo => {
                // Buscamos a qué tipo de doc corresponde según la clave (ej: "_dni_")
                const tipoEncontrado = DOCUMENTOS_REQUERIDOS.find(req => nombreArchivo.includes(`_${req.clave}_`));
                if (tipoEncontrado) {
                    docsMap[tipoEncontrado.clave] = nombreArchivo;
                }
            });
        }

        // 2. Generamos el HTML "Solo Lectura" (Sin lápiz ni nube)
        const htmlDocumentos = generarColumnaDocumentosReadOnly(docsMap);

        // --- RENDERIZADO DE FILA ---
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="fw-bold text-center align-middle">${nroExp}</td>
            <td class="align-middle">${fecha}</td>
            <td class="align-middle">${dni}</td>
            <td class="text-uppercase align-middle" style="font-size: 0.85rem;">${nombre}</td>
            <td class="align-middle">${producto}</td>
            <td class="fw-bold align-middle text-primary">${monto}</td>
            <td class="text-center align-middle">${cuotas}</td>
            <td class="text-center align-middle">${periodo}</td>
            <td class="text-center align-middle">${tea}</td>

            <td style="min-width: 240px;">
                <div class="card border-0 bg-light p-2" style="max-height: 200px; overflow-y: auto;">
                    ${htmlDocumentos}
                </div>
            </td>

            <td class="align-middle text-center">
                <div class="d-flex flex-column gap-2">
                    <button class="btn btn-sm btn-success fw-bold shadow-sm" onclick="abrirAprobar(${id})">
                        <i class="fas fa-check-circle me-1"></i> APROBAR
                    </button>
                    <button class="btn btn-sm btn-warning text-white fw-bold shadow-sm" onclick="abrirObservar(${id})">
                        <i class="fas fa-exclamation-circle me-1"></i> OBSERVAR
                    </button>
                    <button class="btn btn-sm btn-danger fw-bold shadow-sm" onclick="abrirRechazar(${id})">
                        <i class="fas fa-times-circle me-1"></i> RECHAZAR
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Genera la lista de documentos pero SOLO con botón de ver
function generarColumnaDocumentosReadOnly(docsMap) {
    let html = '';

    DOCUMENTOS_REQUERIDOS.forEach(doc => {
        const nombreArchivo = docsMap[doc.clave];
        const existe = !!nombreArchivo; // true si hay archivo

        let estadoIcono = '';

        if (existe) {
            // DOCUMENTO CARGADO -> Botón Azul "Ver"
            // Usamos encodeURIComponent por si el nombre tiene espacios
            const url = `${URL_VER_DOC}/${encodeURIComponent(nombreArchivo)}`;
            estadoIcono = `
                <button class="btn btn-sm btn-outline-info border-0 py-0"
                        title="Visualizar Documento"
                        onclick="window.open('${url}', '_blank')">
                    <i class="fas fa-eye fa-lg"></i>
                </button>
            `;
        } else {
            // NO CARGADO -> Icono Gris (Alerta visual)
            estadoIcono = `
                <span class="text-muted" title="No cargado">
                    <i class="fas fa-minus-circle"></i>
                </span>
            `;
        }

        html += `
            <div class="d-flex justify-content-between align-items-center mb-1 border-bottom pb-1">
                <span class="text-secondary small fw-bold" style="font-size: 0.7rem;">${doc.nombre}</span>
                <div>${estadoIcono}</div>
            </div>
        `;
    });

    return html;
}


// ================= 2. LÓGICA DE MODALES =================

// --- CARGAR DETALLE (Reutilizable) ---
async function cargarDetalleModal(id) {
    const res = await fetch(`${API_URL}/creditos/evaluaciones/${id}`);
    if (!res.ok) throw new Error("No se pudo cargar el detalle del expediente");
    return await res.json();
}

// A. APROBAR
window.abrirAprobar = async function(id) {
    try {
        const data = await cargarDetalleModal(id);

        // Llenar campos informativos (readonly)
        document.getElementById('hdnIdSolicitudAprobar').value = data.idExpediente;
        document.getElementById('txtDniAprobar').value = data.dni;
        document.getElementById('txtMontoSolAprobar').value = data.montoSolicitado;
        document.getElementById('txtCuotasSolAprobar').value = data.cuotas || 0;
        document.getElementById('txtPeriodoSolAprobar').value = data.periodo || '';
        document.getElementById('txtProductoAprobar').value = data.producto;
        document.getElementById('txtActividadAprobar').value = data.actividad || '';
        document.getElementById('txtRecurrenciaAprobar').value = data.recurrencia || '';
        document.getElementById('txtRiesgoAprobar').value = data.riesgo || '';

        // Pre-llenar campos de decisión con lo solicitado (para agilizar)
        document.getElementById('txtMontoAprobado').value = data.montoSolicitado;
        document.getElementById('txtCuotasAprobadas').value = data.cuotas || '';
        document.getElementById('txtPeriodoAprobado').value = data.periodo || '';

        // Limpiar campos variables
        document.getElementById('txtTemAprobado').value = '';
        document.getElementById('txtTeaAprobado').value = '';
        document.getElementById('txtResolucionAprobar').value = '';

        modalAprobar.show();
    } catch (error) {
        swal("Error", "No se pudo cargar la información del socio.", "error");
    }
};

document.getElementById('formAprobar').addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
        codExpediente: document.getElementById('hdnIdSolicitudAprobar').value,
        codTipoDecision: DECISION.APROBADO,
        observacion: document.getElementById('txtResolucionAprobar').value,

        // Datos Financieros
        montoAprobado: parseFloat(document.getElementById('txtMontoAprobado').value),
        cuotasAprobadas: parseInt(document.getElementById('txtCuotasAprobadas').value),
        // Si tu backend espera String en periodoAprobado, manda el valor del input
        periodoAprobado: document.getElementById('txtPeriodoAprobado').value,
        temAprobado: parseFloat(document.getElementById('txtTemAprobado').value || 0),
        teaAprobada: parseFloat(document.getElementById('txtTeaAprobado').value || 0)
    };

    enviarDecision(payload, modalAprobar);
});

// B. OBSERVAR
window.abrirObservar = function(id) {
    document.getElementById('hdnIdSolicitudObservar').value = id;
    document.getElementById('txtDetalleObservacion').value = '';
    modalObservar.show();
};

document.getElementById('formObservar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        codExpediente: document.getElementById('hdnIdSolicitudObservar').value,
        codTipoDecision: DECISION.OBSERVADO,
        observacion: document.getElementById('txtDetalleObservacion').value
    };
    enviarDecision(payload, modalObservar);
});

// C. RECHAZAR
window.abrirRechazar = function(id) {
    document.getElementById('hdnIdSolicitudRechazar').value = id;
    modalRechazar.show();
};

document.getElementById('btnConfirmarRechazo').addEventListener('click', () => {
    const payload = {
        codExpediente: document.getElementById('hdnIdSolicitudRechazar').value,
        codTipoDecision: DECISION.RECHAZADO,
        observacion: "Solicitud Rechazada por el evaluador."
    };
    enviarDecision(payload, modalRechazar);
});


// ================= 3. ENVÍO AL BACKEND =================

async function enviarDecision(payload, modalInstance) {
    try {
        // Bloquear botones visualmente si deseas...

        const response = await fetch(`${API_URL}/creditos/evaluaciones`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) throw new Error("Error procesando la decisión en el servidor");

        // Éxito
        swal("Éxito", "Evaluación registrada correctamente", "success");
        modalInstance.hide();
        listarEvaluaciones(); // Refrescar lista

    } catch (error) {
        console.error(error);
        swal("Error", "No se pudo registrar la operación.", "error");
    }
}