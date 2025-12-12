const API_URL = 'http://localhost:8080/api/v1';
const txtDniBuscar = document.getElementById('txtDniBuscar');

// --- 1. VARIABLE DE CONTEXTO (Necesaria para saber qué archivo subimos) ---
let contextoCarga = { idExp: null, claveTipo: null };

// --- 2. LISTENER DEL INPUT FILE (Sube el archivo apenas lo seleccionas) ---
const fileInput = document.getElementById('globalFileInput');
if (fileInput) {
    fileInput.addEventListener('change', async function() {
        if (this.files && this.files[0]) {
            await procesarSubidaArchivo(this.files[0]);
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    cargarListasDesplegables();
    // listarSolicitudes(); // Se llama al final, está ok.
});

// --- TU CÓDIGO ORIGINAL DE CARGA DE COMBOS (INTACTO) ---
async function cargarListasDesplegables() {
    try {
        const [prodRes, recuRes, riesRes, periRes] = await Promise.all([
            fetch(`${API_URL}/creditos/productos`),
            fetch(`${API_URL}/creditos/recurrencias`),
            fetch(`${API_URL}/creditos/riesgos`),
            fetch(`${API_URL}/creditos/periodos`)
        ]);

        if (!prodRes.ok || !recuRes.ok || !riesRes.ok || !periRes.ok) {
            throw new Error('Error conectando con los catálogos del servicio');
        }

        const productos = await prodRes.json();
        const recurrencias = await recuRes.json();
        const riesgos = await riesRes.json();
        const periodos = await periRes.json();

        // Mantenemos tus nombres de campos originales
        llenarSelect('cboProducto', productos, 'codProducto', 'nombre'); // Ajusta 'nombre' si tu DTO devuelve 'nombreProducto'
        llenarSelect('cboRecurrencia', recurrencias, 'codRecurrencia', 'nombre');
        llenarSelect('cboRiesgo', riesgos, 'codRiesgo', 'descripcion');
        llenarSelect('cboPeriodo', periodos, 'codPeriodo', 'descripcion');

    } catch (error) {
        console.error(error);
        alert('Error: No se pudieron cargar las listas del formulario.');
    }
}

function llenarSelect(idSelect, lista, idCampo, nombreCampo) {
    const select = document.getElementById(idSelect);
    if (!select) return;
    select.innerHTML = '<option selected disabled>-- Seleccione --</option>';

    lista.forEach(item => {
        const option = document.createElement('option');
        option.value = item[idCampo];
        option.textContent = item[nombreCampo];
        select.appendChild(option);
    });
}

// --- TU CÓDIGO DE BÚSQUEDA SOCIO (INTACTO) ---
txtDniBuscar.addEventListener('blur', async function() {
    const dni = this.value.trim();
    if (dni.length !== 8) return;

    try {
        document.body.style.cursor = 'wait';
        limpiarCamposSocio();

        const response = await fetch(`${API_URL}/socio/buscar/${dni}`);

        if (!response.ok) {
            const mensajeError = await response.text();
            throw new Error(mensajeError);
        }

        const socio = await response.json();
        llenarFormularioSocio(socio);

    } catch (error) {
        limpiarCamposSocio();
        let msg = error.message;
        try { msg = JSON.parse(msg).mesagge || msg; } catch(e){}
        alert("⚠️ ATENCIÓN:\n" + msg);
    } finally {
        document.body.style.cursor = 'default';
    }
});

function llenarFormularioSocio(socio) {
    document.getElementById('txtDniResultado').value = socio.dni;
    document.getElementById('txtNombreCompleto').value = socio.nombresCompletos;
    document.getElementById('txtNRegistro').value = socio.codSocio;
    document.getElementById('txtEdad').value = socio.edad + ' AÑOS';
    document.getElementById('txtFechaNacimiento').value = socio.fechaNacimiento;
    document.getElementById('txtEstadoCivil').value = socio.estadoCivil;
    document.getElementById('txtDomicilio').value = socio.domicilio;
    document.getElementById('txtDepartamento').value = socio.departamento;
    document.getElementById('txtProvincia').value = socio.provincia;
    document.getElementById('txtDistrito').value = socio.distrito;
    document.getElementById('txtSector').value = socio.sector;
}

function limpiarCamposSocio() {
    const ids = [
        'txtDniResultado', 'txtNombreCompleto', 'txtNRegistro',
        'txtEdad', 'txtFechaNacimiento', 'txtEstadoCivil',
        'txtDomicilio', 'txtDepartamento', 'txtProvincia',
        'txtDistrito', 'txtSector'
    ];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

// --- TU CÓDIGO DE CÁLCULOS (INTACTO) ---
const txtTem = document.getElementById('txtTem');
const txtTea = document.getElementById('txtTea');

txtTem.addEventListener('input', function() {
    const valorTem = parseFloat(this.value);
    if (!isNaN(valorTem) && valorTem >= 0) {
        const temDecimal = valorTem / 100;
        const teaDecimal = Math.pow((1 + temDecimal), 12) - 1;
        const teaPorcentaje = (teaDecimal * 100).toFixed(2);
        txtTea.value = teaPorcentaje;
    } else {
        txtTea.value = '';
    }
});

// --- TU CÓDIGO DE REGISTRO (INTACTO) ---
// --- TU CÓDIGO DE REGISTRO (CORREGIDO) ---
const btnRegistrar = document.getElementById('btnRegistrar');

btnRegistrar.addEventListener('click', async function() {
    const dniSocio = document.getElementById('txtDniResultado').value;
    const temValor = document.getElementById('txtTem').value;
    const teaValor = document.getElementById('txtTea').value;

    // Pequeña ayuda para validar selects vacíos
    const getVal = (id) => {
        const el = document.getElementById(id);
        return (el && el.value !== "-- Seleccione --") ? el.value : null;
    };

    // Usamos parseFloat para asegurar que los valores sean números
    const datosSolicitud = {
        dniSocio: dniSocio,
        montoSolicitado: parseFloat(document.getElementById('txtMonto').value),
        codProducto: parseInt(getVal('cboProducto')),
        codRecurrencia: parseInt(getVal('cboRecurrencia')),
        codRiesgo: parseInt(getVal('cboRiesgo')),
        codPeriodo: parseInt(getVal('cboPeriodo')),
        actividad: document.getElementById('txtActividad').value,
        codTrabajador: 1 // Asumimos que el trabajador es fijo por ahora
        // No enviamos TEM/TEA porque esos son datos calculados, no solicitados.
        // El backend no los necesita para el SS-1.
    };

    // --- VALIDACIONES ---
    if (!dniSocio) { alert("⚠️ Error: Debes buscar y validar un socio primero."); return; }
    if (datosSolicitud.montoSolicitado <= 0 || isNaN(datosSolicitud.montoSolicitado)) { alert("⚠️ Error: El monto debe ser un número mayor a 0."); return; }
    if (!datosSolicitud.codProducto || !datosSolicitud.codRecurrencia) { alert("⚠️ Error: Complete todos los campos de selección."); return; }
    // --- FIN VALIDACIONES ---

    try {
        btnRegistrar.disabled = true;
        btnRegistrar.textContent = "Procesando...";

        // --- CAMBIO CLAVE: Llama al nuevo Orquestador (SN-1) ---
        const response = await fetch(`${API_URL}/credito/solicitar-expediente`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datosSolicitud)
        });

        if (!response.ok) {
            // Manejo de errores de validación de la capa SE-1 y SE-2
            let errorMsg = await response.text();
            try {
                // Intenta parsear el JSON de error que manda el GlobalExceptionHandler
                const errorJson = JSON.parse(errorMsg);
                errorMsg = errorJson.message || errorMsg;
            } catch(e){}

            throw new Error(errorMsg);
        }

        // Si el Orquestador SN-1 (Service) retorna 201 Created
        alert(`✅ ¡Solicitud Registrada y Orquestada con Éxito!`);
        location.reload();

    } catch (error) {
        alert("❌ Error en el proceso (SOA): " + error.message);
    } finally {
        btnRegistrar.disabled = false;
        btnRegistrar.textContent = "REGISTRAR SOLICITUD";
    }
});

// =========================================================
// AQUÍ EMPIEZA LO DE LA TABLA Y DOCUMENTOS (CORREGIDO)
// =========================================================

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
    listarSolicitudes();
});

async function listarSolicitudes() {
    try {
        const response = await fetch(`${API_URL}/creditos/listado`);

        if (!response.ok) {
            throw new Error('Error al obtener las solicitudes');
        }

        const solicitudes = await response.json();
        console.log("Datos recibidos:", solicitudes);
        renderizarTabla(solicitudes);

    } catch (error) {
        console.error('Error:', error);
    }
}

function renderizarTabla(datos) {
    const tbody = document.getElementById('tablaCreditos');
    tbody.innerHTML = '';

    datos.forEach(solicitud => {

        const id = solicitud.codExpediente;
        const fecha = solicitud.fechaSolicitud;
        const monto = solicitud.montoSolicitado;
        const socioObj = solicitud.socio || {};

        let nombreSocio = '';
        if (socioObj.nombresCompletos) {
            nombreSocio = socioObj.nombresCompletos;
        } else {
            const n = socioObj.nombres || socioObj.nombre || '';
            const ap = socioObj.apellidoPaterno || '';
            const am = socioObj.apellidoMaterno || '';
            nombreSocio = `${n} ${ap} ${am}`.trim();
        }
        if (!nombreSocio) nombreSocio = 'SOCIO SIN NOMBRE';

        const dniSocio = socioObj.dni || '---'; // <-- AQUÍ YA TIENES EL DNI CORRECTO DE LA FILA

        // Nombres de productos tal como los tenías
        const nombreProducto = solicitud.producto?.nombreProducto || '---';
        const periodo = solicitud.periodo?.nombrePeriodo || '---';
        const cuotas = solicitud.cuotas || 1;

        // Mapeo de documentos
        const docsMap = {};
        if (solicitud.documentos && solicitud.documentos.length > 0) {
            solicitud.documentos.forEach(doc => {
                const nombreArchivo = doc.nombreArchivo;
                DOCUMENTOS_REQUERIDOS.forEach(req => {
                    if (nombreArchivo && nombreArchivo.includes(`_${req.clave}_`)) {
                        docsMap[req.clave] = nombreArchivo;
                    }
                });
            });
        }

        // --- CAMBIO CLAVE: Pasamos dniSocio como tercer parámetro ---
        const htmlExpediente = generarColumnaExpediente(id, docsMap, dniSocio);

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td class="fw-bold text-center">${id}</td>
            <td>${fecha}</td>
            <td>${dniSocio}</td>
            <td class="text-uppercase">${nombreSocio}</td>
            <td class="text-uppercase">${nombreProducto}</td>
            <td class="fw-bold">S/ ${parseFloat(monto).toFixed(2)}</td>
            <td class="text-center">${cuotas}</td>
            <td class="text-center">${periodo}</td>

            <td style="min-width: 300px;">
                <div class="d-flex flex-column gap-1">
                    ${htmlExpediente}
                </div>
                <button class="btn btn-success w-100 mt-3 btn-sm text-white fw-bold shadow-sm"
                        onclick="enviarAEvaluacion(${id})">
                    <i class="fas fa-paper-plane me-2"></i> ENVIAR A EVALUACIÓN
                </button>
            </td>
        `;

        tbody.appendChild(fila);
    });
}

// --- CAMBIO CLAVE: Recibimos dniSocio ---
function generarColumnaExpediente(idSolicitud, docsMap, dniSocio) {
    let html = '';

    DOCUMENTOS_REQUERIDOS.forEach(doc => {
        const nombreArchivo = docsMap[doc.clave];
        const existe = !!nombreArchivo;

        let botonPrincipal = '';

        if (existe) {
            // OJO (Ver)
            const urlVer = `${API_URL}/creditos/documentos/ver/${nombreArchivo}`;
            botonPrincipal = `
                <button class="btn btn-sm btn-info text-white p-0 me-2"
                        title="Ver Documento"
                        onclick="window.open('${urlVer}', '_blank')">
                    <i class="fas fa-eye"></i>
                </button>
            `;
        } else {
            // NUBE (Subir) - AQUÍ PASAMOS EL DNI AL ONCLICK
            botonPrincipal = `
                <button class="btn btn-sm btn-link text-secondary p-0 me-2"
                        title="Subir archivo"
                        onclick="abrirModalCarga(${idSolicitud}, '${doc.clave}', '${dniSocio}')">
                    <i class="fas fa-cloud-upload-alt"></i>
                </button>
            `;
        }

        // LÁPIZ (Editar) - TAMBIÉN PASAMOS EL DNI
        const botonEditar = `
            <button class="btn btn-sm btn-link text-secondary p-0"
                    title="Actualizar / Reemplazar"
                    onclick="abrirModalCarga(${idSolicitud}, '${doc.clave}', '${dniSocio}')">
                <i class="fas fa-pen"></i>
            </button>
        `;

        html += `
            <div class="d-flex justify-content-between align-items-center border-bottom pb-1 mb-1">
                <span class="small text-muted fw-bold" style="font-size: 0.75rem;">${doc.nombre}</span>
                <div class="btn-group">
                    ${botonPrincipal}
                    ${botonEditar}
                </div>
            </div>
        `;
    });

    return html;
}

// --- FUNCIONES DE SUBIDA E INTEGRACIÓN ---

// --- CAMBIO CLAVE: Recibimos dniSocio directamente ---
function abrirModalCarga(idSolicitud, tipoDoc, dniSocio) {
    contextoCarga.idExp = idSolicitud;
    contextoCarga.claveTipo = tipoDoc;

    // 1. INTERCEPTAMOS SI ES CENTRAL DE RIESGOS
    if (tipoDoc === 'riesgos') {

        // Validamos usando el dato que vino de la fila, no del input
        if (!dniSocio || dniSocio === '---') {
            alert("⚠️ Error: Este registro no tiene un DNI válido.");
            return;
        }

        const usarIntegracion = confirm(
            `🌐 SISTEMA SOA DETECTADO\n\n` +
            `DNI SOCIO: ${dniSocio}\n` +
            `El documento 'CENTRAL DE RIESGOS' permite integración automática.\n\n` +
            `¿Desea conectarse a EXPERIAN para descargar el historial?\n` +
            `(Haga clic en CANCELAR para subir un archivo manualmente)`
        );

        if (usarIntegracion) {
            invocarApiExperian(idSolicitud, dniSocio);
            return;
        }
    }

    // 2. SI NO ES RIESGOS, COMPORTAMIENTO NORMAL
    const fileInput = document.getElementById('globalFileInput');
    if(fileInput) {
        fileInput.value = '';
        fileInput.click();
    }
}

async function invocarApiExperian(idExpediente, dni) {
    try {
        document.body.style.cursor = 'wait';
        alert("⏳ Conectando con API Experian...\nDescargando reporte de crédito...");

        const url = `${API_URL}/integraciones/experian?dni=${dni}&codExpediente=${idExpediente}`;
        const response = await fetch(url, { method: 'POST' });

        if (!response.ok) {
            const errorTxt = await response.text();
            throw new Error(errorTxt);
        }

        const data = await response.json();
        alert(`✅ ¡INTEGRACIÓN EXITOSA!\nSe descargó el reporte: ${data.nombreArchivo}`);
        listarSolicitudes();

    } catch (error) {
        console.error(error);
        let msg = error.message;
        try { msg = JSON.parse(msg).mesagge; } catch(e){}
        alert("❌ Error en la integración: " + msg);
    } finally {
        document.body.style.cursor = 'default';
        contextoCarga = { idExp: null, claveTipo: null };
    }
}

async function procesarSubidaArchivo(archivo) {
    if (!contextoCarga.idExp || !contextoCarga.claveTipo) return;

    const LIMITE_MB = 50;
    if (archivo.size > LIMITE_MB * 1024 * 1024) {
        alert(`❌ El archivo es muy pesado. Máximo permitido: ${LIMITE_MB}MB.`);
        document.getElementById('globalFileInput').value = '';
        return;
    }

    const formData = new FormData();
    formData.append('archivo', archivo);
    formData.append('codExpediente', contextoCarga.idExp);
    formData.append('tipoDoc', contextoCarga.claveTipo);

    try {
        document.body.style.cursor = 'wait';
        const response = await fetch(`${API_URL}/creditos/documentos/subir`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorTxt = await response.text();
            throw new Error(errorTxt);
        }

        const data = await response.json();
        alert(`✅ Archivo cargado correctamente.`);
        listarSolicitudes();

    } catch (error) {
        console.error("Error subida:", error);
        let msg = error.message;
        try { msg = JSON.parse(msg).mesagge; } catch(e){}
        alert("❌ Error al subir: " + msg);
    } finally {
        document.body.style.cursor = 'default';
        contextoCarga = { idExp: null, claveTipo: null };
    }
}

// --- FUNCIÓN ENVIAR A EVALUACIÓN (CONECTADA AL BACKEND) ---
async function enviarAEvaluacion(id) {
    // 1. Confirmación visual
    const confirmar = confirm(
        `¿Está seguro de enviar la Solicitud N° ${id} a evaluación?\n\n` +
        `⚠️ Una vez enviada, pasará al área de créditos y no se podrán subir ni editar más documentos.`
    );

    if (!confirmar) return;

    try {
        // 2. Feedback visual de carga
        document.body.style.cursor = 'wait';

        // 3. Llamada al Backend (PUT)
        // La URL asume que tu controlador responde en /api/v1/creditos/{id}/enviar
        const response = await fetch(`${API_URL}/creditos/${id}/enviar`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // 4. Manejo de Errores (Validación de documentos del Backend)
        if (!response.ok) {
            // Intentamos leer el mensaje JSON que manda el backend (Map.of("error", ...))
            const errorData = await response.json().catch(() => null);
            const mensajeError = errorData?.error || await response.text() || "No se pudo enviar la solicitud.";

            throw new Error(mensajeError);
        }

        // 5. Éxito
        const data = await response.json();
        alert(`✅ ÉXITO: ${data.mensaje}`);

        // 6. Recargar la tabla para que la solicitud desaparezca de esta vista
        listarSolicitudes();

    } catch (error) {
        console.error(error);
        alert(`❌ NO SE PUDO ENVIAR:\n${error.message}`);
    } finally {
        document.body.style.cursor = 'default';
    }
}
